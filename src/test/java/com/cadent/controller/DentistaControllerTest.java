package com.cadent.controller;

import com.cadent.entity.Dentista;
import com.cadent.exception.DuplicateResourceException;
import com.cadent.exception.ResourceNotFoundException;
import com.cadent.repository.DentistaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DentistaControllerTest {

    @Mock
    private DentistaRepository dentistaRepository;

    @InjectMocks
    private DentistaController dentistaController;

    private Dentista dentista;

    @BeforeEach
    void setUp() {
        dentista = Dentista.builder()
                .id(1L)
                .nome("Dr. João Santos")
                .crm("SP-12345")
                .especialidade("Ortodontia")
                .telefone("11988887777")
                .build();
    }

    @Test
    @DisplayName("Deve listar todos os dentistas com sucesso")
    void testListarTodos() {
        when(dentistaRepository.findAll()).thenReturn(List.of(dentista));

        ResponseEntity<List<Dentista>> response = dentistaController.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Dr. João Santos", response.getBody().get(0).getNome());
    }

    @Test
    @DisplayName("Deve buscar dentista por ID com sucesso")
    void testBuscarPorIdSucesso() {
        when(dentistaRepository.findById(1L)).thenReturn(Optional.of(dentista));

        ResponseEntity<Dentista> response = dentistaController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Dr. João Santos", response.getBody().getNome());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar ID inexistente")
    void testBuscarPorIdNaoEncontrado() {
        when(dentistaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> dentistaController.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve criar dentista com sucesso")
    void testCriarSucesso() {
        when(dentistaRepository.findByCrm(dentista.getCrm())).thenReturn(Optional.empty());
        when(dentistaRepository.save(any(Dentista.class))).thenReturn(dentista);

        ResponseEntity<Dentista> response = dentistaController.criar(dentista);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Dr. João Santos", response.getBody().getNome());
    }

    @Test
    @DisplayName("Deve lançar DuplicateResourceException ao criar dentista com CRM duplicado")
    void testCriarCrmDuplicado() {
        when(dentistaRepository.findByCrm(dentista.getCrm())).thenReturn(Optional.of(dentista));

        assertThrows(DuplicateResourceException.class, () -> dentistaController.criar(dentista));
        verify(dentistaRepository, never()).save(any(Dentista.class));
    }

    @Test
    @DisplayName("Deve atualizar dentista com sucesso")
    void testAtualizarSucesso() {
        Dentista dentistaAtualizado = Dentista.builder()
                .nome("Dr. João Santos Silva")
                .crm("SP-12345")
                .especialidade("Endodontia")
                .telefone("11977776666")
                .build();

        when(dentistaRepository.findById(1L)).thenReturn(Optional.of(dentista));
        when(dentistaRepository.save(any(Dentista.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<Dentista> response = dentistaController.atualizar(1L, dentistaAtualizado);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Dr. João Santos Silva", response.getBody().getNome());
        assertEquals("Endodontia", response.getBody().getEspecialidade());
    }

    @Test
    @DisplayName("Deve deletar dentista com sucesso")
    void testDeletarSucesso() {
        when(dentistaRepository.existsById(1L)).thenReturn(true);
        doNothing().when(dentistaRepository).deleteById(1L);

        ResponseEntity<Void> response = dentistaController.deletar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(dentistaRepository, times(1)).deleteById(1L);
    }
}

