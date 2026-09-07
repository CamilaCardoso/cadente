package com.cadent.repository;

import com.cadent.entity.Procedimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ProcedimentoRepository extends JpaRepository<Procedimento, Long> {
    
    // Buscar por nome (case-insensitive)
    Optional<Procedimento> findByNomeIgnoreCase(String nome);
    
    // Listar todos ordenados por nome
    List<Procedimento> findAllByOrderByNomeAsc();
}