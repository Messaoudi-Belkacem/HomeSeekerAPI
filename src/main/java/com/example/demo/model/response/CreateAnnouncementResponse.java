package com.example.demo.model.response;

import com.example.demo.model.Announcement;

public record CreateAnnouncementResponse(Announcement announcement, String message) {}