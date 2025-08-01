package com.nisum.app.infrastructure.controller;

import java.security.Principal;

import com.nisum.app.application.service.IUserService;
import com.nisum.app.infrastructure.dto.user.UserInfoResponseDto;
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
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProfileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IUserService userService;

    @InjectMocks
    private ProfileController profileController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(profileController).build();
    }

    @Test
    void testGetCurrentUserProfile() throws Exception {
        String email = "test@example.com";
        // Mock Principal for controller method
        Principal principal = mock(Principal.class);
        when(principal.getName()).thenReturn(email);

        UserInfoResponseDto responseDto = new UserInfoResponseDto();
        responseDto.setEmail(email);
        responseDto.setIsActive(true);
        responseDto.setName("test");
        responseDto.setId(UUID.randomUUID());
        responseDto.setLastLogin(LocalDateTime.now());
        responseDto.setCreated(LocalDateTime.now());
        responseDto.setToken("token123");


        when(userService.getUserProfile(email)).thenReturn(responseDto);

        mockMvc.perform(get("/api/profile/me")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(responseDto.getId().toString())))
                .andExpect(jsonPath("$.token", is(responseDto.getToken())))
                .andExpect(jsonPath("$.isactive", is(responseDto.getIsActive())));
    }
}
