package com.nisum.app.domain.specification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailValidationSpecificationTest {

    private EmailValidationSpecification emailValidationSpec;
    private final String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";

    @BeforeEach
    void setUp() {
        emailValidationSpec = new EmailValidationSpecification(emailRegex);
    }

    @Test
    void isSatisfiedBy_WithValidEmail_ShouldReturnTrue() {
        // Arrange
        String validEmail = "test@example.com";

        // Act
        boolean result = emailValidationSpec.isSatisfiedBy(validEmail);

        // Assert
        assertTrue(result);
    }

    @Test
    void isSatisfiedBy_WithValidEmailWithPlus_ShouldReturnTrue() {
        // Arrange
        String validEmail = "test+tag@example.com";

        // Act
        boolean result = emailValidationSpec.isSatisfiedBy(validEmail);

        // Assert
        assertTrue(result);
    }

    @Test
    void isSatisfiedBy_WithValidEmailWithDots_ShouldReturnTrue() {
        // Arrange
        String validEmail = "test.user@example.co.uk";

        // Act
        boolean result = emailValidationSpec.isSatisfiedBy(validEmail);

        // Assert
        assertTrue(result);
    }

    @Test
    void isSatisfiedBy_WithInvalidEmailNoAt_ShouldReturnFalse() {
        // Arrange
        String invalidEmail = "testexample.com";

        // Act
        boolean result = emailValidationSpec.isSatisfiedBy(invalidEmail);

        // Assert
        assertFalse(result);
    }

    @Test
    void isSatisfiedBy_WithInvalidEmailNoDomain_ShouldReturnFalse() {
        // Arrange
        String invalidEmail = "test@";

        // Act
        boolean result = emailValidationSpec.isSatisfiedBy(invalidEmail);

        // Assert
        assertFalse(result);
    }

    @Test
    void isSatisfiedBy_WithNullEmail_ShouldReturnFalse() {
        // Act
        boolean result = emailValidationSpec.isSatisfiedBy(null);

        // Assert
        assertFalse(result);
    }

    @Test
    void isSatisfiedBy_WithEmptyEmail_ShouldReturnFalse() {
        // Act
        boolean result = emailValidationSpec.isSatisfiedBy("");

        // Assert
        assertFalse(result);
    }

    @Test
    void getErrorMessage_ShouldReturnExpectedMessage() {
        // Act
        String errorMessage = emailValidationSpec.getErrorMessage();

        // Assert
        assertEquals("formato invalido de email", errorMessage);
    }
}
