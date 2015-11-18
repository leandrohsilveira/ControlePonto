package br.com.lhs.pontocontrol.fx.main;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class MainControllerFX implements Initializable {

	private static final Logger logger = Logger.getLogger(MainControllerFX.class.getName());

	@FXML
	private Label title;

	@FXML
	private WebView webView;

	@FXML
	private ImageView appIcon;

	@Override
	public void initialize(final URL url, final ResourceBundle rb) {
		appIcon.setImage(new Image(MainControllerFX.class.getClassLoader().getResourceAsStream("img/icon.png")));
		final WebEngine engine = webView.getEngine();
		engine.onErrorProperty().set(event -> logger.severe(event.getMessage()));
		engine.onAlertProperty().set(event -> {
			final Alert alert = new Alert(AlertType.WARNING);
			alert.setHeaderText(event.getData());
			alert.setTitle("Mensagem");
			alert.showAndWait();
		});

		engine.load("http://localhost:8441/pontocontrol/view");
	}

	/**
	 * Enables Firebug Lite for debugging a webEngine.
	 *
	 * @param engine
	 *           the webEngine for which debugging is to be enabled.
	 */
	private static void enableFirebug(final WebEngine engine) {
		engine.executeScript(
				"if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
	}

}
