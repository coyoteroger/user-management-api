package com.nisum.app.infrastructure.dto.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "Email es requerido")
    @Email(message = "formato de Email invalido")
    private String email;

    @NotBlank(message = "Password es requerido")
    private String password;
}