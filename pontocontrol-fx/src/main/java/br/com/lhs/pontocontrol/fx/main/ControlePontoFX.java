package br.com.lhs.pontocontrol.fx.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import br.com.pontocontrol.controleponto.ApplicationFactory;
import br.com.pontocontrol.controleponto.PathsManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ControlePontoFX extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		ApplicationFactory.runApplication(ControlePontoFX.class);
		new ApplicationServer(8441).run();

		final String projectRoot = PathsManager.getInstance().projectRootPath();
		final URL sceneUrl = PathsManager.getInstance().getFileResource(projectRoot, "/fxml/Scene.fxml").toURI().toURL();
		final String cssPath = PathsManager.getInstance().getFileResource(projectRoot, "/styles/Styles.css").toURI().getPath();

		final Parent root = FXMLLoader.load(sceneUrl);

		final Scene scene = new Scene(root);

		scene.getStylesheets().add("file:///" + cssPath);

		stage.setTitle("Controle Ponto [PontoControl FX]");
		stage.setScene(scene);
		stage.show();
	}

	/**
	 * The main() method is ignored in correctly deployed JavaFX application. main() serves only as fallback in case the application can not be launched through deployment artifacts, e.g., in IDEs with limited FX support. NetBeans ignores main().
	 *
	 * @param args
	 *           the command line arguments
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}

}

class ApplicationServer {

	private int port;
	private ServerSocket serverSocket;

	public ApplicationServer(int port) {
		super();
		this.port = port;
	}

	public void run() {
		if (serverSocket == null) {
			new Thread(() -> {
				final boolean execute = true;
				try {
					serverSocket = new ServerSocket(port);
					final ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 10, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
					while (execute) {
						final Socket client = serverSocket.accept();
						executor.execute(() -> {
							try {
								final BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
								final String line = in.readLine();
								if (StringUtils.isNotBlank(line)) {
									final OutputStream out = client.getOutputStream();

									final String[] props = line.split(" ");
									final String path = props[1];
									final FileChannel channel = FileChannel.open(PathsManager.getInstance().getFileResource(PathsManager.getInstance().projectRootPath(), path).toPath(), StandardOpenOption.READ);
									IOUtils.copyLarge(Channels.newInputStream(channel), out);
									IOUtils.closeQuietly(channel);
									IOUtils.closeQuietly(out);
								}
							} catch (final IOException e) {
								e.printStackTrace();
							}
						});
					}
				} catch (final IOException e1) {
					e1.printStackTrace();
					serverSocket = null;
					System.exit(-1);
				}
			}).start();
		} else {
			throw new IllegalStateException("O servidor da aplicação já está rodando.");
		}
	}

}
