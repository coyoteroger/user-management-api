package com.nisum.app.infrastructure.dto.user;

import com.nisum.app.infrastructure.dto.phone.PhoneDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationRequestDto {

    @NotBlank(message = "Nombre es requerido")
    private String name;

    @NotBlank(message = "Email es requerido")
    @Email(message = "formato de emial invalido")
    private String email;

    @NotBlank(message = "password es requerido")
    private String password;

    @NotNull(message = "al menos ingrese un numero de telefono")
    @Valid
    private List<PhoneDto> phones;
}

