/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pontocontrol.controleponto.view;

import br.com.pontocontrol.controleponto.SessaoManager;
import br.com.pontocontrol.controleponto.controller.ControllerFactory;
import br.com.pontocontrol.controleponto.controller.IExportadorXLSController;
import br.com.pontocontrol.controleponto.controller.IFolhaPontoController;
import br.com.pontocontrol.controleponto.controller.json.impl.FolhaMensalPontoJSON;
import br.com.pontocontrol.controleponto.model.ConfiguracoesUsuario;
import br.com.pontocontrol.controleponto.model.FolhaMensalPonto;
import br.com.pontocontrol.controleponto.model.RegistroDiarioPonto;
import br.com.pontocontrol.controleponto.util.TimeUtils;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author silveira
 */
public class FolhaPontoMensalViewState {

   private static final SimpleDateFormat MES_ANO_FORMATTER = new SimpleDateFormat("MMMM yyyy");
   private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("0.00");

   private final IFolhaPontoController folhaPontocontroller = ControllerFactory.localizar(IFolhaPontoController.class);
   private final IExportadorXLSController exportadorController = ControllerFactory.localizar(IExportadorXLSController.class);

   private final ConfiguracoesUsuario usuarioAutenticado = SessaoManager.getInstance().getUsuarioAutenticado();

   private Calendar dataExibicao = Calendar.getInstance();
   private FolhaMensalPonto folhaMensal;
   private Double totalMensal;
   private Double variacaoMensal;
   private long totalEsperadoMensal;

   public FolhaPontoMensalViewState() {
      carregarFolhaMensal();
   }

   private void carregarFolhaMensal() {
      int mes = dataExibicao.get(Calendar.MONTH);
      int ano = dataExibicao.get(Calendar.YEAR);

      FolhaMensalPontoJSON json = folhaPontocontroller.recuperarFolhaMensal(ano, mes);
      if (json != null) {
         folhaMensal = json.toModel();
         long usrOffset = usuarioAutenticado.getOffset();
         long usrExp = usrOffset / TimeUtils.OFFSET_1_HORA;
         totalMensal = folhaMensal.calcularTotalMensal(usrExp);
         variacaoMensal = folhaMensal.calcularVariacaoMensal(usrExp);
         totalEsperadoMensal = folhaMensal.calcularTotalMensalEsperado(usrExp);
      }
   }

   public void proximoMes() {
      dataExibicao.add(Calendar.MONTH, 1);
      carregarFolhaMensal();
   }

   public void mesAnterior() {
      dataExibicao.add(Calendar.MONTH, -1);
      carregarFolhaMensal();
   }

   public void mesAtual() {
      dataExibicao = Calendar.getInstance();
      carregarFolhaMensal();
   }

   public void proximoAno() {
      dataExibicao.add(Calendar.YEAR, 1);
      carregarFolhaMensal();
   }

   public void anoAnterior() {
      dataExibicao.add(Calendar.YEAR, -1);
      carregarFolhaMensal();
   }

   public Date getIntervaloAtual() {
      return dataExibicao.getTime();
   }

   public String getIntervaloAtualAsString() {
      return MES_ANO_FORMATTER.format(getIntervaloAtual());
   }

   public Set<Map.Entry<Integer, RegistroDiarioPonto>> getListaRegistros() {
      return folhaMensal != null ? folhaMensal.getRegistros().entrySet() : new LinkedHashSet<>();
   }

   public String getTotalMensal() {
      return DECIMAL_FORMATTER.format(totalMensal);
   }

   public String getVariacaoMensal() {
      return DECIMAL_FORMATTER.format(variacaoMensal);
   }

   public String getTotalEsperadoMensal() {
      return Long.toString(totalEsperadoMensal);
   }

   public RegistroDiarioPonto getRegistroDiario(int dia) {
      carregarFolhaMensal();
      return folhaMensal.getRegistros().get(dia);
   }

   public boolean removerRegistroDiario(int dia) {
      RegistroDiarioPonto removido = folhaMensal.getRegistros().remove(dia);
      folhaPontocontroller.sincronizar(folhaMensal);
      carregarFolhaMensal();
      return removido != null;
   }

   public boolean extrairParaXLS() {
      carregarFolhaMensal();
      return exportadorController.extrair(folhaMensal, usuarioAutenticado.getPathUsuario());
   }

   public void registrarAgora() {
      dataExibicao = Calendar.getInstance();
      carregarFolhaMensal();
      int dia = dataExibicao.get(Calendar.DAY_OF_MONTH);
      RegistroDiarioPonto registroDoDia = folhaMensal.getRegistros().getOrDefault(dia, new RegistroDiarioPonto(dia));
      registroDoDia.registrarProximoAgora();
      folhaMensal.getRegistros().putIfAbsent(dia, registroDoDia);
      folhaPontocontroller.sincronizar(folhaMensal);
      carregarFolhaMensal();
   }

}
