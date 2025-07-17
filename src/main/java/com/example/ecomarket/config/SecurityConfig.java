package com.example.ecomarket.config; // Asegúrate de que este sea el paquete correcto

import com.example.ecomarket.security.JwtAuthenticationFilter;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Constructor para inyección de dependencias
    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Configuración del AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    // Este Bean configura qué rutas Spring Security debe IGNORAR completamente.
    // Esto es ideal para recursos públicos como Swagger UI, archivos estáticos o tu endpoint de login.
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(
                // Rutas de Swagger UI y la especificación OpenAPI
                "/swagger-ui/**",          // Interfaz de usuario de Swagger (HTML, CSS, JS)
                "/v3/api-docs/**",         // Archivo JSON/YAML de la especificación OpenAPI
                "/v3/api-doc",             // Una ruta alternativa para la especificación
                "/webjars/**",             // Recursos estáticos de Swagger UI servidos vía WebJars
                "/swagger-ui.html",        // Ruta común para acceder a la página principal de Swagger

                // Otras rutas públicas que quieres que sean ignoradas por Spring Security
                "/api/logins/**",          // Tu endpoint de login (si no quieres que pase por ningún filtro)
                "/h2-console/**",          // Si usas H2 Console para desarrollo
                "/login",                  // Posibles rutas genéricas de login/logout
                "/logout",
                "/authenticate",
                "/favicon.ico",            // El icono de la pestaña del navegador
                "/error"                   // La página de error por defecto de Spring Boot
        );
    }



    // Define la cadena de filtros de seguridad HTTP para el resto de tus APIs.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilita la protección CSRF (Cross-Site Request Forgery).
            // Esto es común para APIs RESTful que usan tokens (JWT) en lugar de sesiones basadas en cookies.
            .csrf(AbstractHttpConfigurer::disable)
            // Configura las reglas de autorización para las solicitudes HTTP.
            .authorizeHttpRequests(authorize -> authorize
                // Rutas que requieren un rol específico de ADMIN
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // Rutas que requieren cualquier rol de USER o ADMIN
                .requestMatchers("/api/pedidos/**", "/api/inventario/**", "/api/reportes/**").hasAnyRole("USER", "ADMIN")
                // Cualquier otra solicitud NO ignorada por webSecurityCustomizer() REQUIERE AUTENTICACIÓN
                .anyRequest().authenticated()
            )
            // Configura la política de gestión de sesiones como SIN ESTADO (STATELESS).
            // Esto es esencial para aplicaciones que usan JWT, ya que cada solicitud contiene su propia autenticación.
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Deshabilita la autenticación HTTP Basic si no la estás usando.
            .httpBasic(AbstractHttpConfigurer::disable);

        // Añade el filtro JWT personalizado ANTES del filtro de autenticación de nombre de usuario/contraseña de Spring Security.
        // Esto asegura que el JWT sea procesado primero para autenticar al usuario.
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }



    // Define el codificador de contraseñas (PasswordEncoder) que se usará para cifrar y verificar contraseñas.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configura el proveedor de autenticación.
    // Usa CustomUserDetailsService para cargar los detalles del usuario y el PasswordEncoder para verificar la contraseña.
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }



    // Define la configuración de OpenAPI para generar la documentación de la API.
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth"; // Nombre del esquema de seguridad JWT

        return new OpenAPI()
                // Información general de la API
                .info(new Info().title("Ecomarket API")
                        .description("API para la gestión de usuarios, pedidos e inventario del Ecomarket.")
                        .version("v1.0.0")
                        .termsOfService("http://swagger.io/terms/") // Términos de servicio
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))) // Licencia
                // Documentación externa (opcional)
                .externalDocs(new ExternalDocumentation()
                        .description("Documentación Externa de Ecomarket")
                        .url("https://confluence.example.com/ecomarket-docs"))
                // Configuración de seguridad global para la API
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP) // Tipo de esquema HTTP
                                        .scheme("bearer")              // Esquema "bearer" para JWT
                                        .bearerFormat("JWT")           // Formato del token
                                        .description("Autenticación JWT - Ingrese el token Bearer (ej. Bearer eyJhbGciOi...)"))); // Descripción en Swagger UI
    }
}