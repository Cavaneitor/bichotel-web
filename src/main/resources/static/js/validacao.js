/* =========================================================
   BichoTel — validacao.js
   Regras de validação usadas nas páginas de Clientes e
   Agendamentos, no front-end, antes de enviar para a API.
   O back-end (Spring) também valida — esta camada só dá
   feedback imediato ao usuário.
   ========================================================= */

const Validacao = (() => {

  const REGEX_CPF = /^\d{3}\.\d{3}\.\d{3}-\d{2}$/;
  const REGEX_TELEFONE = /^\d{4,5}-\d{4}$/;
  const REGEX_VALOR = /^\d+,\d{2}$/;

  function stringVazia(valor) {
    return valor === null || valor === undefined || valor.trim().length === 0;
  }

  function validarCpf(cpf) {
    if (stringVazia(cpf)) return "O CPF é obrigatório.";
    if (!REGEX_CPF.test(cpf.trim())) return "CPF inválido. Use o formato xxx.xxx.xxx-xx.";
    return null;
  }

  function validarTelefone(telefone) {
    if (stringVazia(telefone)) return "O telefone é obrigatório.";
    if (!REGEX_TELEFONE.test(telefone.trim())) return "Telefone inválido. Use o formato 99999-9999.";
    return null;
  }

  function validarObrigatorio(valor, rotulo) {
    if (stringVazia(valor)) return `O campo "${rotulo}" é obrigatório.`;
    return null;
  }

  /** Valida o formato de "12,00" sem converter (a conversão final é feita no back-end). */
  function validarValorTexto(valorTexto) {
    if (stringVazia(valorTexto)) return "O valor é obrigatório.";
    if (!REGEX_VALOR.test(valorTexto.trim())) return "O campo 'Valor' deve conter apenas números e uma vírgula. Ex: 12,00";
    const numero = parseFloat(valorTexto.trim().replace(",", "."));
    if (isNaN(numero) || numero <= 0) return "O valor do agendamento deve ser maior que zero.";
    return null;
  }

  /** Valida uma data (input type=date, formato yyyy-mm-dd) não anterior a hoje. */
  function validarData(dataTexto) {
    if (stringVazia(dataTexto)) return "A data é obrigatória.";
    const dataEscolhida = new Date(dataTexto + "T00:00:00");
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    if (isNaN(dataEscolhida.getTime())) return "Data inválida.";
    if (dataEscolhida < hoje) return "ATENÇÃO! A data não pode ser anterior ao dia atual.";
    return null;
  }

  /* ---------- Máscaras simples de digitação ---------- */

  function aplicarMascaraCpf(input) {
    input.addEventListener("input", () => {
      let v = input.value.replace(/\D/g, "").slice(0, 11);
      v = v.replace(/(\d{3})(\d)/, "$1.$2");
      v = v.replace(/(\d{3})(\d)/, "$1.$2");
      v = v.replace(/(\d{3})(\d{1,2})$/, "$1-$2");
      input.value = v;
    });
  }

  function aplicarMascaraTelefone(input) {
    input.addEventListener("input", () => {
      let v = input.value.replace(/\D/g, "").slice(0, 9);
      if (v.length > 4) {
        v = v.replace(/(\d{4,5})(\d{4})$/, "$1-$2");
      }
      input.value = v;
    });
  }

  function aplicarMascaraValor(input) {
    input.addEventListener("input", () => {
      let v = input.value.replace(/[^\d,]/g, "");
      const partes = v.split(",");
      if (partes.length > 2) v = partes[0] + "," + partes.slice(1).join("");
      input.value = v;
    });
  }

  /* ---------- Exibição de erro em campo ---------- */

  function marcarErro(fieldEl, mensagem) {
    fieldEl.classList.add("has-error");
    const msgEl = fieldEl.querySelector(".error-msg");
    if (msgEl) msgEl.textContent = mensagem;
  }

  function limparErro(fieldEl) {
    fieldEl.classList.remove("has-error");
    const msgEl = fieldEl.querySelector(".error-msg");
    if (msgEl) msgEl.textContent = "";
  }

  return {
    stringVazia,
    validarCpf,
    validarTelefone,
    validarObrigatorio,
    validarValorTexto,
    validarData,
    aplicarMascaraCpf,
    aplicarMascaraTelefone,
    aplicarMascaraValor,
    marcarErro,
    limparErro,
  };
})();
