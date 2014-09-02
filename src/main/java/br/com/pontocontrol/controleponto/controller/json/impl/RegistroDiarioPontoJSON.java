/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.controller.json.impl;

import br.com.pontocontrol.controleponto.controller.json.BaseJSON;
import br.com.pontocontrol.controleponto.model.RegistroDiarioPonto;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Casa
 */
public class RegistroDiarioPontoJSON extends BaseJSON<RegistroDiarioPonto, RegistroDiarioPontoJSON> {
    
    private static final long serialVersionUID = 1L;
    
    public RegistroDiarioPontoJSON(RegistroDiarioPonto reg) {
        dia = reg.getDia();
        
        entrada = reg.getEntrada() != null ? DEFAULT_FORMATTER.format(reg.getEntrada()) : null;
        almoco = reg.getAlmoco()!= null ? DEFAULT_FORMATTER.format(reg.getAlmoco()) : null;
        retorno = reg.getRetorno()!= null ? DEFAULT_FORMATTER.format(reg.getRetorno()) : null;
        saida = reg.getSaida()!= null ? DEFAULT_FORMATTER.format(reg.getSaida()) : null;
        
        totalExpediente = reg.calcularTotalExpedienteAsNumber();
        totalAlmoco = reg.calcularTotalAlmocoAsNumber();
    }
    
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ISO_TIME;

    public Integer dia;
    
    public String entrada;
    public String almoco;
    public String retorno;
    public String saida;
    
    public Double totalExpediente;
    public Double totalAlmoco;
    
    public static RegistroDiarioPonto toModel(RegistroDiarioPontoJSON json) {
        RegistroDiarioPonto registroPonto = new RegistroDiarioPonto();
        
        registroPonto.setDia(json.dia);
        if(json.entrada != null) {
            registroPonto.setEntrada(LocalTime.from(DEFAULT_FORMATTER.parse(json.entrada)));
        }
        if(json.almoco != null) {
            registroPonto.setAlmoco(LocalTime.from(DEFAULT_FORMATTER.parse(json.almoco)));
        }
        if(json.retorno != null) {
            registroPonto.setRetorno(LocalTime.from(DEFAULT_FORMATTER.parse(json.retorno)));
        }
        if(json.saida != null) {
           registroPonto.setSaida(LocalTime.from(DEFAULT_FORMATTER.parse(json.saida)));
        }
        
        return registroPonto;
    }
    
    @Override
    public RegistroDiarioPonto toModel() {
        return RegistroDiarioPontoJSON.toModel(this);
    }

    @Override
    public Class<RegistroDiarioPontoJSON> getJsonClass() {
        return RegistroDiarioPontoJSON.class;
    }

}
