package br.com.bichotel.dto;

import br.com.bichotel.model.Agendamento;

/** Formato em que a API devolve um agendamento, já com os dados do cliente "achatados". */
public class AgendamentoResponse {

    private Long id;
    private Long clienteId;
    private String clienteTutor;
    private String clienteAnimal;
    private String servico;
    private String data;
    private double valor;
    private String observacao;
    private String status;

    public static AgendamentoResponse from(Agendamento agendamento) {
        AgendamentoResponse dto = new AgendamentoResponse();
        dto.id = agendamento.getId();
        dto.clienteId = agendamento.getCliente().getId();
        dto.clienteTutor = agendamento.getCliente().getTutor();
        dto.clienteAnimal = agendamento.getCliente().getAnimal();
        dto.servico = agendamento.getServico();
        dto.data = agendamento.getData().toString(); // yyyy-MM-dd
        dto.valor = agendamento.getValor();
        dto.observacao = agendamento.getObservacao();
        dto.status = agendamento.getStatus();
        return dto;
    }

    public Long getId() { return id; }
    public Long getClienteId() { return clienteId; }
    public String getClienteTutor() { return clienteTutor; }
    public String getClienteAnimal() { return clienteAnimal; }
    public String getServico() { return servico; }
    public String getData() { return data; }
    public double getValor() { return valor; }
    public String getObservacao() { return observacao; }
    public String getStatus() { return status; }
}
