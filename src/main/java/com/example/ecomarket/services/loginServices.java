package com.example.ecomarket.services;

import com.example.ecomarket.repository.LoginRepository;
import com.example.ecomarket.model.LoginModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service

public class loginServices {
    @Autowired
    private LoginRepository loginRepository;

    /*listar libros */
    public List<LoginModel> getLogins(){
        return loginRepository.obtenerLogin();
    }

    /*Guardar Login */
    public LoginModel saveLogin(LoginModel loginModel){
        return loginRepository.guardar(loginModel);
    }

    /*Retornar Login */
    public LoginModel updateLogin(LoginModel loginModel){
        return loginRepository.actualizar(loginModel);
    }

    /*retornar Login */
    public LoginModel getrutModel(String rut){
        return loginRepository.buscarPorRut(rut);
    }

    /*Eliminar Login */
    public String deleteLogin(String rut){
        loginRepository.Eliminar(rut);
        return "Login eliminado";
    }

    public int totalLoginsV1(){
        return loginRepository.obtenerLogin().size();
    }

}
