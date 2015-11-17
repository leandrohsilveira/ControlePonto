package br.com.lhs.pontocontrol.fx.main;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class MainControllerFX implements Initializable {

	@FXML
	private Label title;

	@FXML
	private WebView webView;

	@Override
	public void initialize(final URL url, final ResourceBundle rb) {
		final WebEngine engine = webView.getEngine();

		// PathsManager.getInstance().getFileResource(PathsManager.getInstance().projectRootPath(), "/html/ponto-control-fx.html");

		engine.load("http://localhost:8441/pontocontrol/view");
	}

}
