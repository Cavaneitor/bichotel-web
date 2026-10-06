package br.com.bichotel.service;

import br.com.bichotel.model.Cliente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários da validação de campos do ClienteService.
 * validarCampos() não acessa o repositório/banco, então pode ser testado
 * isoladamente sem subir o contexto do Spring.
 */
class ClienteServiceTest {

    private final ClienteService service = new ClienteService();

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
}
