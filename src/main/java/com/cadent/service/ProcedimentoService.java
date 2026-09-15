package com.cadent.service;

import com.cadent.entity.Procedimento;
import com.cadent.exception.DuplicateResourceException;
import com.cadent.exception.ResourceNotFoundException;
import com.cadent.repository.ProcedimentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProcedimentoService {

    private final ProcedimentoRepository procedimentoRepository;

    public ProcedimentoService(ProcedimentoRepository procedimentoRepository) {
        this.procedimentoRepository = procedimentoRepository;
    }

    @Transactional(readOnly = true)
    public List<Procedimento> listarTodos() {
        return procedimentoRepository.findAllByOrderByNomeAsc();
    }

    @Transactional(readOnly = true)
    public Procedimento buscarPorId(Long id) {
        return procedimentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Procedimento com ID " + id + " não encontrado"));
    }

    @Transactional(readOnly = true)
    public Procedimento buscarPorNome(String nome) {
        return procedimentoRepository.findByNomeIgnoreCase(nome)
                .orElseThrow(() -> new ResourceNotFoundException("Procedimento '" + nome + "' não encontrado"));
    }

    @Transactional
    public Procedimento salvar(Procedimento procedimento) {
        if (procedimentoRepository.findByNomeIgnoreCase(procedimento.getNome()).isPresent()) {
            throw new DuplicateResourceException("Já existe um procedimento com nome '" + procedimento.getNome() + "'");
        }

        if (procedimento.getValorPadrao() == null || procedimento.getValorPadrao().signum() <= 0) {
            throw new IllegalArgumentException("Valor padrão deve ser maior que zero");
        }

        return procedimentoRepository.save(procedimento);
    }

    @Transactional
    public Procedimento atualizar(Long id, Procedimento procedimentoAtualizado) {
        Procedimento p = buscarPorId(id);

        if (procedimentoAtualizado.getNome() != null && 
            !p.getNome().equalsIgnoreCase(procedimentoAtualizado.getNome())) {
            
            if (procedimentoRepository.findByNomeIgnoreCase(procedimentoAtualizado.getNome()).isPresent()) {
                throw new DuplicateResourceException("Já existe um procedimento com nome '" + procedimentoAtualizado.getNome() + "'");
            }
            p.setNome(procedimentoAtualizado.getNome());
        }

        if (procedimentoAtualizado.getDescricao() != null) {
            p.setDescricao(procedimentoAtualizado.getDescricao());
        }

        if (procedimentoAtualizado.getValorPadrao() != null) {
            if (procedimentoAtualizado.getValorPadrao().signum() <= 0) {
                throw new IllegalArgumentException("Valor padrão deve ser maior que zero");
            }
            p.setValorPadrao(procedimentoAtualizado.getValorPadrao());
        }

        return procedimentoRepository.save(p);
    }

    @Transactional
    public void deletar(Long id) {
        Procedimento procedimento = buscarPorId(id);
        procedimentoRepository.delete(procedimento);
    }

    @Transactional(readOnly = true)
    public Long contarProcedimentos() {
        return procedimentoRepository.count();
    }
}