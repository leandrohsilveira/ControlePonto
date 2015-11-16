package br.com.lhs.pontocontrol.jetty;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.jetty.deploy.DeploymentManager;
import org.eclipse.jetty.deploy.PropertiesConfigurationManager;
import org.eclipse.jetty.deploy.providers.WebAppProvider;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.LowResourceMonitor;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.webapp.Configuration;

import br.com.pontocontrol.controleponto.PathsManager;

public class ApplicationServer {

	private Server server;
	private int port;

	private ApplicationServer(int port) {
		this.port = port;
		try {
			run();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private void run() throws FileNotFoundException {
		String jettyHomeBuild = PathsManager.getInstance().projectRootPath();

		// Find jetty home and base directories
		String homePath = System.getProperty("jetty.home", jettyHomeBuild);
		File homeDir = new File(homePath);
		if (!homeDir.exists())
		{
			throw new FileNotFoundException(homeDir.getAbsolutePath());
		}
		String basePath = System.getProperty("jetty.base", homeDir + "/demo-base");
		File baseDir = new File(basePath);
		if (!baseDir.exists())
		{
			throw new FileNotFoundException(baseDir.getAbsolutePath());
		}

		// Configure jetty.home and jetty.base system properties
		String jetty_home = homeDir.getAbsolutePath();
		String jetty_base = baseDir.getAbsolutePath();
		System.setProperty("jetty.home", jetty_home);
		System.setProperty("jetty.base", jetty_base);

		// === jetty.xml ===
		// Setup Threadpool
		QueuedThreadPool threadPool = new QueuedThreadPool();
		threadPool.setMaxThreads(500);

		// Server
		server = new Server(threadPool);

		// Scheduler
		server.addBean(new ScheduledExecutorScheduler());

		// HTTP Configuration
		HttpConfiguration httpConfig = new HttpConfiguration();
		httpConfig.setSecureScheme("https");
		httpConfig.setSecurePort(8443);
		httpConfig.setOutputBufferSize(32768);
		httpConfig.setRequestHeaderSize(8192);
		httpConfig.setResponseHeaderSize(8192);
		httpConfig.setSendServerVersion(true);
		httpConfig.setSendDateHeader(false);
		httpConfig.addCustomizer(new ForwardedRequestCustomizer());

		// Handler Structure
		HandlerCollection handlers = new HandlerCollection();
		ContextHandlerCollection contexts = new ContextHandlerCollection();
		handlers.setHandlers(new Handler[] { contexts, new DefaultHandler() });
		server.setHandler(handlers);

		// Extra options
		server.setDumpAfterStart(false);
		server.setDumpBeforeStop(false);
		server.setStopAtShutdown(true);

		// === jetty-http.xml ===
		ServerConnector http = new ServerConnector(server,
				new HttpConnectionFactory(httpConfig));
		http.setPort(port);
		http.setIdleTimeout(30000);
		server.addConnector(http);

		// === jetty-https.xml ===
		// SSL Context Factory
		// SslContextFactory sslContextFactory = new SslContextFactory();
		// sslContextFactory.setKeyStorePath(jetty_home +
		// "/../../../jetty-server/src/test/config/etc/keystore");
		// sslContextFactory.setKeyStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
		// sslContextFactory.setKeyManagerPassword("OBF:1u2u1wml1z7s1z7a1wnl1u2g");
		// sslContextFactory.setTrustStorePath(jetty_home +
		// "/../../../jetty-server/src/test/config/etc/keystore");
		// sslContextFactory.setTrustStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
		// sslContextFactory.setExcludeCipherSuites("SSL_RSA_WITH_DES_CBC_SHA",
		// "SSL_DHE_RSA_WITH_DES_CBC_SHA", "SSL_DHE_DSS_WITH_DES_CBC_SHA",
		// "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
		// "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
		// "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
		// "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");

		// SSL HTTP Configuration
		HttpConfiguration httpsConfig = new HttpConfiguration(httpConfig);
		httpsConfig.addCustomizer(new SecureRequestCustomizer());

		// SSL Connector
		// ServerConnector sslConnector = new ServerConnector(server,
		// new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
		// new HttpConnectionFactory(https_config));
		// sslConnector.setPort(8443);
		// server.addConnector(sslConnector);

		// === jetty-deploy.xml ===
		DeploymentManager deployer = new DeploymentManager();
		deployer.setContexts(contexts);
		deployer.setContextAttribute(
				"org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
				".*/servlet-api-[^/]*\\.jar$");

		WebAppProvider webappProvider = new WebAppProvider();
		webappProvider.setMonitoredDirName(jetty_base + "/webapps");
		webappProvider.setDefaultsDescriptor(jetty_home + "/etc/webdefault.xml");
		webappProvider.setScanInterval(1);
		webappProvider.setExtractWars(true);
		webappProvider.setConfigurationManager(new PropertiesConfigurationManager());

		deployer.addAppProvider(webappProvider);
		server.addBean(deployer);

		// === setup jetty plus ==
		Configuration.ClassList.setServerDefault(server).addAfter(
				"org.eclipse.jetty.webapp.FragmentConfiguration",
				"org.eclipse.jetty.plus.webapp.EnvConfiguration",
				"org.eclipse.jetty.plus.webapp.PlusConfiguration");

		// === jetty-stats.xml ===
		StatisticsHandler stats = new StatisticsHandler();
		stats.setHandler(server.getHandler());
		server.setHandler(stats);

		// === jetty-requestlog.xml ===
		NCSARequestLog requestLog = new NCSARequestLog();
		requestLog.setFilename(jetty_home + "/logs/yyyy_mm_dd.request.log");
		requestLog.setFilenameDateFormat("yyyy_MM_dd");
		requestLog.setRetainDays(90);
		requestLog.setAppend(true);
		requestLog.setExtended(true);
		requestLog.setLogCookies(false);
		requestLog.setLogTimeZone("GMT");
		RequestLogHandler requestLogHandler = new RequestLogHandler();
		requestLogHandler.setRequestLog(requestLog);
		handlers.addHandler(requestLogHandler);

		// === jetty-lowresources.xml ===
		LowResourceMonitor lowResourcesMonitor = new LowResourceMonitor(server);
		lowResourcesMonitor.setPeriod(1000);
		lowResourcesMonitor.setLowResourcesIdleTimeout(200);
		lowResourcesMonitor.setMonitorThreads(true);
		lowResourcesMonitor.setMaxConnections(0);
		lowResourcesMonitor.setMaxMemory(0);
		lowResourcesMonitor.setMaxLowResourcesTime(5000);
		server.addBean(lowResourcesMonitor);

		// Start the server
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
}
