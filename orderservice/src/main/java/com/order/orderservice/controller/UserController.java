package com.order.orderservice.controller;

import com.order.orderservice.dto.UserCreateRequest;
import com.order.orderservice.dto.UserResponse;
import com.order.orderservice.entity.User;
import com.order.orderservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        log.info("Creating user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("User creation failed: email already exists {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .subRole(request.getSubRole())
                .active(true)
                .build();

        User saved = userRepository.save(user);
        log.info("User created successfully with id {}", saved.getId());
        UserResponse response = UserResponse.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .message("User created successfully")
                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
