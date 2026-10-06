package br.com.bichotel.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "agendamento")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false)
    private String servico;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private double valor;

    @Column(length = 500)
    private String observacao;

    /** "agendado" ou "concluido" — mesmos valores usados no front-end. */
    @Column(nullable = false, length = 20)
    private String status = "agendado";

    public Agendamento() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
