package br.com.lhs.pontocontrol.jetty;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
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

				final HandlerList serverContextHandlersList = new HandlerList();

				final ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
				servletContextHandler.setContextPath("/pontocontrol/controller");
				ServletMapping.config(servletContextHandler);

				final ContextHandler resourcesContextHandler = new ContextHandler("/pontocontrol/view");
				final DefaultHandler defaultHandler = new DefaultHandler();
				final ResourceHandler resourceHandler = new ResourceHandler();
				final GzipHandler gzipHandler = new GzipHandler();
				final HandlerList gzipHandlersList = new HandlerList();
				resourceHandler.setBaseResource(Resource.newResource(WebMainClass.class.getClassLoader().getResource("webapp").toURI()));
				resourceHandler.setDirectoriesListed(true);
				resourceHandler.setWelcomeFiles(new String[] { "ponto-control-fx.html" });
				gzipHandlersList.setHandlers(new Handler[] { resourceHandler, defaultHandler });
				gzipHandler.setHandler(gzipHandlersList);
				resourcesContextHandler.setHandler(gzipHandler);

				serverContextHandlersList.setHandlers(new Handler[] { servletContextHandler, resourcesContextHandler });

				server.setHandler(serverContextHandlersList);
				server.start();
				server.dumpStdErr();
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