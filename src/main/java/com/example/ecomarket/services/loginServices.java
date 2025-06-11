package com.example.ecomarket.services;

import com.example.ecomarket.model.LoginModel;
import com.example.ecomarket.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class loginServices {

    private final LoginRepository loginRepository;

    @Autowired 
    public loginServices(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    public List<LoginModel> obtenerTodosLosLogins() {
        return loginRepository.findAll(); 
    }

    public Optional<LoginModel> buscarLoginPorRut(String rut) {
        return loginRepository.findByRut(rut);
    }

    public LoginModel guardarLogin(LoginModel login) {
        return loginRepository.save(login);
    }

    public LoginModel actualizarLogin(LoginModel login) {
        Optional<LoginModel> existingLoginOptional = loginRepository.findByRut(login.getRut());

        if (existingLoginOptional.isPresent()) {
            LoginModel existingLogin = existingLoginOptional.get();
            existingLogin.setNombreP(login.getNombreP());
            existingLogin.setNombreM(login.getNombreM());
            existingLogin.setApellidoP(login.getApellidoP());
            existingLogin.setApellidoM(login.getApellidoM());
            existingLogin.setCelurlar(login.getCelurlar());
            existingLogin.setCodigoPostal(login.getCodigoPostal());
            existingLogin.setCorreoElectronico(login.getCorreoElectronico());
            existingLogin.setDireccion(login.getDireccion());

            return loginRepository.save(existingLogin); // save() también actualiza si el ID existe
        } else {
            return null;
        }
    }

    public boolean eliminarLogin(String rut) {
        if (loginRepository.existsById(rut)) {
            loginRepository.deleteById(rut);
            return true;
        }
        return false;
    }
}