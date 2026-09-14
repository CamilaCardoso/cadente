package com.cadent.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidadorCpfTest {

    private ValidadorCpf validadorCpf;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        validadorCpf = new ValidadorCpf();
        validadorCpf.initialize(null);
    }

    private void prepareMockContext() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
    }

    @Test
    @DisplayName("Deve retornar true quando CPF for nulo")
    void testCpfNulo() {
        assertTrue(validadorCpf.isValid(null, context));
    }

    @Test
    @DisplayName("Deve retornar true quando CPF for válido e formatado")
    void testCpfValidoFormatado() {
        // CPF válido gerado
        assertTrue(validadorCpf.isValid("529.982.247-25", context));
    }

    @Test
    @DisplayName("Deve retornar true quando CPF for válido e apenas números")
    void testCpfValidoApenasNumeros() {
        assertTrue(validadorCpf.isValid("52998224725", context));
    }

    @Test
    @DisplayName("Deve retornar false quando tamanho do CPF for diferente de 11 dígitos")
    void testCpfTamanhoInvalido() {
        prepareMockContext();
        assertFalse(validadorCpf.isValid("1234567890", context));
        assertFalse(validadorCpf.isValid("123456789012", context));
    }

    @Test
    @DisplayName("Deve retornar false quando CPF tiver todos os dígitos iguais")
    void testCpfDigitosIguais() {
        prepareMockContext();
        assertFalse(validadorCpf.isValid("11111111111", context));
        assertFalse(validadorCpf.isValid("00000000000", context));
    }

    @Test
    @DisplayName("Deve retornar false quando primeiro dígito verificador for inválido")
    void testPrimeiroDigitoInvalido() {
        prepareMockContext();
        // 529982247-15 -> primeiro dígito correto é 2, não 1
        assertFalse(validadorCpf.isValid("52998224715", context));
    }

    @Test
    @DisplayName("Deve retornar false quando segundo dígito verificador for inválido")
    void testSegundoDigitoInvalido() {
        prepareMockContext();
        // 529982247-24 -> segundo dígito correto é 5, não 4
        assertFalse(validadorCpf.isValid("52998224724", context));
    }
}

