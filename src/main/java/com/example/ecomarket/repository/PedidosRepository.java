package com.example.ecomarket.repository;

import com.example.ecomarket.model.PedidosModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidosRepository extends JpaRepository<PedidosModel, String> {
    
}