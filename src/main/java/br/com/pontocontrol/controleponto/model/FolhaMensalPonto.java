/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.model;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Leandro
 */
public class FolhaMensalPonto {

    public FolhaMensalPonto() {
    }

    public FolhaMensalPonto(int ano, int mes) {
        this.ano = ano;
        this.mes = mes;
    }
    
    private int ano;
    private int mes;
    private Map<Integer, RegistroDiarioPonto> registros = new TreeMap<Integer, RegistroDiarioPonto>();
    
    public Double calcularTotalMensal() {
        double total = 0d;
        for (Integer key : registros.keySet()) {
            RegistroDiarioPonto registroDiarioPonto = registros.get(key);
            total += registroDiarioPonto.calcularTotalExpedienteAsNumber();
        }
        return total;
    }

    public Double calcularVariacaoMensal() {
        return calcularTotalMensal() - registros.size();
    }
    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }

    public Map<Integer, RegistroDiarioPonto> getRegistros() {
        return registros;
    }

    public void setRegistros(Map<Integer, RegistroDiarioPonto> registros) {
        this.registros = registros;
    }


}
