package com.example.ecomarket.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin; 
import jakarta.validation.constraints.Min;       
import jakarta.validation.constraints.NotBlank;   


@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Entity 
@Table(name = "Inventario")
public class InventarioModel {
    @Id
    @NotBlank(message = "El ID del inventario no puede estar vacío.")
    private String idInventario;

    @NotBlank(message = "El nombre del producto no puede estar vacío.") 
    private String nombreProducto;

    @Min(value = 0, message = "La cantidad disponible no puede ser negativa.") 
    private int cantidadDisponible;

    @DecimalMin(value = "0.0", inclusive = false, message = "El precio unitario debe ser mayor que cero.")
    private double precioUnitario;

    @NotBlank(message = "La descripción no puede estar vacía.")
    private String descripcion;
}
