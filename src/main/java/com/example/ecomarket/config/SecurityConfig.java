package com.example.ecomarket.config; 

import com.example.ecomarket.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/login/**", "/api/usuarios/crear", "/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**").permitAll() // Rutas públicas (login, registro, Swagger, H2 Console)
                .requestMatchers("/api/admin/**").hasRole("ADMIN")  /*Solo ADMIN puede acceder a /api/admin*/
                .requestMatchers("/api/pedidos/**", "/api/inventario/**").hasAnyRole("USER", "ADMIN") // USER y ADMIN pueden acceder a pedidos e inventario
                .anyRequest().authenticated() // Cualquier otra petición requiere autenticación
            )
            .formLogin(form -> form 
                .loginPage("/login") 
                .permitAll() 
                .defaultSuccessUrl("/home", true) 
                .failureUrl("/login?error=true") 
            )
            .logout(logout -> logout
                .logoutUrl("/logout") 
                .logoutSuccessUrl("/login?logout=true") 
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); 
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder()); 
        return authProvider;
    }
}