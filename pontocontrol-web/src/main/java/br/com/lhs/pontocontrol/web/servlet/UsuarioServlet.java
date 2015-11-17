package br.com.lhs.pontocontrol.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.lhs.pontocontrol.web.vo.UsuarioVO;
import br.com.pontocontrol.controleponto.SessaoManager;
import br.com.pontocontrol.controleponto.controller.json.impl.ConfiguracoesUsuarioJSON;
import br.com.pontocontrol.controleponto.model.ConfiguracoesUsuario;

public class UsuarioServlet extends BaseServlet {

	private static final long serialVersionUID = 2369750916657671735L;

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		final ConfiguracoesUsuario usuarioAutenticado = SessaoManager.getInstance().getUsuarioAutenticado();
		if (usuarioAutenticado != null) {
			respondJson(resp, new ConfiguracoesUsuarioJSON(usuarioAutenticado));
		} else {
			respondError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Para acessar este conteúdo você deve autenticar-se primeiro.");
		}
	}

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		acceptJson(req, resp, UsuarioVO.class, content -> {
			final ConfiguracoesUsuario configuracoesUsuario = new ConfiguracoesUsuario(content.getLogin());
			configuracoesUsuario.setNome(content.getNome());
			configuracoesUsuario.setOffset(Long.valueOf(content.getOffset()));
			final boolean sucesso = SessaoManager.getInstance().criarUsuario(configuracoesUsuario);
			if (sucesso) {
				respondJson(resp, new ConfiguracoesUsuarioJSON(configuracoesUsuario));
			} else {
				respondError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ocorreu um problema inesperado para salvar o usuário informado.");
			}
		});

	}

}