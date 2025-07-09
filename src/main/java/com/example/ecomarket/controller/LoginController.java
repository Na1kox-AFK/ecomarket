package com.example.ecomarket.controller;

import com.example.ecomarket.model.LoginModel;
import com.example.ecomarket.services.loginServices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/logins")
public class LoginController {

    private final loginServices loginServices;



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