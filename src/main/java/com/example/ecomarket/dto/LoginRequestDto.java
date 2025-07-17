package com.example.ecomarket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Solicitud de credenciales para el inicio de sesión.")
public class LoginRequestDto {
    @Schema(description = "RUT o nombre de usuario para el inicio de sesión.", example = "21951945-1", required = true)
    private String username; // Se mapea al rut del LoginModel
    @Schema(description = "Contraseña para el inicio de sesión.", example = "Elturron_16", required = true)
    private String password;
}