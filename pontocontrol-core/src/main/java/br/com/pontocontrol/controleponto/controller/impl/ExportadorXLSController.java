/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pontocontrol.controleponto.controller.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.IndexedColors;

import br.com.pontocontrol.controleponto.SessaoManager;
import br.com.pontocontrol.controleponto.controller.IExportadorXLSController;
import br.com.pontocontrol.controleponto.model.FolhaMensalPonto;
import br.com.pontocontrol.controleponto.model.RegistroDiarioPonto;

/**
 *
 * @author silveira
 */
public class ExportadorXLSController implements IExportadorXLSController {

   private ExportadorXLSController() {
   }

   private static ExportadorXLSController instance;
   private static final Logger LOG = Logger.getLogger(ExportadorXLSController.class.getName());

   /**
    *
    * @return a instância singleton do Controller.
    */
   public static ExportadorXLSController getInstance() {
      if (instance == null) {
         instance = new ExportadorXLSController();
      }
      return instance;
   }

   private interface CELL_INDEX {

      public interface TOTAL_ROW {

         public static final int TOTAL_TXT = 0;
         public static final int TOTAL_VALUE = 1;
         public static final int VAR_TXT = 2;
         public static final int VAR_VALUE = 3;
      }

      public static final int DIA = 0;
      public static final int ENTRADA = 1;
      public static final int ALMOCO = 2;
      public static final int RETORNO = 3;
      public static final int SAIDA = 4;
      public static final int TOTAL_EXP = 5;
      public static final int VARIACAO = 6;
   }

   private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

