// src/main/java/com/example/ecomarket/controller/AuthController.java (o tu ruta de controladores)
package com.example.ecomarket.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/authenticate") // Este es el endpoint que usarás en Postman para el login
    public String authenticateUser(@RequestParam("username") String username,
                                   @RequestParam("password") String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "Login exitoso para el usuario: " + username;

        } catch (Exception e) {
            System.err.println("Fallo de autenticación para el usuario " + username + ": " + e.getMessage());
            return "Error de autenticación: Credenciales inválidas. " + e.getMessage();
        }
    }
}