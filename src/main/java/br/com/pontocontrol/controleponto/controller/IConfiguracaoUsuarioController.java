/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.controller;

import br.com.pontocontrol.controleponto.controller.json.impl.ConfiguracoesUsuarioJSON;
import br.com.pontocontrol.controleponto.model.ConfiguracoesUsuario;

/**
 *
 * @author silveira
 */
public interface IConfiguracaoUsuarioController {
    
    ConfiguracoesUsuarioJSON recuperarConfiguracaoUsuario(String login);
    
    void sincronizar(ConfiguracoesUsuario json);
    
}
