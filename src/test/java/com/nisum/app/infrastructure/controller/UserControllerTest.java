package com.nisum.app.infrastructure.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nisum.app.application.service.IUserService;
import com.nisum.app.infrastructure.dto.login.LoginRequestDto;
import com.nisum.app.infrastructure.dto.user.UserRegistrationRequestDto;
import com.nisum.app.infrastructure.dto.user.UserResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private IUserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testRegisterUser() throws Exception {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto(
                "Test",
                "test@example.com",
                "password123",
                Collections.emptyList()
        );

        UserResponseDto responseDto = UserResponseDto.builder()
                .id(UUID.randomUUID())
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .token("token123")
                .isActive(true)
                .build();

        when(userService.registerUser(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(responseDto.getId().toString())))
                .andExpect(jsonPath("$.token", is(responseDto.getToken())))
                .andExpect(jsonPath("$.isactive", is(responseDto.getIsActive())));
    }

    @Test
    void testLoginUser() throws Exception {
        LoginRequestDto requestDto = new LoginRequestDto("login@example.com", "pass123");

        UserResponseDto responseDto = UserResponseDto.builder()
                .id(UUID.randomUUID())
                .created(LocalDateTime.now())
                .modified(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .token("loginToken")
                .isActive(true)
                .build();

        when(userService.loginUser(requestDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId().toString())))
                .andExpect(jsonPath("$.token", is(responseDto.getToken())))
                .andExpect(jsonPath("$.isactive", is(responseDto.getIsActive())));
    }
}
