package com.example.ecomarket.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.ecomarket.services.ReportesServices; // Importación del servicio de Reportes
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import com.example.ecomarket.model.ReportesModel; // Importación del modelo de Reporte
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.ecomarket.repository.ReportesRepository; // Importación del repositorio de Reportes

@RestController
@RequestMapping("/api/reportes")
public class ReportesController {

    private final ReportesServices reportesServices;

    @Autowired // Inyección de dependencia del ReportesService
    public ReportesController(ReportesServices reportesServices) {
        this.reportesServices = reportesServices;
    }

    @GetMapping
    public ResponseEntity<List<ReportesModel>> getAllReportes() {
        List<ReportesModel> reportes = reportesServices.obtenerTodosLosReportes();
        return new ResponseEntity<>(reportes, HttpStatus.OK);
    }

    @GetMapping("/{idReporte}")
    public ResponseEntity<ReportesModel> getReporteById(@PathVariable String idReporte) {
        Optional<ReportesModel> reporte = reportesServices.buscarReportePorId(idReporte);
        return reporte.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                      .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<ReportesModel> createReporte(@RequestBody ReportesModel reporte) {
        ReportesModel savedReporte = reportesServices.guardarReporte(reporte);
        return new ResponseEntity<>(savedReporte, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ReportesModel> updateReporte(@RequestBody ReportesModel reporte) {
        ReportesModel updatedReporte = reportesServices.actualizarReporte(reporte);
        if (updatedReporte != null) {
            return new ResponseEntity<>(updatedReporte, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{idReporte}")
    public ResponseEntity<Void> deleteReporte(@PathVariable String idReporte) {
        boolean deleted = reportesServices.eliminarReporte(idReporte);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
