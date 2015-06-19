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
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.apache.commons.io.FilenameUtils;

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

   private ConfiguracoesUsuario usuario;
   private final IConfiguracaoUsuarioController confUsuarioController = ControllerFactory.localizar(IConfiguracaoUsuarioController.class);
   private Map<String, JFrame> frames = new HashMap<String, JFrame>();

   public int autenticar(String login) {
      if (usuario == null) {
         ConfiguracoesUsuarioJSON json = confUsuarioController.recuperarConfiguracaoUsuario(login);
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
      File usrDir = new File(model.getPathUsuario());
      if (usrDir.exists()) {
         return false;
      } else {
         usrDir.mkdirs();
         confUsuarioController.sincronizar(model);
         return true;
      }
   }

   public ConfiguracoesUsuario getUsuarioAutenticado() {
      return usuario;
   }

   public void registrarFrame(String id, JFrame frame) {
      if (frames.containsKey(id)) {
         LOG.info(String.format("Atualizando frame de ID \"%s\"", id));
      } else {
         LOG.info(String.format("Registrando frame de ID \"%s\"", id));
      }
      frames.put(id, frame);
   }

   public JFrame getFrame(String id) {
      if (frames.containsKey(id)) {
         LOG.info(String.format("Localizado frame com ID \"%s\"", id));
         return frames.get(id);
      } else {
         LOG.severe(String.format("O frame com ID \"%s\" não foi encontrado!", id));
         return null;
      }
   }

   public void apagarFrame(String id) {
      if (frames.containsKey(id)) {
         LOG.info(String.format("Apagando frame com ID \"%s\"", id));
         frames.remove(id);
      } else {
         LOG.warning(String.format("O frame com ID \"%s\" não foi encontrado!", id));
      }
   }

   public File getFile(String path, String res) {
      return SessaoManager.getFileResource(path, res);
   }

   public String getPathUsuario() {
      return String.format("%s/%s", projectDataPath(), login);
   }

   protected static String projectDataPath() {
      return String.format("%s/data", projectRootPath());
   }

   protected static String projectImagesPath() {
      return String.format("%s/img", projectRootPath());
   }

   protected static String projectBinPath() {
      return String.format("%s/bin", projectRootPath());
   }

   protected static File getFileResource(String path, String resource) {
      return new File(String.format("%s/%s", path, resource));
   }

   protected static String projectRootPath() {
      String buildDir = FilenameUtils.getFullPath(ControlePonto.class.getProtectionDomain().getCodeSource().getLocation().getFile());
      return new File(buildDir).getParentFile().getAbsolutePath();
   }
}
