package br.com.bichotel.config;

import br.com.bichotel.model.Usuario;
import br.com.bichotel.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Garante que exista pelo menos um usuário para login (mesmas credenciais
 * de demonstração usadas no AutenticacaoService do projeto desktop e no
 * front-end: usuário "Cava", senha "1234").
 */
@Component
public class DadosIniciaisConfig implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    public DadosIniciaisConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByUsuario("Cava").isEmpty()) {
            usuarioRepository.save(new Usuario("Cava", "1234"));
        }
    }
}
