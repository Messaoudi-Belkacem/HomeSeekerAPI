package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.model.request.CheckTokenRequest;
import com.example.demo.model.request.LogoutRequest;
import com.example.demo.model.response.AuthenticationResponse;
import com.example.demo.model.response.CheckTokenResponse;
import com.example.demo.model.response.LogoutResponse;
import com.example.demo.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    Logger logger = LoggerFactory.getLogger(AuthenticationController.class.getName());

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody User request
            ) {
        logger.trace("Registering user {}", request);
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody User request
    ) {
        try {
            logger.trace("Logining user {}", request);
            AuthenticationResponse authenticationResponse = authenticationService.authenticate(request);
            return ResponseEntity.ok(authenticationResponse);
        } catch (Exception e) {
            logger.error(e.getMessage());
            AuthenticationResponse authenticationResponse = new AuthenticationResponse(null, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authenticationResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(
            @RequestBody LogoutRequest logoutRequest
    ) {
        try {
            logger.trace("Logout user {}", logoutRequest);
            LogoutResponse logoutResponse = authenticationService.logout(logoutRequest);
            return ResponseEntity.ok(logoutResponse);
        } catch (Exception e) {
            logger.error(e.getMessage());
            LogoutResponse logoutResponse = new LogoutResponse(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(logoutResponse);
        }
    }

    @PostMapping("/check")
    public ResponseEntity<CheckTokenResponse> checkToken(
            @RequestBody CheckTokenRequest checkTokenRequest
    ) {
        try {
            logger.trace("Checking token {}", checkTokenRequest);
            CheckTokenResponse checkTokenResponse = authenticationService.checkToken(checkTokenRequest);
            return ResponseEntity.ok(checkTokenResponse);
        } catch (Exception e) {
            logger.error(e.getMessage());
            CheckTokenResponse checkTokenResponse = new CheckTokenResponse(false, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(checkTokenResponse);
        }
    }
}