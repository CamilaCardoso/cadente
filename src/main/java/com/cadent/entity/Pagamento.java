package com.cadent.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "agendamento_id", nullable = false)
    private Agendamento agendamento;

    private LocalDateTime dataPagamento;

    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    private MetodoPagamento metodo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status = StatusPagamento.PENDENTE;

    public enum MetodoPagamento {
        DINHEIRO,
        DEBITO,
        CREDITO,
        PIX
    }

    public enum StatusPagamento {
        PENDENTE,
        PAGO,
        CANCELADO
    }
}