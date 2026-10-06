/* =========================================================
   BichoTel — clientes.js
   Listagem, busca, cadastro/edição e exclusão de clientes,
   agora via API REST (Spring Boot + banco de dados).
   ========================================================= */

document.addEventListener("DOMContentLoaded", () => {
  Sessao.exigirLogin();

  const corpoTabela = document.getElementById("corpoTabelaClientes");
  const estadoVazio = document.getElementById("estadoVazioClientes");
  const inputBusca = document.getElementById("buscaTutor");

  const modal = document.getElementById("modalCliente");
  const modalTitulo = document.getElementById("modalClienteTitulo");
  const form = document.getElementById("formCliente");
  const btnNovo = document.getElementById("btnNovoCliente");
  const btnSalvar = form.querySelector("button[type='submit']");

  const campos = {
    id: document.getElementById("clienteId"),
    tutor: document.getElementById("tutor"),
    cpf: document.getElementById("cpf"),
    endereco: document.getElementById("endereco"),
    telefone: document.getElementById("telefone"),
    animal: document.getElementById("animal"),
    raca: document.getElementById("raca"),
  };

  Validacao.aplicarMascaraCpf(campos.cpf);
  Validacao.aplicarMascaraTelefone(campos.telefone);

  let cacheClientes = [];

  function escapeHtml(texto) {
    const div = document.createElement("div");
    div.textContent = texto ?? "";
    return div.innerHTML;
  }

  /* ---------- Renderização da tabela ---------- */

  async function renderizar(filtroTutor = "") {
    try {
      cacheClientes = await Dados.listarClientes(filtroTutor);
    } catch (erro) {
      exibirToast(erro.message || "Erro ao carregar clientes.");
      return;
    }

    corpoTabela.innerHTML = "";
    estadoVazio.hidden = cacheClientes.length > 0;

    cacheClientes.forEach(cliente => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>${escapeHtml(cliente.tutor)}</td>
        <td>${escapeHtml(cliente.cpf)}</td>
        <td>${escapeHtml(cliente.telefone)}</td>
        <td>${escapeHtml(cliente.animal)} <span style="color:var(--color-muted)">· ${escapeHtml(cliente.raca)}</span></td>
        <td class="col-actions">
          <button class="btn btn-ghost" data-editar="${cliente.id}" type="button">Editar</button>
          <button class="btn-danger-text" data-excluir="${cliente.id}" type="button">Excluir</button>
        </td>
      `;
      corpoTabela.appendChild(tr);
    });
  }

  /* ---------- Busca dinâmica (com pequeno debounce) ---------- */

  let debounceBusca;
  inputBusca.addEventListener("input", () => {
    clearTimeout(debounceBusca);
    debounceBusca = setTimeout(() => renderizar(inputBusca.value), 300);
  });

  /* ---------- Modal ---------- */

  function abrirModal(cliente = null) {
    form.reset();
    document.querySelectorAll("#formCliente .field").forEach(f => Validacao.limparErro(f));

    if (cliente) {
      modalTitulo.textContent = "Editar cliente";
      campos.id.value = cliente.id;
      campos.tutor.value = cliente.tutor;
      campos.cpf.value = cliente.cpf;
      campos.endereco.value = cliente.endereco;
      campos.telefone.value = cliente.telefone;
      campos.animal.value = cliente.animal;
      campos.raca.value = cliente.raca;
    } else {
      modalTitulo.textContent = "Novo cliente";
      campos.id.value = "";
    }
    modal.classList.add("is-open");
    campos.tutor.focus();
  }

  function fecharModal() {
    modal.classList.remove("is-open");
  }

  btnNovo.addEventListener("click", () => abrirModal());
  modal.querySelectorAll("[data-fechar-modal]").forEach(el => el.addEventListener("click", fecharModal));
  modal.addEventListener("click", (evento) => { if (evento.target === modal) fecharModal(); });
  document.addEventListener("keydown", (evento) => {
    if (evento.key === "Escape" && modal.classList.contains("is-open")) fecharModal();
  });

  /* ---------- Ações da tabela (editar / excluir) ---------- */

  corpoTabela.addEventListener("click", async (evento) => {
    const btnEditar = evento.target.closest("[data-editar]");
    const btnExcluir = evento.target.closest("[data-excluir]");

    if (btnEditar) {
      const cliente = cacheClientes.find(c => c.id === Number(btnEditar.dataset.editar));
      if (cliente) abrirModal(cliente);
    }

    if (btnExcluir) {
      const id = Number(btnExcluir.dataset.excluir);
      if (confirm("Excluir este cliente?")) {
        try {
          await Dados.excluirCliente(id);
          await renderizar(inputBusca.value);
          exibirToast("Cliente excluído.");
        } catch (erro) {
          exibirToast(erro.message || "Erro ao excluir cliente.");
        }
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

    marcar(campos.tutor, Validacao.validarObrigatorio(campos.tutor.value, "Tutor"));
    marcar(campos.cpf, Validacao.validarCpf(campos.cpf.value));
    marcar(campos.endereco, Validacao.validarObrigatorio(campos.endereco.value, "Endereço"));
    marcar(campos.telefone, Validacao.validarTelefone(campos.telefone.value));
    marcar(campos.animal, Validacao.validarObrigatorio(campos.animal.value, "Animal"));
    marcar(campos.raca, Validacao.validarObrigatorio(campos.raca.value, "Raça"));

    if (temErro) return;

    const cliente = {
      id: campos.id.value ? Number(campos.id.value) : undefined,
      tutor: campos.tutor.value.trim(),
      cpf: campos.cpf.value.trim(),
      endereco: campos.endereco.value.trim(),
      telefone: campos.telefone.value.trim(),
      animal: campos.animal.value.trim(),
      raca: campos.raca.value.trim(),
    };

    btnSalvar.disabled = true;
    try {
      await Dados.salvarCliente(cliente);
      fecharModal();
      await renderizar(inputBusca.value);
      exibirToast("Cliente salvo com sucesso.");
    } catch (erro) {
      // erro de validação vindo do back-end (ex: CPF inválido não pego no front)
      exibirToast(erro.message || "Erro ao salvar cliente.");
    } finally {
      btnSalvar.disabled = false;
    }
  });

  renderizar();
});
