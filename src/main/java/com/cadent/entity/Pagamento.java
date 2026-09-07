package com.cadent.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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

    // ALTERAÇÃO: Permite receber o agendamento no JSON da requisição (Write-Only) sem gerar loop infinito no retorno
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "Agendamento é obrigatório")
    @ManyToOne(optional = false)
    @JoinColumn(name = "agendamento_id", nullable = false)
    private Agendamento agendamento;

    private LocalDateTime dataPagamento;

    @Positive(message = "Valor deve ser maior que zero")
    @DecimalMin(value = "0.01", message = "Valor mínimo é R$ 0.01")
    @Column(nullable = false)
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

    @Override
    public String toString() {
        return "Pagamento{" +
                "id=" + id +
                ", valorTotal=" + valorTotal +
                ", status=" + status +
                ", metodo=" + metodo +
                '}';
    }
}