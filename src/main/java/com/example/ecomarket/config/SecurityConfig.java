package com.example.ecomarket.config;

import com.example.ecomarket.services.CustomUserDetailsService;
import io.swagger.v3.oas.models.Components; 
import io.swagger.v3.oas.models.ExternalDocumentation; 
import io.swagger.v3.oas.models.OpenAPI; 
import io.swagger.v3.oas.models.info.Info; 
import io.swagger.v3.oas.models.info.License; 
import io.swagger.v3.oas.models.security.SecurityRequirement; 
import io.swagger.v3.oas.models.security.SecurityScheme; 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(authorize -> authorize
       
                .requestMatchers(
                    "/api/logins/**", 
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/h2-console/**",
                    "/login", 
                    "/logout", 
                    "/authenticate"
                ).permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN") 
                .requestMatchers("/api/pedidos/**", "/api/inventario/**", "/api/reportes/**").hasAnyRole("USER", "ADMIN") 
                .anyRequest().authenticated() 
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


    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth"; 

        return new OpenAPI()
                .info(new Info().title("Ecomarket API")
                                 .description("API para la gestión de usuarios, pedidos e inventario del Ecomarket.")
                                 .version("v1.0.0")
                                 .termsOfService("http://swagger.io/terms/") 
                                 .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                                 .description("Documentación Externa de Ecomarket")
                                 .url("https://confluence.example.com/ecomarket-docs"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) 
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP) 
                                        .scheme("bearer") 
                                        .bearerFormat("JWT")
                                        .description("Autenticación JWT - Ingrese el token Bearer (ej. Bearer eyJhbGciOi...)")));
    }

}