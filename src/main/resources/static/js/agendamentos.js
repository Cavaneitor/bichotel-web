/* =========================================================
   BichoTel — agendamentos.js
   Listagem, filtro por status, cadastro/edição e exclusão de
   agendamentos, agora via API REST (Spring Boot + banco de dados).
   ========================================================= */

document.addEventListener("DOMContentLoaded", () => {
  Sessao.exigirLogin();

  const corpoTabela = document.getElementById("corpoTabelaAgendamentos");
  const estadoVazio = document.getElementById("estadoVazioAgendamentos");
  const pillsFiltro = document.querySelectorAll(".filter-pill");

  const modal = document.getElementById("modalAgendamento");
  const modalTitulo = document.getElementById("modalAgendamentoTitulo");
  const form = document.getElementById("formAgendamento");
  const btnNovo = document.getElementById("btnNovoAgendamento");
  const btnSalvar = form.querySelector("button[type='submit']");
  const selectCliente = document.getElementById("clienteSelect");

  const campos = {
    id: document.getElementById("agendamentoId"),
    cliente: selectCliente,
    servico: document.getElementById("servico"),
    data: document.getElementById("data"),
    valor: document.getElementById("valor"),
    observacao: document.getElementById("observacao"),
  };

  Validacao.aplicarMascaraValor(campos.valor);

  let filtroAtual = "todos";
  let cacheAgendamentos = [];

  function escapeHtml(texto) {
    const div = document.createElement("div");
    div.textContent = texto ?? "";
    return div.innerHTML;
  }

  function formatarData(isoData) {
    const [ano, mes, dia] = isoData.split("-");
    return `${dia}/${mes}/${ano}`;
  }

  function formatarValor(numero) {
    return Number(numero).toLocaleString("pt-BR", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  }

  /* ---------- Popular select de clientes ---------- */

  async function popularSelectClientes(selecionadoId = null) {
    let clientes = [];
    try {
      clientes = await Dados.listarClientes();
    } catch (erro) {
      exibirToast(erro.message || "Erro ao carregar clientes.");
    }
    selectCliente.innerHTML = '<option value="">Selecione um cliente...</option>' +
      clientes.map(c => `<option value="${c.id}">${escapeHtml(c.tutor)} — ${escapeHtml(c.animal)}</option>`).join("");
    if (selecionadoId) selectCliente.value = selecionadoId;
    return clientes;
  }

  /* ---------- Renderização da tabela ---------- */

  async function renderizar() {
    try {
      const statusFiltro = filtroAtual === "todos" ? null : filtroAtual;
      cacheAgendamentos = await Dados.listarAgendamentos(statusFiltro);
    } catch (erro) {
      exibirToast(erro.message || "Erro ao carregar agendamentos.");
      return;
    }

    corpoTabela.innerHTML = "";
    estadoVazio.hidden = cacheAgendamentos.length > 0;

    cacheAgendamentos.forEach(agenda => {
      const nomeCliente = `${agenda.clienteTutor} · ${agenda.clienteAnimal}`;
      const badgeClasse = agenda.status === "concluido" ? "badge-concluido" : "badge-agendado";
      const badgeTexto = agenda.status === "concluido" ? "Concluído" : "Agendado";

      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${formatarData(agenda.data)}</td>
        <td>${escapeHtml(agenda.servico)}</td>
        <td>${escapeHtml(nomeCliente)}</td>
        <td>R$ ${formatarValor(agenda.valor)}</td>
        <td><span class="badge ${badgeClasse}">${badgeTexto}</span></td>
        <td class="col-actions">
          ${agenda.status !== "concluido" ? `<button class="btn btn-ghost" data-concluir="${agenda.id}" type="button">Concluir</button>` : ""}
          <button class="btn btn-ghost" data-editar="${agenda.id}" type="button">Editar</button>
          <button class="btn-danger-text" data-excluir="${agenda.id}" type="button">Excluir</button>
        </td>
      `;
      corpoTabela.appendChild(tr);
    });
  }

  /* ---------- Filtro por status ---------- */

  pillsFiltro.forEach(pill => {
    pill.addEventListener("click", () => {
      pillsFiltro.forEach(p => p.classList.remove("is-active"));
      pill.classList.add("is-active");
      filtroAtual = pill.dataset.filtro;
      renderizar();
    });
  });

  /* ---------- Modal ---------- */

  async function abrirModal(agendamento = null) {
    form.reset();
    document.querySelectorAll("#formAgendamento .field").forEach(f => Validacao.limparErro(f));
    await popularSelectClientes(agendamento ? agendamento.clienteId : null);

    if (agendamento) {
      modalTitulo.textContent = "Editar agendamento";
      campos.id.value = agendamento.id;
      campos.servico.value = agendamento.servico;
      campos.data.value = agendamento.data;
      campos.valor.value = formatarValor(agendamento.valor);
      campos.observacao.value = agendamento.observacao;
    } else {
      modalTitulo.textContent = "Novo agendamento";
      campos.id.value = "";
    }
    modal.classList.add("is-open");
  }

  function fecharModal() {
    modal.classList.remove("is-open");
  }

  btnNovo.addEventListener("click", async () => {
    const clientes = await Dados.listarClientes().catch(() => []);
    if (clientes.length === 0) {
      exibirToast("Cadastre um cliente antes de criar um agendamento.");
      return;
    }
    abrirModal();
  });

  modal.querySelectorAll("[data-fechar-modal]").forEach(el => el.addEventListener("click", fecharModal));
  modal.addEventListener("click", (evento) => { if (evento.target === modal) fecharModal(); });
  document.addEventListener("keydown", (evento) => {
    if (evento.key === "Escape" && modal.classList.contains("is-open")) fecharModal();
  });

  /* ---------- Ações da tabela ---------- */

  corpoTabela.addEventListener("click", async (evento) => {
    const btnEditar = evento.target.closest("[data-editar]");
    const btnExcluir = evento.target.closest("[data-excluir]");
    const btnConcluir = evento.target.closest("[data-concluir]");

    if (btnEditar) {
      const agenda = cacheAgendamentos.find(a => a.id === Number(btnEditar.dataset.editar));
      if (agenda) abrirModal(agenda);
    }

    if (btnExcluir) {
      const id = Number(btnExcluir.dataset.excluir);
      if (confirm("Excluir este agendamento?")) {
        try {
          await Dados.excluirAgendamento(id);
          await renderizar();
          exibirToast("Agendamento excluído.");
        } catch (erro) {
          exibirToast(erro.message || "Erro ao excluir agendamento.");
        }
      }
    }

    if (btnConcluir) {
      const id = Number(btnConcluir.dataset.concluir);
      try {
        await Dados.concluirAgendamento(id);
        await renderizar();
        exibirToast("Agendamento marcado como concluído.");
      } catch (erro) {
        exibirToast(erro.message || "Erro ao concluir agendamento.");
      }
    }
  });

  /* ---------- Envio do formulário (validação local + salvar via API) ---------- */

  form.addEventListener("submit", async (evento) => {
    evento.preventDefault();

    let temErro = false;
    const marcar = (input, mensagem) => {
      const field = input.closest(".field");
      if (mensagem) { Validacao.marcarErro(field, mensagem); temErro = true; }
      else { Validacao.limparErro(field); }
    };

    marcar(campos.cliente, campos.cliente.value ? null : "Selecione um cliente.");
    marcar(campos.servico, Validacao.validarObrigatorio(campos.servico.value, "Serviço"));
    marcar(campos.data, Validacao.validarData(campos.data.value));
    marcar(campos.observacao, Validacao.validarObrigatorio(campos.observacao.value, "Observação"));
    marcar(campos.valor, Validacao.validarValorTexto(campos.valor.value));

    if (temErro) return;

    const agendamento = {
      id: campos.id.value ? Number(campos.id.value) : undefined,
      clienteId: Number(campos.cliente.value),
      servico: campos.servico.value.trim(),
      data: campos.data.value,
      valor: campos.valor.value.trim(),
      observacao: campos.observacao.value.trim(),
    };

    btnSalvar.disabled = true;
    try {
      await Dados.salvarAgendamento(agendamento);
      fecharModal();
      await renderizar();
      exibirToast("Agendamento salvo com sucesso.");
    } catch (erro) {
      exibirToast(erro.message || "Erro ao salvar agendamento.");
    } finally {
      btnSalvar.disabled = false;
    }
  });

  renderizar();
});
