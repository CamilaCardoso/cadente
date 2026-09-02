package com.cadent.repository;

import com.cadent.entity.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    
    List<Agendamento> findByPacienteId(Long pacienteId);
    
    List<Agendamento> findByDentistaId(Long dentistaId);
    
    @Query("SELECT a FROM Agendamento a WHERE a.dataHora BETWEEN :dataInicio AND :dataFim ORDER BY a.dataHora")
    List<Agendamento> findAgendamentosPorPeriodo(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}