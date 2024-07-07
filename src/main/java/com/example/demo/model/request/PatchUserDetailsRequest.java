package com.example.demo.model.request;

import com.example.demo.model.User;

public record PatchUserDetailsRequest(String token, User user) { }
