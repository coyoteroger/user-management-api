package com.nisum.app.infrastructure.controller;

import com.nisum.app.application.service.IUserService;
import com.nisum.app.infrastructure.dto.user.UserInfoResponseDto;
import com.nisum.app.infrastructure.dto.user.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Profile", description = "Profile management operations")
@SecurityRequirement(name = "bearerAuth")
public class ProfileController {

    private final IUserService userService;

    @GetMapping("/me")
    @Operation(
            summary = "Get current user profile",
            description = "Retrieves the profile information of the currently authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Profile information retrieved successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserInfoResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content
            )
    })
    public ResponseEntity<UserInfoResponseDto> getCurrentUserProfile(Principal principal) {
        String userEmail = principal.getName();
        log.info("Retrieving profile for user: {}", userEmail);

        UserInfoResponseDto userProfile = userService.getUserProfile(userEmail);
        log.info("Profile retrieved successfully for user: {}", userEmail);
        return ResponseEntity.ok(userProfile);
    }
}
