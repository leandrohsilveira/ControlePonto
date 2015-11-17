package br.com.lhs.pontocontrol.web.util;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;

public interface AcceptJson<T extends Serializable> {

	void accept(T content) throws IOException, ServletException;

}