   @Override
   public boolean extrair(FolhaMensalPonto folhaMensal, String outputFileDir) {
      Calendar data = Calendar.getInstance();
      data.set(Calendar.MONTH, folhaMensal.getMes());
      data.set(Calendar.YEAR, folhaMensal.getAno());
      final String nomeArquivo = String.format("%s-%s_%s.xls",
                                               SessaoManager.getInstance().getUsuarioAutenticado().getLogin(),
                                               DateFormatUtils.format(data.getTime(), "yyyy-MMMM"),
                                               DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS"));
      final String filePath = String.format("%s/%s", outputFileDir, nomeArquivo);
      File arquivoXLS = new File(filePath);
      FileOutputStream fos = null;
      try {
         arquivoXLS.createNewFile();

         fos = new FileOutputStream(arquivoXLS);

         HSSFWorkbook workbook = HSSFWorkbook.create(InternalWorkbook.createWorkbook());

         final String nomePlanilha = DateFormatUtils.format(data.getTime(), "MMM-yy");
         HSSFSheet planilha = workbook.createSheet(nomePlanilha);

         //TOTAL ROW
         HSSFRow totalRow = planilha.createRow(0);
         totalRow.createCell(CELL_INDEX.TOTAL_ROW.TOTAL_TXT).setCellValue("Total:");
         totalRow.createCell(CELL_INDEX.TOTAL_ROW.TOTAL_VALUE).setCellValue(folhaMensal.calcularTotalMensal());
         totalRow.createCell(CELL_INDEX.TOTAL_ROW.VAR_TXT).setCellValue("Variação:");
         totalRow.createCell(CELL_INDEX.TOTAL_ROW.VAR_VALUE).setCellValue(folhaMensal.calcularVariacaoMensal());

         //HEADER
         HSSFRow headerRow = planilha.createRow(1);
         headerRow.createCell(CELL_INDEX.DIA).setCellValue("Dia");
         headerRow.createCell(CELL_INDEX.ENTRADA).setCellValue("Entrada");
         headerRow.createCell(CELL_INDEX.ALMOCO).setCellValue("Almoço");
         headerRow.createCell(CELL_INDEX.RETORNO).setCellValue("Retorno");
         headerRow.createCell(CELL_INDEX.SAIDA).setCellValue("Saída");
         headerRow.createCell(CELL_INDEX.TOTAL_EXP).setCellValue("Expediente");
         headerRow.createCell(CELL_INDEX.VARIACAO).setCellValue("Variação");

         formatHeaderRow(workbook, headerRow);
         Calendar cal = Calendar.getInstance();
         cal.set(Calendar.YEAR, folhaMensal.getAno());
         cal.set(Calendar.MONTH, folhaMensal.getMes());

         for (int dia = 1; dia <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); dia++) {
            int i = planilha.getPhysicalNumberOfRows();
            HSSFRow row = planilha.createRow(i);
            cal.set(Calendar.DAY_OF_MONTH, dia);
            row.createCell(CELL_INDEX.DIA).setCellValue(DateFormatUtils.format(cal.getTime(), "MMM dd, EEE"));

            RegistroDiarioPonto reg = folhaMensal.getRegistros().get(dia);
            if (reg != null) {
               row.createCell(CELL_INDEX.ENTRADA).setCellValue(reg.getEntrada() != null ? reg.getEntrada().format(TIME_FORMATTER) : "");
               row.createCell(CELL_INDEX.ALMOCO).setCellValue(reg.getAlmoco() != null ? reg.getAlmoco().format(TIME_FORMATTER) : "");
               row.createCell(CELL_INDEX.RETORNO).setCellValue(reg.getRetorno() != null ? reg.getRetorno().format(TIME_FORMATTER) : "");
               row.createCell(CELL_INDEX.SAIDA).setCellValue(reg.getSaida() != null ? reg.getSaida().format(TIME_FORMATTER) : "");
               row.createCell(CELL_INDEX.TOTAL_EXP).setCellValue(reg.isRegistroDiarioCompleto() ? reg.calcularTotalExpedienteAsNumber() : 0);
               row.createCell(CELL_INDEX.VARIACAO).setCellValue(reg.isRegistroDiarioCompleto() ? reg.calcularVariacaoExpediente() : 0);
            } else {
               row.createCell(CELL_INDEX.ENTRADA).setCellValue("");
               row.createCell(CELL_INDEX.ALMOCO).setCellValue("");
               row.createCell(CELL_INDEX.RETORNO).setCellValue("");
               row.createCell(CELL_INDEX.SAIDA).setCellValue("");
               row.createCell(CELL_INDEX.TOTAL_EXP).setCellValue("");
               row.createCell(CELL_INDEX.VARIACAO).setCellValue("");
            }
            if (SessaoManager.getInstance().getUsuarioAutenticado().checarSeDiaExpediente(cal)) {
               formatRow(workbook, row);
            } else {
               formatHeaderRow(workbook, row);
            }
         }
         for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            planilha.autoSizeColumn(i);
         }
         workbook.write(fos);
         fos.flush();
         return true;
      } catch (IOException ex) {
         LOG.log(Level.SEVERE, "Erro ao criar arquivo XLS de saída", ex);
         return false;
      } finally {
         IOUtils.closeQuietly(fos);
      }
   }

   private void formatHeaderRow(HSSFWorkbook workbook, HSSFRow row) {
      HSSFFont headerFont = workbook.createFont();
      headerFont.setFontHeightInPoints((short) 10);
      headerFont.setFontName("Arial");
      headerFont.setColor(IndexedColors.WHITE.getIndex());
      headerFont.setBoldweight((short) 800);
      headerFont.setItalic(false);
      HSSFCellStyle headerStyle = workbook.createCellStyle();
      headerStyle.setFont(headerFont);
      headerStyle.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
      headerStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
      headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
      row.cellIterator().forEachRemaining((cell) -> {
         cell.setCellStyle(headerStyle);
      });
   }

   private void formatRow(HSSFWorkbook workbook, HSSFRow row) {
      HSSFCellStyle styleMid = workbook.createCellStyle();
      styleMid.setBorderTop(HSSFCellStyle.BORDER_THIN);
      styleMid.setBorderBottom(HSSFCellStyle.BORDER_THIN);

      HSSFCellStyle styleInit = workbook.createCellStyle();
      styleInit.cloneStyleFrom(styleMid);
      styleInit.setBorderLeft(HSSFCellStyle.BORDER_THIN);

      HSSFCellStyle styleFinal = workbook.createCellStyle();
      styleFinal.cloneStyleFrom(styleMid);
      styleFinal.setBorderRight(HSSFCellStyle.BORDER_THIN);

      row.cellIterator().forEachRemaining((cell) -> {
         int index = cell.getColumnIndex();
         int numOfCols = row.getPhysicalNumberOfCells();
         if (index == 0) {
            cell.setCellStyle(styleInit);
         } else if (index == (numOfCols - 1)) {
            cell.setCellStyle(styleFinal);
         } else {
            cell.setCellStyle(styleMid);
         }
      });
   }

}
