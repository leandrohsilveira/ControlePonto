package br.com.lhs.pontocontrol.fx.main;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import br.com.pontocontrol.controleponto.PathsManager;
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
	public void initialize(URL url, ResourceBundle rb) {
		final WebEngine engine = webView.getEngine();

		final File file = PathsManager.getInstance().getFileResource(PathsManager.getInstance().projectRootPath(), "/html/ponto-control-fx.html");

		engine.load("file:///" + file.toURI().getPath());
	}
}
