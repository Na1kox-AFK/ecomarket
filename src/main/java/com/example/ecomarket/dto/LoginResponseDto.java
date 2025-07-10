
package com.example.ecomarket.dto;

import lombok.Data;
import lombok.EqualsAndHashCode; 
import org.springframework.hateoas.RepresentationModel; 

import com.example.ecomarket.model.LoginModel;

import io.swagger.v3.oas.annotations.media.Schema; 

@EqualsAndHashCode(callSuper = true)
@Data // Lombok para getters/setters
@Schema(description = "Modelo de respuesta para un usuario, incluyendo enlaces HATEOAS.")
public class LoginResponseDto extends RepresentationModel<LoginResponseDto> {

    @Schema(description = "RUT único del usuario, sirve como identificador principal.", example = "12.345.678-9")
    private String rut;

    @Schema(description = "Nombre de pila del usuario.", example = "Juan")
    private String nombre;

    @Schema(description = "Apellido del usuario.", example = "Pérez")
    private String apellido;

    @Schema(description = "Número de teléfono celular del usuario.", example = "912345678")
    private int celurlar;

    @Schema(description = "Dirección de residencia del usuario.", example = "Av. Siempre Viva 742")
    private String direccion;

    @Schema(description = "Código postal de la dirección del usuario.", example = "8320000")
    private int codigoPostal;

    @Schema(description = "Correo electrónico del usuario.", example = "juan.perez@ecomarket.com")
    private String correoElectronico;

    @Schema(description = "Rol del usuario en el sistema.", example = "USER")
    private String role;

    public LoginResponseDto(LoginModel loginModel) {
        this.rut = loginModel.getRut();
        this.nombre = loginModel.getNombre();
        this.apellido = loginModel.getApellido();
        this.celurlar = loginModel.getCelurlar();
        this.direccion = loginModel.getDireccion();
        this.codigoPostal = loginModel.getCodigoPostal();
        this.correoElectronico = loginModel.getCorreoElectronico();
        this.role = loginModel.getRole();
    }
}