package com.cadent.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidadorCpf implements ConstraintValidator<ValidaCpf, String> {

    @Override
    public void initialize(ValidaCpf annotation) {
    }

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        // Se nulo, deixa a anotação @NotNull/Required tratar
        if (cpf == null) {
            return true;
        }

        // Remove caracteres especiais
        String cleanCpf = cpf.replaceAll("\\D", "");

        // Deve ter exatamente 11 dígitos
        if (cleanCpf.length() != 11) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("CPF deve conter exatamente 11 dígitos e apenas números")
                    .addConstraintViolation();
            return false;
        }

        // Verifica se todos os dígitos são iguais (CPF inválido)
        if (cleanCpf.matches("(\\d)\\1{10}")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("CPF com dígitos repetidos é inválido")
                    .addConstraintViolation();
            return false;
        }

        // Calcula primeiro dígito verificador
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (cleanCpf.charAt(i) - '0') * (10 - i);
        }
        int digit1 = 11 - (sum % 11);
        digit1 = (digit1 >= 10) ? 0 : digit1;

        if (digit1 != (cleanCpf.charAt(9) - '0')) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("CPF inválido (dígito verificador incorreto)")
                    .addConstraintViolation();
            return false;
        }

        // Calcula segundo dígito verificador
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (cleanCpf.charAt(i) - '0') * (11 - i);
        }
        int digit2 = 11 - (sum % 11);
        digit2 = (digit2 >= 10) ? 0 : digit2;

        if (digit2 != (cleanCpf.charAt(10) - '0')) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("CPF inválido (dígito verificador incorreto)")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}