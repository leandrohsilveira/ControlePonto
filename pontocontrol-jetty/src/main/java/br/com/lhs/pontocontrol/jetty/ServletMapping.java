package br.com.lhs.pontocontrol.jetty;

import org.eclipse.jetty.servlet.ServletContextHandler;

import br.com.lhs.pontocontrol.web.servlet.AutenticacaoServlet;
import br.com.lhs.pontocontrol.web.servlet.RegistroMensalServlet;
import br.com.lhs.pontocontrol.web.servlet.UsuarioServlet;

public class ServletMapping {

	public static void config(final ServletContextHandler context) {
		context.addServlet(AutenticacaoServlet.class, "/autenticar");
		context.addServlet(UsuarioServlet.class, "/usuario");
		context.addServlet(RegistroMensalServlet.class, "/folha/mensal");
	}

}
