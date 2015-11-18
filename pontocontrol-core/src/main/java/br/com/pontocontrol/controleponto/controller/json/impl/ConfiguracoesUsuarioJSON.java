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

	private static final long serialVersionUID = -2634865214265402219L;

	public ConfiguracoesUsuarioJSON(ConfiguracoesUsuario model) {
		login = model.getLogin();
		nome = model.getNome();
		segunda = model.isSegunda();
		terca = model.isTerca();
		quarta = model.isQuarta();
		quinta = model.isQuinta();
		sexta = model.isSexta();
		sabado = model.isSabado();
		domingo = model.isDomingo();
		offset = model.getOffset();
	}

	// login e user path.
	public String login;
	public String nome;

	// Expediente
	public boolean segunda = true;
	public boolean terca = true;
	public boolean quarta = true;
	public boolean quinta = true;
	public boolean sexta = true;
	public boolean sabado = false;
	public boolean domingo = false;

	public long offset = TimeUtils.OFFSET_8_HORAS;

	public static ConfiguracoesUsuario toModel(ConfiguracoesUsuarioJSON json) {
		final ConfiguracoesUsuario configuracoesUsuario = new ConfiguracoesUsuario(json.login);
		configuracoesUsuario.setSegunda(json.segunda);
		configuracoesUsuario.setTerca(json.terca);
		configuracoesUsuario.setQuarta(json.quarta);
		configuracoesUsuario.setQuinta(json.quinta);
		configuracoesUsuario.setSexta(json.sexta);
		configuracoesUsuario.setSabado(json.sabado);
		configuracoesUsuario.setDomingo(json.domingo);
		configuracoesUsuario.setOffset(json.offset);
		configuracoesUsuario.setNome(json.nome);
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
