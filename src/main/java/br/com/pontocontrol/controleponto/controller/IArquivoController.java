/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

/**
 *
 * @author Leandro
 */
public interface IArquivoController {
    
    List<Integer> getAvalableFileMonths();

    List<Integer> getAvalableFileMonths(int ano);
    
    String getYearPath();
    
    String getYearPath(int ano);
    
    String getMonthFile();
    
    String getMonthFile(int month);
    
    File recuperarArquivo(String path, String nome);
    
    BufferedWriter getArquivoParaEscrever(File arquivo);
    
    BufferedReader getArquivoParaLer(File arquivo);
    
    boolean salvarArquivo(Writer writer);
    
    boolean fecharArquivoLeitura(Reader reader);
    
}
