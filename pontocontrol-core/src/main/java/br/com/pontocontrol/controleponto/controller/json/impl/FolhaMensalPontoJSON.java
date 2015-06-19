/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.controller.json.impl;

import br.com.pontocontrol.controleponto.controller.json.BaseJSON;
import br.com.pontocontrol.controleponto.model.FolhaMensalPonto;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Leandro
 */
public class FolhaMensalPontoJSON extends BaseJSON<FolhaMensalPonto, FolhaMensalPontoJSON>{
    
    private static final long serialVersionUID = 1L;
    
    public int ano;
    public int mes;
    public Map<Integer, RegistroDiarioPontoJSON> registros = new TreeMap<>();
    public double total;

    private FolhaMensalPontoJSON() {
    }
    
    public FolhaMensalPontoJSON(FolhaMensalPonto model) {
        ano = model.getAno();
        mes = model.getMes();
        total = model.calcularTotalMensal();
        model.getRegistros().entrySet().stream().forEach((entry) -> {
            registros.put(entry.getKey(), new RegistroDiarioPontoJSON(entry.getValue()));
        });
    }
    
    public static FolhaMensalPonto toModel(FolhaMensalPontoJSON json) {
        FolhaMensalPonto folhaMensalPonto = new FolhaMensalPonto();
        folhaMensalPonto.setAno(json.ano);
        folhaMensalPonto.setMes(json.mes);
        for(Integer key :json.registros.keySet()) {
            folhaMensalPonto.getRegistros().put(key, json.registros.get(key).toModel());
        }
        return folhaMensalPonto;
    }
    
    public static FolhaMensalPontoJSON fromJSONStr(String json) {
        return new FolhaMensalPontoJSON().fromJSON(json);
    }
    
    @Override
    public FolhaMensalPonto toModel() {
        return toModel(this);
    }

    @Override
    public Class<FolhaMensalPontoJSON> getJsonClass() {
        return FolhaMensalPontoJSON.class;
    }
}
