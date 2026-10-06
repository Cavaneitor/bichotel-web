package br.com.bichotel.service;

import br.com.bichotel.dto.AgendamentoRequest;
import br.com.bichotel.exception.RecursoNaoEncontradoException;
import br.com.bichotel.model.Agendamento;
import br.com.bichotel.model.Cliente;
import br.com.bichotel.repository.AgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private ClienteService clienteService;

    public List<Agendamento> listar(String status) {
        if (status == null || status.isBlank()) {
            return agendamentoRepository.findAllByOrderByDataAsc();
        }
        return agendamentoRepository.findByStatus(status);
    }

    public Agendamento buscarPorId(Long id) {
        return agendamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agendamento não encontrado."));
    }

    public Agendamento cadastrar(AgendamentoRequest requisicao) {
        Agendamento agendamento = new Agendamento();
        agendamento.setStatus("agendado");
        aplicarDados(agendamento, requisicao);
        return agendamentoRepository.save(agendamento);
    }

    public Agendamento atualizar(Long id, AgendamentoRequest requisicao) {
        Agendamento existente = buscarPorId(id);
        aplicarDados(existente, requisicao);
        return agendamentoRepository.save(existente);
    }

    public Agendamento concluir(Long id) {
        Agendamento existente = buscarPorId(id);
        existente.setStatus("concluido");
        return agendamentoRepository.save(existente);
    }

    public void excluir(Long id) {
        if (!agendamentoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Agendamento não encontrado.");
        }
        agendamentoRepository.deleteById(id);
    }

    /** Valida e aplica os dados da requisição na entidade (usado tanto no cadastro quanto na edição). */
    private void aplicarDados(Agendamento agendamento, AgendamentoRequest requisicao) {
        if (requisicao.getClienteId() == null) {
            throw new IllegalArgumentException("É obrigatório vincular um cliente válido ao agendamento!");
        }
        Cliente cliente = clienteService.buscarPorId(requisicao.getClienteId());

        if (isStringVazia(requisicao.getServico()) ||
            isStringVazia(requisicao.getData()) ||
            isStringVazia(requisicao.getValor()) ||
            isStringVazia(requisicao.getObservacao())) {
            throw new IllegalArgumentException("ATENÇÃO! Todos os campos obrigatórios devem ser preenchidos.");
        }

        agendamento.setCliente(cliente);
        agendamento.setServico(requisicao.getServico().trim());
        agendamento.setObservacao(requisicao.getObservacao().trim());
        agendamento.setValor(converterValor(requisicao.getValor()));
        agendamento.setData(converterData(requisicao.getData()));

        if (requisicao.getStatus() != null && !requisicao.getStatus().isBlank()) {
            agendamento.setStatus(requisicao.getStatus());
        }
    }

    /**
     * Converte o valor monetário (ex: "12,00") para double, validando formato
     * e a regra de negócio de que o valor deve ser maior que zero.
     * Mesma regra usada e testada no AgendamentoService do projeto desktop.
     */
    public double converterValor(String valorTexto) throws IllegalArgumentException {
        if (!valorTexto.trim().matches("\\d+,\\d{2}")) {
            throw new IllegalArgumentException("O campo 'VALOR' deve conter apenas números e uma vírgula. Ex: 12,00");
        }
        try {
            String valorFormatado = valorTexto.trim().replace(",", ".");
            double valorDouble = Double.parseDouble(valorFormatado);
            if (valorDouble <= 0) {
                throw new IllegalArgumentException("O valor do agendamento deve ser maior que zero.");
            }
            return valorDouble;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Formato de valor numérico inválido.");
        }
    }

    /**
     * Converte a data (yyyy-MM-dd, formato do input HTML type=date) para LocalDate,
     * validando que não seja anterior ao dia atual.
     */
    public LocalDate converterData(String dataTexto) throws IllegalArgumentException {
        try {
            LocalDate dataAgendada = LocalDate.parse(dataTexto.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            if (dataAgendada.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("ATENÇÃO! A data do agendamento não pode ser anterior ao dia atual.");
            }
            return dataAgendada;
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida! Use o formato yyyy-MM-dd.");
        }
    }

    private boolean isStringVazia(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
