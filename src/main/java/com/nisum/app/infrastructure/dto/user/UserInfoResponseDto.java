package com.nisum.app.infrastructure.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoResponseDto extends UserResponseDto{

    private String name;
    private String email;

}
