package com.example.ecomarket.model;

// Importaciones necesarias para el modelo de Inventario
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/*importaciones de Jakarta*/
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "Inventario") // Nombre de la tabla en la base de datos


public class InventarioModel {

    @Id
    private String idInventario; 
    private String nombreProducto; 
    private int cantidadDisponible; 
    private double precioUnitario;
    private String descripcion; 


}
