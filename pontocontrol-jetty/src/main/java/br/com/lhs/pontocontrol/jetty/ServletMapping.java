package br.com.lhs.pontocontrol.jetty;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.servlet.ServletContextHandler;

import br.com.pontocontrol.controleponto.SessaoManager;
import br.com.pontocontrol.controleponto.controller.ControllerFactory;
import br.com.pontocontrol.controleponto.controller.IFolhaPontoController;
import br.com.pontocontrol.controleponto.controller.json.impl.ConfiguracoesUsuarioJSON;
import br.com.pontocontrol.controleponto.controller.json.impl.FolhaMensalPontoJSON;
import br.com.pontocontrol.controleponto.model.ConfiguracoesUsuario;
import br.com.pontocontrol.controleponto.model.FolhaMensalPonto;

public class ServletMapping {

	public static void config(ServletContextHandler context) {
		context.addServlet(POSTAutenticar.class, "/autenticar");
		context.addServlet(GETUsuarioAutenticado.class, "/usuario");
		context.addServlet(GETRegistroMensal.class, "/folha/mensal");
	}

}

class GETRegistroMensal extends HttpServlet {

	private static final long serialVersionUID = -6357457030505936418L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final Integer mes = Integer.valueOf(req.getParameter("mes"));
		final Integer ano = Integer.valueOf(req.getParameter("ano"));
		final IFolhaPontoController folhaPontoController = ControllerFactory.localizar(IFolhaPontoController.class);
		final FolhaMensalPontoJSON mensalPontoJSON = folhaPontoController.recuperarFolhaMensal(ano, mes);
		if (mensalPontoJSON != null) {
			resp.getWriter().print(mensalPontoJSON.toJSON());
		} else {
			resp.getWriter().print(new FolhaMensalPontoJSON(new FolhaMensalPonto()).toJSON());
		}
		resp.setContentType(MimeTypes.Type.APPLICATION_JSON_UTF_8.asString());
		resp.setStatus(HttpServletResponse.SC_OK);
	}

}

class GETUsuarioAutenticado extends HttpServlet {

	private static final long serialVersionUID = 2369750916657671735L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final ConfiguracoesUsuario usuarioAutenticado = SessaoManager.getInstance().getUsuarioAutenticado();
		if (usuarioAutenticado != null) {
			final ConfiguracoesUsuarioJSON configuracoesUsuarioJSON = new ConfiguracoesUsuarioJSON(usuarioAutenticado);
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType(MimeTypes.Type.APPLICATION_JSON_UTF_8.asString());
			resp.getWriter().print(configuracoesUsuarioJSON.toJSON());
			return;
		}
		resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

}

class POSTAutenticar extends HttpServlet {

	private static final long serialVersionUID = 3966806345787674814L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
