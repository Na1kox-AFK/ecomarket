package com.example.ecomarket.repository;

import org.springframework.stereotype.Repository;
import com.example.ecomarket.model.InventarioModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<InventarioModel, String> {
    Optional<InventarioModel> findByIdInventario(String idInventario);
    
}
