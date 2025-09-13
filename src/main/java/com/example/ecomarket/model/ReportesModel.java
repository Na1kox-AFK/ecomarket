package com.example.ecomarket.model;

/*Importacionesde lombook*/
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/*Importaciones para la base de datos*/
import jakarta.persistence.Entity;  
import jakarta.persistence.Table;
import jakarta.persistence.Id;

/*Usando importaciones de lombok*/
@Data
@AllArgsConstructor
@NoArgsConstructor
/*Usando importaciones de Jakarta*/
@Entity
@Table(name = "Reportes")


public class ReportesModel {

    @Id
    private String idReporte;

    private String nombreUsuario;
    private String rutUsuario;
    private String correoElectronico;
    private String descripcionProblema;
    private String estadoReporte; 

}
