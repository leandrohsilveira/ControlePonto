/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.controller.impl;

import br.com.pontocontrol.controleponto.ExtObject;
import br.com.pontocontrol.controleponto.SessaoManager;
import br.com.pontocontrol.controleponto.controller.IArquivoController;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Leandro
 */
public class ArquivoController extends ExtObject implements IArquivoController {
    
    public static final String PREFIXO_PASTAS = "Y";
    public static final String PREFIXO_ARQUIVOS = "reg";
    public static final String ARQUIVO_SPLITTER = "_";
    public static final String SUFIXO_ARQUIVOS_PARSER = "MMM";
    public static final String EXTENSAO_ARQUIVOS = "cpr";

    //SINGLETON PATTERN
    
    private ArquivoController() {}
    private static ArquivoController instance;
    public static ArquivoController getInstance() {
        if(instance == null) {
            LOG.info(format("Criando nova instância singleton da classe \"%s\"", ArquivoController.class.getName()));
            instance = new ArquivoController();
        }
        return instance;
    }
    
    //CLASS:
    
    private static final Logger LOG = Logger.getLogger(ArquivoController.class.getName());
    
    @Override
    public String getYearPath() {
        Calendar calendar = Calendar.getInstance();
        int ano = calendar.get(Calendar.YEAR);
        return getYearPath(ano);
    }

    @Override
    public String getYearPath(int ano) {
        String path = format("%s/%s_%d", SessaoManager.getInstance().getUsuarioAutenticado().getPathUsuario(), PREFIXO_PASTAS, ano);
        LOG.info(format("Recuperando diretório do ANO %d: %s", ano, path));
        return path;
    }
    
    @Override
    public List<Integer> getAvalableYearFolders() {
        String pathUsuario = SessaoManager.getInstance().getUsuarioAutenticado().getPathUsuario();
        File dir = new File(pathUsuario);
        List<Integer> saida = new ArrayList<Integer>();
        if(dir.exists()) {
            for (String path : dir.list()) {
                File file = new File(format("%s/%s", pathUsuario, path));
                if(file.isDirectory()) {
                    final String nomeDir = FilenameUtils.getBaseName(file.getName());
                    final String[] dirSplit = nomeDir.split(ARQUIVO_SPLITTER);
                    if(dirSplit.length == 2 && PREFIXO_PASTAS.equalsIgnoreCase(dirSplit[0])) {
                        String anoStr = dirSplit[1];
                        try {
                            saida.add(Integer.valueOf(anoStr));
                        } catch (NumberFormatException ex) {
                            LOG.log(Level.SEVERE, format("Erro ao executar parse da pasta \"%s\", o padrão \"%s\" não é um ano válido.", nomeDir, anoStr), ex);
                        }
                    }
                    
                }
                
            }
        }
        return saida;
    }
    
    @Override
    public List<Integer> getAvalableFileMonths(int ano) {
        File dir = new File(getYearPath(ano));
        List<Integer> saida = new ArrayList<Integer>();
        if(dir.exists()) {
            File[] listFiles = dir.listFiles();
            for (File file : listFiles) {
                final String ext = FilenameUtils.getExtension(file.getName());
                final String nomeArquivo = FilenameUtils.getBaseName(file.getName());
                final String[] nomeArquivoSplit = nomeArquivo.split("_");
                if(file.isFile() && EXTENSAO_ARQUIVOS.equalsIgnoreCase(ext) && PREFIXO_ARQUIVOS.equalsIgnoreCase(nomeArquivoSplit[0])) {
                    String mesStr = nomeArquivoSplit[1];
                    try {
                        Date date = new SimpleDateFormat(SUFIXO_ARQUIVOS_PARSER).parse(mesStr);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        saida.add(calendar.get(Calendar.MONTH));
                    } catch (ParseException ex) {
                        LOG.log(Level.SEVERE, format("Erro ao executar parse de data da entrada \"%s\" através do padrão \"MMM\" o arquivo será descartado.", mesStr), ex);
                    }
                }
            }
        }
        return saida;
    }
    
    @Override
    public List<Integer> getAvalableFileMonths() {
        return getAvalableFileMonths(Calendar.getInstance().get(Calendar.YEAR));
    }

    @Override
    public String getMonthFile(int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, month);
        return format("%s%s%s.%s", PREFIXO_ARQUIVOS, ARQUIVO_SPLITTER, formatDate(SUFIXO_ARQUIVOS_PARSER, calendar.getTime()), EXTENSAO_ARQUIVOS);
    }
    
    @Override
    public String getMonthFile() {
        return getMonthFile(Calendar.getInstance().get(Calendar.MONTH));
    }
    
    @Override
    public File recuperarArquivo(String path, String nome) {
        return recuperarArquivo(path, nome, false);
    }

    @Override
    public File recuperarArquivo(String path, String nome, boolean criar) {
        File dir = new File(path);
        if(!dir.exists() && criar) {
            dir.mkdirs();
        }
        String diretorioArquivo = format("%s/%s", path, nome);
        LOG.info(format("Recuperando arquivo: %s", diretorioArquivo));
        File arquivo = new File(diretorioArquivo);
        if(arquivo.exists()) {
            LOG.info(format("Arquivo %s encontrado.", nome));
        } else {
            if(criar) {
                LOG.info(format("Arquivo %s não foi encontrado e será criado.", nome));
                try {
                    arquivo.createNewFile();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, format("Erro ao criar o arquivo \"%s\"", nome), ex);
                }
            } else {
                LOG.info(format("Arquivo %s não foi encontrado.", nome));
            }
        }
        return arquivo;
    }
    
    @Override
    public BufferedWriter getArquivoParaEscrever(File arquivo) {
        try {
            return new BufferedWriter(new FileWriter(arquivo));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Erro ao obter o objeto escritor para o arquivo", ex);
            return null;
        }
    }

    @Override
    public BufferedReader getArquivoParaLer(File arquivo) {
        try {
            return new BufferedReader(new FileReader(arquivo));
        } catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, "Erro ao obter o objeto leitor para o arquivo", ex);
            return null;
        }
    }
    
    @Override
    public boolean salvarArquivo(Writer writer) {
        try {
            writer.flush();
            writer.close();
            return true;
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Erro ao fechar escritor de arquivos", ex);
            return false;
        }
    }

    @Override
    public boolean fecharArquivoLeitura(Reader reader) {
        try {
            reader.close();
            return true;
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Erro ao fechar leitor de arquivos", ex);
            return false;
        }
    }

}
