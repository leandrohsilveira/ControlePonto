/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.controller;

import br.com.pontocontrol.controleponto.controller.impl.ArquivoController;
import br.com.pontocontrol.controleponto.controller.impl.ConfiguracaoUsuarioController;
import br.com.pontocontrol.controleponto.controller.impl.ExportadorXLSController;
import br.com.pontocontrol.controleponto.controller.impl.FolhaPontoController;
import java.util.logging.Logger;

/**
 *
 * @author Leandro
 */
public class ControllerFactory {
    
    private static final Logger LOG = Logger.getLogger(ControllerFactory.class.getName());
    
    public static <I> I localizar(Class<I> controllerInterface) {
        if(controllerInterface == null) {
            LOG.severe("Classe informada é nula.");
            return null;
        }
        if(controllerInterface.isInterface()) {
            if(controllerInterface.isAssignableFrom(ArquivoController.class)) {
                return (I) ArquivoController.getInstance();
            }
            if(controllerInterface.isAssignableFrom(FolhaPontoController.class)) {
                return (I) FolhaPontoController.getInstance();
            }
            if(controllerInterface.isAssignableFrom(ConfiguracaoUsuarioController.class)) {
                return (I) ConfiguracaoUsuarioController.getInstance();
            }
            if(controllerInterface.isAssignableFrom(ExportadorXLSController.class)) {
                return (I) ExportadorXLSController.getInstance();
            }
            LOG.severe("Implementação de controle não localizada para a interface informada.");
            return null;
        } else {
            LOG.severe("A classe informada para recuperar instancia de controle não é uma interface.");
            return null;
        }
    }
    
}
