/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.controller;

import br.com.pontocontrol.controleponto.model.FolhaMensalPonto;

/**
 *
 * @author silveira
 */
public interface IExportadorXLSController {
    
    boolean extrair(FolhaMensalPonto folhaMensal, String output);
    
}
