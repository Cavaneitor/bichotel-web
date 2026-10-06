package br.com.bichotel.dto;

/** Formato em que o front-end envia os dados de um agendamento. */
public class AgendamentoRequest {

    private Long clienteId;
    private String servico;
    private String data;     // yyyy-MM-dd (formato do <input type="date">)
    private String valor;    // texto no formato "12,00", validado no service
    private String observacao;
    private String status;

    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
