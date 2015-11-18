/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pontocontrol.controleponto;

import java.io.File;
import java.util.logging.Logger;

import br.com.pontocontrol.controleponto.controller.ControllerFactory;
import br.com.pontocontrol.controleponto.controller.IConfiguracaoUsuarioController;
import br.com.pontocontrol.controleponto.controller.json.impl.ConfiguracoesUsuarioJSON;
import br.com.pontocontrol.controleponto.model.ConfiguracoesUsuario;

/**
 *
 * @author silveira
 */
public class SessaoManager extends BaseSessaoManager {

	private SessaoManager() {
	}

	private static final Logger LOG = Logger.getLogger(SessaoManager.class.getName());
	private static SessaoManager instance;

	public interface LOGIN_STATUS {

		public static final int OK = 1;
		public static final int USUARIO_NAO_EXISTE = 2;
		public static final int JA_AUTENTICADO = 3;
	}

	public static SessaoManager getInstance() {
		if (instance == null) {
			instance = new SessaoManager();
		}
		return instance;
	}

	private final IConfiguracaoUsuarioController confUsuarioController = ControllerFactory.localizar(IConfiguracaoUsuarioController.class);

	public int autenticar(String login) {
		if (usuario == null) {
			final ConfiguracoesUsuarioJSON json = confUsuarioController.recuperarConfiguracaoUsuario(login);
			if (json != null) {
				usuario = json.toModel();
				return LOGIN_STATUS.OK;
			}
			return LOGIN_STATUS.USUARIO_NAO_EXISTE;
		} else {
			LOG.warning(String.format("Já existe um usuário autenticado neste momento com o login \"%s\"", usuario.getLogin()));
			return LOGIN_STATUS.JA_AUTENTICADO;
		}
	}

	public boolean criarUsuario(ConfiguracoesUsuario model) {
		final File usrDir = new File(PathsManager.getInstance().getPathUsuario(model.getLogin()));
		if (usrDir.exists()) {
			return false;
		} else {
			usrDir.mkdirs();
			confUsuarioController.sincronizar(model);
			return true;
		}
	}

	public void atualizarUsuario(ConfiguracoesUsuario model) {
		if (!criarUsuario(model)) {
			confUsuarioController.sincronizar(model);
		}
	}

}
