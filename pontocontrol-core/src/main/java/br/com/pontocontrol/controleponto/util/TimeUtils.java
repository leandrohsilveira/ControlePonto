/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pontocontrol.controleponto.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 *
 * @author Casa
 */
public class TimeUtils {

   public static final String FORMATO_HORA = "HH:mm";
   public static final long OFFSET_1_SEGUNDO = 1l;
   public static final long OFFSET_1_MINUTO = OFFSET_1_SEGUNDO * 60;
   public static final long OFFSET_1_HORA = OFFSET_1_MINUTO * 60;
   public static final long OFFSET_8_HORAS = OFFSET_1_HORA * 8;
   public static final long OFFSET_12_HORAS = OFFSET_1_HORA * 12;
   public static final long OFFSET_1_DIA = OFFSET_1_HORA * 24;

   public static final long SYSTEM_DEFAULT_OFFSET = OFFSET_8_HORAS;

   public static LocalTime subtrairLocalTimes(final LocalTime base, final LocalTime valor) {
      return base.minusHours(valor.getHour()).minusMinutes(valor.getMinute()).minusSeconds(valor.getSecond()).minusNanos(valor.getNano());
   }

   public static LocalTime somarLocalTimes(final LocalTime base, final LocalTime valor) {
      return base.plusHours(valor.getHour()).plusMinutes(valor.getMinute()).plusSeconds(valor.getSecond()).plusNanos(valor.getNano());
   }

   public static LocalTime toLocalTime(String localTimeString) {
      return toLocalTime(localTimeString, FORMATO_HORA);
   }

   public static LocalTime toLocalTime(String localTimeString, String formato) {
      try {
         if (formato != null) {
            return LocalTime.parse(localTimeString, DateTimeFormatter.ofPattern(formato));
         } else {
            return toLocalTimeISO(localTimeString);
         }
      } catch (DateTimeParseException e) {
         return null;
      }
   }

   public static LocalTime toLocalTimeISO(String localTimeString) {
      try {
         return LocalTime.parse(localTimeString, DateTimeFormatter.ISO_TIME);
      } catch (DateTimeParseException e) {
         return null;
      }
   }

   public static String fromLocalTime(LocalTime time) {
      return fromLocalTime(time, FORMATO_HORA);
   }

   public static String fromLocalTime(LocalTime time, String formato) {
      if (time == null) {
         return "";
      }
      if (formato != null) {
         return DateTimeFormatter.ofPattern(formato).format(time);
      } else {
         return fromLocalTimeISO(time);
      }
   }

   public static String fromLocalTimeISO(LocalTime time) {
      if (time != null) {
         return DateTimeFormatter.ISO_TIME.format(time);
      }
      return "";
   }

   public static double toNumberLocalTime(LocalTime time) {
      return toNumberLocalTime(time, OFFSET_8_HORAS);
   }

   public static double toNumberLocalTime(LocalTime time, Long offset) {
      if (time != null) {
         offset = offset != null ? offset : OFFSET_8_HORAS;
         return (double) (time.getHour() * 3600 + time.getMinute() * 60 + time.getSecond()) / offset;
      }
      return 0d;
   }

   public static LocalTime fromNumberLocalTime(double value, Long offset) {
      offset = offset != null ? offset : OFFSET_8_HORAS;
      boolean negativo = value < 0;
      value = negativo ? value * -1 : value;
      double valorComOffset = value * ((double) offset);
      int hora = (int) (valorComOffset >= OFFSET_1_HORA ? (valorComOffset / OFFSET_1_HORA) : 0l);
      int horaRest = (int) (valorComOffset >= OFFSET_1_HORA ? (valorComOffset % OFFSET_1_HORA) : valorComOffset);
      int minuto = (int) (horaRest >= OFFSET_1_MINUTO ? (horaRest / OFFSET_1_MINUTO) : 0l);
      int segundo = (int) (horaRest >= OFFSET_1_MINUTO ? (horaRest % OFFSET_1_MINUTO) : horaRest);
      return LocalTime.of(hora, minuto, segundo);
   }

   public static String fromNumberLocalTimeFormatted(double value, Long offset) {
      offset = offset != null ? offset : OFFSET_8_HORAS;
      boolean negativo = value < 0;
      value = negativo ? value * -1 : value;
      double valorComOffset = value * ((double) offset);
      int hora = (int) (valorComOffset >= OFFSET_1_HORA ? (valorComOffset / OFFSET_1_HORA) : 0l);
      int horaRest = (int) (valorComOffset >= OFFSET_1_HORA ? (valorComOffset % OFFSET_1_HORA) : valorComOffset);
      int minuto = (int) (horaRest >= OFFSET_1_MINUTO ? (horaRest / OFFSET_1_MINUTO) : 0l);
      int segundo = (int) (horaRest >= OFFSET_1_MINUTO ? (horaRest % OFFSET_1_MINUTO) : horaRest);
      if (negativo) {
         return String.format("-%s:%s:%s", NumberUtils.formatNumber("#00", hora), NumberUtils.formatNumber("00", minuto), NumberUtils.formatNumber("00", segundo));
      } else {
         return String.format("%s:%s:%s", NumberUtils.formatNumber("00", hora), NumberUtils.formatNumber("00", minuto), NumberUtils.formatNumber("00", segundo));
      }
   }

}
