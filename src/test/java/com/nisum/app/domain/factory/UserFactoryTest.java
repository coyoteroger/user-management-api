package com.nisum.app.domain.factory;

import com.nisum.app.domain.model.User;
import com.nisum.app.infrastructure.dto.phone.PhoneDto;
import com.nisum.app.infrastructure.dto.user.UserRegistrationRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserFactoryTest {

    private UserFactory userFactory;
    private UserRegistrationRequestDto registrationRequestDto;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        userFactory = new UserFactory();

        PhoneDto phoneDto = new PhoneDto("1234567", "1", "57");
        registrationRequestDto = new UserRegistrationRequestDto(
                "Juan Rodriguez",
                "juan@rodriguez.org",
                "Password123",
                List.of(phoneDto)
        );
    }

    @Test
    void createUser_WithValidData_ShouldCreateUserWithEncodedPassword() {
        // Arrange
        String token = "mock-jwt-token";

        // Act
        User result = userFactory.createUser(registrationRequestDto, token);

        // Assert
        assertNotNull(result);
        assertEquals("Juan Rodriguez", result.getName());
        assertEquals("juan@rodriguez.org", result.getEmail());
        assertNotEquals("Password123", result.getPassword()); // Password should be encoded
        assertTrue(passwordEncoder.matches("Password123", result.getPassword()));
        assertEquals(token, result.getToken());
        assertTrue(result.getIsActive());
    }

    @Test
    void createUser_WithValidData_ShouldCreateUserWithPhones() {
        // Arrange
        String token = "mock-jwt-token";

        // Act
        User result = userFactory.createUser(registrationRequestDto, token);

        // Assert
        assertNotNull(result.getPhones());
        assertEquals(1, result.getPhones().size());
        assertEquals("1234567", result.getPhones().get(0).getNumber());
        assertEquals("1", result.getPhones().get(0).getCitycode());
        assertEquals("57", result.getPhones().get(0).getContrycode());
        assertEquals(result, result.getPhones().get(0).getUser());
    }

    @Test
    void createUser_WithMultiplePhones_ShouldCreateUserWithAllPhones() {
        // Arrange
        String token = "mock-jwt-token";
        PhoneDto phone1 = new PhoneDto("1234567", "1", "57");
        PhoneDto phone2 = new PhoneDto("7654321", "2", "58");

        UserRegistrationRequestDto requestWithMultiplePhones = new UserRegistrationRequestDto(
                "Juan Rodriguez",
                "juan@rodriguez.org",
                "Password123",
                List.of(phone1, phone2)
        );

        // Act
        User result = userFactory.createUser(requestWithMultiplePhones, token);

        // Assert
        assertNotNull(result.getPhones());
        assertEquals(2, result.getPhones().size());

        assertEquals("1234567", result.getPhones().get(0).getNumber());
        assertEquals("1", result.getPhones().get(0).getCitycode());
        assertEquals("57", result.getPhones().get(0).getContrycode());

        assertEquals("7654321", result.getPhones().get(1).getNumber());
        assertEquals("2", result.getPhones().get(1).getCitycode());
        assertEquals("58", result.getPhones().get(1).getContrycode());
    }

    @Test
    void createUser_WithEmailUppercase_ShouldCreateUserWithLowercaseEmail() {
        // Arrange
        String token = "mock-jwt-token";
        PhoneDto phoneDto = new PhoneDto("1234567", "1", "57");
        UserRegistrationRequestDto requestWithUppercaseEmail = new UserRegistrationRequestDto(
                "Juan Rodriguez",
                "JUAN@RODRIGUEZ.ORG",
                "Password123",
                List.of(phoneDto)
        );

        // Act
        User result = userFactory.createUser(requestWithUppercaseEmail, token);

        // Assert
        assertEquals("juan@rodriguez.org", result.getEmail());
    }
}
