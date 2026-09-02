package com.cadent.controller;

import com.cadent.entity.Paciente;
import com.cadent.exception.DuplicateResourceException;
import com.cadent.exception.ResourceNotFoundException;
import com.cadent.repository.PacienteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pacientes")
@Validated
public class PacienteController {

    @Autowired
    private PacienteRepository pacienteRepository;

    // GET - Listar todos os pacientes
    @GetMapping
    public ResponseEntity<List<Paciente>> listarTodos() {
        List<Paciente> pacientes = pacienteRepository.findAll();
        return ResponseEntity.ok(pacientes);
    }

    // GET - Buscar paciente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPorId(@PathVariable Long id) {
        Optional<Paciente> paciente = pacienteRepository.findById(id);
        if (paciente.isEmpty()) {
            throw new ResourceNotFoundException("Paciente com ID " + id + " não encontrado");
        }
        return ResponseEntity.ok(paciente.get());
    }

    // GET - Buscar por CPF
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Paciente> buscarPorCpf(@PathVariable String cpf) {
        Optional<Paciente> paciente = pacienteRepository.findByCpf(cpf);
        if (paciente.isEmpty()) {
            throw new ResourceNotFoundException("Paciente com CPF " + cpf + " não encontrado");
        }
        return ResponseEntity.ok(paciente.get());
    }

    // POST - Criar novo paciente
    @PostMapping
    public ResponseEntity<Paciente> criar(@Valid @RequestBody Paciente paciente) {
        // Verifica se CPF já existe
        if (pacienteRepository.findByCpf(paciente.getCpf()).isPresent()) {
            throw new DuplicateResourceException("CPF " + paciente.getCpf() + " já cadastrado");
        }

        // Verifica se email já existe
        if (paciente.getEmail() != null && pacienteRepository.findByEmail(paciente.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Email " + paciente.getEmail() + " já cadastrado");
        }

        Paciente pacienteCriado = pacienteRepository.save(paciente);
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteCriado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paciente> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Paciente pacienteAtualizado) {

        Optional<Paciente> pacienteExistente = pacienteRepository.findById(id);
        if (pacienteExistente.isEmpty()) {
            throw new ResourceNotFoundException("Paciente com ID " + id + " não encontrado");
        }

        Paciente p = pacienteExistente.get();

        // Verifica se está tentando mudar CPF pra um que já existe
        if (!p.getCpf().equals(pacienteAtualizado.getCpf())) {
            if (pacienteRepository.findByCpf(pacienteAtualizado.getCpf()).isPresent()) {
                throw new DuplicateResourceException("CPF " + pacienteAtualizado.getCpf() + " já cadastrado");
            }
            p.setCpf(pacienteAtualizado.getCpf());
        }

        if (pacienteAtualizado.getNome() != null) p.setNome(pacienteAtualizado.getNome());
        if (pacienteAtualizado.getEmail() != null) p.setEmail(pacienteAtualizado.getEmail());
        if (pacienteAtualizado.getTelefone() != null) p.setTelefone(pacienteAtualizado.getTelefone());

        Paciente salvo = pacienteRepository.save(p);
        return ResponseEntity.ok(salvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!pacienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Paciente com ID " + id + " não encontrado");
        }
        pacienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}