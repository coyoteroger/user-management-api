package com.nisum.app.infrastructure.dto.phone;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneDto {

    @NotBlank(message = "numero de telefono es requerido")
    private String number;

    @NotBlank(message = "codio de la ciudad es requerido")
    private String citycode;

    @NotBlank(message = "codigo de pais es requerido")
    private String contrycode;
}
