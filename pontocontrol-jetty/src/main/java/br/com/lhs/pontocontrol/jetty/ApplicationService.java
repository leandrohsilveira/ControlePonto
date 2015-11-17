package br.com.lhs.pontocontrol.jetty;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.resource.Resource;

import br.com.lhs.pontocontrol.web.WebMainClass;
import br.com.pontocontrol.controleponto.ApplicationFactory;

public class ApplicationService {

	private static final Logger logger = Logger.getLogger(ApplicationService.class.getName());

	private final int port;
	private Server server;
	private final Class<?> mainClass;

	public ApplicationService(final int port, final Class<?> mainClass) {
		this.port = port;
		this.mainClass = mainClass;
	}

	public void run() {
		ApplicationFactory.runApplication(mainClass);
		new Thread(() -> {
			try {
				server = new Server();

				final ServerConnector http = new ServerConnector(server);
				http.setHost("localhost");
				http.setPort(port);
				http.setIdleTimeout(30000);
				server.addConnector(http);

				final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
				context.setContextPath("/ponto-control-fx");

				final ResourceHandler resourceHandler = new ResourceHandler();
				Resource baseResource = Resource.newResource(WebMainClass.class.getClassLoader().getResource("webapp").toURI());
				resourceHandler.setBaseResource(baseResource);
				resourceHandler.setDirectoriesListed(true);
				resourceHandler.setWelcomeFiles(new String[] { "ponto-control-fx.html" });
				final HandlerList handlers = new HandlerList();
				final GzipHandler gzip = new GzipHandler();
				handlers.setHandlers(new Handler[] { resourceHandler, new DefaultHandler() });
				gzip.setHandler(handlers);
				context.setHandler(gzip);
				server.setHandler(context);
				ServletMapping.config(context);
				server.start();
				server.join();
			} catch (final InterruptedException e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
				System.exit(-1);
			} catch (final Exception e) {
				logger.log(Level.SEVERE, e.getMessage(), e);
				System.exit(-1);
			}
		}).start();

	}

	public void stop() {
		try {
			server.stop();
		} catch (final Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			System.exit(-1);
		}
	}

}