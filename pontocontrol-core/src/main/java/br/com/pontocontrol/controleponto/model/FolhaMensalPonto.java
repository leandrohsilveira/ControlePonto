/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pontocontrol.controleponto.model;

import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Leandro
 */
public class FolhaMensalPonto {

   public FolhaMensalPonto() {
   }

   public FolhaMensalPonto(int ano, int mes) {
      this.ano = ano;
      this.mes = mes;
   }

   private int ano;
   private int mes;
   private Map<Integer, RegistroDiarioPonto> registros = new TreeMap<>();

   public Double calcularTotalMensal() {
      return registros.entrySet().stream().mapToDouble((entry) -> entry.getValue().calcularTotalExpedienteAsNumber()).sum();
   }

   public Double calcularTotalMensal(long expediente) {
      return (calcularTotalMensal() * ((double) expediente));
   }

   public Double calcularVariacaoMensal() {
      return calcularTotalMensal() - calcularTotalMensalEsperado();
   }

   public Double calcularVariacaoMensal(long expediente) {
      return (calcularVariacaoMensal() * ((double) expediente));
   }

   public long calcularTotalMensalEsperado() {
      if (Calendar.getInstance().get(Calendar.MONTH) == mes) {
         return registros.entrySet().stream().filter((entry) -> entry.getValue().contabilizarRegistroDiario()).count();
      } else {
         return registros.entrySet().stream().count();
      }
   }

   public long calcularTotalMensalEsperado(long expediente) {
      return calcularTotalMensalEsperado() * expediente;
   }

   public int getAno() {
      return ano;
   }

   public void setAno(int ano) {
      this.ano = ano;
   }

   public int getMes() {
      return mes;
   }

   public void setMes(int mes) {
      this.mes = mes;
   }

   public Map<Integer, RegistroDiarioPonto> getRegistros() {
      return registros;
   }

   public void setRegistros(Map<Integer, RegistroDiarioPonto> registros) {
      this.registros = registros;
   }

}
