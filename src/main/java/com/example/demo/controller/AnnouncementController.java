package com.example.demo.controller;

import com.example.demo.excption.BadRequestException;
import com.example.demo.excption.InternalServerException;
import com.example.demo.excption.ResourceNotFoundException;
import com.example.demo.model.Announcement;
import com.example.demo.repository.AnnouncementRepository;
import com.example.demo.service.ImageService;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/announcements")
public class AnnouncementController {
    private final AnnouncementRepository announcementRepository;
    private final ImageService imageService;

    // Constructor
    public AnnouncementController(
            AnnouncementRepository announcementRepository,
            ImageService imageService
    ) {
        this.announcementRepository = announcementRepository;
        this.imageService = imageService;
    }

    // Get
    @GetMapping
    public ResponseEntity<?> getAnnouncementsByPaginationAndSorting(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        try {
            System.out.println("getAnnouncementsByPaginationAndSorting is called!");
            Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<Announcement> announcementPage = announcementRepository.findAll(pageable);
            return new ResponseEntity<>(announcementPage, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid request parameters");
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Resource not found");
        } catch (Exception e) {
            throw new InternalServerException("An internal server error occurred");
        }
    }

    @GetMapping("/images/{fileName}")
    public ResponseEntity<?> downloadImageFromFileSystem(@PathVariable String fileName) throws IOException {
        byte[] imageData = imageService.downloadImageFromFileSystem(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/jpeg"))
                .body(imageData);
    }

    /*@GetMapping("/user")
    private ResponseEntity<?> findAllOwnedAnnouncements(Pageable pageable, Principal principal) {
        Page<Announcement> announcementPage = announcementRepository.findByOwner(principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
        List<Announcement> announcements = announcementPage.getContent();
        List<AnnouncementWithImages> announcementsWithImages = new ArrayList<>();

        for (Announcement announcement : announcements) {
            List<byte[]> images = imageService.downloadImages(announcement.getId());
            AnnouncementWithImages announcementWithImages = new AnnouncementWithImages(
                    announcement.getId(),
                    announcement.getTitle(),
                    announcement.getArea(),
                    announcement.getNumberOfRooms(),
                    announcement.getPropertyType(),
                    announcement.getLocation(),
                    announcement.getState(),
                    announcement.getPrice(),
                    announcement.getDescription(),
                    announcement.getOwner(),
                    images
            );
            announcementsWithImages.add(announcementWithImages);
        }

        Page<AnnouncementWithImages> announcementWithImagesPage = new PageImpl<>(announcementsWithImages, pageable, announcementsWithImages.size());

        return new ResponseEntity<>(announcementWithImagesPage, HttpStatus.OK);
        // TODO Handle the exceptions
    }*/

    /*@GetMapping("/user/{requestedId}")
    private ResponseEntity<AnnouncementResponse> findOwnedAnnouncementById(@PathVariable Long requestedId, Principal principal) {
        Optional<Announcement> announcementOptional = Optional.ofNullable(announcementRepository.findByIdAndOwner(requestedId, principal.getName()));
        if (announcementOptional.isPresent()) {
            Announcement announcement = announcementOptional.get();
            List<byte[]> images = imageService.downloadImages(announcement.getId());

            AnnouncementResponse response = new AnnouncementResponse();
            response.setAnnouncement(announcement);
            response.setImages(images);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }*/

    /*@GetMapping("/{id}")
    public ResponseEntity<AnnouncementResponse> getAnnouncementById(@PathVariable Long id) {
        Optional<Announcement> announcementOptional = announcementRepository.findById(id);
        if (announcementOptional.isPresent()) {
            Announcement announcement = announcementOptional.get();
            List<byte[]> images = imageService.downloadImages(announcement.getId());

            AnnouncementResponse response = new AnnouncementResponse();
            response.setAnnouncement(announcement);
            response.setImages(images);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }*/

    @PostMapping
    public ResponseEntity<?> createAnnouncement(
            @RequestPart("data") Announcement announcement,
            @RequestPart("images") List<MultipartFile> multipartFileList,
            Principal principal
    ) {
        System.out.println("createAnnouncement is called!");
        try {
            StringBuilder response = new StringBuilder();
            announcement.setOwner(principal.getName());
            Announcement savedAnnouncement = announcementRepository.save(announcement);
            for (MultipartFile item : multipartFileList) {
                response.append(imageService.uploadImageToFileSystem(item, savedAnnouncement.getId()));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("Announcement created successfully and response: " + response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while processing files: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred: " + e.getMessage());
        }
    }

    @PostMapping("/images")
    public ResponseEntity<?> uploadImageToFIleSystem(
            @RequestParam("image")MultipartFile file,
            @RequestParam("announcementId")Integer announcementId
    ) throws IOException {
        String uploadImage = imageService.uploadImageToFileSystem(file, announcementId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(uploadImage);
    }

    /*@PutMapping("/{id}")
    public ResponseEntity<?> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcementDetails) {
        Optional<Announcement> optionalAnnouncement = announcementRepository.findById(id);
        if (optionalAnnouncement.isPresent()) {
            Announcement existingAnnouncement = optionalAnnouncement.get();
            existingAnnouncement.setTitle(announcementDetails.getTitle());
            existingAnnouncement.setArea(announcementDetails.getArea());
            existingAnnouncement.setDescription(announcementDetails.getDescription());
            existingAnnouncement.setLocation(announcementDetails.getLocation());
            existingAnnouncement.setPrice(announcementDetails.getPrice());
            existingAnnouncement.setNumberOfRooms(announcementDetails.getNumberOfRooms());
            existingAnnouncement.setPropertyType(announcementDetails.getPropertyType());
            existingAnnouncement.setState(announcementDetails.getState());
            announcementRepository.save(existingAnnouncement);
            return new ResponseEntity<>("Book updated successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Book not found", HttpStatus.NOT_FOUND);
        }
    }*/

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable Integer id) {
        Optional<Announcement> optionalAnnouncement = announcementRepository.findById(id);
        if (optionalAnnouncement.isPresent()) {
            announcementRepository.deleteById(id);
            return new ResponseEntity<>("Announcement deleted successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Announcement not found", HttpStatus.NOT_FOUND);
        }
    }
}
