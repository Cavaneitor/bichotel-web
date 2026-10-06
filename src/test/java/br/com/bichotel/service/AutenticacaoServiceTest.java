package br.com.bichotel.service;

import br.com.bichotel.model.Usuario;
import br.com.bichotel.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do AutenticacaoService, usando um repositório MOCK
 * (Mockito) no lugar do banco de dados real — não é necessário MySQL
 * para rodar estes testes.
 */
@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    private AutenticacaoService service;

    @BeforeEach
    void montarService() {
        service = new AutenticacaoService(usuarioRepository);
    }

    @Test
    void deveAutenticarComCredenciaisValidas() {
        when(usuarioRepository.findByUsuario("Cava"))
                .thenReturn(Optional.of(new Usuario("Cava", "1234")));

        assertTrue(service.autenticar("Cava", "1234"));
    }

    @Test
    void deveLancarExcecaoParaSenhaIncorreta() {
        when(usuarioRepository.findByUsuario("Cava"))
                .thenReturn(Optional.of(new Usuario("Cava", "1234")));

        assertThrows(IllegalArgumentException.class, () -> service.autenticar("Cava", "senhaErrada"));
    }

    @Test
    void deveLancarExcecaoParaUsuarioInexistente() {
        when(usuarioRepository.findByUsuario("desconhecido"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.autenticar("desconhecido", "1234"));
    }

    @Test
    void deveLancarExcecaoParaCamposVazios() {
        assertThrows(IllegalArgumentException.class, () -> service.autenticar("", "1234"));
    }
}
