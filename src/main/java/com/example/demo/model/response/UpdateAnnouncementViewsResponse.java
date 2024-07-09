package com.example.demo.model.response;

import com.example.demo.model.Announcement;

public record UpdateAnnouncementViewsResponse(Announcement announcement, String message) {}