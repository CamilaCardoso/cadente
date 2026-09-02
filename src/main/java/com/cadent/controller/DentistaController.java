package com.cadent.controller;

import com.cadent.entity.Dentista;
import com.cadent.exception.DuplicateResourceException;
import com.cadent.exception.ResourceNotFoundException;
import com.cadent.repository.DentistaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dentistas")
@Validated
public class DentistaController {

    @Autowired
    private DentistaRepository dentistaRepository;

    @GetMapping
    public ResponseEntity<List<Dentista>> listarTodos() {
        List<Dentista> dentistas = dentistaRepository.findAll();
        return ResponseEntity.ok(dentistas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dentista> buscarPorId(@PathVariable Long id) {
        Optional<Dentista> dentista = dentistaRepository.findById(id);
        if (dentista.isEmpty()) {
            throw new ResourceNotFoundException("Dentista com ID " + id + " não encontrado");
        }
        return ResponseEntity.ok(dentista.get());
    }

    @PostMapping
    public ResponseEntity<Dentista> criar(@Valid @RequestBody Dentista dentista) {
        // Verifica se CRM já existe
        if (dentistaRepository.findByCrm(dentista.getCrm()).isPresent()) {
            throw new DuplicateResourceException("CRM " + dentista.getCrm() + " já cadastrado");
        }

        Dentista dentistaCriado = dentistaRepository.save(dentista);
        return ResponseEntity.status(HttpStatus.CREATED).body(dentistaCriado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dentista> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Dentista dentistaAtualizado) {

        Optional<Dentista> dentistaExistente = dentistaRepository.findById(id);
        if (dentistaExistente.isEmpty()) {
            throw new ResourceNotFoundException("Dentista com ID " + id + " não encontrado");
        }

        Dentista d = dentistaExistente.get();

        // Verifica se está tentando mudar CRM pra um que já existe
        if (!d.getCrm().equals(dentistaAtualizado.getCrm())) {
            if (dentistaRepository.findByCrm(dentistaAtualizado.getCrm()).isPresent()) {
                throw new DuplicateResourceException("CRM " + dentistaAtualizado.getCrm() + " já cadastrado");
            }
            d.setCrm(dentistaAtualizado.getCrm());
        }

        if (dentistaAtualizado.getNome() != null) d.setNome(dentistaAtualizado.getNome());
        if (dentistaAtualizado.getEspecialidade() != null) d.setEspecialidade(dentistaAtualizado.getEspecialidade());
        if (dentistaAtualizado.getTelefone() != null) d.setTelefone(dentistaAtualizado.getTelefone());

        Dentista salvo = dentistaRepository.save(d);
        return ResponseEntity.ok(salvo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!dentistaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Dentista com ID " + id + " não encontrado");
        }
        dentistaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}