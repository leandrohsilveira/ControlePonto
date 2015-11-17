package br.com.lhs.pontocontrol.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.pontocontrol.controleponto.SessaoManager;

public class AutenticacaoServlet extends HttpServlet {

	private static final long serialVersionUID = 3966806345787674814L;

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		final String login = req.getParameter("login");
		final int autenticar = SessaoManager.getInstance().autenticar(login);
		switch (autenticar) {
		case SessaoManager.LOGIN_STATUS.OK:
		case SessaoManager.LOGIN_STATUS.JA_AUTENTICADO:
			resp.setStatus(HttpServletResponse.SC_OK);
			break;
		case SessaoManager.LOGIN_STATUS.USUARIO_NAO_EXISTE:
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			break;
		default:
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			break;
		}
	}

}