package br.com.lhs.pontocontrol.web.servlet;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import br.com.lhs.pontocontrol.web.util.JsonParser;
import br.com.pontocontrol.controleponto.SessaoManager;
import br.com.pontocontrol.controleponto.controller.json.impl.ConfiguracoesUsuarioJSON;
import br.com.pontocontrol.controleponto.model.ConfiguracoesUsuario;

public class UsuarioServlet extends HttpServlet {

	private static final long serialVersionUID = 2369750916657671735L;

	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		final ConfiguracoesUsuario usuarioAutenticado = SessaoManager.getInstance().getUsuarioAutenticado();
		if (usuarioAutenticado != null) {
			final ConfiguracoesUsuarioJSON configuracoesUsuarioJSON = new ConfiguracoesUsuarioJSON(usuarioAutenticado);
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType("application/json");
			resp.getWriter().print(configuracoesUsuarioJSON.toJSON());
			return;
		}
		resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		final String requestContent = IOUtils.toString(req.getReader());
		if (StringUtils.isNotBlank(requestContent)) {
			final String contentType = req.getContentType();
			if ("application/json".equalsIgnoreCase(contentType)) {
				final UsuarioVO usuario = JsonParser.parse(requestContent, UsuarioVO.class);
				final ConfiguracoesUsuario configuracoesUsuario = new ConfiguracoesUsuario(usuario.getLogin());
				configuracoesUsuario.setNome(usuario.getNome());
				configuracoesUsuario.setOffset(Long.valueOf(usuario.getOffset()));
				final boolean sucesso = SessaoManager.getInstance().criarUsuario(configuracoesUsuario);
				if (sucesso) {
					resp.setStatus(HttpServletResponse.SC_OK);
				} else {
					resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ocorreu um problema inesperado para salvar o usuário informado.");
				}
			} else {
				resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "O ContentType da requisição para este serviço deve ser \"application/json\".");
			}
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Esta requisição deve conter um conteúdo JSON para cadastrar um novo usuário.");
		}
	}

	private class UsuarioVO implements Serializable {

		/**
		 *
		 */
		private static final long serialVersionUID = -72339803418658716L;

		private String nome;

		private String login;

		private String offset;

		public String getNome() {
			return nome;
		}

		public void setNome(final String nome) {
			this.nome = nome;
		}

		public String getLogin() {
			return login;
		}

		public void setLogin(final String login) {
			this.login = login;
		}

		public String getOffset() {
			return offset;
		}

		public void setOffset(final String offset) {
			this.offset = offset;
		}

	}

}