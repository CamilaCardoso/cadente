package com.cadent.controller;

import com.cadent.entity.Procedimento;
import com.cadent.exception.DuplicateResourceException;
import com.cadent.exception.ResourceNotFoundException;
import com.cadent.repository.ProcedimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/procedimentos")
@Validated
public class ProcedimentoController {

    @Autowired
    private ProcedimentoRepository procedimentoRepository;

    /**
     * GET /api/procedimentos
     * Listar todos os procedimentos ordenados por nome
     */
    @GetMapping
    public ResponseEntity<List<Procedimento>> listarTodos() {
        List<Procedimento> procedimentos = procedimentoRepository.findAllByOrderByNomeAsc();
        return ResponseEntity.ok(procedimentos);
    }

    /**
     * GET /api/procedimentos/{id}
     * Buscar procedimento específico por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Procedimento> buscarPorId(@PathVariable Long id) {
        Optional<Procedimento> procedimento = procedimentoRepository.findById(id);
        
        if (procedimento.isEmpty()) {
            throw new ResourceNotFoundException(
                "Procedimento com ID " + id + " não encontrado"
            );
        }
        
        return ResponseEntity.ok(procedimento.get());
    }

    /**
     * GET /api/procedimentos/nome/{nome}
     * Buscar procedimento por nome (case-insensitive)
     */
    @GetMapping("/nome/{nome}")
    public ResponseEntity<Procedimento> buscarPorNome(@PathVariable String nome) {
        Optional<Procedimento> procedimento = procedimentoRepository.findByNomeIgnoreCase(nome);
        
        if (procedimento.isEmpty()) {
            throw new ResourceNotFoundException(
                "Procedimento '" + nome + "' não encontrado"
            );
        }
        
        return ResponseEntity.ok(procedimento.get());
    }

    /**
     * POST /api/procedimentos
     * Criar novo procedimento
     * 
     * Body esperado:
     * {
     *   "nome": "Limpeza Dental",
     *   "descricao": "Limpeza profissional dos dentes",
     *   "valorPadrao": 150.00
     * }
     */
    @PostMapping
    public ResponseEntity<Procedimento> criar(@Valid @RequestBody Procedimento procedimento) {
        
        // Validar se nome já existe
        if (procedimentoRepository.findByNomeIgnoreCase(procedimento.getNome()).isPresent()) {
            throw new DuplicateResourceException(
                "Já existe um procedimento com nome '" + procedimento.getNome() + "'"
            );
        }

        // Validações adicionais
        if (procedimento.getValorPadrao() == null || procedimento.getValorPadrao().signum() <= 0) {
            throw new IllegalArgumentException("Valor padrão deve ser maior que zero");
        }

        Procedimento procedimentoCriado = procedimentoRepository.save(procedimento);
        return ResponseEntity.status(HttpStatus.CREATED).body(procedimentoCriado);
    }

    /**
     * PUT /api/procedimentos/{id}
     * Atualizar procedimento existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<Procedimento> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Procedimento procedimentoAtualizado) {

        Optional<Procedimento> procedimentoExistente = procedimentoRepository.findById(id);
        
        if (procedimentoExistente.isEmpty()) {
            throw new ResourceNotFoundException(
                "Procedimento com ID " + id + " não encontrado"
            );
        }

        Procedimento p = procedimentoExistente.get();

        // Atualizar nome (se diferente, verificar duplicação)
        if (procedimentoAtualizado.getNome() != null && 
            !p.getNome().equalsIgnoreCase(procedimentoAtualizado.getNome())) {
            
            if (procedimentoRepository.findByNomeIgnoreCase(procedimentoAtualizado.getNome()).isPresent()) {
                throw new DuplicateResourceException(
                    "Já existe um procedimento com nome '" + procedimentoAtualizado.getNome() + "'"
                );
            }
            p.setNome(procedimentoAtualizado.getNome());
        }

        // Atualizar descrição
        if (procedimentoAtualizado.getDescricao() != null) {
            p.setDescricao(procedimentoAtualizado.getDescricao());
        }

        // Atualizar valor
        if (procedimentoAtualizado.getValorPadrao() != null) {
            if (procedimentoAtualizado.getValorPadrao().signum() <= 0) {
                throw new IllegalArgumentException("Valor padrão deve ser maior que zero");
            }
            p.setValorPadrao(procedimentoAtualizado.getValorPadrao());
        }

        Procedimento salvo = procedimentoRepository.save(p);
        return ResponseEntity.ok(salvo);
    }

    /**
     * DELETE /api/procedimentos/{id}
     * Deletar procedimento
     * 
     * Nota: Se o procedimento está vinculado a agendamentos,
     * a exclusão será rejeitada (foreign key constraint)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        
        if (!procedimentoRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                "Procedimento com ID " + id + " não encontrado"
            );
        }

        procedimentoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/procedimentos/stats/count
     * Retornar quantidade total de procedimentos
     */
    @GetMapping("/stats/count")
    public ResponseEntity<Long> contarProcedimentos() {
        Long total = procedimentoRepository.count();
        return ResponseEntity.ok(total);
    }
}