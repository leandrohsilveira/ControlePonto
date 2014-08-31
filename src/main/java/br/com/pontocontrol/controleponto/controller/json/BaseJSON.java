/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.controller.json;

import br.com.pontocontrol.controleponto.ExtObject;
import com.google.gson.Gson;
import java.io.Serializable;

/**
 *
 * @author Leandro
 * @param <M>
 * @param <J>
 */
public abstract class BaseJSON<M, J> extends ExtObject implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    public String toJSON() {
        return new Gson().toJson(this);
    }
    
    public J fromJSON(String json) {
        return new Gson().fromJson(json, getJsonClass());
    }
    
    public abstract Class<J> getJsonClass();
    
    public abstract M toModel();
    
}
