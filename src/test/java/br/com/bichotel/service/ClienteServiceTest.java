package br.com.bichotel.service;

import br.com.bichotel.model.Cliente;
import br.com.bichotel.repository.AgendamentoRepository;
import br.com.bichotel.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do ClienteService.
 * validarCampos() não acessa repositório, mas o construtor agora exige
 * ClienteRepository e AgendamentoRepository — usamos mocks (Mockito) para
 * não depender do banco de dados real.
 */
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private AgendamentoRepository agendamentoRepository;

    private ClienteService service;

    @BeforeEach
    void montarService() {
        service = new ClienteService(clienteRepository, agendamentoRepository);
    }

    private Cliente clienteValido() {
        Cliente c = new Cliente();
        c.setTutor("Marina Souza");
        c.setCpf("123.456.789-00");
        c.setEndereco("Rua das Flores, 120");
        c.setTelefone("99988-7766");
        c.setAnimal("Thor");
        c.setRaca("Golden Retriever");
        return c;
    }

    @Test
    void deveAceitarClienteComTodosOsCamposValidos() {
        assertDoesNotThrow(() -> service.validarCampos(clienteValido()));
    }

    @Test
    void deveLancarExcecaoParaCampoObrigatorioVazio() {
        Cliente cliente = clienteValido();
        cliente.setTutor("");
        assertThrows(IllegalArgumentException.class, () -> service.validarCampos(cliente));
    }

    @Test
    void deveLancarExcecaoParaCpfSemPontuacao() {
        Cliente cliente = clienteValido();
        cliente.setCpf("12345678900");
        assertThrows(IllegalArgumentException.class, () -> service.validarCampos(cliente));
    }

    @Test
    void deveLancarExcecaoParaTelefoneInvalido() {
        Cliente cliente = clienteValido();
        cliente.setTelefone("999999999");
        assertThrows(IllegalArgumentException.class, () -> service.validarCampos(cliente));
    }

    @Test
    void deveAceitarTelefoneComNoveDigitos() {
        Cliente cliente = clienteValido();
        cliente.setTelefone("9999-4433");
        assertDoesNotThrow(() -> service.validarCampos(cliente));
    }

    /**
     * Regressão do bug #01 (GitHub Issue): excluir cliente com agendamento
     * vinculado não pode mais estourar um erro de banco (constraint de FK) —
     * agora deve lançar IllegalArgumentException com mensagem clara, ANTES
     * de chegar no banco.
     */
    @Test
    void deveImpedirExclusaoDeClienteComAgendamentoVinculado() {
        Long idCliente = 1L;
        when(clienteRepository.existsById(idCliente)).thenReturn(true);
        when(agendamentoRepository.existsByClienteId(idCliente)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.excluir(idCliente));

        assertTrue(ex.getMessage().contains("agendamentos vinculados"));
    }

    @Test
    void devePermitirExclusaoDeClienteSemAgendamentoVinculado() {
        Long idCliente = 2L;
        when(clienteRepository.existsById(idCliente)).thenReturn(true);
        when(agendamentoRepository.existsByClienteId(idCliente)).thenReturn(false);

        assertDoesNotThrow(() -> service.excluir(idCliente));
    }
}
