package com.example.ecomarket.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Pedidos")
public class PedidosModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idPedido;
    private String rutCliente;
    private String idProducto;
    private int cantidad;
    private double precioUnitarioProducto;
    private double precioTotal;
    private LocalDateTime fechaPedido;
    private String estado;
}