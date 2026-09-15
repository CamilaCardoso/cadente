package com.cadent.service;

import com.cadent.entity.Paciente;
import com.cadent.exception.DuplicateResourceException;
import com.cadent.exception.ResourceNotFoundException;
import com.cadent.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    public Optional<Paciente> buscarPorCpf(String cpf) {
        return pacienteRepository.findByCpf(cpf);
    }

    @Transactional(readOnly = true)
    public Paciente buscarPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado com o ID: " + id));
    }

    @Transactional
    public Paciente salvar(Paciente paciente) {
        if (pacienteRepository.existsByCpf(paciente.getCpf())) {
            throw new DuplicateResourceException("Já existe um paciente cadastrado com o CPF: " + paciente.getCpf());
        }

        if (paciente.getEmail() != null && !paciente.getEmail().isBlank()) {
            if (pacienteRepository.existsByEmail(paciente.getEmail())) {
                throw new DuplicateResourceException("Já existe um paciente cadastrado com o e-mail: " + paciente.getEmail());
            }
        }

        return pacienteRepository.save(paciente);
    }

    @Transactional
    public Paciente atualizar(Long id, Paciente pacienteAtualizado) {
        Paciente pacienteExistente = buscarPorId(id);

        if (pacienteAtualizado.getEmail() != null && !pacienteAtualizado.getEmail().equals(pacienteExistente.getEmail())) {
            if (pacienteRepository.existsByEmail(pacienteAtualizado.getEmail())) {
                throw new DuplicateResourceException("Já existe um paciente cadastrado com o e-mail: " + pacienteAtualizado.getEmail());
            }
            pacienteExistente.setEmail(pacienteAtualizado.getEmail());
        }

        pacienteExistente.setNome(pacienteAtualizado.getNome());
        pacienteExistente.setTelefone(pacienteAtualizado.getTelefone());

        return pacienteRepository.save(pacienteExistente);
    }

    @Transactional
    public void deletar(Long id) {
        Paciente paciente = buscarPorId(id);
        pacienteRepository.delete(paciente);
    }
}