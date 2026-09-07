package com.cadent.repository;

import com.cadent.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    
    // Buscar pagamento por agendamento
    Optional<Pagamento> findByAgendamentoId(Long agendamentoId);
    
    // Listar pagamentos por status
    List<Pagamento> findByStatus(Pagamento.StatusPagamento status);
    
    // Contar pagamentos por status
    long countByStatus(Pagamento.StatusPagamento status);
}