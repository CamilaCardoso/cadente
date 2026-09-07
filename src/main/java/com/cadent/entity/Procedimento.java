package com.cadent.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "procedimento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Procedimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome do procedimento é obrigatório")
    @Column(nullable = false, unique = true)
    private String nome;

    private String descricao;

    @Positive(message = "Valor deve ser maior que zero")
    @DecimalMin(value = "0.01", message = "Valor mínimo é R$ 0.01")
    @Column(nullable = false)
    private BigDecimal valorPadrao;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToMany(mappedBy = "procedimentos")
    private List<Agendamento> agendamentos;

    @Override
    public String toString() {
        return "Procedimento{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", valorPadrao=" + valorPadrao +
                '}';
    }
}