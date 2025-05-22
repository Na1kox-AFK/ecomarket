package com.example.ecomarket.model;

/*importacion de lombok */
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/*ocupando las importaciones de lombok */
@Data
@AllArgsConstructor
@NoArgsConstructor

/*Creacion de atributos para el Login, y no se generan las constructores tanto con parametros como sin parametros gracias a lombok*/
public class Login {
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
