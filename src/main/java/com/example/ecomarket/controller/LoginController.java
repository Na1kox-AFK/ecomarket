package com.example.ecomarket.controller;

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
import com.example.ecomarket.model.LoginModel;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ecomarket")

public class LoginController {

    @Autowired
    private loginServices loginServices;

    @GetMapping
    public List<LoginModel> listaLogin(){
        return loginServices.getLogins();
    }

    @PostMapping
    public LoginModel agregarLogin(@RequestBody LoginModel login){
        return loginServices.saveLogin(login);
    }

    @GetMapping("{rut}")
    public LoginModel buscarLogin(@PathVariable String rut){
        return loginServices.getrutModel(rut);
    }

    @PutMapping("{rut}")
    public LoginModel actualizarLogin(@PathVariable String rut,@RequestBody LoginModel login){
        return loginServices.updateLogin(login);
    }

    @DeleteMapping("{rut}")
    public String eliminarLogin(@PathVariable String rut){
        return loginServices.deleteLogin(rut);
    }

    @GetMapping("/total")
    public int totalLoginsV1(){
        return loginServices.totalLoginsV1();
    }
}