package com.cadent.integration;

import com.cadent.entity.Pagamento;
import com.cadent.entity.Pagamento.StatusPagamento;
import org.junit.jupiter.api.Test;

public class PagamentoIntegrationTest {

    @Test
    void exemploTestePagamento() {
        Pagamento pendente = new Pagamento();
        pendente.setStatus(StatusPagamento.PENDENTE);

        Pagamento p1 = new Pagamento();
        p1.setStatus(StatusPagamento.PAGO);

        Pagamento p2 = new Pagamento();
        p2.setStatus(StatusPagamento.PENDENTE);

        Pagamento p3 = new Pagamento();
        p3.setStatus(StatusPagamento.CANCELADO);
    }
}