package br.com.bichotel.controller;

import br.com.bichotel.dto.LoginRequest;
import br.com.bichotel.service.AutenticacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AutenticacaoService autenticacaoService;

    @PostMapping("/login")
    public Map<String, Boolean> login(@RequestBody LoginRequest requisicao) {
        boolean autenticado = autenticacaoService.autenticar(requisicao.getUsuario(), requisicao.getSenha());
        return Map.of("autenticado", autenticado);
    }
}
