package com.example.demo.controller;

import com.example.demo.excption.BadRequestException;
import com.example.demo.excption.InternalServerException;
import com.example.demo.excption.ResourceNotFoundException;
import com.example.demo.model.Announcement;
import com.example.demo.model.response.CreateAnnouncementResponse;
import com.example.demo.repository.AnnouncementRepository;
import com.example.demo.service.AnnouncementService;
import com.example.demo.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/announcements")
public class AnnouncementController {

    private final AnnouncementRepository announcementRepository;
    private final ImageService imageService;
    private final AnnouncementService announcementService;

    Logger logger = LoggerFactory.getLogger(AnnouncementController.class.getName());

    // Constructor
    public AnnouncementController(
            AnnouncementRepository announcementRepository,
            ImageService imageService,
            AnnouncementService announcementService
    ) {
        this.announcementRepository = announcementRepository;
        this.imageService = imageService;
        this.announcementService = announcementService;
    }

    // Get
    @GetMapping
    public ResponseEntity<?> getAnnouncementsByPaginationAndSorting(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        try {
            logger.trace("getAnnouncementsByPaginationAndSorting is called!");
            Sort.Direction sortDirection = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            Page<Announcement> announcementsPage = announcementRepository.findAll(pageable);
            return new ResponseEntity<>(announcementsPage, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid request parameters");
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException("Resource not found");
        } catch (Exception e) {
            throw new InternalServerException("An internal server error occurred");
        }
    }

    @GetMapping("/user")
    private ResponseEntity<?> getAllOwnedAnnouncements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            Principal principal
    ) {
        logger.trace("getAllOwnedAnnouncements is called!");
        try {
            Sort.Direction sortDirection = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
            Page<Announcement> announcementsPage = announcementService.getAllAnnouncementsByUser(pageable, principal);
            return new ResponseEntity<>(announcementsPage, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            throw new BadRequestException("Invalid request parameters");
        } catch (ResourceNotFoundException e) {
            logger.error(e.getMessage());
            throw new ResourceNotFoundException("Resource not found");
        } catch (Exception e) {
            logger.error(e.getMessage());
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


    // Post
    @PostMapping
    public ResponseEntity<CreateAnnouncementResponse> createAnnouncement(
            @RequestPart("data") Announcement announcement,
            @RequestPart("images") List<MultipartFile> multipartFileList,
            Principal principal
    ) {
        System.out.println("createAnnouncement is called!");
        try {
            Announcement announcementResponse = announcementService.createAnnouncement(announcement, multipartFileList, principal);
            CreateAnnouncementResponse createAnnouncementResponse = new CreateAnnouncementResponse(announcementResponse, "announcement created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(createAnnouncementResponse);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CreateAnnouncementResponse(null, "Error occurred while processing files: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CreateAnnouncementResponse(null, "An error occurred: " + e.getMessage()));
        }
        /*System.out.println("createAnnouncement is called!");
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
        }*/
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

    @PatchMapping("/views")
    public ResponseEntity<?> incrementAnnouncementViews(@RequestParam int announcementID) {
        logger.trace("incrementAnnouncementViews is called!");
        try {
            return announcementService.incrementAnnouncementViews(announcementID);
        } catch (ResourceNotFoundException e) {
            logger.error(e.getMessage());
            throw new ResourceNotFoundException("Resource not found");
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerException("An internal server error occurred");
        }
    }

    // Delete
    @DeleteMapping
    public ResponseEntity<?> deleteAnnouncement(
            @RequestParam Integer announcementId,
            Principal principal
    ) {
        return announcementService.deleteAnnouncement(principal, announcementId);
    }
}
