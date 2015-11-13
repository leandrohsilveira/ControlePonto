/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pontocontrol.controleponto.swing.util;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import org.apache.commons.lang.StringUtils;

import br.com.pontocontrol.controleponto.PathsManager;

/**
 *
 * @author Leandro
 */
public class SwingUtils {

   private static final String TIME_PATTERN_A = "HH:mm:ss";
   private static final String TIME_PATTERN_B = "HH:mm";
   private static final String TIME_PATTERN_C = "HH";

   private static final String DATE_PATTERN_A = "dd/MM/yyyy";
   private static final String DATE_PATTERN_B = "dd/MM";
   private static final String DATE_PATTERN_C = "dd";

   private static final Logger LOG = Logger.getLogger(SwingUtils.class.getName());

   private static final String TIME_MASK = "##:##:##";
   private static final String DATE_MASK = "##/##/####";

   private static final SimpleDateFormat SIMPLE_DATE_FORMATTER_A = new SimpleDateFormat(DATE_PATTERN_A);
   private static final SimpleDateFormat SIMPLE_TIME_FORMATTER_A = new SimpleDateFormat(TIME_PATTERN_A);
   private static final SimpleDateFormat SIMPLE_DATE_FORMATTER_B = new SimpleDateFormat(DATE_PATTERN_B);
   private static final SimpleDateFormat SIMPLE_TIME_FORMATTER_B = new SimpleDateFormat(TIME_PATTERN_B);
   private static final SimpleDateFormat SIMPLE_DATE_FORMATTER_C = new SimpleDateFormat(DATE_PATTERN_C);
   private static final SimpleDateFormat SIMPLE_TIME_FORMATTER_C = new SimpleDateFormat(TIME_PATTERN_C);

   private static final DateTimeFormatter LOCAL_TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN_A);

   public static void setTimeMask(JFormattedTextField field) {
      setMask(field, TIME_MASK);
   }

   public static void setMask(JFormattedTextField field, String mask) {
      try {
         MaskFormatter maskFormatter = new MaskFormatter(mask);
         maskFormatter.setPlaceholder("");
         maskFormatter.install(field);
      } catch (ParseException ex) {
         LOG.log(Level.SEVERE, "Erro ao definir máscara no campo", ex);
      }
   }

   public static void setDateMasks(JFormattedTextField... fields) {
      setMasks(DATE_MASK, fields);
   }

   public static void setTimeMasks(JFormattedTextField... fields) {
      setMasks(TIME_MASK, fields);
   }

   public static void setMasks(String mask, JFormattedTextField... fields) {
      for (JFormattedTextField field : fields) {
         setMask(field, mask);
      }
   }

   public static void validateField(SimpleDateFormat formatter, JTextField field) throws ParseException {
      String valor = field.getText();
      if (StringUtils.isNotBlank(valor)) {
         Date date = formatter.parse(valor);
         field.setText(formatter.format(date));
      }
   }

   public static void validateDateFields(JTextField... fields) {
      for (JTextField field : fields) {
         Calendar dataDoCampo = null;
         Calendar dataSaida = Calendar.getInstance();
         try {
            dataDoCampo = getCalendarValueFromField(SIMPLE_DATE_FORMATTER_A, field);
            dataSaida = dataDoCampo;
         } catch (ParseException ex) {
            try {
               dataDoCampo = getCalendarValueFromField(SIMPLE_DATE_FORMATTER_B, field);
               dataSaida.set(Calendar.DAY_OF_MONTH, dataDoCampo.get(Calendar.DAY_OF_MONTH));
               dataSaida.set(Calendar.MONTH, dataDoCampo.get(Calendar.MONTH));
            } catch (ParseException ex1) {
               try {
                  dataDoCampo = getCalendarValueFromField(SIMPLE_DATE_FORMATTER_C, field);
                  dataSaida.set(Calendar.DAY_OF_MONTH, dataDoCampo.get(Calendar.DAY_OF_MONTH));
               } catch (ParseException ex2) {
               }
            }
         }
         if (dataDoCampo != null) {
            String valTxt = SIMPLE_DATE_FORMATTER_A.format(dataSaida.getTime());
            field.setText(valTxt);
         } else {
            field.setText("");
         }
      }
   }

   public static void validateTimeFields(JTextField... fields) {
      for (JTextField field : fields) {
         Calendar dataSaida = null;
         try {
            dataSaida = getCalendarValueFromField(SIMPLE_TIME_FORMATTER_A, field);
         } catch (ParseException ex) {
            try {
               dataSaida = getCalendarValueFromField(SIMPLE_TIME_FORMATTER_B, field);
            } catch (ParseException ex1) {
               try {
                  dataSaida = getCalendarValueFromField(SIMPLE_TIME_FORMATTER_C, field);
               } catch (ParseException ex2) {
               }
            }
         }
         if (dataSaida != null) {
            String valTxt = SIMPLE_TIME_FORMATTER_A.format(dataSaida.getTime());
            field.setText(valTxt);
         } else {
            field.setText("");
         }
      }
   }

   public static LocalTime getLocalTimeValueFromField(JTextField field) {
      String value = field.getText();
      if (StringUtils.isNotBlank(value)) {
         try {
            return LocalTime.from(LOCAL_TIME_FORMATTER.parse(value));
         } catch (DateTimeParseException e) {
         }
      }
      return null;
   }

   public static Date getDateValueFromField(JTextField field) {
      String value = field.getText();
      if (StringUtils.isNotBlank(value)) {
         try {
            return getDateValueFromField(SIMPLE_DATE_FORMATTER_A, field);
         } catch (ParseException e) {
         }
      }
      return null;
   }

   public static Date getDateValueFromField(SimpleDateFormat formatter, JTextField field) throws ParseException {
      String value = field.getText();
      if (StringUtils.isNotBlank(value)) {
         return formatter.parse(value);
      }
      return null;
   }

   public static Calendar getCalendarValueFromField(SimpleDateFormat formatter, JTextField field) throws ParseException {
      Calendar cal = Calendar.getInstance();
      cal.setTime(getDateValueFromField(formatter, field));
      return cal;
   }

   public static Image getImage(String resource) {
      File imageFile = PathsManager.getInstance().getImageFile(resource);
      try {
         return ImageIO.read(imageFile);
      } catch (IOException ex) {
         LOG.log(Level.SEVERE, null, ex);
         return null;
      }
   }

}
