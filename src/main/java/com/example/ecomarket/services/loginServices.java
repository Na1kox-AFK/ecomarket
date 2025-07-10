package com.example.ecomarket.services;

import com.example.ecomarket.model.LoginModel;
import com.example.ecomarket.repository.LoginRepository;

import org.springframework.security.crypto.password.PasswordEncoder; // ¡NUEVA IMPORTACIÓN!
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class loginServices {

    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder; // ¡NUEVO CAMPO!

    // Constructor con la inyección de PasswordEncoder
    public loginServices(LoginRepository loginRepository, PasswordEncoder passwordEncoder) {
        this.loginRepository = loginRepository;
        this.passwordEncoder = passwordEncoder; // ¡ASIGNAR!
    }

    public List<LoginModel> obtenerTodosLosLogins() {
        return loginRepository.findAll();
    }

    public Optional<LoginModel> buscarLoginPorRut(String rut) {
        return loginRepository.findByRut(rut);
    }

    // Método para guardar un NUEVO usuario (con codificación de contraseña)
    public LoginModel registrarNuevoUsuario(LoginModel login) {
        if (loginRepository.findByRut(login.getRut()).isPresent()) {
            throw new IllegalArgumentException("El RUT ya está registrado.");
        }
        // ¡Codificar la contraseña antes de guardar!
        login.setContrasena(passwordEncoder.encode(login.getContrasena()));
        // Asegurarse de que el rol por defecto esté seteado si no viene del front
        if (login.getRole() == null || login.getRole().isEmpty()) {
            login.setRole("USER"); // Rol por defecto
        }
        return loginRepository.save(login);
    }

    // Tu método guardarLogin original, que ahora podría usarse para un propósito diferente,
    // o eliminarse si registrarNuevoUsuario es el método principal para crear.
    // Si mantienes este, debes asegurarte de que la contraseña esté codificada antes de que llegue aquí,
    // o codificarla aquí también.
    public LoginModel guardarLogin(LoginModel login) {
        // En un contexto de Spring Security, deberías codificar la contraseña aquí también
        // si este método es para crear usuarios.
        // login.setContrasena(passwordEncoder.encode(login.getContrasena()));
        return loginRepository.save(login);
    }


    public LoginModel actualizarLogin(LoginModel login) {
        Optional<LoginModel> existingLoginOptional = loginRepository.findByRut(login.getRut());

        if (existingLoginOptional.isPresent()) {
            LoginModel existingLogin = existingLoginOptional.get();

            // Actualiza los campos existentes
            existingLogin.setNombre(login.getNombre());
            existingLogin.setApellido(login.getApellido());
            existingLogin.setCelurlar(login.getCelurlar());
            existingLogin.setCodigoPostal(login.getCodigoPostal());
            existingLogin.setCorreoElectronico(login.getCorreoElectronico());
            existingLogin.setDireccion(login.getDireccion());

            // ¡NUEVO: Lógica para actualizar la contraseña si se proporciona una nueva!
            if (login.getContrasena() != null && !login.getContrasena().isEmpty()) {
                existingLogin.setContrasena(passwordEncoder.encode(login.getContrasena()));
            }

            // ¡NUEVO: Actualizar el rol!
            if (login.getRole() != null && !login.getRole().isEmpty()) {
                 existingLogin.setRole(login.getRole());
            }

            return loginRepository.save(existingLogin);
        } else {
            return null;
        }
    }

    public boolean eliminarLogin(String rut) {
        if (loginRepository.existsById(rut)) {
            loginRepository.deleteById(rut);
            return true;
        }
        return false;
    }
}