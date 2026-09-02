package com.cadent.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "agendamento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Paciente é obrigatório")
    @ManyToOne(optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @NotNull(message = "Dentista é obrigatório")
    @ManyToOne(optional = false)
    @JoinColumn(name = "dentista_id", nullable = false)
    private Dentista dentista;

    @NotNull(message = "Data e hora são obrigatórias")
    @Column(nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status = StatusAgendamento.AGENDADO;

    private String observacoes;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "agendamento_procedimento",
            joinColumns = @JoinColumn(name = "agendamento_id"),
            inverseJoinColumns = @JoinColumn(name = "procedimento_id")
    )
    private List<Procedimento> procedimentos;

    @OneToMany(mappedBy = "agendamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pagamento> pagamentos;

    public enum StatusAgendamento {
        AGENDADO,
        REALIZADO,
        CANCELADO
    }
}