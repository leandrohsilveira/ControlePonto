package br.com.lhs.pontocontrol.web.util;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;

public interface RespondJson<T extends Serializable> {

	T respond(T content) throws IOException, ServletException;

}