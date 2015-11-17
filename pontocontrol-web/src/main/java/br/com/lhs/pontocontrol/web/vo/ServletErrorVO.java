package br.com.lhs.pontocontrol.web.vo;

import java.io.Serializable;

public class ServletErrorVO implements Serializable {

	private static final long serialVersionUID = 6344350247263799256L;

	private int code;
	private String mensagem;

	public ServletErrorVO() {
	}

	public ServletErrorVO(final int code, final String mensagem) {
		super();
		this.code = code;
		this.mensagem = mensagem;
	}

	public int getCode() {
		return code;
	}

	public void setCode(final int code) {
		this.code = code;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(final String mensagem) {
		this.mensagem = mensagem;
	}

}