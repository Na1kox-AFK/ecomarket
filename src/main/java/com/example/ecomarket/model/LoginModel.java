package com.example.ecomarket.model;

/*importacion de lombok */
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/*Importaciones para la base de datos*/
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/*ocupando las importaciones de lombok */
@Data
@AllArgsConstructor
@NoArgsConstructor

/*Usando importaciones de Jakarta*/
@Entity
@Table(name = "Logins")

/*Creacion de atributos para el Login, y no se generan las constructores tanto con parametros como sin parametros gracias a lombok*/
public class LoginModel {
    @Id
    private String rut;

    private String nombreP;
    private String nombreM;
    private String apellidoP;
    private String apellidoM;
    private int celurlar;
    private String direccion;
    private int codigoPostal;
    private String correoElectronico;
    
}
