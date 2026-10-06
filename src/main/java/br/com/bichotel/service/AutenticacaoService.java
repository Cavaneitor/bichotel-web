package br.com.bichotel.service;

import br.com.bichotel.model.Usuario;
import br.com.bichotel.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public AutenticacaoService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Valida as credenciais informadas contra a tabela usuario.
     * Retorna true se estiverem corretas, ou lança uma exceção com o erro específico.
     *
     * Observação: a senha é comparada em texto puro para manter a paridade com o
     * AutenticacaoService original do projeto desktop. Para um ambiente de produção
     * real, o recomendado é armazenar a senha com hash (ex: BCrypt) — fica como
     * melhoria futura fora do escopo desta atividade.
     */
    public boolean autenticar(String usuario, String senha) throws IllegalArgumentException {
        if (usuario == null || usuario.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Preencha o Usuário e a Senha para efetuar o login.");
        }

        Optional<Usuario> encontrado = usuarioRepository.findByUsuario(usuario);
        if (encontrado.isPresent() && encontrado.get().getSenha().equals(senha)) {
            return true;
        }
        throw new IllegalArgumentException("Usuário INVÁLIDO | Senha INCORRETA.");
    }
}
