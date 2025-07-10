// src/main/java/com/example/ecomarket/exception/ResourceNotFoundException.java

package com.example.ecomarket.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Esta anotación hará que Spring devuelva automáticamente un 404 Not Found
// cuando esta excepción sea lanzada.
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}