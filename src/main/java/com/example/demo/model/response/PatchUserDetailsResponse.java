package com.example.demo.model.response;

import com.example.demo.model.User;

public record PatchUserDetailsResponse(User user, String message) {}