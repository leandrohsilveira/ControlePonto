/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.controller.impl;

import br.com.pontocontrol.controleponto.ExtObject;
import br.com.pontocontrol.controleponto.controller.ControllerFactory;
import br.com.pontocontrol.controleponto.controller.IArquivoController;
import br.com.pontocontrol.controleponto.controller.IFolhaPontoController;
import br.com.pontocontrol.controleponto.controller.json.impl.FolhaMensalPontoJSON;
import br.com.pontocontrol.controleponto.controller.json.impl.RegistroDiarioPontoJSON;
import br.com.pontocontrol.controleponto.model.FolhaMensalPonto;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Leandro
 */
public class FolhaPontoController extends ExtObject implements IFolhaPontoController {

    private static final Logger LOG = Logger.getLogger(FolhaPontoController.class.getName());
    private IArquivoController arquivoController;

    private FolhaPontoController() {
    }
    
    private static FolhaPontoController instance;
    
    public static FolhaPontoController getInstance() {
        if(instance == null) {
            instance = new FolhaPontoController();
        }
        return instance;
    }
    
    @Override
    public FolhaMensalPontoJSON recuperarFolhaMensal() {
        return recuperarFolhaMensal(getAnoAtual(), getMesAtual());
    }

    @Override
    public FolhaMensalPontoJSON recuperarFolhaMensal(int ano, int mes) {
        try {
            IArquivoController cont = getArquivoController();
            File arquivo = cont.recuperarArquivo(cont.getYearPath(ano), cont.getMonthFile(mes));
            if(arquivo.exists()) {
                BufferedReader reader = cont.getArquivoParaLer(arquivo);
                String conteudo = reader.readLine();
                cont.fecharArquivoLeitura(reader);
                return FolhaMensalPontoJSON.fromJSONStr(conteudo);
            } else {
                FolhaMensalPontoJSON model = new FolhaMensalPontoJSON(new FolhaMensalPonto(ano, mes));
                return model;
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public RegistroDiarioPontoJSON recuperarRegistroDiario(FolhaMensalPontoJSON folhaMensal) {
        return recuperarRegistroDiario(getDiaAtual(), folhaMensal);
    }

    @Override
    public RegistroDiarioPontoJSON recuperarRegistroDiario(int dia, FolhaMensalPontoJSON folhaMensal) {       
        return folhaMensal.registros.get(dia);
    }

    public IArquivoController getArquivoController() {
        if(arquivoController == null) {
            arquivoController = ControllerFactory.localizar(IArquivoController.class);
        }
        return arquivoController;
    }

    @Override
    public void sincronizar(FolhaMensalPontoJSON json) {
        File arquivo = getArquivoController().recuperarArquivo(getArquivoController().getYearPath(json.ano), getArquivoController().getMonthFile(json.mes), true);
        try (BufferedWriter writer = getArquivoController().getArquivoParaEscrever(arquivo);){
            writer.write(json.toJSON());
            getArquivoController().salvarArquivo(writer);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sincronizar(FolhaMensalPonto model) {
        sincronizar(new FolhaMensalPontoJSON(model));
    }
    
}
