package br.com.bichotel.controller;

import br.com.bichotel.dto.AgendamentoRequest;
import br.com.bichotel.dto.AgendamentoResponse;
import br.com.bichotel.model.Agendamento;
import br.com.bichotel.service.AgendamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @GetMapping
    public List<AgendamentoResponse> listar(@RequestParam(required = false) String status) {
        return agendamentoService.listar(status).stream()
                .map(AgendamentoResponse::from)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AgendamentoResponse buscarPorId(@PathVariable Long id) {
        return AgendamentoResponse.from(agendamentoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AgendamentoResponse> cadastrar(@RequestBody AgendamentoRequest requisicao) {
        Agendamento salvo = agendamentoService.cadastrar(requisicao);
        return ResponseEntity.status(HttpStatus.CREATED).body(AgendamentoResponse.from(salvo));
    }

    @PutMapping("/{id}")
    public AgendamentoResponse atualizar(@PathVariable Long id, @RequestBody AgendamentoRequest requisicao) {
        return AgendamentoResponse.from(agendamentoService.atualizar(id, requisicao));
    }

    @PatchMapping("/{id}/concluir")
    public AgendamentoResponse concluir(@PathVariable Long id) {
        return AgendamentoResponse.from(agendamentoService.concluir(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        agendamentoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
