/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.controller.json.impl;

import br.com.pontocontrol.controleponto.controller.json.BaseJSON;
import br.com.pontocontrol.controleponto.model.ConfiguracoesUsuario;
import br.com.pontocontrol.controleponto.util.TimeUtils;

/**
 *
 * @author silveira
 */
public class ConfiguracoesUsuarioJSON extends BaseJSON<ConfiguracoesUsuario, ConfiguracoesUsuarioJSON> {

    public ConfiguracoesUsuarioJSON(ConfiguracoesUsuario model) {
        login = model.getLogin();
        segunda = model.isSegunda();
        terca = model.isTerca();
        quarta = model.isQuarta();
        quinta = model.isQuinta();
        sexta = model.isSexta();
        sabado = model.isSabado();
        domingo = model.isDomingo();
        offset = model.getExpediente();
    }
    
    //login e user path.
    public String login;
    
    //Expediente
    public boolean segunda = true;
    public boolean terca = true;
    public boolean quarta = true;
    public boolean quinta = true;
    public boolean sexta = true;
    public boolean sabado = false;
    public boolean domingo = false;
    
    public long offset = TimeUtils.OFFSET_8_HORAS;
    
    public static ConfiguracoesUsuario toModel(ConfiguracoesUsuarioJSON json) {
        ConfiguracoesUsuario configuracoesUsuario = new ConfiguracoesUsuario(json.login);
        configuracoesUsuario.setSegunda(json.segunda);
        configuracoesUsuario.setTerca(json.terca);
        configuracoesUsuario.setQuarta(json.quarta);
        configuracoesUsuario.setQuinta(json.quinta);
        configuracoesUsuario.setSexta(json.sexta);
        configuracoesUsuario.setSabado(json.sabado);
        configuracoesUsuario.setDomingo(json.domingo);
        configuracoesUsuario.setOffset(json.offset);
        return configuracoesUsuario;
    }

    @Override
    public Class<ConfiguracoesUsuarioJSON> getJsonClass() {
        return ConfiguracoesUsuarioJSON.class;
    }

    @Override
    public ConfiguracoesUsuario toModel() {
        return ConfiguracoesUsuarioJSON.toModel(this);
    }
    
}
