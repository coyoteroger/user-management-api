package com.nisum.app.domain.specification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidationSpecificationTest {

    private PasswordValidationSpecification passwordValidationSpec;
    private final String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";

    @BeforeEach
    void setUp() {
        passwordValidationSpec = new PasswordValidationSpecification(passwordRegex);
    }

    @Test
    void isSatisfiedBy_WithValidPassword_ShouldReturnTrue() {
        // Arrange
        String validPassword = "Password123";

        // Act
        boolean result = passwordValidationSpec.isSatisfiedBy(validPassword);

        // Assert
        assertTrue(result);
    }

    @Test
    void isSatisfiedBy_WithValidPasswordLonger_ShouldReturnTrue() {
        // Arrange
        String validPassword = "MySecurePassword123";

        // Act
        boolean result = passwordValidationSpec.isSatisfiedBy(validPassword);

        // Assert
        assertTrue(result);
    }

    @Test
    void isSatisfiedBy_WithPasswordTooShort_ShouldReturnFalse() {
        // Arrange
        String invalidPassword = "Pass1";

        // Act
        boolean result = passwordValidationSpec.isSatisfiedBy(invalidPassword);

        // Assert
        assertFalse(result);
    }

    @Test
    void isSatisfiedBy_WithPasswordNoUppercase_ShouldReturnFalse() {
        // Arrange
        String invalidPassword = "password123";

        // Act
        boolean result = passwordValidationSpec.isSatisfiedBy(invalidPassword);

        // Assert
        assertFalse(result);
    }

    @Test
    void isSatisfiedBy_WithPasswordNoLowercase_ShouldReturnFalse() {
        // Arrange
        String invalidPassword = "PASSWORD123";

        // Act
        boolean result = passwordValidationSpec.isSatisfiedBy(invalidPassword);

        // Assert
        assertFalse(result);
    }

    @Test
    void isSatisfiedBy_WithPasswordNoDigits_ShouldReturnFalse() {
        // Arrange
        String invalidPassword = "Password";

        // Act
        boolean result = passwordValidationSpec.isSatisfiedBy(invalidPassword);

        // Assert
        assertFalse(result);
    }

    @Test
    void isSatisfiedBy_WithNullPassword_ShouldReturnFalse() {
        // Act
        boolean result = passwordValidationSpec.isSatisfiedBy(null);

        // Assert
        assertFalse(result);
    }

    @Test
    void isSatisfiedBy_WithEmptyPassword_ShouldReturnFalse() {
        // Act
        boolean result = passwordValidationSpec.isSatisfiedBy("");

        // Assert
        assertFalse(result);
    }

    @Test
    void getErrorMessage_ShouldReturnExpectedMessage() {
        // Act
        String errorMessage = passwordValidationSpec.getErrorMessage();

        // Assert
        assertEquals("el password debe contener al menos 8 caracteres, incluyendo mayusculas, letras minisculas y numeros", errorMessage);
    }
}
