package com.cadent.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Table(name = "dentista")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dentista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "CRM é obrigatório")
    @Column(unique = true, nullable = false)
    private String crm;

    private String especialidade;

    private String telefone;

    @OneToMany(mappedBy = "dentista", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Agendamento> agendamentos;
}