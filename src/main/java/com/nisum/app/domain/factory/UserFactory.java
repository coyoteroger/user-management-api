package com.nisum.app.domain.factory;


import com.nisum.app.domain.model.Phone;
import com.nisum.app.domain.model.User;
import com.nisum.app.infrastructure.dto.phone.PhoneDto;
import com.nisum.app.infrastructure.dto.user.UserRegistrationRequestDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserFactory {

    private final BCryptPasswordEncoder passwordEncoder;

    public UserFactory() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public User createUser(UserRegistrationRequestDto requestDto, String token) {
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User user = User.builder()
                .name(requestDto.getName())
                .email(requestDto.getEmail().toLowerCase())
                .password(encodedPassword)
                .token(token)
                .isActive(true)
                .build();

        List<Phone> phones = createPhones(requestDto.getPhones(), user);
        user.setPhones(phones);

        return user;
    }

    private List<Phone> createPhones(List<PhoneDto> phoneDtos, User user) {
        return phoneDtos.stream()
                .map(phoneDto -> Phone.builder()
                        .number(phoneDto.getNumber())
                        .citycode(phoneDto.getCitycode())
                        .contrycode(phoneDto.getContrycode())
                        .user(user)
                        .build())
                .collect(Collectors.toList());
    }
}

