package com.cadent.controller;

import com.cadent.entity.Dentista;
import com.cadent.exception.DuplicateResourceException;
import com.cadent.exception.ResourceNotFoundException;
import com.cadent.service.DentistaService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DentistaControllerTest {

    @Mock
    private DentistaService dentistaService;

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
        when(dentistaService.listarTodos()).thenReturn(List.of(dentista));

        ResponseEntity<List<Dentista>> response = dentistaController.listarTodos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Dr. João Santos", response.getBody().get(0).getNome());
    }

    @Test
    @DisplayName("Deve buscar dentista por ID com sucesso")
    void testBuscarPorIdSucesso() {
        when(dentistaService.buscarPorId(1L)).thenReturn(dentista);

        ResponseEntity<Dentista> response = dentistaController.buscarPorId(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Dr. João Santos", response.getBody().getNome());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException ao buscar ID inexistente")
    void testBuscarPorIdNaoEncontrado() {
        when(dentistaService.buscarPorId(99L))
                .thenThrow(new ResourceNotFoundException("Dentista com ID 99 não encontrado"));

        assertThrows(ResourceNotFoundException.class, () -> dentistaController.buscarPorId(99L));
    }

    @Test
    @DisplayName("Deve criar dentista com sucesso")
    void testCriarSucesso() {
        when(dentistaService.salvar(any(Dentista.class))).thenReturn(dentista);

        ResponseEntity<Dentista> response = dentistaController.criar(dentista);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Dr. João Santos", response.getBody().getNome());
    }

    @Test
    @DisplayName("Deve lançar DuplicateResourceException ao criar dentista com CRM duplicado")
    void testCriarCrmDuplicado() {
        when(dentistaService.salvar(any(Dentista.class)))
                .thenThrow(new DuplicateResourceException("CRM SP-12345 já cadastrado"));

        assertThrows(DuplicateResourceException.class, () -> dentistaController.criar(dentista));
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

        when(dentistaService.atualizar(eq(1L), any(Dentista.class))).thenReturn(dentistaAtualizado);

        ResponseEntity<Dentista> response = dentistaController.atualizar(1L, dentistaAtualizado);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Dr. João Santos Silva", response.getBody().getNome());
        assertEquals("Endodontia", response.getBody().getEspecialidade());
    }

    @Test
    @DisplayName("Deve deletar dentista com sucesso")
    void testDeletarSucesso() {
        doNothing().when(dentistaService).deletar(1L);

        ResponseEntity<Void> response = dentistaController.deletar(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(dentistaService, times(1)).deletar(1L);
    }
}