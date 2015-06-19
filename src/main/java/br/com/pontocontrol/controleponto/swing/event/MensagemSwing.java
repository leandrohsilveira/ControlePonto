/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pontocontrol.controleponto.swing.event;

import br.com.pontocontrol.controleponto.event.Mensagem;
import java.awt.Component;
import javax.swing.JOptionPane;

/**
 *
 * @author silveira
 */
public abstract class MensagemSwing implements Mensagem {

   public MensagemSwing(Component componentePai) {
      this.componentePai = componentePai;
   }

   protected final Component componentePai;

   @Override
   public boolean exibirMensagem(String sumario, String descricao, int tipo) {
      int tipoSwing;
      switch (tipo) {
         case TIPO_FATAL:
         case TIPO_ERRO:
            tipoSwing = JOptionPane.ERROR_MESSAGE;
            break;
         case TIPO_ALERTA:
            tipoSwing = JOptionPane.WARNING_MESSAGE;
            break;
         case TIPO_INFO:
         default:
            tipoSwing = JOptionPane.INFORMATION_MESSAGE;
            break;
      }
      return jOptionPane(sumario, descricao, tipoSwing);
   }

   protected abstract boolean jOptionPane(String sumario, String descricao, int tipo);

}
