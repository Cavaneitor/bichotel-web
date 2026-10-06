/* =========================================================
   BichoTel — common.js
   Comportamentos usados em todas as páginas internas
   (protegidas por login): guarda de sessão, logout, toast.
   ========================================================= */

const Sessao = (() => {
  const CHAVE = "bichotel_logado";

  function estaLogado() {
    return sessionStorage.getItem(CHAVE) === "true";
  }

  function entrar() {
    sessionStorage.setItem(CHAVE, "true");
  }

  function sair() {
    sessionStorage.removeItem(CHAVE);
    window.location.href = "login.html";
  }

  /** Chame no topo das páginas internas para redirecionar quem não logou. */
  function exigirLogin() {
    if (!estaLogado()) {
      window.location.href = "login.html";
    }
  }

  return { estaLogado, entrar, sair, exigirLogin };
})();

function exibirToast(mensagem) {
  let toast = document.querySelector(".toast");
  if (!toast) {
    toast = document.createElement("div");
    toast.className = "toast";
    document.body.appendChild(toast);
  }
  toast.textContent = mensagem;
  toast.classList.add("is-visible");
  clearTimeout(toast._timeout);
  toast._timeout = setTimeout(() => toast.classList.remove("is-visible"), 2600);
}

document.addEventListener("DOMContentLoaded", () => {
  const btnSair = document.querySelector("[data-acao='sair']");
  if (btnSair) {
    btnSair.addEventListener("click", () => Sessao.sair());
  }
});
