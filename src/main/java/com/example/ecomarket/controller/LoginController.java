
package com.example.ecomarket.controller;

import com.example.ecomarket.model.LoginModel;
import com.example.ecomarket.services.loginServices;
import com.example.ecomarket.dto.LoginResponseDto; 

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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; 


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/logins")
@Tag(name = "Gestión de Usuarios y Autenticación", description = "Operaciones CRUD y de autenticación para los usuarios del Ecomarket.")
public class LoginController {

    private final loginServices loginServices;

    public LoginController(loginServices loginServices) {
        this.loginServices = loginServices;
    }

    @Operation(summary = "Obtener todos los usuarios registrados", description = "Retorna una lista completa de todos los usuarios (logins) en el sistema, con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponseDto.class))) 
    })
    @GetMapping
    public ResponseEntity<CollectionModel<LoginResponseDto>> getAllLogins() {
        List<LoginModel> logins = loginServices.obtenerTodosLosLogins();


        List<LoginResponseDto> loginDtos = logins.stream()
                .map(login -> {
                    LoginResponseDto dto = new LoginResponseDto(login);
                    // Enlace a sí mismo
                    dto.add(linkTo(methodOn(LoginController.class).getLoginByRut(login.getRut())).withSelfRel());

                    return dto;
                })
                .collect(Collectors.toList());


        Link selfLink = linkTo(methodOn(LoginController.class).getAllLogins()).withSelfRel();


        CollectionModel<LoginResponseDto> collectionModel = CollectionModel.of(loginDtos, selfLink);

        return new ResponseEntity<>(collectionModel, HttpStatus.OK);
    }

    @Operation(summary = "Buscar un usuario por su RUT", description = "Busca y retorna los detalles de un usuario específico utilizando su RUT como identificador, con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponseDto.class))), 
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado con el RUT proporcionado")
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
            dto.add(linkTo(methodOn(LoginController.class).updateLogin(null)).withRel("update-login"));
            dto.add(linkTo(methodOn(LoginController.class).deleteLogin(login.getRut())).withRel("delete-login")); 



            return new ResponseEntity<>(dto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

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
            dto.add(linkTo(methodOn(LoginController.class).updateLogin(null)).withRel("update-login"));
            dto.add(linkTo(methodOn(LoginController.class).deleteLogin(savedLogin.getRut())).withRel("delete-login"));

            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Actualizar un usuario existente", description = "Actualiza los datos de un usuario existente. Si se incluye una nueva contraseña, esta será codificada. Retorna el usuario actualizado con enlaces HATEOAS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponseDto.class))), 
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado para actualizar")
    })
    @PutMapping
    public ResponseEntity<LoginResponseDto> updateLogin(@RequestBody LoginModel login) {
        LoginModel updatedLogin = loginServices.actualizarLogin(login);
        if (updatedLogin != null) {
            LoginResponseDto dto = new LoginResponseDto(updatedLogin);

            dto.add(linkTo(methodOn(LoginController.class).getLoginByRut(updatedLogin.getRut())).withSelfRel());
            dto.add(linkTo(methodOn(LoginController.class).getAllLogins()).withRel("all-logins"));
            dto.add(linkTo(methodOn(LoginController.class).deleteLogin(updatedLogin.getRut())).withRel("delete-login"));

            return new ResponseEntity<>(dto, HttpStatus.OK);
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