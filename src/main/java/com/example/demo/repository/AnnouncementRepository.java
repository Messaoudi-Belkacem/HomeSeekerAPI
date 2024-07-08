package com.example.demo.repository;

import com.example.demo.model.Announcement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {
    Page<Announcement> findByOwner(String owner, PageRequest pageRequest);
}