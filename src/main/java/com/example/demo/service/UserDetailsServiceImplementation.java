package com.example.demo.service;

import com.example.demo.controller.UserController;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImplementation implements UserDetailsService {

    private final UserRepository repository;

    Logger logger = LoggerFactory.getLogger(UserDetailsServiceImplementation.class.getName());

    public UserDetailsServiceImplementation(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }

    public UserDetails updateUser(User updates, String username) {
        logger.trace("updateUser is called");
        logger.trace("username: {}", username);
        logger.trace("updates: {}", updates.toString());
        Optional<User> user = repository.findByUsername(username);
        if(user.isPresent())
        {
            user.get().setUsername(updates.getUsername());
            user.get().setFirstName(updates.getFirstName());
            user.get().setLastName(updates.getLastName());
            user.get().setPhone(updates.getPhone());
            return repository.save(user.get());
        }
        throw new UsernameNotFoundException("User not found");
    }
}