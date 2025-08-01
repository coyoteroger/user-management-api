package com.nisum.app.application.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceImplTest {

    private JwtServiceImpl jwtService;
    private final String testSecret = "myTestSecretKey123456789myTestSecretKey123456789";
    private final long testExpiration = 86400000; // 24 hours
    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(testSecret, testExpiration);
    }

    @Test
    void generateToken_WithValidEmail_ShouldReturnToken() {
        // Act
        String token = jwtService.generateToken(testEmail);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    void extractEmail_WithValidToken_ShouldReturnEmail() {
        // Arrange
        String token = jwtService.generateToken(testEmail);

        // Act
        String extractedEmail = jwtService.extractEmail(token);

        // Assert
        assertEquals(testEmail, extractedEmail);
    }

    @Test
    void extractExpiration_WithValidToken_ShouldReturnFutureDate() {
        // Arrange
        String token = jwtService.generateToken(testEmail);

        // Act
        Date expiration = jwtService.extractExpiration(token);

        // Assert
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void isTokenExpired_WithValidToken_ShouldReturnFalse() {
        // Arrange
        String token = jwtService.generateToken(testEmail);

        // Act
        Boolean isExpired = jwtService.isTokenExpired(token);

        // Assert
        assertFalse(isExpired);
    }

    @Test
    void validateToken_WithValidTokenAndMatchingEmail_ShouldReturnTrue() {
        // Arrange
        String token = jwtService.generateToken(testEmail);

        // Act
        Boolean isValid = jwtService.validateToken(token, testEmail);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_WithValidTokenButDifferentEmail_ShouldReturnFalse() {
        // Arrange
        String token = jwtService.generateToken(testEmail);
        String differentEmail = "different@example.com";

        // Act
        Boolean isValid = jwtService.validateToken(token, differentEmail);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void generateToken_WithDifferentEmails_ShouldGenerateDifferentTokens() {
        // Arrange
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";

        // Act
        String token1 = jwtService.generateToken(email1);
        String token2 = jwtService.generateToken(email2);

        // Assert
        assertNotEquals(token1, token2);
    }
}
