package com.example.ecomarket.controller;

import com.example.ecomarket.model.PedidosModel;
import com.example.ecomarket.services.PedidosServices;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pedidos")
public class PedidosController {

    private final PedidosServices pedidosServices;

    public PedidosController(PedidosServices pedidosService) {
        this.pedidosServices = pedidosService;
    }

    @PostMapping
    public ResponseEntity<PedidosModel> crearPedido(@RequestBody PedidosModel pedido) {
        try {
            PedidosModel nuevoPedido = pedidosServices.crearPedido(pedido);
            return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación al crear pedido: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Error interno al crear pedido: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidosModel> getPedidoById(@PathVariable("id") String id) {
        Optional<PedidosModel> pedido = pedidosServices.buscarPedidoPorId(id);
        return pedido.map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<PedidosModel>> getAllPedidos() {
        List<PedidosModel> pedidos = pedidosServices.obtenerTodosLosPedidos();
        return new ResponseEntity<>(pedidos, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<PedidosModel> actualizarPedido(@RequestBody PedidosModel pedido) {
        PedidosModel pedidoActualizado = pedidosServices.actualizarPedido(pedido);
        if (pedidoActualizado != null) {
            return new ResponseEntity<>(pedidoActualizado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPedido(@PathVariable("id") String id) {
        boolean eliminado = pedidosServices.eliminarPedido(id);
        if (eliminado) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}