package br.com.bichotel.service;

import br.com.bichotel.exception.RecursoNaoEncontradoException;
import br.com.bichotel.model.Cliente;
import br.com.bichotel.repository.AgendamentoRepository;
import br.com.bichotel.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final AgendamentoRepository agendamentoRepository;

    @Autowired
    public ClienteService(ClienteRepository clienteRepository, AgendamentoRepository agendamentoRepository) {
        this.clienteRepository = clienteRepository;
        this.agendamentoRepository = agendamentoRepository;
    }

    public List<Cliente> listar(String tutor) {
        if (tutor == null || tutor.isBlank()) {
            return clienteRepository.findAll();
        }
        return clienteRepository.findByTutorContainingIgnoreCase(tutor);
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado."));
    }

    public Cliente cadastrar(Cliente cliente) {
        cliente.setId(null);
        validarCampos(cliente);
        return clienteRepository.save(cliente);
    }

    public Cliente atualizar(Long id, Cliente dados) {
        Cliente existente = buscarPorId(id);
        validarCampos(dados);

        existente.setTutor(dados.getTutor());
        existente.setCpf(dados.getCpf());
        existente.setEndereco(dados.getEndereco());
        existente.setTelefone(dados.getTelefone());
        existente.setAnimal(dados.getAnimal());
        existente.setRaca(dados.getRaca());

        return clienteRepository.save(existente);
    }

    public void excluir(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Cliente não encontrado.");
        }
        // Correção do bug #01: antes de excluir, verifica se existem agendamentos
        // vinculados a este cliente. Sem essa checagem, o banco rejeitava a exclusão
        // por causa da chave estrangeira, e o erro bruto do SQL vazava para a tela.
        if (agendamentoRepository.existsByClienteId(id)) {
            throw new IllegalArgumentException(
                "Não é possível excluir: existem agendamentos vinculados a este cliente. " +
                "Exclua ou reatribua os agendamentos antes de excluir o cliente.");
        }
        clienteRepository.deleteById(id);
    }

    /**
     * Mesma regra de negócio do ClienteService do projeto desktop:
     * campos obrigatórios, CPF no formato xxx.xxx.xxx-xx e telefone no formato 99999-9999.
     */
    public void validarCampos(Cliente cliente) throws IllegalArgumentException {
        if (isStringVazia(cliente.getTutor()) ||
            isStringVazia(cliente.getCpf()) ||
            isStringVazia(cliente.getEndereco()) ||
            isStringVazia(cliente.getTelefone()) ||
            isStringVazia(cliente.getAnimal()) ||
            isStringVazia(cliente.getRaca())) {

            throw new IllegalArgumentException("Todos os campos devem ser preenchidos.");
        }
        if (!cliente.getCpf().matches("[0-9]{3}[.][0-9]{3}[.][0-9]{3}[-][0-9]{2}")) {
            throw new IllegalArgumentException("ATENÇÃO! CPF inválido. Use o formato xxx.xxx.xxx-xx.");
        }
        if (!cliente.getTelefone().matches("[0-9]{4,5}[-][0-9]{4}")) {
            throw new IllegalArgumentException("ATENÇÃO! Telefone inválido. Use o formato 99999-9999.");
        }
    }

    private boolean isStringVazia(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
