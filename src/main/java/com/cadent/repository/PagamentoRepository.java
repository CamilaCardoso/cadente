package com.cadent.repository;

import com.cadent.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    Optional<Pagamento> findByAgendamentoId(Long agendamentoId);
    List<Pagamento> findByStatus(Pagamento.StatusPagamento status);
}