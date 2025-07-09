package com.example.ecomarket.controller;

import com.example.ecomarket.model.InventarioModel;
import com.example.ecomarket.services.InventarioServices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioServices inventarioServices;


    public InventarioController(InventarioServices inventarioServices) {
        this.inventarioServices = inventarioServices;
    }

    @GetMapping
    public ResponseEntity<List<InventarioModel>> obtenerTodosLosProductos() {
        List<InventarioModel> productos = inventarioServices.obtenerTodosLosProductos();
        return new ResponseEntity<>(productos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventarioModel> buscarProductoPorId(@PathVariable("id") String id) {
        Optional<InventarioModel> producto = inventarioServices.buscarProductoPorId(id);
        return producto.map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                       .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<InventarioModel> crearProducto(@RequestBody InventarioModel producto) {
        InventarioModel nuevoProducto = inventarioServices.guardarProducto(producto);
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<InventarioModel> actualizarProducto(@RequestBody InventarioModel producto) {
        InventarioModel productoActualizado = inventarioServices.actualizarProducto(producto);
        if (productoActualizado != null) {
            return new ResponseEntity<>(productoActualizado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable("id") String id) {
        boolean eliminado = inventarioServices.eliminarProducto(id);
        if (eliminado) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}