package br.com.lhs.pontocontrol.web.vo;

import java.io.Serializable;

public class UsuarioVO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -72339803418658716L;

	private String nome;
	private String login;
	private String offset;

	public UsuarioVO() {
	}

	public UsuarioVO(final String nome, final String login, final String offset) {
		super();
		this.nome = nome;
		this.login = login;
		this.offset = offset;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(final String nome) {
		this.nome = nome;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(final String login) {
		this.login = login;
	}

	public String getOffset() {
		return offset;
	}

	public void setOffset(final String offset) {
		this.offset = offset;
	}

}