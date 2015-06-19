/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pontocontrol.controleponto.event;

/**
 *
 * @author silveira
 */
public interface Mensagem {

   public static final int TIPO_INFO = 0;
   public static final int TIPO_ALERTA = 1;
   public static final int TIPO_ERRO = 2;
   public static final int TIPO_FATAL = 3;

   boolean exibirMensagem(String sumario, String descricao, int tipo);

}
