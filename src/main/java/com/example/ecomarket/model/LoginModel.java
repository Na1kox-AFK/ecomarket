package com.example.ecomarket.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import io.swagger.v3.oas.annotations.media.Schema; // NUEVA IMPORTACIÓN

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Logins")
@Schema(description = "Modelo de datos para un usuario (login) en el sistema Ecomarket.") // Anotación a nivel de clase
public class LoginModel implements UserDetails {

    @Id
    @Schema(description = "RUT único del usuario, sirve como identificador principal para el login.", example = "12.345.678-9")
    private String rut;

    @Schema(description = "Nombre de pila del usuario.", example = "Juan")
    private String nombre;

    @Schema(description = "Apellido del usuario.", example = "Pérez")
    private String apellido;

    @Schema(description = "Número de teléfono celular del usuario.", example = "912345678", minimum = "100000000", maximum = "999999999") 
    private int celurlar;

    @Schema(description = "Dirección de residencia del usuario.", example = "Av. Siempre Viva 742, Springfield")
    private String direccion;

    @Schema(description = "Código postal de la dirección del usuario.", example = "8320000")
    private int codigoPostal;

    @Schema(description = "Correo electrónico del usuario, debe ser único.", example = "juan.perez@ecomarket.com")
    private String correoElectronico;

    @Schema(description = "Contraseña del usuario. Se espera en texto plano al registrar/actualizar y se guarda codificada.", example = "passwordSegura123")
    private String contrasena;

    @Schema(description = "Rol del usuario en el sistema. Determina los permisos de acceso. Valores permitidos: USER, ADMIN.", example = "USER", allowableValues = {"USER", "ADMIN"})
    private String role = "USER";

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }

    @Override
    public String getPassword() {
        return this.contrasena;
    }

    @Override
    public String getUsername() {
        return this.rut;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}