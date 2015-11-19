package br.com.lhs.pontocontrol.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import br.com.pontocontrol.controleponto.SessaoManager;
import br.com.pontocontrol.controleponto.controller.json.impl.ConfiguracoesUsuarioJSON;
import br.com.pontocontrol.controleponto.model.ConfiguracoesUsuario;

public class AutenticacaoServlet extends BaseServlet {

	private static final long serialVersionUID = 3966806345787674814L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final Boolean autenticado = SessaoManager.getInstance().isUsuarioAutenticado();
		final JsonObject jsonObject = new JsonObject();
		jsonObject.add("autenticado", new JsonPrimitive(autenticado));
		respondJson(resp, jsonObject);
	}

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		accept(MEDIA_TYPE.X_WWW_FORM_URLENCODED, req, resp, () -> {
			final String login = req.getParameter("login");
			final int autenticar = SessaoManager.getInstance().autenticar(login);
			switch (autenticar) {
			case SessaoManager.LOGIN_STATUS.OK:
			case SessaoManager.LOGIN_STATUS.JA_AUTENTICADO:
				final ConfiguracoesUsuario usuarioAutenticado = SessaoManager.getInstance().getUsuarioAutenticado();
				respondJson(resp, new ConfiguracoesUsuarioJSON(usuarioAutenticado));
				break;
			case SessaoManager.LOGIN_STATUS.USUARIO_NAO_EXISTE:
				respondError(resp, HttpServletResponse.SC_NOT_FOUND, "O usuário informado não existe!");
				break;
			default:
				respondError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado ao processar a resposta.");
				break;
			}
		});
	}

}