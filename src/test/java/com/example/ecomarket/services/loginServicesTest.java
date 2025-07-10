package com.example.ecomarket.services;

import com.example.ecomarket.model.LoginModel;
import com.example.ecomarket.repository.LoginRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class loginServicesTest {

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private loginServices loginServices;

    private LoginModel user1;
    private LoginModel user2;
    private final String RAW_PASSWORD = "password123";
    private final String ENCODED_PASSWORD = "$2a$10$abcdefghijklmnopqrstuvwx.abcdefghijklmnopqrstuvwxyz";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(passwordEncoder.matches(RAW_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        user1 = new LoginModel("11111111-1", "Juan", "Perez", 987654321, "Calle Falsa 123", 1234567, "juan.p@example.com", ENCODED_PASSWORD, "USER");
        user2 = new LoginModel("22222222-2", "Maria", "Lopez", 912345678, "Avenida Siempre Viva 742", 7654321, "maria.l@example.com", ENCODED_PASSWORD, "ADMIN");
    }

    @Test
    void testRegistrarNuevoUsuarioSuccess() {
        LoginModel newUser = new LoginModel("33333333-3", "Carlos", "Gomez", 911223344, "Bosque Encantado 1", 3334445, "carlos.g@example.com", RAW_PASSWORD, "USER");

        when(loginRepository.findByRut(newUser.getRut())).thenReturn(Optional.empty());
        LoginModel savedUser = new LoginModel(newUser.getRut(), newUser.getNombre(), newUser.getApellido(), newUser.getCelurlar(), newUser.getDireccion(), newUser.getCodigoPostal(), newUser.getCorreoElectronico(), ENCODED_PASSWORD, newUser.getRole());
        when(loginRepository.save(any(LoginModel.class))).thenReturn(savedUser);

        LoginModel result = loginServices.registrarNuevoUsuario(newUser);

        assertNotNull(result);
        assertEquals(newUser.getRut(), result.getRut());
        assertEquals(ENCODED_PASSWORD, result.getContrasena());
        assertEquals("USER", result.getRole());

        verify(loginRepository, times(1)).findByRut(newUser.getRut());
        verify(passwordEncoder, times(1)).encode(RAW_PASSWORD);
        verify(loginRepository, times(1)).save(any(LoginModel.class));
    }

    @Test
    void testRegistrarNuevoUsuarioRutExistente() {
        LoginModel existingUser = new LoginModel("11111111-1", "Juan", "Perez", 987654321, "Calle Falsa 123", 1234567, "juan.p@example.com", RAW_PASSWORD, "USER");

        when(loginRepository.findByRut(existingUser.getRut())).thenReturn(Optional.of(user1));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            loginServices.registrarNuevoUsuario(existingUser);
        });

        assertEquals("El RUT ya está registrado.", thrown.getMessage());

        verify(loginRepository, times(1)).findByRut(existingUser.getRut());
        verify(passwordEncoder, never()).encode(anyString());
        verify(loginRepository, never()).save(any(LoginModel.class));
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
    void testActualizarLoginExistenteConNuevaContrasena() {
        LoginModel updatedData = new LoginModel("11111111-1", "Juanito", "Perez", 987654321, "Calle Falsa 123", 1234567, "juan.p@example.com", RAW_PASSWORD, "ADMIN");

        when(loginRepository.findById(updatedData.getRut())).thenReturn(Optional.of(user1));
        when(passwordEncoder.encode(RAW_PASSWORD)).thenReturn(ENCODED_PASSWORD);
        LoginModel savedUpdatedUser = new LoginModel(updatedData.getRut(), updatedData.getNombre(), updatedData.getApellido(), updatedData.getCelurlar(), updatedData.getDireccion(), updatedData.getCodigoPostal(), updatedData.getCorreoElectronico(), ENCODED_PASSWORD, updatedData.getRole());
        when(loginRepository.save(any(LoginModel.class))).thenReturn(savedUpdatedUser);

        LoginModel result = loginServices.actualizarLogin(updatedData);

        assertNotNull(result);
        assertEquals("Juanito", result.getNombre());
        assertEquals("ADMIN", result.getRole());
        assertEquals(ENCODED_PASSWORD, result.getContrasena());

        verify(loginRepository, times(1)).findById(updatedData.getRut());
        verify(passwordEncoder, times(1)).encode(RAW_PASSWORD);
        verify(loginRepository, times(1)).save(any(LoginModel.class));
    }

    @Test
    void testActualizarLoginExistenteSinCambiarContrasena() {
        LoginModel updatedData = new LoginModel("11111111-1", "Juanito", "Perez", 987654321, "Calle Falsa 123", 1234567, "juan.p@example.com", null, "USER");

        when(loginRepository.findById(updatedData.getRut())).thenReturn(Optional.of(user1));
        LoginModel savedUpdatedUser = new LoginModel(updatedData.getRut(), updatedData.getNombre(), updatedData.getApellido(), updatedData.getCelurlar(), updatedData.getDireccion(), updatedData.getCodigoPostal(), updatedData.getCorreoElectronico(), user1.getContrasena(), updatedData.getRole());
        when(loginRepository.save(any(LoginModel.class))).thenReturn(savedUpdatedUser);

        LoginModel result = loginServices.actualizarLogin(updatedData);

        assertNotNull(result);
        assertEquals("Juanito", result.getNombre());
        assertEquals(user1.getContrasena(), result.getContrasena());
        assertEquals("USER", result.getRole());

        verify(loginRepository, times(1)).findById(updatedData.getRut());
        verify(passwordEncoder, never()).encode(anyString());
        verify(loginRepository, times(1)).save(any(LoginModel.class));
    }

    @Test
    void testActualizarLoginNoExistente() {
        LoginModel nonExistentUser = new LoginModel("99999999-9", "Fake", "User", 123456789, "Nowhere", 0, "fake@example.com", RAW_PASSWORD, "USER");

        when(loginRepository.findById(nonExistentUser.getRut())).thenReturn(Optional.empty());

        LoginModel result = loginServices.actualizarLogin(nonExistentUser);

        assertNull(result);
        verify(loginRepository, times(1)).findById(nonExistentUser.getRut());
        verify(passwordEncoder, never()).encode(anyString());
        verify(loginRepository, never()).save(any(LoginModel.class));
    }

    @Test
    void testEliminarLoginExistente() {
        when(loginRepository.existsById("11111111-1")).thenReturn(true);
        doNothing().when(loginRepository).deleteById("11111111-1");

        boolean deleted = loginServices.eliminarLogin("11111111-1");

        assertTrue(deleted);
        verify(loginRepository, times(1)).existsById("11111111-1");
        verify(loginRepository, times(1)).deleteById("11111111-1");
    }

    @Test
    void testEliminarLoginNoExistente() {
        when(loginRepository.existsById("99999999-9")).thenReturn(false);

        boolean deleted = loginServices.eliminarLogin("99999999-9");

        assertFalse(deleted);
        verify(loginRepository, times(1)).existsById("99999999-9");
        verify(loginRepository, never()).deleteById(anyString());
    }
}