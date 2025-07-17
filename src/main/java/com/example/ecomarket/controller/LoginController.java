package com.example.ecomarket.controller;

import com.example.ecomarket.model.LoginModel;
import com.example.ecomarket.services.loginServices;
import com.example.ecomarket.dto.LoginResponseDto;
import com.example.ecomarket.security.JwtTokenProvider;
import com.example.ecomarket.dto.JwtResponseDto;
import com.example.ecomarket.dto.LoginRequestDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException; // Importar AuthenticationException

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/logins")
@Tag(name = "Gestión de Usuarios y Autenticación", description = "Operaciones CRUD y de autenticación para los usuarios del Ecomarket.")
public class LoginController {

    private final loginServices loginServices;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginController(loginServices loginServices, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.loginServices = loginServices;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // --- ENDPOINT PARA AUTENTICACIÓN ---
    @Operation(summary = "Autenticar usuario y obtener JWT", description = "Recibe el RUT y la contraseña del usuario para autenticarlo y, si las credenciales son válidas, retorna un token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa, token JWT retornado",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = JwtResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas (Unauthorized)",
                    content = @Content(mediaType = "text/plain", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "Credenciales inválidas.")))
    })
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDto loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            String jwt = jwtTokenProvider.generateToken(authentication);

            return ResponseEntity.ok(new JwtResponseDto(jwt));

        } catch (AuthenticationException e) { // Capturar la excepción específica de autenticación
            return new ResponseEntity<>("Credenciales inválidas.", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            // Manejar otras excepciones inesperadas
            return new ResponseEntity<>("Error interno del servidor: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --- OBTENER TODOS LOS USUARIOS ---
    @Operation(summary = "Obtener todos los usuarios registrados", description = "Retorna una lista completa de todos los usuarios (logins) en el sistema, con enlaces HATEOAS. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (Forbidden - requiere rol ADMIN)")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<LoginResponseDto>> getAllLogins() {
        List<LoginModel> logins = loginServices.obtenerTodosLosLogins();
        
        List<LoginResponseDto> loginDtos = logins.stream()
                .map(login -> {
                    LoginResponseDto dto = new LoginResponseDto(login);
                    // Enlace a sí mismo (GET por RUT)
                    dto.add(linkTo(methodOn(LoginController.class).getLoginByRut(login.getRut())).withSelfRel());
                    // Enlace para actualizar (PUT)
                    dto.add(linkTo(methodOn(LoginController.class).updateLogin(login.getRut(), null)).withRel("update-login"));
                    // Enlace para eliminar (DELETE)
                    dto.add(linkTo(methodOn(LoginController.class).deleteLogin(login.getRut())).withRel("delete-login"));
                    return dto;
                })
                .collect(Collectors.toList());
        
        // Enlace a la colección completa
        Link selfLink = linkTo(methodOn(LoginController.class).getAllLogins()).withSelfRel();
        CollectionModel<LoginResponseDto> collectionModel = CollectionModel.of(loginDtos, selfLink);
        return new ResponseEntity<>(collectionModel, HttpStatus.OK);
    }

    // --- BUSCAR USUARIO POR RUT ---
    @Operation(summary = "Buscar un usuario por su RUT", description = "Busca y retorna los detalles de un usuario específico utilizando su RUT como identificador, con enlaces HATEOAS. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado con el RUT proporcionado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (Forbidden - requiere rol ADMIN)")
    })
    @GetMapping("/{rut}")
    public ResponseEntity<LoginResponseDto> getLoginByRut(
            @Parameter(description = "RUT del usuario a buscar (ej. 12.345.678-9)", required = true, example = "12.345.678-9")
            @PathVariable String rut) {
        Optional<LoginModel> loginOptional = loginServices.buscarLoginPorRut(rut);
        if (loginOptional.isPresent()) {
            LoginModel login = loginOptional.get();
            LoginResponseDto dto = new LoginResponseDto(login);
            // Añadir enlaces HATEOAS
            dto.add(linkTo(methodOn(LoginController.class).getLoginByRut(login.getRut())).withSelfRel());
            dto.add(linkTo(methodOn(LoginController.class).getAllLogins()).withRel("all-logins"));
            dto.add(linkTo(methodOn(LoginController.class).updateLogin(login.getRut(), null)).withRel("update-login")); // Se usa null para el @RequestBody
            dto.add(linkTo(methodOn(LoginController.class).deleteLogin(login.getRut())).withRel("delete-login"));
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // --- CREAR UN NUEVO USUARIO (REGISTRO) ---
    @Operation(summary = "Crear un nuevo usuario", description = "Registra un nuevo usuario en la base de datos. La contraseña debe enviarse en texto plano y será codificada por el sistema. Retorna el usuario creado con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida o RUT ya existente",
                    content = @Content(mediaType = "text/plain", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "El RUT ya está registrado.")))
    })
    @PostMapping
    public ResponseEntity<LoginResponseDto> createLogin(@RequestBody LoginModel login) {
        try {
            LoginModel savedLogin = loginServices.guardarLogin(login);
            LoginResponseDto dto = new LoginResponseDto(savedLogin);
            dto.add(linkTo(methodOn(LoginController.class).getLoginByRut(savedLogin.getRut())).withSelfRel());
            dto.add(linkTo(methodOn(LoginController.class).getAllLogins()).withRel("all-logins"));
            dto.add(linkTo(methodOn(LoginController.class).updateLogin(savedLogin.getRut(), null)).withRel("update-login"));
            dto.add(linkTo(methodOn(LoginController.class).deleteLogin(savedLogin.getRut())).withRel("delete-login"));
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // --- ACTUALIZAR UN USUARIO EXISTENTE POR RUT ---
    @Operation(summary = "Actualizar un usuario existente por RUT", description = "Actualiza los datos de un usuario existente especificado por su RUT en la URL. La contraseña debe enviarse en texto plano y será codificada. Retorna el usuario actualizado con enlaces HATEOAS. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida (RUT en URL y cuerpo no coinciden o datos incorrectos)",
                    content = @Content(mediaType = "text/plain", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "El RUT en la URL no coincide con el RUT en el cuerpo de la solicitud."))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado para actualizar"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (Forbidden - requiere rol ADMIN)")
    })
    @PutMapping("/{rut}") // <--- Aquí el cambio principal: RUT en la URL
    public ResponseEntity<LoginResponseDto> updateLogin(
            @Parameter(description = "RUT del usuario a actualizar (debe coincidir con el RUT en el cuerpo de la solicitud)", required = true, example = "12.345.678-9")
            @PathVariable String rut,
            @RequestBody LoginModel login) {

        // Validar que el RUT en la URL coincida con el RUT en el cuerpo de la solicitud
        if (!rut.equals(login.getRut())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        LoginModel updatedLogin = loginServices.actualizarLogin(login);
        if (updatedLogin != null) {
            LoginResponseDto dto = new LoginResponseDto(updatedLogin);
            dto.add(linkTo(methodOn(LoginController.class).getLoginByRut(updatedLogin.getRut())).withSelfRel());
            dto.add(linkTo(methodOn(LoginController.class).getAllLogins()).withRel("all-logins"));
            dto.add(linkTo(methodOn(LoginController.class).updateLogin(updatedLogin.getRut(), null)).withRel("update-login")); // Se usa null para el @RequestBody
            dto.add(linkTo(methodOn(LoginController.class).deleteLogin(updatedLogin.getRut())).withRel("delete-login"));
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // --- ELIMINAR UN USUARIO ---
    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario de la base de datos utilizando su RUT. Requiere rol ADMIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente (No Content)"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado para eliminar"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado (Forbidden - requiere rol ADMIN)")
    })
    @DeleteMapping("/{rut}")
    public ResponseEntity<Void> deleteLogin(
            @Parameter(description = "RUT del usuario a eliminar", required = true, example = "12.345.678-9")
            @PathVariable String rut) {
        boolean deleted = loginServices.eliminarLogin(rut);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}