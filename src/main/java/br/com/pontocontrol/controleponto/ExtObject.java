/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Leandro
 */
public class ExtObject {
    
    protected static String format(String pattern, Object... args) {
        return String.format(pattern, args);
    }
    
    protected static String formatNumber(Object arg) {
        return DecimalFormat.getNumberInstance().format(arg);
    }
    
    protected static String formatNumber(String pattern, Object arg) {
        return new DecimalFormat(pattern).format(arg);
    }
    
    protected static String formatDate(String pattern, Date date) {
        return new SimpleDateFormat(pattern).format(date);
    }
    
    protected static String formatDate(Date date) {
        return SimpleDateFormat.getDateTimeInstance().format(date);
    }
    
    protected static Date now() {
        return new Date();
    }
    
    protected static String formatNow() {
        return formatDate(now());
    }
    
    protected static String formatNow(String pattern) {
        return formatDate(pattern, now());
    }
    
    protected static Class<?> main() {
        return ControlePonto.class;
    }
    
    protected static String mainPath() {
        final String path = main().getProtectionDomain().getCodeSource().getLocation().getPath();
        return path.contains(".exe") || path.contains(".jar") ? path.replaceAll(".exe", "").replaceAll(".jar", "") : path;
    }
    
    protected static int getMesAtual() {
        return Calendar.getInstance().get(Calendar.MONTH);
    }
    
    protected static int getDiaAtual() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }
    
    protected static int getAnoAtual() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }
    
}
