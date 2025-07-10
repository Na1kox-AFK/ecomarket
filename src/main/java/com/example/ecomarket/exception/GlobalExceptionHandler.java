// src/main/java/com/example/ecomarket/exception/GlobalExceptionHandler.java

package com.example.ecomarket.exception; // Asegúrate de que el paquete sea correcto

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest; // Necesario para obtener detalles de la solicitud
import org.springframework.dao.DataIntegrityViolationException; // Para errores de base de datos (duplicados, FKs)
import org.springframework.http.converter.HttpMessageNotReadableException; // Para JSON malformado
import org.springframework.web.HttpRequestMethodNotSupportedException; // Para 405 Method Not Allowed
import org.springframework.web.servlet.resource.NoResourceFoundException; // Para 404 Not Found en Spring Boot 3+ (endpoints no existentes)
import org.springframework.security.access.AccessDeniedException; // Para 403 Forbidden (problemas de autorización de Spring Security)

import java.time.LocalDateTime; // Para añadir un timestamp a la respuesta de error
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Indica que esta clase maneja excepciones de controladores globalmente
public class GlobalExceptionHandler {

    // -------------------------------------------------------------------------------------------------------------
    // 1. Manejo de Errores de Validación (@Valid, @NotBlank, etc.) - HTTP 400 Bad Request
    // Cuando el JSON enviado no cumple con las anotaciones de validación en el modelo.
    // -------------------------------------------------------------------------------------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField(); // Nombre del campo que falló
            String errorMessage = error.getDefaultMessage();    // Mensaje de error (definido en la anotación)
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // -------------------------------------------------------------------------------------------------------------
    // 2. Manejo de JSON Malformado o Cuerpos de Solicitud Ilegibles - HTTP 400 Bad Request
    // Ocurre si el JSON enviado tiene errores de sintaxis, o si el tipo de datos no coincide (ej. string en int).
    // -------------------------------------------------------------------------------------------------------------
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.BAD_REQUEST.value());
        errorDetails.put("error", "Bad Request");
        errorDetails.put("message", "Cuerpo de la solicitud JSON inválido o malformado. Por favor, revisa la sintaxis y tipos de datos.");
        errorDetails.put("path", request.getDescription(false).replace("uri=", "")); // Elimina el prefijo "uri="

        // Intenta obtener un mensaje más específico de la causa raíz si está disponible
        if (ex.getRootCause() != null) {
            errorDetails.put("detail", ex.getRootCause().getMessage());
        } else {
            errorDetails.put("detail", ex.getMessage());
        }
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }


    // -------------------------------------------------------------------------------------------------------------
    // 3. Manejo de Recursos No Encontrados (Endpoint o Recurso con ID no existe) - HTTP 404 Not Found
    // A. Para endpoints que no existen en tu aplicación. (Spring Boot 3+ específico)
    //    Nota: Para que este @ExceptionHandler atrape NoResourceFoundException,
    //    a veces necesitas añadir 'spring.mvc.throw-exception-if-no-handler-found=true'
    //    en tu application.properties, aunque en versiones recientes puede no ser necesario.
    // -------------------------------------------------------------------------------------------------------------
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFoundException(NoResourceFoundException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.NOT_FOUND.value());
        errorDetails.put("error", "Not Found");
        errorDetails.put("message", "El recurso o endpoint solicitado no fue encontrado.");
        errorDetails.put("path", ex.getResourcePath()); // La ruta que no se encontró
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    // B. Para tu excepción personalizada cuando un recurso por ID no se encuentra.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.NOT_FOUND.value());
        errorDetails.put("error", "Not Found");
        errorDetails.put("message", ex.getMessage()); // El mensaje que pasaste a la excepción
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    // -------------------------------------------------------------------------------------------------------------
    // 4. Manejo de Método HTTP No Permitido - HTTP 405 Method Not Allowed
    // Ocurre cuando intentas usar un método HTTP incorrecto para una URL existente (ej. GET en un @PostMapping).
    // -------------------------------------------------------------------------------------------------------------
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.METHOD_NOT_ALLOWED.value());
        errorDetails.put("error", "Method Not Allowed");
        errorDetails.put("message", "El método HTTP '" + ex.getMethod() + "' no está permitido para esta URL.");
        errorDetails.put("allowedMethods", ex.getSupportedHttpMethods()); // Métodos que sí están permitidos
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // -------------------------------------------------------------------------------------------------------------
    // 5. Manejo de Errores de Integridad de Datos (BD) - HTTP 409 Conflict
    // Ocurre por violaciones de restricciones de base de datos (ej. clave única duplicada, FK no existe).
    // -------------------------------------------------------------------------------------------------------------
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.CONFLICT.value());
        errorDetails.put("error", "Conflict");
        errorDetails.put("message", "Error de integridad de datos. Posiblemente un valor duplicado o una restricción de clave foránea violada.");
        errorDetails.put("detail", ex.getMostSpecificCause().getMessage()); // Mensaje más específico de la BD
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    // -------------------------------------------------------------------------------------------------------------
    // 6. Manejo de Acceso Denegado (Autorización) - HTTP 403 Forbidden
    // Ocurre cuando el usuario está autenticado, pero no tiene los roles/permisos necesarios.
    // -------------------------------------------------------------------------------------------------------------
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.FORBIDDEN.value());
        errorDetails.put("error", "Forbidden");
        errorDetails.put("message", "Acceso denegado. No tienes permiso para realizar esta acción.");
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    // -------------------------------------------------------------------------------------------------------------
    // 7. Manejo Genérico de Otras Excepciones (Catch-all) - HTTP 500 Internal Server Error
    // Captura cualquier otra excepción no manejada específicamente para evitar respuestas crudas al cliente.
    // -------------------------------------------------------------------------------------------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, WebRequest request) {
        // MUY IMPORTANTE: En producción, no expongas ex.getMessage() directamente si contiene información sensible.
        // Registra el error completo en tus logs (ej. con un logger como SLF4J/Logback).
        ex.printStackTrace(); // Para depuración, imprime en consola. En prod, usa un logger.

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDetails.put("error", "Internal Server Error");
        errorDetails.put("message", "Ocurrió un error inesperado en el servidor. Por favor, inténtalo de nuevo más tarde o contacta al soporte.");
        errorDetails.put("detail", ex.getMessage()); // Solo para depuración o si el mensaje es seguro
        errorDetails.put("path", request.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}