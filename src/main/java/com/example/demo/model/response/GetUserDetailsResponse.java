package com.example.demo.model.response;

import com.example.demo.model.User;

public record GetUserDetailsResponse(User user, String message) {}