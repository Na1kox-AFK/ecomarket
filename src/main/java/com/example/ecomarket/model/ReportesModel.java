package com.example.ecomarket.model;

/*Importaciondes de lombok */
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

/*Creacion de los atributos de nuestra clase*/
public class ReportesModel {
    private int Id_promocion;
    private String Nombre_Promocion;
    private String Fecha_emicion;
    private String Fecha_Caducacion;
}
