/* =========================================================
   BichoTel — login.js
   Validação de campos no front-end + autenticação via API
   REST (POST /api/auth/login), que consulta o banco de dados.
   ========================================================= */

document.addEventListener("DOMContentLoaded", () => {
  if (Sessao.estaLogado()) {
    window.location.href = "clientes.html";
    return;
  }

  const form = document.getElementById("formLogin");
  const alertBox = document.getElementById("loginAlert");
  const fieldUsuario = document.getElementById("fieldUsuario");
  const fieldSenha = document.getElementById("fieldSenha");
  const inputUsuario = document.getElementById("usuario");
  const inputSenha = document.getElementById("senha");
  const botaoEntrar = form.querySelector("button[type='submit']");

  function mostrarAlerta(mensagem) {
    alertBox.textContent = mensagem;
    alertBox.classList.add("is-visible");
  }

  function esconderAlerta() {
    alertBox.classList.remove("is-visible");
  }

  form.addEventListener("submit", async (evento) => {
    evento.preventDefault();
    esconderAlerta();
    Validacao.limparErro(fieldUsuario);
    Validacao.limparErro(fieldSenha);

    const usuario = inputUsuario.value.trim();
    const senha = inputSenha.value;

    let temErro = false;
    const erroUsuario = Validacao.validarObrigatorio(usuario, "Usuário");
    if (erroUsuario) { Validacao.marcarErro(fieldUsuario, erroUsuario); temErro = true; }

    const erroSenha = Validacao.validarObrigatorio(senha, "Senha");
    if (erroSenha) { Validacao.marcarErro(fieldSenha, erroSenha); temErro = true; }

    if (temErro) return;

    botaoEntrar.disabled = true;
    botaoEntrar.textContent = "Entrando...";

    try {
      const resultado = await Dados.login(usuario, senha);
      if (resultado && resultado.autenticado) {
        Sessao.entrar();
        window.location.href = "clientes.html";
      } else {
        mostrarAlerta("Usuário inválido ou senha incorreta.");
      }
    } catch (erro) {
      mostrarAlerta(erro.message || "Usuário inválido ou senha incorreta.");
    } finally {
      botaoEntrar.disabled = false;
      botaoEntrar.textContent = "Entrar";
    }
  });
});
