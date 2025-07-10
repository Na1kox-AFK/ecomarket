package com.example.ecomarket.services;

import org.springframework.stereotype.Service;

import com.example.ecomarket.repository.ReportesRepository;
import com.example.ecomarket.model.ReportesModel; 

import java.util.List;
import java.util.Optional;

@Service
public class ReportesServices {
 
    private final ReportesRepository reportesRepository;


    public ReportesServices(ReportesRepository reportesRepository) {
        this.reportesRepository = reportesRepository;
    }


    public List<ReportesModel> obtenerTodosLosReportes() {
        return reportesRepository.findAll();
    }

 
    public Optional<ReportesModel> buscarReportePorId(String idReporte) {

        return reportesRepository.findByIdReporte(idReporte);

    }


    public ReportesModel guardarReporte(ReportesModel reporte) {
        return reportesRepository.save(reporte);
    }


    public ReportesModel actualizarReporte(ReportesModel reporte) {
         Optional<ReportesModel> existingReporteOptional = reportesRepository.findById(reporte.getIdReporte());
         if (existingReporteOptional.isPresent()) {
             ReportesModel existingReporte = existingReporteOptional.get();
             existingReporte.setDescripcionProblema(reporte.getDescripcionProblema());
             existingReporte.setEstadoReporte(reporte.getEstadoReporte());

             return reportesRepository.save(existingReporte);
         } else {
             return null;
         }
    }


    public boolean eliminarReporte(String idReporte) {
        if (reportesRepository.existsById(idReporte)) {
            reportesRepository.deleteById(idReporte);
            return true;
        }
        return false;
    }
}