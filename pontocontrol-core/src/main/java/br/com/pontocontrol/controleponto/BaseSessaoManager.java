/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pontocontrol.controleponto;

import br.com.pontocontrol.controleponto.model.ConfiguracoesUsuario;

/**
 *
 * @author silveira
 */
public abstract class BaseSessaoManager {

   public interface LOGIN_STATUS {

      public static final int OK = 1;
      public static final int USUARIO_NAO_EXISTE = 2;
      public static final int JA_AUTENTICADO = 3;

   }

   protected ConfiguracoesUsuario usuario;

   public ConfiguracoesUsuario getUsuarioAutenticado() {
      return usuario;
   }

}
