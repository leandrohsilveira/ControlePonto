/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pontocontrol.controleponto.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author silveira
 */
public class DateUtils {

   public static String formatDate(String pattern, Date date) {
      return new SimpleDateFormat(pattern).format(date);
   }

   public static String formatDate(Date date) {
      return SimpleDateFormat.getDateTimeInstance().format(date);
   }

   public static Date now() {
      return new Date();
   }

   public static String formatNow() {
      return formatDate(now());
   }

   public static String formatNow(String pattern) {
      return formatDate(pattern, now());
   }
}
