/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto;

import br.com.pontocontrol.controleponto.controller.ControllerFactory;
import br.com.pontocontrol.controleponto.controller.IConfiguracaoUsuarioController;
import br.com.pontocontrol.controleponto.controller.json.impl.ConfiguracoesUsuarioJSON;
import br.com.pontocontrol.controleponto.model.ConfiguracoesUsuario;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 *
 * @author silveira
 */
public class SessaoManager extends ExtObject {

    private SessaoManager() {}
    
    private static final Logger LOG = Logger.getLogger(SessaoManager.class.getName());
    private static SessaoManager instance;
    
    public interface LOGIN_STATUS {
        public static final int OK = 1;
        public static final int USUARIO_NAO_EXISTE = 2;
        public static final int JA_AUTENTICADO = 3;
    }
    
    public static SessaoManager getInstance() {
        if(instance == null) {
            instance = new SessaoManager();
        }
        return instance;
    }
    
    private ConfiguracoesUsuario usuario;
    private IConfiguracaoUsuarioController confUsuarioController;
    private Map<String, JFrame> frames = new HashMap<String, JFrame>();
            
    public int autenticar(String login) {
        if(usuario == null) {
            ConfiguracoesUsuarioJSON json = getConfUsuarioController().recuperarConfiguracaoUsuario(login);
            if(json != null) {
                usuario = json.toModel();
                return LOGIN_STATUS.OK;
            }
            return LOGIN_STATUS.USUARIO_NAO_EXISTE;
        } else {
            LOG.warning(format("Já existe um usuário autenticado neste momento com o login \"%s\"", usuario.getLogin()));
            return LOGIN_STATUS.JA_AUTENTICADO;
        }
    }
    
    public boolean criarUsuario(ConfiguracoesUsuario model) {
        File usrDir = new File(model.getPathUsuario());
        if(usrDir.exists()) {
            return false;
        } else {
            usrDir.mkdirs();
            getConfUsuarioController().sincronizar(model);
            return true;
        }
    }
    
    public ConfiguracoesUsuario getUsuarioAutenticado() {
        return usuario;
    }
    
    private IConfiguracaoUsuarioController getConfUsuarioController() {
        if(confUsuarioController == null) {
            confUsuarioController = ControllerFactory.localizar(IConfiguracaoUsuarioController.class);
        }
        return confUsuarioController;
    }
    
    
    public void registrarFrame(String id, JFrame frame) {
        if(frames.containsKey(id)) {
            LOG.info(format("Atualizando frame de ID \"%s\"", id));
        } else {
            LOG.info(format("Registrando frame de ID \"%s\"", id));
        }
        frames.put(id, frame);
    }
    
    public JFrame getFrame(String id) {
        if(frames.containsKey(id)) {
            LOG.info(format("Localizado frame com ID \"%s\"", id));
            return frames.get(id);
        } else {
            LOG.severe(format("O frame com ID \"%s\" não foi encontrado!", id));
            return null;
        }
    }
    
    public void apagarFrame(String id) {
        if(frames.containsKey(id)) {
            LOG.info(format("Apagando frame com ID \"%s\"", id));
            frames.remove(id);
        } else {
            LOG.warning(format("O frame com ID \"%s\" não foi encontrado!", id));
        }
    }
    
    public File getFile(String path, String res) {
        return SessaoManager.getFileResource(path, res);
    }
    
    public Image getImageResource(String res) {
        return SessaoManager.getFileImageResource(res);
    }
    
}
