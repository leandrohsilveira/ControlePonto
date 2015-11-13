/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pontocontrol.controleponto.controller.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import br.com.pontocontrol.controleponto.ExtObject;
import br.com.pontocontrol.controleponto.PathsManager;
import br.com.pontocontrol.controleponto.controller.IConfiguracaoUsuarioController;
import br.com.pontocontrol.controleponto.controller.json.impl.ConfiguracoesUsuarioJSON;
import br.com.pontocontrol.controleponto.model.ConfiguracoesUsuario;

/**
 *
 * @author Leandro
 */
public class ConfiguracaoUsuarioController extends ExtObject implements IConfiguracaoUsuarioController {

   private ConfiguracaoUsuarioController() {
   }

   private static final Logger LOG = Logger.getLogger(ConfiguracaoUsuarioController.class.getName());
   private static final String ARQUIVO_CONFIGURACAO_PATTERN = "cf_%s.pcf";
   private static ConfiguracaoUsuarioController instance;

   public static ConfiguracaoUsuarioController getInstance() {
      if (instance == null) {
         instance = new ConfiguracaoUsuarioController();
      }
      return instance;
   }

   @Override
   public ConfiguracoesUsuarioJSON recuperarConfiguracaoUsuario(String login) {
      File arquivo = new File(String.format("%s/%s", PathsManager.getInstance().getPathUsuario(login), String.format(ARQUIVO_CONFIGURACAO_PATTERN, login)));
      if (arquivo.exists()) {
         try (BufferedReader reader = new BufferedReader(new FileReader(arquivo));) {
            String conteudo = reader.readLine();
            return new Gson().fromJson(conteudo, ConfiguracoesUsuarioJSON.class);
         } catch (Exception e) {
            LOG.log(Level.SEVERE, String.format("Erro ao executar leitura de arquivo de configuração para o usuário com login \"%s\"", login), e);
         }
      }
      return null;
   }

   @Override
   public void sincronizar(ConfiguracoesUsuario model) {
      if (model == null) {
         LOG.warning("ConfiguracoesUsuario null, nenhuma configuração de usuário foi sincronizada.");
         return;
      }
      File arquivo = new File(String.format("%s/%s", PathsManager.getInstance().getPathUsuario(model.getLogin()), String.format(ARQUIVO_CONFIGURACAO_PATTERN, model.getLogin())));
      if (arquivo.exists()) {
         LOG.info(String.format("Atualizando o arquivo de configuração do usuário de login \"%s\"", model.getLogin()));
      } else {
         LOG.info(String.format("Não existe um arquivo de configuração para o usuário de login \"%s\", este arquivo será criado.", model.getLogin()));
      }
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo));) {
         writer.write(new ConfiguracoesUsuarioJSON(model).toJSON());
         LOG.info(String.format("As configurações do usuário com login \"%s\" foram atualizadas.", model.getLogin()));
      } catch (Exception e) {
         LOG.log(Level.SEVERE, String.format("Erro ao executar leitura de arquivo de configuração para o usuário com login \"%s\"", model.getLogin()), e);
      }
   }

}
