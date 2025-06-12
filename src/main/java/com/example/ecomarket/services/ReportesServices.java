package com.example.ecomarket.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.ecomarket.repository.ReportesRepository; // Importación del repositorio de Reportes
import com.example.ecomarket.model.ReportesModel; // Importación del modelo de Reporte

import java.util.List;
import java.util.Optional;

@Service
public class ReportesServices {
    private final ReportesRepository.ReporteRepository reportesRepository;

    @Autowired
    public ReportesServices(ReportesRepository.ReporteRepository reportesRepository) {
        this.reportesRepository = reportesRepository;
    }

    // Método para obtener todos los reportes
    public List<ReportesModel> obtenerTodosLosReportes() {
        return reportesRepository.findAll();
    }

    // Método para buscar un reporte por su ID
    public Optional<ReportesModel> buscarReportePorId(String idReporte) {
        return reportesRepository.findByIdReporte(idReporte);
    }

    // Método para guardar un nuevo reporte
    public ReportesModel guardarReporte(ReportesModel reporte) {
        return reportesRepository.save(reporte);
    }

    // Método para actualizar un reporte existente
    public ReportesModel actualizarReporte(ReportesModel reporte) {
        return reportesRepository.save(reporte);
    }

    // Método para eliminar un reporte por su ID
    public boolean eliminarReporte(String idReporte) {
        if (reportesRepository.existsById(idReporte)) {
            reportesRepository.deleteById(idReporte);
            return true;
        }
        return false;
    }

}
