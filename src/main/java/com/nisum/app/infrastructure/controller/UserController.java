package com.nisum.app.infrastructure.controller;

import com.nisum.app.application.service.IUserService;
import com.nisum.app.infrastructure.dto.login.LoginRequestDto;
import com.nisum.app.infrastructure.dto.user.UserRegistrationRequestDto;
import com.nisum.app.infrastructure.dto.user.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "User registration and authentication API")
public class UserController {

    private final IUserService userService;

    @PostMapping(value = "/users",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided information")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRegistrationRequestDto requestDto) {
        log.info("Received user registration request for email: {}", requestDto.getEmail());
        UserResponseDto response = userService.registerUser(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token")
    public ResponseEntity<UserResponseDto> loginUser(@Valid @RequestBody LoginRequestDto requestDto) {
        log.info("Received login request for email: {}", requestDto.getEmail());
        UserResponseDto response = userService.loginUser(requestDto);
        return ResponseEntity.ok(response);
    }
}
