// src/main/java/com/example/ecomarket/config/DataInitializer.java (o similar)

package com.example.ecomarket.config; // Ajusta el paquete según tu estructura

import com.example.ecomarket.model.LoginModel;
import com.example.ecomarket.repository.LoginRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;

    // Inyecta el repositorio y el codificador de contraseñas
    public DataInitializer(LoginRepository loginRepository, PasswordEncoder passwordEncoder) {
        this.loginRepository = loginRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Este Bean se ejecutará automáticamente al iniciar la aplicación
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Verifica si el usuario de prueba ya existe para no crearlo múltiples veces
            if (loginRepository.findByRut("21951945-1").isEmpty()) {
                System.out.println("Creando usuario de prueba...");

                // Crea el modelo de usuario de login
                LoginModel testUser = new LoginModel(
                    "21951945-1",            // rut
                    "Matias",                  // nombre
                    "Diaz",                 // apellido
                    973831037,               // celular
                    "los naranjos 123",       // direccion
                    12345,                   // codigoPostal
                    "juan.perez@example.com", // correoElectronico
                    passwordEncoder.encode("Elturron_16"), // <--- IMPORTANTE: Codificar la contraseña
                    "ADMIN"                  
                );

                loginRepository.save(testUser);
                System.out.println("Usuario 'Matias' con RUT '21951945-1' y rol 'ADMIN' creado exitosamente.");
                System.out.println("Contraseña (texto plano) para login: Elturron_16");
            } else {
                System.out.println("Usuario de prueba '21951945-1' ya existe.");
            }
        };
    }
}