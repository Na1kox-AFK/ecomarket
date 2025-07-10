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
            if (loginRepository.findByRut("11111111-1").isEmpty()) {
                System.out.println("Creando usuario de prueba...");

                // Crea el modelo de usuario de login
                LoginModel testUser = new LoginModel(
                    "11111111-1",            // rut
                    "Juan",                  // nombre
                    "Perez",                 // apellido
                    987654321,               // celular
                    "Calle Falsa 123",       // direccion
                    12345,                   // codigoPostal
                    "juan.perez@example.com", // correoElectronico
                    passwordEncoder.encode("password123"), // <--- IMPORTANTE: Codificar la contraseña
                    "ADMIN"                  // role (puedes cambiarlo a "USER" si lo prefieres)
                );

                // Guarda el usuario en la base de datos
                loginRepository.save(testUser);
                System.out.println("Usuario 'Juan Perez' con RUT '11111111-1' y rol 'ADMIN' creado exitosamente.");
                System.out.println("Contraseña (texto plano) para login: password123");
            } else {
                System.out.println("Usuario de prueba '11111111-1' ya existe.");
            }
        };
    }
}