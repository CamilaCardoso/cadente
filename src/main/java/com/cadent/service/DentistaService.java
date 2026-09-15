package com.cadent.service;

import com.cadent.entity.Dentista;
import com.cadent.exception.DuplicateResourceException;
import com.cadent.exception.ResourceNotFoundException;
import com.cadent.repository.DentistaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DentistaService {

    private final DentistaRepository dentistaRepository;

    public DentistaService(DentistaRepository dentistaRepository) {
        this.dentistaRepository = dentistaRepository;
    }

    @Transactional(readOnly = true)
    public List<Dentista> listarTodos() {
        return dentistaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Dentista buscarPorId(Long id) {
        return dentistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dentista com ID " + id + " não encontrado"));
    }

    @Transactional(readOnly = true)
    public Optional<Dentista> buscarPorCrm(String crm) {
        return dentistaRepository.findByCrm(crm);
    }

    @Transactional
    public Dentista salvar(Dentista dentista) {
        if (dentistaRepository.existsByCrm(dentista.getCrm())) {
            throw new DuplicateResourceException("CRM " + dentista.getCrm() + " já cadastrado");
        }
        return dentistaRepository.save(dentista);
    }

    @Transactional
    public Dentista atualizar(Long id, Dentista dentistaAtualizado) {
        Dentista dentistaExistente = buscarPorId(id);

        if (!dentistaExistente.getCrm().equals(dentistaAtualizado.getCrm())) {
            if (dentistaRepository.existsByCrm(dentistaAtualizado.getCrm())) {
                throw new DuplicateResourceException("CRM " + dentistaAtualizado.getCrm() + " já cadastrado");
            }
            dentistaExistente.setCrm(dentistaAtualizado.getCrm());
        }

        if (dentistaAtualizado.getNome() != null) dentistaExistente.setNome(dentistaAtualizado.getNome());
        if (dentistaAtualizado.getEspecialidade() != null) dentistaExistente.setEspecialidade(dentistaAtualizado.getEspecialidade());
        if (dentistaAtualizado.getTelefone() != null) dentistaExistente.setTelefone(dentistaAtualizado.getTelefone());

        return dentistaRepository.save(dentistaExistente);
    }

    @Transactional
    public void deletar(Long id) {
        Dentista dentista = buscarPorId(id);
        dentistaRepository.delete(dentista);
    }
}