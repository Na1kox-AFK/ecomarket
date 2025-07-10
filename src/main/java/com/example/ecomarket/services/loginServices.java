package com.example.ecomarket.services;

import com.example.ecomarket.model.LoginModel;
import com.example.ecomarket.repository.LoginRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class loginServices {

    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder; 


    public loginServices(LoginRepository loginRepository, PasswordEncoder passwordEncoder) {
        this.loginRepository = loginRepository;
        this.passwordEncoder = passwordEncoder; 
    }

    public List<LoginModel> obtenerTodosLosLogins() {
        return loginRepository.findAll();
    }

    public Optional<LoginModel> buscarLoginPorRut(String rut) {
        return loginRepository.findByRut(rut);
    }

 
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


    public LoginModel guardarLogin(LoginModel login) {

        return loginRepository.save(login);
    }


    public LoginModel actualizarLogin(LoginModel login) {
        Optional<LoginModel> existingLoginOptional = loginRepository.findByRut(login.getRut());

        if (existingLoginOptional.isPresent()) {
            LoginModel existingLogin = existingLoginOptional.get();


            existingLogin.setNombre(login.getNombre());
            existingLogin.setApellido(login.getApellido());
            existingLogin.setCelurlar(login.getCelurlar());
            existingLogin.setCodigoPostal(login.getCodigoPostal());
            existingLogin.setCorreoElectronico(login.getCorreoElectronico());
            existingLogin.setDireccion(login.getDireccion());


            if (login.getContrasena() != null && !login.getContrasena().isEmpty()) {
                existingLogin.setContrasena(passwordEncoder.encode(login.getContrasena()));
            }


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