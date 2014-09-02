/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Leandro
 */
public class ExtObject {
    
    private static final Logger LOG = Logger.getLogger(ExtObject.class.getName());
    
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
    
    protected static File getFileResource(String resource) {
        try {
            final URL url = main().getClassLoader().getResource(resource);
            if(url != null) {
                URI uri = url.toURI();
                return new File(uri);
            } else {
                LOG.severe("Resource não encontrado.");
            }
        } catch (URISyntaxException ex) {
            LOG.log(Level.SEVERE, "Erro de sintaxe de URI.", ex);
        }
        return null;
    }
    
    public static Image getFileImageResource(String res) {
        try {
            final File file = getFileResource(res);
            return ImageIO.read(file);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Erro ao ler ícone para janela e barra de tarefas.", ex);
            return null;
        }
    }
    
     public InputStream getResourceAsStream(String res) {
        return main().getResourceAsStream(res);
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
