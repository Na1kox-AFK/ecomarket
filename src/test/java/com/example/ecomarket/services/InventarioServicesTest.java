
package com.example.ecomarket.services;

import com.example.ecomarket.model.InventarioModel;
import com.example.ecomarket.repository.InventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventarioServicesTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks 
    private InventarioServices inventarioServices;

    private InventarioModel producto1;
    private InventarioModel producto2;

    @BeforeEach 
    void setUp() {
        MockitoAnnotations.openMocks(this); 
        producto1 = new InventarioModel("P001", "Manzanas", 100, 1.50, "Fruta fresca");
        producto2 = new InventarioModel("P002", "Leche", 50, 2.00, "Leche entera");
    }

    @Test
    void testObtenerTodosLosProductos() {
    
        when(inventarioRepository.findAll()).thenReturn(Arrays.asList(producto1, producto2));

        List<InventarioModel> productos = inventarioServices.obtenerTodosLosProductos();

        assertNotNull(productos);
        assertEquals(2, productos.size());
        assertEquals("Manzanas", productos.get(0).getNombreProducto());
        verify(inventarioRepository, times(1)).findAll(); 
    }

    @Test
    void testBuscarProductoPorIdExistente() {
       
        when(inventarioRepository.findById("P001")).thenReturn(Optional.of(producto1));

        Optional<InventarioModel> foundProduct = inventarioServices.buscarProductoPorId("P001");

        assertTrue(foundProduct.isPresent());
        assertEquals("Manzanas", foundProduct.get().getNombreProducto());
        verify(inventarioRepository, times(1)).findById("P001");
    }

    @Test
    void testBuscarProductoPorIdNoExistente() {

        when(inventarioRepository.findById("P999")).thenReturn(Optional.empty());

        Optional<InventarioModel> foundProduct = inventarioServices.buscarProductoPorId("P999");

        assertFalse(foundProduct.isPresent());
        verify(inventarioRepository, times(1)).findById("P999");
    }

    @Test
    void testGuardarProducto() {
   
        when(inventarioRepository.save(producto1)).thenReturn(producto1);

        InventarioModel savedProduct = inventarioServices.guardarProducto(producto1);

        assertNotNull(savedProduct);
        assertEquals("Manzanas", savedProduct.getNombreProducto());
        verify(inventarioRepository, times(1)).save(producto1);
    }

    @Test
    void testActualizarProductoExistente() {
        InventarioModel productoActualizado = new InventarioModel("P001", "Manzanas Rojas", 90, 1.60, "Fruta fresca de temporada");

    
        when(inventarioRepository.findById("P001")).thenReturn(Optional.of(producto1));
   
        when(inventarioRepository.save(any(InventarioModel.class))).thenReturn(productoActualizado);

        InventarioModel result = inventarioServices.actualizarProducto(productoActualizado);

        assertNotNull(result);
        assertEquals("Manzanas Rojas", result.getNombreProducto());
        assertEquals(90, result.getCantidadDisponible());
        assertEquals(1.60, result.getPrecioUnitario());
        verify(inventarioRepository, times(1)).findById("P001");
        verify(inventarioRepository, times(1)).save(any(InventarioModel.class));
    }

    @Test
    void testActualizarProductoNoExistente() {
        InventarioModel productoNoExistente = new InventarioModel("P999", "Banana", 20, 0.80, "Fruta");

        when(inventarioRepository.findById("P999")).thenReturn(Optional.empty());

        InventarioModel result = inventarioServices.actualizarProducto(productoNoExistente);

        assertNull(result);
        verify(inventarioRepository, times(1)).findById("P999");
        verify(inventarioRepository, never()).save(any(InventarioModel.class));
    }

    @Test
    void testEliminarProductoExistente() {
        when(inventarioRepository.existsById("P001")).thenReturn(true);
 
        doNothing().when(inventarioRepository).deleteById("P001");

        boolean eliminado = inventarioServices.eliminarProducto("P001");

        assertTrue(eliminado);
        verify(inventarioRepository, times(1)).existsById("P001");
        verify(inventarioRepository, times(1)).deleteById("P001");
    }

    @Test
    void testEliminarProductoNoExistente() {
        when(inventarioRepository.existsById("P999")).thenReturn(false);

        boolean eliminado = inventarioServices.eliminarProducto("P999");

        assertFalse(eliminado);
        verify(inventarioRepository, times(1)).existsById("P999");
        verify(inventarioRepository, never()).deleteById(anyString());
    }
}