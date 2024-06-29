package com.example.demo.repository;

import com.example.demo.model.ImageData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<ImageData, Long> {
    Optional<ImageData> findByName(String fileName);
    List<ImageData> findAllByAnnouncementId(Long announcementId);
}
