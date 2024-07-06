package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.response.GetUserDetailsResponse;
import com.example.demo.service.JwtService;
import com.example.demo.service.UserDetailsServiceImplementation;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserDetailsServiceImplementation userDetailsServiceImplementation;
    private final JwtService jwtService;

    public UserController(UserDetailsServiceImplementation userDetailsServiceImplementation, JwtService jwtService) {
        this.userDetailsServiceImplementation = userDetailsServiceImplementation;
        this.jwtService = jwtService;
    }

    @GetMapping("/user")
    public ResponseEntity<GetUserDetailsResponse> getUserDetails(@RequestParam String token) {
        try {
            String username = jwtService.extractUsername(token);
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
}