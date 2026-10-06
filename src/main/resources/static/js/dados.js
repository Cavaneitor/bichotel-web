/* =========================================================
   BichoTel — dados.js
   Cliente da API REST (Spring Boot) via fetch. Substitui a
   versão anterior baseada em localStorage: agora os dados
   vêm do banco de dados, através do back-end.
   ========================================================= */

const Dados = (() => {

  const BASE = "/api";

  async function tratarResposta(resp) {
    if (resp.status === 204) return null;
    const corpo = await resp.json().catch(() => null);
    if (!resp.ok) {
      const mensagem = (corpo && corpo.erro) ? corpo.erro : "Erro inesperado ao comunicar com o servidor.";
      throw new Error(mensagem);
    }
    return corpo;
  }

  /* ---------- Clientes ---------- */

  async function listarClientes(filtroTutor) {
    const url = filtroTutor ? `${BASE}/clientes?tutor=${encodeURIComponent(filtroTutor)}` : `${BASE}/clientes`;
    const resp = await fetch(url);
    return tratarResposta(resp);
  }

  async function salvarCliente(cliente) {
    const metodo = cliente.id ? "PUT" : "POST";
    const url = cliente.id ? `${BASE}/clientes/${cliente.id}` : `${BASE}/clientes`;
    const resp = await fetch(url, {
      method: metodo,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(cliente),
    });
    return tratarResposta(resp);
  }

  async function excluirCliente(id) {
    const resp = await fetch(`${BASE}/clientes/${id}`, { method: "DELETE" });
    return tratarResposta(resp);
  }

  /* ---------- Agendamentos ---------- */

  async function listarAgendamentos(status) {
    const url = status && status !== "todos" ? `${BASE}/agendamentos?status=${encodeURIComponent(status)}` : `${BASE}/agendamentos`;
    const resp = await fetch(url);
    return tratarResposta(resp);
  }

  async function salvarAgendamento(agendamento) {
    const metodo = agendamento.id ? "PUT" : "POST";
    const url = agendamento.id ? `${BASE}/agendamentos/${agendamento.id}` : `${BASE}/agendamentos`;
    const resp = await fetch(url, {
      method: metodo,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(agendamento),
    });
    return tratarResposta(resp);
  }

  async function concluirAgendamento(id) {
    const resp = await fetch(`${BASE}/agendamentos/${id}/concluir`, { method: "PATCH" });
    return tratarResposta(resp);
  }

  async function excluirAgendamento(id) {
    const resp = await fetch(`${BASE}/agendamentos/${id}`, { method: "DELETE" });
    return tratarResposta(resp);
  }

  /* ---------- Autenticação ---------- */

  async function login(usuario, senha) {
    const resp = await fetch(`${BASE}/auth/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ usuario, senha }),
    });
    return tratarResposta(resp);
  }

  return {
    listarClientes, salvarCliente, excluirCliente,
    listarAgendamentos, salvarAgendamento, concluirAgendamento, excluirAgendamento,
    login,
  };
})();
