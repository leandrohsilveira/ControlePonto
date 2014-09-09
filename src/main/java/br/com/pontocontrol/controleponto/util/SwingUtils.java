/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFormattedTextField;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Leandro
 */
public class SwingUtils {
    
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATE_PATTERN = "dd/MM/yyyy";
    private static final Logger LOG = Logger.getLogger(SwingUtils.class.getName());
    private static final String TIME_MASK = "##:##:##";
    private static final String DATE_MASK = "##/##/####";
    
    private static final SimpleDateFormat SIMPLE_DATE_FORMATTER = new SimpleDateFormat(DATE_PATTERN);
    private static final SimpleDateFormat SIMPLE_TIME_FORMATTER = new SimpleDateFormat(TIME_PATTERN);
    private static final DateTimeFormatter LOCAL_TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);
    
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
    
     public static void validateFields(SimpleDateFormat formatter, JTextField... fields) {
        for (JTextField field : fields) {
            try {
                String valor = field.getText();
                if(StringUtils.isNotBlank(valor)) {
                    Date date = formatter.parse(valor);
                    field.setText(formatter.format(date));
                }
            } catch (ParseException ex) {
                field.setText("");
            }
            
        }
    }
    
    public static void validateDateFields(JTextField... fields) {
        validateFields(SIMPLE_DATE_FORMATTER, fields);
    }
    
    public static void validateTimeFields(JTextField... fields) {
        validateFields(SIMPLE_TIME_FORMATTER, fields);
    }
    
    public static LocalTime getLocalTimeValueFromField(JTextField field) {
        String value = field.getText();
        if(StringUtils.isNotBlank(value)) {
            try {
                return LocalTime.from(LOCAL_TIME_FORMATTER.parse(value));
            } catch (DateTimeParseException e) {}
        }
        return null;
    }
    
    public static Date getDateValueFromField(JTextField field) {
        String value = field.getText();
        if(StringUtils.isNotBlank(value)) {
            try {
                return SIMPLE_DATE_FORMATTER.parse(value);
            } catch (ParseException e) {}
        }
        return null;
    }
    
}
