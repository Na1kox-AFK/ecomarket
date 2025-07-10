package com.example.ecomarket.services;

import com.example.ecomarket.model.PedidosModel;
import com.example.ecomarket.model.InventarioModel;
import com.example.ecomarket.model.LoginModel;
import com.example.ecomarket.repository.PedidosRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PedidosServices {

    private final PedidosRepository pedidosRepository;
    private final InventarioServices inventarioServices;
    private final loginServices loginServices;

    public PedidosServices(PedidosRepository pedidosRepository,
                         InventarioServices inventarioServices,
                         loginServices loginServices) {
        this.pedidosRepository = pedidosRepository;
        this.inventarioServices = inventarioServices;
        this.loginServices = loginServices;
    }

    @Transactional
    public PedidosModel crearPedido(PedidosModel pedido) {
        Optional<LoginModel> clienteOptional = loginServices.buscarLoginPorRut(pedido.getRutCliente());
        if (clienteOptional.isEmpty()) {
            throw new IllegalArgumentException("Error de validación: Cliente con RUT " + pedido.getRutCliente() + " no encontrado.");
        }

        Optional<InventarioModel> productoOptional = inventarioServices.buscarProductoPorId(pedido.getIdProducto());
        if (productoOptional.isEmpty()) {
            throw new IllegalArgumentException("Error de validación: Producto con ID " + pedido.getIdProducto() + " no encontrado.");
        }
        InventarioModel producto = productoOptional.get();

        if (producto.getCantidadDisponible() < pedido.getCantidad()) {
            throw new IllegalArgumentException("Error de validación: Stock insuficiente para el producto '" + producto.getNombreProducto() + "'. Cantidad disponible: " + producto.getCantidadDisponible());
        }

        producto.setCantidadDisponible(producto.getCantidadDisponible() - pedido.getCantidad());
        inventarioServices.guardarProducto(producto);

        pedido.setPrecioUnitarioProducto(producto.getPrecioUnitario());
        pedido.setPrecioTotal(producto.getPrecioUnitario() * pedido.getCantidad());
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado("PENDIENTE");

        return pedidosRepository.save(pedido);
    }

    public Optional<PedidosModel> buscarPedidoPorId(String idPedido) {
        return pedidosRepository.findById(idPedido);
    }

    public List<PedidosModel> obtenerTodosLosPedidos() {
        return pedidosRepository.findAll();
    }

    public PedidosModel actualizarPedido(PedidosModel pedido) {
        Optional<PedidosModel> existingPedidoOptional = pedidosRepository.findById(pedido.getIdPedido());
        if (existingPedidoOptional.isPresent()) {
            PedidosModel existingPedido = existingPedidoOptional.get();
            existingPedido.setCantidad(pedido.getCantidad());
            existingPedido.setEstado(pedido.getEstado());
            return pedidosRepository.save(existingPedido);
        } else {
            return null;
        }
    }

    public boolean eliminarPedido(String idPedido) {
        if (pedidosRepository.existsById(idPedido)) {
            pedidosRepository.deleteById(idPedido);
            return true;
        }
        return false;
    }
}