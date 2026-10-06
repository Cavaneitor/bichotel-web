package br.com.bichotel.service;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários dos métodos puros de conversão/validação de AgendamentoService
 * (converterValor e converterData), que não dependem de banco de dados nem de
 * contexto Spring — mesma estratégia usada no projeto desktop.
 */
class AgendamentoServiceTest {

    private final AgendamentoService service = new AgendamentoService();

    @Test
    void deveConverterValorValidoCorretamente() {
        double valor = service.converterValor("150,00");
        assertEquals(150.00, valor, 0.001);
    }

    @Test
    void deveLancarExcecaoParaValorComFormatoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> service.converterValor("abc"));
    }

    @Test
    void deveLancarExcecaoParaValorComPontoEmVezDeVirgula() {
        assertThrows(IllegalArgumentException.class, () -> service.converterValor("150.00"));
    }

    @Test
    void deveLancarExcecaoParaValorZeroOuNegativo() {
        assertThrows(IllegalArgumentException.class, () -> service.converterValor("0,00"));
    }

    @Test
    void deveConverterDataValidaCorretamente() {
        String dataFutura = LocalDate.now().plusDays(5).format(DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate resultado = service.converterData(dataFutura);
        assertEquals(LocalDate.now().plusDays(5), resultado);
    }

    @Test
    void deveLancarExcecaoParaDataRetroativa() {
        String dataPassada = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        assertThrows(IllegalArgumentException.class, () -> service.converterData(dataPassada));
    }

    @Test
    void deveLancarExcecaoParaDataComFormatoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> service.converterData("31-12-2026"));
    }
}
