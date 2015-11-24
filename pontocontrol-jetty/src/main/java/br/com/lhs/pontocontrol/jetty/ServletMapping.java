package br.com.lhs.pontocontrol.jetty;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import br.com.lhs.pontocontrol.web.servlet.AutenticacaoServlet;
import br.com.lhs.pontocontrol.web.servlet.RegistrarAgoraServlet;
import br.com.lhs.pontocontrol.web.servlet.RegistroMensalServlet;
import br.com.lhs.pontocontrol.web.servlet.UsuarioServlet;

public class ServletMapping {

	public static void config(final ServletContextHandler contextHandler) {
		contextHandler.addServlet(new ServletHolder(AutenticacaoServlet.class), "/login");
		contextHandler.addServlet(new ServletHolder(UsuarioServlet.class), "/usuario");
		contextHandler.addServlet(new ServletHolder(RegistroMensalServlet.class), "/restrito/folha/mensal");
		contextHandler.addServlet(new ServletHolder(RegistrarAgoraServlet.class), "/restrito/registrar");
	}

}