package com.example.ecomarket.repository;

import com.example.ecomarket.model.ReportesModel; // Importación del modelo de Reporte
import org.springframework.data.jpa.repository.JpaRepository; // Importación de JpaRepository para operaciones CRUD
import org.springframework.stereotype.Repository;
  

/* Importación de Java */
import java.util.Optional;

@Repository
public class ReportesRepository {
/* Hacemos una extensión para que obtenga todos los métodos del CRUD */
    public interface ReporteRepository extends JpaRepository<ReportesModel, String> {

        /* Con esta función podemos hacer que la importación Optional busque detalladamente el atributo idReporte */
        Optional<ReportesModel> findByIdReporte(String idReporte);
    }
}
