package com.example.ecomarket.controller;

import com.example.ecomarket.model.LoginModel;
import com.example.ecomarket.services.loginServices;

import io.swagger.v3.oas.annotations.Operation; 
import io.swagger.v3.oas.annotations.Parameter; 
import io.swagger.v3.oas.annotations.media.Content; 
import io.swagger.v3.oas.annotations.media.Schema; 
import io.swagger.v3.oas.annotations.responses.ApiResponse; 
import io.swagger.v3.oas.annotations.responses.ApiResponses; 
import io.swagger.v3.oas.annotations.tags.Tag; 

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/logins")
@Tag(name = "Gestión de Usuarios y Autenticación", description = "Operaciones CRUD y de autenticación para los usuarios del Ecomarket.")
public class LoginController {

    private final loginServices loginServices;

    public LoginController(loginServices loginServices) {
        this.loginServices = loginServices;
    }

    @Operation(summary = "Obtener todos los usuarios registrados", description = "Retorna una lista completa de todos los usuarios (logins) en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoginModel.class)))
    })
    @GetMapping
    public ResponseEntity<List<LoginModel>> getAllLogins() {
        List<LoginModel> logins = loginServices.obtenerTodosLosLogins();
        return new ResponseEntity<>(logins, HttpStatus.OK);
    }

    @Operation(summary = "Buscar un usuario por su RUT", description = "Busca y retorna los detalles de un usuario específico utilizando su RUT como identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoginModel.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado con el RUT proporcionado")
    })
    @GetMapping("/{rut}")
    public ResponseEntity<LoginModel> getLoginByRut(
            @Parameter(description = "RUT del usuario a buscar (ej. 12.345.678-9)", required = true, example = "12.345.678-9")
            @PathVariable String rut) {
        Optional<LoginModel> login = loginServices.buscarLoginPorRut(rut);
        return login.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Crear un nuevo usuario", description = "Registra un nuevo usuario en la base de datos. La contraseña debe enviarse en texto plano y será codificada por el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoginModel.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida o RUT ya existente",
                    content = @Content(mediaType = "text/plain", examples = @io.swagger.v3.oas.annotations.media.ExampleObject(value = "El RUT ya está registrado.")))
    })
    @PostMapping
    public ResponseEntity<LoginModel> createLogin(@RequestBody LoginModel login) {
        try {
            LoginModel savedLogin = loginServices.guardarLogin(login);
            return new ResponseEntity<>(savedLogin, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Actualizar un usuario existente", description = "Actualiza los datos de un usuario existente. Si se incluye una nueva contraseña, esta será codificada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoginModel.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado para actualizar")
    })
    @PutMapping
    public ResponseEntity<LoginModel> updateLogin(@RequestBody LoginModel login) {
        LoginModel updatedLogin = loginServices.actualizarLogin(login);
        if (updatedLogin != null) {
            return new ResponseEntity<>(updatedLogin, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario de la base de datos utilizando su RUT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente (No Content)"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado para eliminar")
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