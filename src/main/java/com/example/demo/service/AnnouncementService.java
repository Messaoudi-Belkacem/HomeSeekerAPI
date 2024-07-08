package com.example.demo.service;

import com.example.demo.model.Announcement;
import com.example.demo.model.User;
import com.example.demo.repository.AnnouncementRepository;
import com.example.demo.repository.FileDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@Service
public class AnnouncementService {
    @Autowired
    private AnnouncementRepository announcementRepository;

    @Autowired
    private ImageService imageService;

    public Announcement createAnnouncement(Announcement announcement, List<MultipartFile> multipartFileList, Principal principal) throws IOException {
        StringBuilder response = new StringBuilder();
        announcement.setOwner(principal.getName());
        Announcement savedAnnouncement = announcementRepository.save(announcement);
        for (MultipartFile item : multipartFileList) {
            response.append(imageService.uploadImageToFileSystem(item, savedAnnouncement.getId()));
        }
        return announcement;
    }

}
