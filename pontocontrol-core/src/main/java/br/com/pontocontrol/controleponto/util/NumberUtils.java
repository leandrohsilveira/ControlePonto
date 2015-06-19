/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pontocontrol.controleponto.util;

import java.text.DecimalFormat;

/**
 *
 * @author silveira
 */
public class NumberUtils {

   public static String formatNumber(Object arg) {
      return DecimalFormat.getNumberInstance().format(arg);
   }

   public static String formatNumber(String pattern, Object arg) {
      return new DecimalFormat(pattern).format(arg);
   }
}
