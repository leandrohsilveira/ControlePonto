/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pontocontrol.controleponto.swing.event;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 *
 * @author silveira
 */
public class MensagemConfirmacao extends MensagemSwing {

   public MensagemConfirmacao(Component componentePai) {
      super(componentePai);
   }

   @Override
   protected boolean jOptionPane(String sumario, String descricao, int tipo) {
      return JOptionPane.showConfirmDialog(componentePai, descricao, sumario, JOptionPane.YES_NO_OPTION, tipo) == JOptionPane.YES_OPTION;
   }

}
