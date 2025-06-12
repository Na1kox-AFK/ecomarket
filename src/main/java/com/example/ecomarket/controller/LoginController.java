package com.example.ecomarket.controller;

<<<<<<< HEAD
=======
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.PatchExchange;
import com.example.ecomarket.services.loginServices;
>>>>>>> f7c2eb267cf7da2067d8a8e03f486cd69656599d
import com.example.ecomarket.model.LoginModel;
import com.example.ecomarket.services.loginServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/logins")
public class LoginController {

    private final loginServices loginServices;


    @Autowired // Inyección de dependencia del LoginService
    public LoginController(loginServices loginServices) {
        this.loginServices = loginServices;
    }

    @GetMapping
    public ResponseEntity<List<LoginModel>> getAllLogins() {
        List<LoginModel> logins = loginServices.obtenerTodosLosLogins();
        return new ResponseEntity<>(logins, HttpStatus.OK);
    }


    @GetMapping("/{rut}")
    public ResponseEntity<LoginModel> getLoginByRut(@PathVariable String rut) {
        Optional<LoginModel> login = loginServices.buscarLoginPorRut(rut);
        return login.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @PostMapping
    public ResponseEntity<LoginModel> createLogin(@RequestBody LoginModel login) {
        LoginModel savedLogin = loginServices.guardarLogin(login);
        return new ResponseEntity<>(savedLogin, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<LoginModel> updateLogin(@RequestBody LoginModel login) {
        LoginModel updatedLogin = loginServices.actualizarLogin(login);
        if (updatedLogin != null) {
            return new ResponseEntity<>(updatedLogin, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{rut}")
    public ResponseEntity<Void> deleteLogin(@PathVariable String rut) {
        boolean deleted = loginServices.eliminarLogin(rut);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}