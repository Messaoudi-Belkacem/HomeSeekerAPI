package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.model.request.CheckTokenRequest;
import com.example.demo.model.request.LogoutRequest;
import com.example.demo.model.response.AuthenticationResponse;
import com.example.demo.model.response.CheckTokenResponse;
import com.example.demo.model.response.LogoutResponse;
import com.example.demo.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody User request
            ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody User request
    ) {
        try {
            AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
            return ResponseEntity.ok(authenticationResponse);
        } catch (Exception e) {
            AuthenticationResponse authenticationResponse = new AuthenticationResponse(null, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authenticationResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(
            @RequestBody LogoutRequest logoutRequest
    ) {
        try {
            LogoutResponse logoutResponse = authenticationService.logout(logoutRequest);
            return ResponseEntity.ok(logoutResponse);
        } catch (Exception e) {
            LogoutResponse logoutResponse = new LogoutResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(logoutResponse);
        }
    }

    @PostMapping("/check")
    public ResponseEntity<CheckTokenResponse> checkToken(
            @RequestBody CheckTokenRequest checkTokenRequest
    ) {
        try {
            CheckTokenResponse checkTokenResponse = authenticationService.checkToken(checkTokenRequest);
            return ResponseEntity.ok(checkTokenResponse);
        } catch (Exception e) {
            CheckTokenResponse checkTokenResponse = new CheckTokenResponse(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(checkTokenResponse);
        }
    }
}