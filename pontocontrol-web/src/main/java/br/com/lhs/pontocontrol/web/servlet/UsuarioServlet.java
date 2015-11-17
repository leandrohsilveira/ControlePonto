package br.com.lhs.pontocontrol.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

}