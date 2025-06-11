package com.example.ecomarket.repository;

import com.example.ecomarket.model.LoginModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*importacion de Java*/
import java.util.Optional; 

@Repository
/*Hacemos una extencion para que obtenga todos los metodos del CRUD */
public interface LoginRepository extends JpaRepository<LoginModel, String> {

    /*Con esta funcion podemos hacer que la importacion Optional busque detalladamente el atributo rut*/
    Optional<LoginModel> findByRut(String rut);

}