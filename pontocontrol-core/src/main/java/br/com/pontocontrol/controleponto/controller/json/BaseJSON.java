/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.controller.json;

import java.io.Serializable;

import com.google.gson.Gson;

/**
 *
 * @author Leandro
 * @param <M>
 * @param <J>
 */
public abstract class BaseJSON<M, J> implements Serializable {

	private static final long serialVersionUID = 1L;

	public String toJSON() {
		return new Gson().toJson(this);
	}

	public J fromJSON(final String json) {
		return new Gson().fromJson(json, getJsonClass());
	}

	public abstract Class<J> getJsonClass();

	public abstract M toModel();

}
