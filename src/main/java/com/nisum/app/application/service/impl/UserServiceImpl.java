package com.nisum.app.application.service.impl;

import com.nisum.app.application.service.IJwtService;
import com.nisum.app.application.service.IUserService;
import com.nisum.app.domain.factory.UserFactory;
import com.nisum.app.domain.model.User;
import com.nisum.app.domain.repository.UserRepository;
import com.nisum.app.domain.specification.EmailValidationSpecification;
import com.nisum.app.domain.specification.PasswordValidationSpecification;
import com.nisum.app.infrastructure.dto.login.LoginRequestDto;
import com.nisum.app.infrastructure.dto.user.UserInfoResponseDto;
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
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final IJwtService jwtService;
    private final EmailValidationSpecification emailValidationSpec;
    private final PasswordValidationSpecification passwordValidationSpec;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
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

    @Override
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

        // Reuse existing token if still valid, otherwise generate a new one
        String existingToken = user.getToken();
        String tokenToReturn;
        if (existingToken != null && jwtService.validateToken(existingToken, user.getEmail())) {
            tokenToReturn = existingToken;
            log.debug("Reusing existing valid token for user: {}", user.getEmail());
        } else {
            tokenToReturn = jwtService.generateToken(user.getEmail());
            user.updateToken(tokenToReturn);
            log.debug("Generated new token for user: {}", user.getEmail());
        }
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

    @Override
    public UserInfoResponseDto getUserProfile(String email) {
        log.info("Retrieving profile for user with email: {}", email);

        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        log.info("Profile retrieved successfully for user: {}", email);
        return buildUserInfoResponse(user);
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

    private UserInfoResponseDto buildUserInfoResponse(User user) {
        UserInfoResponseDto dto = new UserInfoResponseDto();
        dto.setId(user.getId());
        dto.setCreated(user.getCreated());
        dto.setModified(user.getModified());
        dto.setLastLogin(user.getLastLogin());
        dto.setToken(user.getToken());
        dto.setIsActive(user.getIsActive());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }


}

