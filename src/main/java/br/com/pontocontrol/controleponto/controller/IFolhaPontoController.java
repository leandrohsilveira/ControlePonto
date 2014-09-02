/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.controller;

import br.com.pontocontrol.controleponto.controller.json.impl.FolhaMensalPontoJSON;
import br.com.pontocontrol.controleponto.controller.json.impl.RegistroDiarioPontoJSON;
import br.com.pontocontrol.controleponto.model.FolhaMensalPonto;

/**
 *
 * @author Leandro
 */
public interface IFolhaPontoController {
    
    IArquivoController getArquivoController();
    
    FolhaMensalPontoJSON recuperarFolhaMensal();
    
    FolhaMensalPontoJSON recuperarFolhaMensal(int ano, int mes);
    
    RegistroDiarioPontoJSON recuperarRegistroDiario(FolhaMensalPontoJSON folhaMensal);
    
    RegistroDiarioPontoJSON recuperarRegistroDiario(int dia, FolhaMensalPontoJSON folhaMensal);
    
    void sincronizar(FolhaMensalPontoJSON json);
    
    void sincronizar(FolhaMensalPonto model);
    
}
