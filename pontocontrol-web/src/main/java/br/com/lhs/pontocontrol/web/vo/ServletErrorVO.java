package br.com.lhs.pontocontrol.web.vo;

import java.io.Serializable;

public class ServletErrorVO implements Serializable {

	private static final long serialVersionUID = 6344350247263799256L;

	private int status;
	private String mensagem;

	public ServletErrorVO() {
	}

	public ServletErrorVO(final int status, final String mensagem) {
		super();
		this.status = status;
		this.mensagem = mensagem;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(final String mensagem) {
		this.mensagem = mensagem;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(final int status) {
		this.status = status;
	}

}