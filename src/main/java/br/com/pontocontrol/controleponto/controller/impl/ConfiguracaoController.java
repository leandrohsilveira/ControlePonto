/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.controller.impl;

import br.com.pontocontrol.controleponto.ExtObject;

/**
 *
 * @author Leandro
 */
public class ConfiguracaoController extends ExtObject {

    private ConfiguracaoController() {
    }
    
    public static final String ARQUIVO_CONF_DIR = format("%s/config", mainPath());
    
    private static ConfiguracaoController instance;
    
    public static ConfiguracaoController getInstance() {
        return instance = instance != null ? instance : new ConfiguracaoController();
    }
    
}
