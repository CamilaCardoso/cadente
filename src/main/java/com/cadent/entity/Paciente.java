package com.cadent.entity;

import com.cadent.validation.ValidaCpf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "paciente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @ValidaCpf
    @NotBlank(message = "CPF é obrigatório")
    @Column(unique = true, nullable = false)
    private String cpf;

    @Email(message = "Email inválido")
    @Column(unique = true)
    private String email;

    private String telefone;

    private LocalDate dataNascimento;

    private String endereco;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    private List<Agendamento> agendamentos;

    @PrePersist
    protected void onCreate() {
        dataCadastro = LocalDateTime.now();
    }
}