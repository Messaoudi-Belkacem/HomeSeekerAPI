package com.example.demo.service;

import com.example.demo.model.Announcement;
import com.example.demo.model.User;
import com.example.demo.model.response.DeleteAnnouncementResponse;
import com.example.demo.repository.AnnouncementRepository;
import com.example.demo.repository.FileDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
public class AnnouncementService {
    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private ImageService imageService;

    public Announcement createAnnouncement(
            Announcement announcement,
            List<MultipartFile> multipartFileList,
            Principal principal
    ) throws IOException {
        announcement.setOwner(principal.getName());
        Announcement savedAnnouncement = announcementRepository.save(announcement);
        for (MultipartFile item : multipartFileList) {
            imageService.uploadImageToFileSystem(item, savedAnnouncement.getId());
        }
        return announcement;
    }

    public Page<Announcement> getAllAnnouncementsByUser(Pageable pageable, Principal principal) {
        Page<Announcement> announcementsPage;
        announcementsPage = announcementRepository.findByOwner(
                principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "title"))
                )
        );
        return announcementsPage;
    }

    public ResponseEntity<DeleteAnnouncementResponse> deleteAnnouncement(Principal principal, Integer announcementId) {
        Optional<Announcement> optionalAnnouncement = announcementRepository.findById(announcementId);
        if (optionalAnnouncement.isPresent()) {
            if (optionalAnnouncement.get().getOwner().equals(principal.getName())) {
                announcementRepository.deleteById(announcementId);
                DeleteAnnouncementResponse deleteAnnouncementResponse = new DeleteAnnouncementResponse("Announcement deleted successfully");
                return new ResponseEntity<>(deleteAnnouncementResponse, HttpStatus.OK);
            } else {
                DeleteAnnouncementResponse deleteAnnouncementResponse = new DeleteAnnouncementResponse("You are not allowed to delete this announcement");
                return new ResponseEntity<>(deleteAnnouncementResponse, HttpStatus.FORBIDDEN);
            }
        } else {
            DeleteAnnouncementResponse deleteAnnouncementResponse = new DeleteAnnouncementResponse("Announcement not found");
            return new ResponseEntity<>(deleteAnnouncementResponse, HttpStatus.NOT_FOUND);
        }
    }

}
