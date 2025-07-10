package com.example.ecomarket.services;

import com.example.ecomarket.model.ReportesModel;
import com.example.ecomarket.repository.ReportesRepository;
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

class ReportesServicesTest {

    @Mock
    private ReportesRepository reportesRepository;

    @InjectMocks
    private ReportesServices reportesServices;

    private ReportesModel reporte1;
    private ReportesModel reporte2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        reporte1 = new ReportesModel("R001", "Juan Perez", "11111111-1", "juan.p@example.com", "No puedo iniciar sesión", "Pendiente");
        reporte2 = new ReportesModel("R002", "Maria Lopez", "22222222-2", "maria.l@example.com", "Problemas con mi último pedido", "En Proceso");
    }

    @Test
    void testObtenerTodosLosReportes() {
        when(reportesRepository.findAll()).thenReturn(Arrays.asList(reporte1, reporte2));

        List<ReportesModel> reportes = reportesServices.obtenerTodosLosReportes();

        assertNotNull(reportes);
        assertEquals(2, reportes.size());
        assertEquals("Juan Perez", reportes.get(0).getNombreUsuario());
        assertEquals("Problemas con mi último pedido", reportes.get(1).getDescripcionProblema());

        verify(reportesRepository, times(1)).findAll();
    }

    @Test
    void testBuscarReportePorIdExistente() {
        when(reportesRepository.findById("R001")).thenReturn(Optional.of(reporte1));

        Optional<ReportesModel> foundReporte = reportesServices.buscarReportePorId("R001");

        assertTrue(foundReporte.isPresent());
        assertEquals("Juan Perez", foundReporte.get().getNombreUsuario());
        assertEquals("Pendiente", foundReporte.get().getEstadoReporte());

        verify(reportesRepository, times(1)).findById("R001");
    }

    @Test
    void testBuscarReportePorIdNoExistente() {
        when(reportesRepository.findById("R999")).thenReturn(Optional.empty());

        Optional<ReportesModel> foundReporte = reportesServices.buscarReportePorId("R999");

        assertFalse(foundReporte.isPresent());

        verify(reportesRepository, times(1)).findById("R999");
    }

    @Test
    void testGuardarReporte() {
        ReportesModel nuevoReporte = new ReportesModel(null, "Pedro Gomez", "33333333-3", "pedro.g@example.com", "Sugerencia de nueva función", "Pendiente");
        ReportesModel savedReporte = new ReportesModel("R003", "Pedro Gomez", "33333333-3", "pedro.g@example.com", "Sugerencia de nueva función", "Pendiente");

        when(reportesRepository.save(any(ReportesModel.class))).thenReturn(savedReporte);

        ReportesModel result = reportesServices.guardarReporte(nuevoReporte);

        assertNotNull(result);
        assertEquals("R003", result.getIdReporte());
        assertEquals("Pedro Gomez", result.getNombreUsuario());

        verify(reportesRepository, times(1)).save(nuevoReporte);
    }

    @Test
    void testActualizarReporteExistente() {
        ReportesModel reporteActualizadoData = new ReportesModel("R001", "Juan Perez", "11111111-1", "juan.p@example.com", "No puedo iniciar sesión - Resuelto", "Resuelto");

        when(reportesRepository.findById("R001")).thenReturn(Optional.of(reporte1));
        when(reportesRepository.save(any(ReportesModel.class))).thenReturn(reporteActualizadoData);

        ReportesModel result = reportesServices.actualizarReporte(reporteActualizadoData);

        assertNotNull(result);
        assertEquals("R001", result.getIdReporte());
        assertEquals("Resuelto", result.getEstadoReporte());
        assertEquals("No puedo iniciar sesión - Resuelto", result.getDescripcionProblema());

        verify(reportesRepository, times(1)).findById("R001");
        verify(reportesRepository, times(1)).save(any(ReportesModel.class));
    }

    @Test
    void testActualizarReporteNoExistente() {
        ReportesModel nonExistentReporte = new ReportesModel("R999", "Fake User", "99999999-9", "fake@example.com", "Non-existent problem", "Pendiente");

        when(reportesRepository.findById("R999")).thenReturn(Optional.empty());

        ReportesModel result = reportesServices.actualizarReporte(nonExistentReporte);

        assertNull(result);

        verify(reportesRepository, times(1)).findById("R999");
        verify(reportesRepository, never()).save(any(ReportesModel.class));
    }

    @Test
    void testEliminarReporteExistente() {
        when(reportesRepository.existsById("R001")).thenReturn(true);
        doNothing().when(reportesRepository).deleteById("R001");

        boolean eliminado = reportesServices.eliminarReporte("R001");

        assertTrue(eliminado);

        verify(reportesRepository, times(1)).existsById("R001");
        verify(reportesRepository, times(1)).deleteById("R001");
    }

    @Test
    void testEliminarReporteNoExistente() {
        when(reportesRepository.existsById("R999")).thenReturn(false);

        boolean eliminado = reportesServices.eliminarReporte("R999");

        assertFalse(eliminado);

        verify(reportesRepository, times(1)).existsById("R999");
        verify(reportesRepository, never()).deleteById(anyString());
    }
}