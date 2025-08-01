package com.nisum.app.application.service;

import com.nisum.app.domain.factory.UserFactory;
import com.nisum.app.domain.model.User;
import com.nisum.app.domain.repository.UserRepository;
import com.nisum.app.domain.specification.EmailValidationSpecification;
import com.nisum.app.domain.specification.PasswordValidationSpecification;
import com.nisum.app.infrastructure.dto.login.LoginRequestDto;
import com.nisum.app.infrastructure.dto.user.UserRegistrationRequestDto;
import com.nisum.app.infrastructure.dto.user.UserResponseDto;
import com.nisum.app.infrastructure.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final JwtService jwtService;
    private final EmailValidationSpecification emailValidationSpec;
    private final PasswordValidationSpecification passwordValidationSpec;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public UserResponseDto registerUser(UserRegistrationRequestDto requestDto) {
        log.info("Attempting to register user with email: {}", requestDto.getEmail());

        validateUserRegistration(requestDto);

        String token = jwtService.generateToken(requestDto.getEmail());
        User user = userFactory.createUser(requestDto, token);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        return buildUserResponse(savedUser);
    }

    @Transactional
    public UserResponseDto loginUser(LoginRequestDto requestDto) {
        log.info("Attempting login for email: {}", requestDto.getEmail());

        if (!emailValidationSpec.isSatisfiedBy(requestDto.getEmail())) {
            throw new BusinessException("Credenciales inválidas");
        }

        User user = userRepository.findByEmail(requestDto.getEmail().toLowerCase())
                .orElseThrow(() -> new BusinessException("Credenciales inválidas"));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new BusinessException("Credenciales inválidas");
        }

        String newToken = jwtService.generateToken(user.getEmail());
        user.updateToken(newToken);
        user.updateLastLogin();

        User updatedUser = userRepository.save(user);
        log.info("User logged in successfully: {}", updatedUser.getEmail());

        return buildUserResponse(updatedUser);
    }

    private void validateUserRegistration(UserRegistrationRequestDto requestDto) {
        if (!emailValidationSpec.isSatisfiedBy(requestDto.getEmail())) {
            throw new BusinessException(emailValidationSpec.getErrorMessage());
        }

        if (!passwordValidationSpec.isSatisfiedBy(requestDto.getPassword())) {
            throw new BusinessException(passwordValidationSpec.getErrorMessage());
        }

        if (userRepository.existsByEmail(requestDto.getEmail().toLowerCase())) {
            throw new BusinessException("este correo ya existe");
        }
    }

    private UserResponseDto buildUserResponse(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .created(user.getCreated())
                .modified(user.getModified())
                .lastLogin(user.getLastLogin())
                .token(user.getToken())
                .isActive(user.getIsActive())
                .build();
    }
}

