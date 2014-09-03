/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    
    private static final Logger LOG = Logger.getLogger(SwingUtils.class.getName());
    private static final String TIME_MASK = "##:##:##";
    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("HH:mm:ss");
    
    public static JFormattedTextField setTimeMask(JFormattedTextField field) {
        return setMask(field, TIME_MASK);
    }
    
    public static JFormattedTextField setMask(JFormattedTextField field, String mask) {
        try {
            MaskFormatter maskFormatter = new MaskFormatter(mask);
            maskFormatter.setPlaceholder("");
            maskFormatter.install(field);
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, "Erro ao definir máscara no campo", ex);
        }
        return field;
    }
    
    public static void setTimeMasks(JFormattedTextField... fields) {
        setMasks(TIME_MASK, fields);
    }
    
    public static void setMasks(String mask, JFormattedTextField... fields) {
        for (JFormattedTextField field : fields) {
            setMask(field, mask);
        }
    }
    
    public static void validateDateFields(JTextField... fields) {
        for (JTextField field : fields) {
            try {
                String valor = field.getText();
                if(StringUtils.isNotBlank(valor)) {
                    Date date = TIME_FORMATTER.parse(valor);
                    field.setText(TIME_FORMATTER.format(date));
                }
            } catch (ParseException ex) {
                field.setText("");
            }
            
        }
    }
    
}
