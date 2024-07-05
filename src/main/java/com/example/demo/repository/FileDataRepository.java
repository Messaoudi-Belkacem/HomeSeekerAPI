package com.example.demo.repository;

import com.example.demo.model.FileData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileDataRepository extends JpaRepository<FileData, Integer> {
    Optional<FileData> findByName(String fileName);
    List<FileData> findByAnnouncementId(Integer announcementId);
}
