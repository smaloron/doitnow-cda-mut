package com.example.doitnow.controller;

import com.example.doitnow.dto.auth.AuthenticationRequest;
import com.example.doitnow.dto.auth.AuthenticationResponse;
import com.example.doitnow.dto.auth.RegisterRequest;
import com.example.doitnow.model.User;
import com.example.doitnow.repository.UserRepository;
import com.example.doitnow.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Dans controller/AuthenticationController.java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @GetMapping("/all-users")
    public List<User> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll()).getBody();
    }
}