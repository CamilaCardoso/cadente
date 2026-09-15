package com.cadent.controller;

import com.cadent.entity.Dentista;
import com.cadent.service.DentistaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/dentistas")
@Validated
public class DentistaController {

    private final DentistaService dentistaService;

    public DentistaController(DentistaService dentistaService) {
        this.dentistaService = dentistaService;
    }

    @GetMapping
    public ResponseEntity<List<Dentista>> listarTodos() {
        return ResponseEntity.ok(dentistaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dentista> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(dentistaService.buscarPorId(id));
    }

    @GetMapping("/crm/{crm}")
    public ResponseEntity<Dentista> buscarPorCrm(@PathVariable String crm) {
        return dentistaService.buscarPorCrm(crm)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Dentista> criar(@Valid @RequestBody Dentista dentista) {
        Dentista dentistaCriado = dentistaService.salvar(dentista);
        return ResponseEntity.status(HttpStatus.CREATED).body(dentistaCriado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dentista> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody Dentista dentistaAtualizado) {
        return ResponseEntity.ok(dentistaService.atualizar(id, dentistaAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        dentistaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}