package com.nisum.app.application.service.impl;

import com.nisum.app.application.service.IJwtService;
import com.nisum.app.domain.factory.UserFactory;
import com.nisum.app.domain.model.User;
import com.nisum.app.domain.repository.UserRepository;
import com.nisum.app.domain.specification.EmailValidationSpecification;
import com.nisum.app.domain.specification.PasswordValidationSpecification;
import com.nisum.app.infrastructure.dto.login.LoginRequestDto;
import com.nisum.app.infrastructure.dto.phone.PhoneDto;
import com.nisum.app.infrastructure.dto.user.UserRegistrationRequestDto;
import com.nisum.app.infrastructure.dto.user.UserResponseDto;
import com.nisum.app.infrastructure.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFactory userFactory;

    @Mock
    private IJwtService jwtService;

    @Mock
    private EmailValidationSpecification emailValidationSpec;

    @Mock
    private PasswordValidationSpecification passwordValidationSpec;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRegistrationRequestDto validRegistrationRequest;
    private LoginRequestDto validLoginRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        PhoneDto phoneDto = new PhoneDto("1234567", "1", "57");
        validRegistrationRequest = new UserRegistrationRequestDto(
                "Juan Rodriguez",
                "juan@rodriguez.org",
                "Password123",
                List.of(phoneDto)
        );

        validLoginRequest = new LoginRequestDto("juan@rodriguez.org", "Password123");

        mockUser = User.builder()
                .id(UUID.randomUUID())
                .name("Juan Rodriguez")
                .email("juan@rodriguez.org")
                .password("encodedPassword")
                .token("mock-jwt-token")
                .isActive(true)
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .build();
    }

    @Test
    void registerUser_WithValidData_ShouldReturnUserResponse() {
        // Arrange
        when(emailValidationSpec.isSatisfiedBy(anyString())).thenReturn(true);
        when(passwordValidationSpec.isSatisfiedBy(anyString())).thenReturn(true);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(jwtService.generateToken(anyString())).thenReturn("mock-jwt-token");
        when(userFactory.createUser(any(UserRegistrationRequestDto.class), anyString())).thenReturn(mockUser);
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        UserResponseDto result = userService.registerUser(validRegistrationRequest);

        // Assert
        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
        assertEquals(mockUser.getToken(), result.getToken());
        assertEquals(mockUser.getIsActive(), result.getIsActive());

        verify(userRepository).existsByEmail("juan@rodriguez.org");
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken("juan@rodriguez.org");
    }

    @Test
    void registerUser_WithExistingEmail_ShouldThrowBusinessException() {
        // Arrange
        when(emailValidationSpec.isSatisfiedBy(anyString())).thenReturn(true);
        when(passwordValidationSpec.isSatisfiedBy(anyString())).thenReturn(true);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.registerUser(validRegistrationRequest));

        assertEquals("este correo ya existe", exception.getMessage());
        verify(userRepository).existsByEmail("juan@rodriguez.org");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WithInvalidEmail_ShouldThrowBusinessException() {
        // Arrange
        when(emailValidationSpec.isSatisfiedBy(anyString())).thenReturn(false);
        when(emailValidationSpec.getErrorMessage()).thenReturn("Email format is invalid");

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.registerUser(validRegistrationRequest));

        assertEquals("Email format is invalid", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void registerUser_WithInvalidPassword_ShouldThrowBusinessException() {
        // Arrange
        when(emailValidationSpec.isSatisfiedBy(anyString())).thenReturn(true);
        when(passwordValidationSpec.isSatisfiedBy(anyString())).thenReturn(false);
        when(passwordValidationSpec.getErrorMessage()).thenReturn("Password format is invalid");

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.registerUser(validRegistrationRequest));

        assertEquals("Password format is invalid", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginUser_WithValidCredentials_ShouldReturnUserResponse() {
        // Arrange
        when(emailValidationSpec.isSatisfiedBy(anyString())).thenReturn(true);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken(anyString())).thenReturn("new-jwt-token");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Act
        UserResponseDto result = userService.loginUser(validLoginRequest);

        // Assert
        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());

        verify(userRepository).findByEmail("juan@rodriguez.org");
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken("juan@rodriguez.org");
        verify(passwordEncoder).matches("Password123", "encodedPassword");
    }

    @Test
    void loginUser_WithInvalidEmail_ShouldThrowBusinessException() {
        // Arrange
        when(emailValidationSpec.isSatisfiedBy(anyString())).thenReturn(false);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.loginUser(validLoginRequest));

        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void loginUser_WithNonExistentUser_ShouldThrowBusinessException() {
        // Arrange
        when(emailValidationSpec.isSatisfiedBy(anyString())).thenReturn(true);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.loginUser(validLoginRequest));

        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(userRepository).findByEmail("juan@rodriguez.org");
    }

    @Test
    void loginUser_WithInvalidPassword_ShouldThrowBusinessException() {
        // Arrange
        when(emailValidationSpec.isSatisfiedBy(anyString())).thenReturn(true);
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.loginUser(validLoginRequest));

        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(userRepository).findByEmail("juan@rodriguez.org");
        verify(userRepository, never()).save(any(User.class));
    }
}
