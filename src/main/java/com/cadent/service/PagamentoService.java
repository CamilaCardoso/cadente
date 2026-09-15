package com.cadent.service;

import com.cadent.entity.Agendamento;
import com.cadent.entity.Pagamento;
import com.cadent.exception.DuplicateResourceException;
import com.cadent.exception.ResourceNotFoundException;
import com.cadent.repository.AgendamentoRepository;
import com.cadent.repository.PagamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final AgendamentoRepository agendamentoRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository,
                            AgendamentoRepository agendamentoRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.agendamentoRepository = agendamentoRepository;
    }

    @Transactional(readOnly = true)
    public List<Pagamento> listarTodos() {
        return pagamentoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Pagamento buscarPorId(Long id) {
        return pagamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento com ID " + id + " não encontrado"));
    }

    @Transactional(readOnly = true)
    public Pagamento buscarPorAgendamento(Long agendamentoId) {
        return pagamentoRepository.findByAgendamentoId(agendamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento para agendamento " + agendamentoId + " não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Pagamento> buscarPorStatus(String status) {
        try {
            Pagamento.StatusPagamento statusEnum = Pagamento.StatusPagamento.valueOf(status.toUpperCase());
            return pagamentoRepository.findByStatus(statusEnum);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido. Use: PENDENTE, PAGO ou CANCELADO");
        }
    }

    @Transactional
    public Pagamento salvar(Pagamento pagamento) {
        if (pagamento.getAgendamento() == null || pagamento.getAgendamento().getId() == null) {
            throw new IllegalArgumentException("Agendamento é obrigatório");
        }

        Long agendamentoId = pagamento.getAgendamento().getId();

        Agendamento agendamento = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new ResourceNotFoundException("Agendamento com ID " + agendamentoId + " não encontrado"));

        if (pagamentoRepository.findByAgendamentoId(agendamentoId).isPresent()) {
            throw new DuplicateResourceException("Já existe um pagamento registrado para este agendamento");
        }

        if (pagamento.getValorTotal() == null || pagamento.getValorTotal().signum() <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }

        pagamento.setAgendamento(agendamento);
        return pagamentoRepository.save(pagamento);
    }

    @Transactional
    public Pagamento atualizar(Long id, Pagamento pagamentoAtualizado) {
        Pagamento p = buscarPorId(id);

        if (pagamentoAtualizado.getDataPagamento() != null) {
            p.setDataPagamento(pagamentoAtualizado.getDataPagamento());
        }

        if (pagamentoAtualizado.getValorTotal() != null) {
            if (pagamentoAtualizado.getValorTotal().signum() <= 0) {
                throw new IllegalArgumentException("Valor deve ser maior que zero");
            }
            p.setValorTotal(pagamentoAtualizado.getValorTotal());
        }

        if (pagamentoAtualizado.getMetodo() != null) {
            p.setMetodo(pagamentoAtualizado.getMetodo());
        }

        if (pagamentoAtualizado.getStatus() != null) {
            p.setStatus(pagamentoAtualizado.getStatus());
        }

        return pagamentoRepository.save(p);
    }

    @Transactional
    public void deletar(Long id) {
        Pagamento pagamento = buscarPorId(id);
        pagamentoRepository.delete(pagamento);
    }

    @Transactional(readOnly = true)
    public long contarPagos() {
        return pagamentoRepository.countByStatus(Pagamento.StatusPagamento.PAGO);
    }

    @Transactional(readOnly = true)
    public long contarPendentes() {
        return pagamentoRepository.countByStatus(Pagamento.StatusPagamento.PENDENTE);
    }
}