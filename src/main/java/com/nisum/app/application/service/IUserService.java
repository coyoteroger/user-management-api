package com.nisum.app.application.service;

import com.nisum.app.infrastructure.dto.login.LoginRequestDto;
import com.nisum.app.infrastructure.dto.user.UserInfoResponseDto;
import com.nisum.app.infrastructure.dto.user.UserRegistrationRequestDto;
import com.nisum.app.infrastructure.dto.user.UserResponseDto;

public interface IUserService {

    UserResponseDto registerUser(UserRegistrationRequestDto requestDto);
    UserResponseDto loginUser(LoginRequestDto requestDto);
    UserInfoResponseDto getUserProfile(String email);

}
