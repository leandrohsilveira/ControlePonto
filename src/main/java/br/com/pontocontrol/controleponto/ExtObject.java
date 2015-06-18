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
import org.apache.commons.io.FilenameUtils;

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
    
    protected static String projectRootPath() {
        String buildDir = FilenameUtils.getFullPath(main().getProtectionDomain().getCodeSource().getLocation().getFile());
        return new File(buildDir).getParentFile().getAbsolutePath();
    }
    
    protected static String projectDataPath() {
        return format("%s/data", projectRootPath());
    }
    
    protected static String projectImagesPath() {
        return format("%s/img", projectRootPath());
    }
    
    protected static String projectBinPath() {
        return format("%s/bin", projectRootPath());
    }
    
    protected static File getFileResource(String path, String resource) {
        return new File(format("%s/%s", path, resource));
    }
    
    protected static Image getFileImageResource(String res) {
        try {
            final File file = getFileResource(projectImagesPath(), res);
            return ImageIO.read(file);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Erro ao ler ícone para janela e barra de tarefas.", ex);
            return null;
        }
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
