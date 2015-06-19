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
public class MensagemInformativa extends MensagemSwing {

   public MensagemInformativa(Component componentePai) {
      super(componentePai);
   }

   @Override
   protected boolean jOptionPane(String sumario, String descricao, int tipo) {
      JOptionPane.showMessageDialog(componentePai, descricao, sumario, tipo);
      return true;
   }

}
