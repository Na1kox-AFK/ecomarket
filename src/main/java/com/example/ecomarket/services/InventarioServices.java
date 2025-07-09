package com.example.ecomarket.services;

import org.springframework.stereotype.Service;
import com.example.ecomarket.model.InventarioModel;
import com.example.ecomarket.repository.InventarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class InventarioServices {

    private final InventarioRepository inventarioRepository;

    public InventarioServices(InventarioRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }

    public List<InventarioModel> obtenerTodosLosProductos() {
        return inventarioRepository.findAll();
    }

    public Optional<InventarioModel> buscarProductoPorId(String idInventario) {
        return inventarioRepository.findById(idInventario);
    }

    public InventarioModel guardarProducto(InventarioModel producto) {
        return inventarioRepository.save(producto);
    }

    public InventarioModel actualizarProducto(InventarioModel producto) {
        Optional<InventarioModel> existingProductOptional = inventarioRepository.findById(producto.getIdInventario());

        if (existingProductOptional.isPresent()) {
            InventarioModel existingProduct = existingProductOptional.get();

            existingProduct.setNombreProducto(producto.getNombreProducto());
            existingProduct.setCantidadDisponible(producto.getCantidadDisponible());
            existingProduct.setPrecioUnitario(producto.getPrecioUnitario());
            existingProduct.setDescripcion(producto.getDescripcion());

            return inventarioRepository.save(existingProduct);
        } else {
            return null;
        }
    }

    public boolean eliminarProducto(String idInventario) {
        if (inventarioRepository.existsById(idInventario)) {
            inventarioRepository.deleteById(idInventario);
            return true;
        }
        return false;
    }
}