package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.request.PatchUserDetailsRequest;
import com.example.demo.model.response.GetUserDetailsResponse;
import com.example.demo.model.response.PatchUserDetailsResponse;
import com.example.demo.service.JwtService;
import com.example.demo.service.UserDetailsServiceImplementation;
import io.jsonwebtoken.JwtException;
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

    public UserController(UserDetailsServiceImplementation userDetailsServiceImplementation, JwtService jwtService) {
        this.userDetailsServiceImplementation = userDetailsServiceImplementation;
        this.jwtService = jwtService;
    }

    @GetMapping()
    public ResponseEntity<GetUserDetailsResponse> getUserDetails(@RequestParam String token) {
        try {
            System.out.println("getUserDetails is called");
            System.out.println("token: " + token);
            String username = jwtService.extractUsername(token);
            System.out.println("username: " + username);
            User user = (User) userDetailsServiceImplementation.loadUserByUsername(username);
            GetUserDetailsResponse getUserDetailsResponse = new GetUserDetailsResponse(user, "User fetched successfully");
            return ResponseEntity.ok(getUserDetailsResponse);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GetUserDetailsResponse(null, "User not found"));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new GetUserDetailsResponse(null, "Invalid token"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GetUserDetailsResponse(null, "An unexpected error occurred"));
        }
    }

    @PatchMapping
    public ResponseEntity<PatchUserDetailsResponse> updateUserPartially(@RequestBody PatchUserDetailsRequest patchUserDetailsRequest) {
        try {
            System.out.println("updateUserPartially is called");
            String token = patchUserDetailsRequest.token();
            String username = jwtService.extractUsername(token);
            User updates = patchUserDetailsRequest.user();
            System.out.println("updates: " + updates);
            User updatedUser = (User) userDetailsServiceImplementation.updateUser(updates, username);
            if (Objects.equals(updatedUser.getId(), updates.getId())) {
                PatchUserDetailsResponse response = new PatchUserDetailsResponse(updatedUser, "User fetched successfully");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new PatchUserDetailsResponse(null, "Wrong user id"));
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new PatchUserDetailsResponse(null, "User not found"));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new PatchUserDetailsResponse(null, "Invalid token"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new PatchUserDetailsResponse(null, "An unexpected error occurred"));
        }
    }
}