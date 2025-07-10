
package com.example.ecomarket.services;

import com.example.ecomarket.model.LoginModel;
import com.example.ecomarket.repository.LoginRepository;
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

class LoginServicesTest {

    @Mock
    private LoginRepository loginRepository;

    @InjectMocks
    private loginServices loginServices; 

    private LoginModel user1;
    private LoginModel user2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user1 = new LoginModel("11111111-1", "Juan", "Perez", 911111111, "Calle 123", 12345, "juan@example.com");
        user2 = new LoginModel("22222222-2", "Maria", "Gonzalez", 922222222, "Av. Siempre Viva 456", 67890, "maria@example.com");
    }

    @Test
    void testObtenerTodosLosLogins() {
        when(loginRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<LoginModel> users = loginServices.obtenerTodosLosLogins();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("Juan", users.get(0).getNombre());
        verify(loginRepository, times(1)).findAll();
    }

    @Test
    void testBuscarLoginPorRutExistente() {
        when(loginRepository.findByRut("11111111-1")).thenReturn(Optional.of(user1));

        Optional<LoginModel> foundUser = loginServices.buscarLoginPorRut("11111111-1");

        assertTrue(foundUser.isPresent());
        assertEquals("Juan", foundUser.get().getNombre());
        verify(loginRepository, times(1)).findByRut("11111111-1");
    }

    @Test
    void testBuscarLoginPorRutNoExistente() {
        when(loginRepository.findByRut("99999999-9")).thenReturn(Optional.empty());

        Optional<LoginModel> foundUser = loginServices.buscarLoginPorRut("99999999-9");

        assertFalse(foundUser.isPresent());
        verify(loginRepository, times(1)).findByRut("99999999-9");
    }

    @Test
    void testGuardarLogin() {
        when(loginRepository.save(user1)).thenReturn(user1);

        LoginModel savedUser = loginServices.guardarLogin(user1);

        assertNotNull(savedUser);
        assertEquals("Juan", savedUser.getNombre());
        verify(loginRepository, times(1)).save(user1);
    }

    @Test
    void testActualizarLoginExistente() {
        LoginModel userActualizado = new LoginModel("11111111-1", "Juanito", "Perez R.", 911111111, "Calle Falsa 123", 12345, "juanito@example.com");

        when(loginRepository.findByRut("11111111-1")).thenReturn(Optional.of(user1));
        when(loginRepository.save(any(LoginModel.class))).thenReturn(userActualizado);

        LoginModel result = loginServices.actualizarLogin(userActualizado);

        assertNotNull(result);
        assertEquals("Juanito", result.getNombre());
        assertEquals("Perez R.", result.getApellido());
        verify(loginRepository, times(1)).findByRut("11111111-1");
        verify(loginRepository, times(1)).save(any(LoginModel.class));
    }

    @Test
    void testActualizarLoginNoExistente() {
        LoginModel userNoExistente = new LoginModel("99999999-9", "Pepe", "Botella", 900000000, "Calle sin nombre", 0, "pepe@example.com");

        when(loginRepository.findByRut("99999999-9")).thenReturn(Optional.empty());

        LoginModel result = loginServices.actualizarLogin(userNoExistente);

        assertNull(result);
        verify(loginRepository, times(1)).findByRut("99999999-9");
        verify(loginRepository, never()).save(any(LoginModel.class));
    }

    @Test
    void testEliminarLoginExistente() {
        when(loginRepository.existsById("11111111-1")).thenReturn(true);
        doNothing().when(loginRepository).deleteById("11111111-1");

        boolean eliminado = loginServices.eliminarLogin("11111111-1");

        assertTrue(eliminado);
        verify(loginRepository, times(1)).existsById("11111111-1");
        verify(loginRepository, times(1)).deleteById("11111111-1");
    }

    @Test
    void testEliminarLoginNoExistente() {
        when(loginRepository.existsById("99999999-9")).thenReturn(false);

        boolean eliminado = loginServices.eliminarLogin("99999999-9");

        assertFalse(eliminado);
        verify(loginRepository, times(1)).existsById("99999999-9");
        verify(loginRepository, never()).deleteById(anyString());
    }
}