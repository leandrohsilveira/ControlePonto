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
			respondNoContent(resp);
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
				respondError(resp, HttpServletResponse.SC_CONFLICT, "O usuário informado já existe!");
			}
		});

	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		acceptJson(req, resp, UsuarioVO.class, content -> {
			if (SessaoManager.getInstance().isUsuarioAutenticado()) {
				final ConfiguracoesUsuario configuracoesUsuario = SessaoManager.getInstance().getUsuarioAutenticado();
				configuracoesUsuario.setNome(content.getNome());
				configuracoesUsuario.setOffset(Long.valueOf(content.getOffset()));
				SessaoManager.getInstance().atualizarUsuario(configuracoesUsuario);
				respondNoContent(resp);
			} else {
				respondError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Você deve estar autenticado para atualizar seu usuário.");
			}

		});
	}

}