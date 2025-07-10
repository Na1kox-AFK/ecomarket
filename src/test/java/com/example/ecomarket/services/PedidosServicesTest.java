package com.example.ecomarket.services;

import com.example.ecomarket.model.InventarioModel;
import com.example.ecomarket.model.LoginModel;
import com.example.ecomarket.model.PedidosModel;
import com.example.ecomarket.repository.PedidosRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedidosServicesTest {

    @Mock
    private PedidosRepository pedidosRepository;

    @Mock
    private InventarioServices inventarioServices;

    @Mock
    private loginServices loginServices;

    @InjectMocks
    private PedidosServices pedidosServices;

    private PedidosModel pedidoRequest;
    private LoginModel clienteExistente;
    private InventarioModel productoDisponible;
    private InventarioModel productoSinStock;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        clienteExistente = new LoginModel("11111111-1", "Juan", "Perez", 911111111, "Calle 123", 12345, "juan@example.com");
        productoDisponible = new InventarioModel("P001", "Manzanas", 10, 1.50, "Fruta fresca");
        productoSinStock = new InventarioModel("P002", "Leche", 0, 2.00, "Leche entera");

        pedidoRequest = new PedidosModel();
        pedidoRequest.setRutCliente("11111111-1");
        pedidoRequest.setIdProducto("P001");
        pedidoRequest.setCantidad(2);
    }

    @Test
    void testCrearPedidoExitoso() {
        when(loginServices.buscarLoginPorRut(pedidoRequest.getRutCliente())).thenReturn(Optional.of(clienteExistente));
        when(inventarioServices.buscarProductoPorId(pedidoRequest.getIdProducto())).thenReturn(Optional.of(productoDisponible));
        when(inventarioServices.guardarProducto(any(InventarioModel.class))).thenReturn(productoDisponible);
        when(pedidosRepository.save(any(PedidosModel.class))).thenAnswer(invocation -> {
            PedidosModel savedPedido = invocation.getArgument(0);
            savedPedido.setIdPedido("PEDIDO-ABC-123");
            return savedPedido;
        });

        PedidosModel result = pedidosServices.crearPedido(pedidoRequest);

        assertNotNull(result);
        assertEquals("PEDIDO-ABC-123", result.getIdPedido());
        assertEquals("PENDIENTE", result.getEstado());
        assertEquals(2, result.getCantidad());
        assertEquals(3.00, result.getPrecioTotal());
        assertNotNull(result.getFechaPedido());

        verify(loginServices, times(1)).buscarLoginPorRut("11111111-1");
        verify(inventarioServices, times(1)).buscarProductoPorId("P001");
        verify(inventarioServices, times(1)).guardarProducto(any(InventarioModel.class));
        verify(pedidosRepository, times(1)).save(any(PedidosModel.class));

        assertEquals(8, productoDisponible.getCantidadDisponible());
    }

    @Test
    void testCrearPedidoClienteNoEncontrado() {
        when(loginServices.buscarLoginPorRut(pedidoRequest.getRutCliente())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            pedidosServices.crearPedido(pedidoRequest);
        });

        assertTrue(exception.getMessage().contains("Cliente con RUT " + pedidoRequest.getRutCliente() + " no encontrado."));
        verify(inventarioServices, never()).buscarProductoPorId(anyString());
        verify(pedidosRepository, never()).save(any(PedidosModel.class));
    }

    @Test
    void testCrearPedidoProductoNoEncontrado() {
        when(loginServices.buscarLoginPorRut(pedidoRequest.getRutCliente())).thenReturn(Optional.of(clienteExistente));
        when(inventarioServices.buscarProductoPorId(pedidoRequest.getIdProducto())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            pedidosServices.crearPedido(pedidoRequest);
        });

        assertTrue(exception.getMessage().contains("Producto con ID " + pedidoRequest.getIdProducto() + " no encontrado."));
        verify(loginServices, times(1)).buscarLoginPorRut(anyString());
        verify(inventarioServices, times(1)).buscarProductoPorId(anyString());
        verify(inventarioServices, never()).guardarProducto(any(InventarioModel.class));
        verify(pedidosRepository, never()).save(any(PedidosModel.class));
    }

    @Test
    void testCrearPedidoStockInsuficiente() {
        pedidoRequest.setCantidad(15);

        when(loginServices.buscarLoginPorRut(pedidoRequest.getRutCliente())).thenReturn(Optional.of(clienteExistente));
        when(inventarioServices.buscarProductoPorId(pedidoRequest.getIdProducto())).thenReturn(Optional.of(productoDisponible));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            pedidosServices.crearPedido(pedidoRequest);
        });

        assertTrue(exception.getMessage().contains("Stock insuficiente para el producto 'Manzanas'. Cantidad disponible: 10"));
        verify(inventarioServices, never()).guardarProducto(any(InventarioModel.class));
        verify(pedidosRepository, never()).save(any(PedidosModel.class));
    }

    @Test
    void testObtenerTodosLosPedidos() {
        PedidosModel p1 = new PedidosModel("ID1", "RUT1", "PROD1", 1, 10.0, 10.0, LocalDateTime.now(), "PENDIENTE");
        PedidosModel p2 = new PedidosModel("ID2", "RUT2", "PROD2", 2, 5.0, 10.0, LocalDateTime.now(), "COMPLETADO");
        when(pedidosRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<PedidosModel> pedidos = pedidosServices.obtenerTodosLosPedidos();

        assertNotNull(pedidos);
        assertEquals(2, pedidos.size());
        verify(pedidosRepository, times(1)).findAll();
    }

    @Test
    void testBuscarPedidoPorIdExistente() {
        PedidosModel p1 = new PedidosModel("ID1", "RUT1", "PROD1", 1, 10.0, 10.0, LocalDateTime.now(), "PENDIENTE");
        when(pedidosRepository.findById("ID1")).thenReturn(Optional.of(p1));

        Optional<PedidosModel> foundPedido = pedidosServices.buscarPedidoPorId("ID1");

        assertTrue(foundPedido.isPresent());
        assertEquals("RUT1", foundPedido.get().getRutCliente());
        verify(pedidosRepository, times(1)).findById("ID1");
    }

    @Test
    void testBuscarPedidoPorIdNoExistente() {
        when(pedidosRepository.findById("ID999")).thenReturn(Optional.empty());

        Optional<PedidosModel> foundPedido = pedidosServices.buscarPedidoPorId("ID999");

        assertFalse(foundPedido.isPresent());
        verify(pedidosRepository, times(1)).findById("ID999");
    }

    @Test
    void testActualizarPedidoExistente() {
        PedidosModel existingPedido = new PedidosModel("PEDIDO-ABC-123", "11111111-1", "P001", 2, 1.50, 3.00, LocalDateTime.now(), "PENDIENTE");
        PedidosModel updatedPedidoData = new PedidosModel("PEDIDO-ABC-123", "11111111-1", "P001", 3, 1.50, 4.50, LocalDateTime.now(), "COMPLETADO");

        when(pedidosRepository.findById("PEDIDO-ABC-123")).thenReturn(Optional.of(existingPedido));
        when(pedidosRepository.save(any(PedidosModel.class))).thenReturn(updatedPedidoData);

        PedidosModel result = pedidosServices.actualizarPedido(updatedPedidoData);

        assertNotNull(result);
        assertEquals("COMPLETADO", result.getEstado());
        assertEquals(3, result.getCantidad());
        verify(pedidosRepository, times(1)).findById("PEDIDO-ABC-123");
        verify(pedidosRepository, times(1)).save(any(PedidosModel.class));
    }

    @Test
    void testActualizarPedidoNoExistente() {
        PedidosModel nonExistentPedido = new PedidosModel("NON_EXISTENT_ID", "RUT", "PROD", 1, 10.0, 10.0, LocalDateTime.now(), "PENDIENTE");
        when(pedidosRepository.findById("NON_EXISTENT_ID")).thenReturn(Optional.empty());

        PedidosModel result = pedidosServices.actualizarPedido(nonExistentPedido);

        assertNull(result);
        verify(pedidosRepository, times(1)).findById("NON_EXISTENT_ID");
        verify(pedidosRepository, never()).save(any(PedidosModel.class));
    }

    @Test
    void testEliminarPedidoExistente() {
        when(pedidosRepository.existsById("PEDIDO-ABC-123")).thenReturn(true);
        doNothing().when(pedidosRepository).deleteById("PEDIDO-ABC-123");

        boolean eliminado = pedidosServices.eliminarPedido("PEDIDO-ABC-123");

        assertTrue(eliminado);
        verify(pedidosRepository, times(1)).existsById("PEDIDO-ABC-123");
        verify(pedidosRepository, times(1)).deleteById("PEDIDO-ABC-123");
    }

    @Test
    void testEliminarPedidoNoExistente() {
        when(pedidosRepository.existsById("NON_EXISTENT_ID")).thenReturn(false);

        boolean eliminado = pedidosServices.eliminarPedido("NON_EXISTENT_ID");

        assertFalse(eliminado);
        verify(pedidosRepository, times(1)).existsById("NON_EXISTENT_ID");
        verify(pedidosRepository, never()).deleteById(anyString());
    }
}