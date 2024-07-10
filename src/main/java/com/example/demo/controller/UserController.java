package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.request.PatchUserDetailsRequest;
import com.example.demo.model.response.GetUserDetailsResponse;
import com.example.demo.model.response.PatchUserDetailsResponse;
import com.example.demo.service.JwtService;
import com.example.demo.service.UserDetailsServiceImplementation;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserDetailsServiceImplementation userDetailsServiceImplementation;
    private final JwtService jwtService;

    Logger logger = LoggerFactory.getLogger(UserController.class.getName());

    public UserController(UserDetailsServiceImplementation userDetailsServiceImplementation, JwtService jwtService) {
        this.userDetailsServiceImplementation = userDetailsServiceImplementation;
        this.jwtService = jwtService;
    }

    @GetMapping()
    public ResponseEntity<GetUserDetailsResponse> getUserDetails(@RequestParam String token) {
        try {
            logger.trace("getUserDetails is called");
            logger.trace("token: {}", token);
            String username = jwtService.extractUsername(token);
            logger.trace("username: {}", username);
            User user = (User) userDetailsServiceImplementation.loadUserByUsername(username);
            GetUserDetailsResponse getUserDetailsResponse = new GetUserDetailsResponse(user, "User fetched successfully");
            return ResponseEntity.ok(getUserDetailsResponse);
        } catch (UsernameNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GetUserDetailsResponse(null, "User not found"));
        } catch (JwtException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new GetUserDetailsResponse(null, "Invalid token"));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GetUserDetailsResponse(null, "An unexpected error occurred"));
        }
    }

    @PatchMapping
    public ResponseEntity<PatchUserDetailsResponse> updateUserPartially(@RequestBody PatchUserDetailsRequest patchUserDetailsRequest) {
        try {
            logger.trace("updateUserPartially is called");
            String token = patchUserDetailsRequest.token();
            String username = jwtService.extractUsername(token);
            User updates = patchUserDetailsRequest.user();
            logger.trace("updates: {}", updates);
            User updatedUser = (User) userDetailsServiceImplementation.updateUser(updates, username);
            if (Objects.equals(updatedUser.getId(), updates.getId())) {
                PatchUserDetailsResponse response = new PatchUserDetailsResponse(updatedUser, "User fetched successfully");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new PatchUserDetailsResponse(null, "Wrong user id"));
            }
        } catch (UsernameNotFoundException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new PatchUserDetailsResponse(null, "User not found"));
        } catch (JwtException e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new PatchUserDetailsResponse(null, "Invalid token"));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PatchUserDetailsResponse(null, "An unexpected error occurred"));
        }
    }
}