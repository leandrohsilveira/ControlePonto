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
import br.com.pontocontrol.controleponto.controller.json.impl.RegistroDiarioPontoJSON;
import br.com.pontocontrol.controleponto.event.Evento;
import br.com.pontocontrol.controleponto.event.Mensagem;
import br.com.pontocontrol.controleponto.model.ConfiguracoesUsuario;
import br.com.pontocontrol.controleponto.model.FolhaMensalPonto;
import br.com.pontocontrol.controleponto.model.RegistroDiarioPonto;
import br.com.pontocontrol.controleponto.util.TimeUtils;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author silveira
 */
public class PontoControlViewState {

   private static final SimpleDateFormat MES_ANO_FORMATTER = new SimpleDateFormat("MMMM yyyy");
   private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
   private static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat("0.00");
   private static final String TIME_PATTERN = "HH:mm:ss";

   private final IFolhaPontoController folhaPontocontroller = ControllerFactory.localizar(IFolhaPontoController.class);
   private final IExportadorXLSController exportadorController = ControllerFactory.localizar(IExportadorXLSController.class);
   private long usrExp;
   private final ConfiguracoesUsuario usuarioAutenticado = SessaoManager.getInstance().getUsuarioAutenticado();

   private Calendar dataExibicao = Calendar.getInstance();
   private Calendar dataEdicao;

   private FolhaMensalPonto folhaMensal;
   private FolhaMensalPontoJSON folhaPontoJson;
   private Double totalMensal;
   private Double variacaoMensal;
   private long totalEsperadoMensal;

   private RegistroDiarioPonto registroDiario;
   private LocalTime totalExpediente;
   private LocalTime totalAlmoco;
   private Double variacaoExpediente;

   private Evento fazerDepoisRegistrarAgora;
   private Evento fazerDepoisRemoverRegistro;
   private Evento fazerDepoisSalvarRegistro;
   private Mensagem interfaceMensagemConfirmacao;
   private Mensagem interfaceMensagemInformativa;

   public PontoControlViewState() {
      carregarFolhaMensal();
   }

   private void carregarFolhaMensal() {
      int mes = dataExibicao.get(Calendar.MONTH);
      int ano = dataExibicao.get(Calendar.YEAR);

      folhaPontoJson = folhaPontocontroller.recuperarFolhaMensal(ano, mes);
      if (folhaPontoJson != null) {
         folhaMensal = folhaPontoJson.toModel();
         usrExp = usuarioAutenticado.getExpediente() / TimeUtils.OFFSET_1_HORA;
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

   public String getEntrada() {
      return TimeUtils.fromLocalTime(registroDiario.getEntrada(), TIME_PATTERN);
   }

   public String getAlmoco() {
      return TimeUtils.fromLocalTime(registroDiario.getAlmoco(), TIME_PATTERN);
   }

   public String getRetorno() {
      return TimeUtils.fromLocalTime(registroDiario.getRetorno(), TIME_PATTERN);
   }

   public String getSaida() {
      return TimeUtils.fromLocalTime(registroDiario.getSaida(), TIME_PATTERN);
   }

   public void setEntrada(String time) {
      registroDiario.setEntrada(TimeUtils.toLocalTime(time, TIME_PATTERN));
   }

   public void setAlmoco(String time) {
      registroDiario.setAlmoco(TimeUtils.toLocalTime(time, TIME_PATTERN));
   }

   public void setRetorno(String time) {
      registroDiario.setRetorno(TimeUtils.toLocalTime(time, TIME_PATTERN));
   }

   public void setSaida(String time) {
      registroDiario.setSaida(TimeUtils.toLocalTime(time, TIME_PATTERN));
   }

   public void setDataSelecionada(String dataSelecionada) {
      try {
         dataEdicao = Calendar.getInstance();
         dataEdicao.setTime(DATE_FORMATTER.parse(dataSelecionada));
         int dia = dataEdicao.get(Calendar.DAY_OF_MONTH);
         editarRegistroDiario(dia);
      } catch (ParseException ex) {
         Logger.getLogger(PontoControlViewState.class.getName()).log(Level.SEVERE, null, ex);
         exibirMensagemInformativa("Erro ao converter a data.", String.format("Não foi possível converter o valor \"%s\" para data, o padrão informado deve ser dd/mm/yyyy (%s)", dataSelecionada, DATE_FORMATTER.format(new Date())), Mensagem.TIPO_ERRO);
      }
   }

   public Date getIntervaloSelecionado() {
      return dataExibicao.getTime();
   }

   public String getIntervaloSelecionadoAsString() {
      return MES_ANO_FORMATTER.format(getIntervaloSelecionado());
   }

   public Date getDataSelecionada() {
      return dataEdicao != null ? dataEdicao.getTime() : null;
   }

   public String getDataSelecionadaAsString() {
      return dataEdicao != null ? DATE_FORMATTER.format(getDataSelecionada()) : "";
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

   public String getTotalExpediente() {
      if (totalExpediente != null) {
         return String.format("%s (%.3f)", TimeUtils.fromLocalTime(totalExpediente, TIME_PATTERN), (TimeUtils.toNumberLocalTime(totalExpediente, usuarioAutenticado.getExpediente())));
      }
      return "-";
   }

   public String getTotalAlmoco() {
      if (totalAlmoco != null) {
         return String.format("%s (%.3f)", TimeUtils.fromLocalTime(totalAlmoco, TIME_PATTERN), (TimeUtils.toNumberLocalTime(totalAlmoco, usuarioAutenticado.getAlmoco())));
      }
      return "-";
   }

   public String getVariacaoExpediente() {
      if (variacaoExpediente != null) {
         return String.format("%s (%.3f)", TimeUtils.fromLocalTime(TimeUtils.fromNumberLocalTime(variacaoExpediente, usuarioAutenticado.getExpediente()), TIME_PATTERN), (variacaoExpediente));
      }
      return "-";
   }

   public String getTotalEsperadoMensal() {
      return Long.toString(totalEsperadoMensal);
   }

   public RegistroDiarioPonto getRegistroDiario(int dia) {
      carregarFolhaMensal();
      return folhaMensal.getRegistros().get(dia);
   }

   public void removerRegistroDiario(int dia) {
      if (exibirMensagemConfirmacao("Remover registro de ponto.", "Deseja realmente remover este registro?")) {
         RegistroDiarioPonto removido = folhaMensal.getRegistros().remove(dia);
         if (removido != null) {
            folhaPontocontroller.sincronizar(folhaMensal);
            carregarFolhaMensal();
            executarEvento(fazerDepoisRemoverRegistro);
            exibirMensagemInformativa("Registro removido", "O Registro foi removido com sucesso.", Mensagem.TIPO_INFO);
         } else {
            exibirMensagemInformativa("Registro NÃO removido", "Ocorreu um erro ao excluir o Registro selecionado.", Mensagem.TIPO_ERRO);
         }
      }
   }

   public boolean salvarRegistroDiario() {
      if (exibirMensagemConfirmacao("Salvar registro", "Deseja realmente salvar o registro de ponto?")) {
         calcularRegistroDiario();
         int dia = dataEdicao.get(Calendar.DAY_OF_MONTH);
         registroDiario.setDia(dia);
         folhaMensal.getRegistros().put(registroDiario.getDia(), registroDiario);
         folhaPontocontroller.sincronizar(folhaMensal);
         carregarFolhaMensal();
         executarEvento(fazerDepoisSalvarRegistro);
         exibirMensagemInformativa("Registro armazenado", "O registro de ponto foi armazenado com sucesso.", Mensagem.TIPO_INFO);
         return true;
      }
      return true;
   }

   public void editarRegistroDiario(int dia) {
      RegistroDiarioPontoJSON registroJson = folhaPontoJson.registros.get(dia);
      if (registroJson != null) {
         dataEdicao = Calendar.getInstance();
         dataEdicao.set(Calendar.DAY_OF_MONTH, dia);
         registroDiario = registroJson.toModel();
         calcularRegistroDiario();
      } else {
         registroDiario = new RegistroDiarioPonto();
         calcularRegistroDiario();
      }
   }

   public void novoRegistroDiario() {
      dataEdicao = null;
      registroDiario = new RegistroDiarioPonto();
      calcularRegistroDiario();
   }

   public void calcularRegistroDiario() {
      if (registroDiario != null) {
         totalAlmoco = registroDiario.calcularTotalAlmoco();
         totalExpediente = registroDiario.calcularTotalExpediente();
         variacaoExpediente = registroDiario.calcularVariacaoExpediente();
      }
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
      executarEvento(fazerDepoisRegistrarAgora);
   }

   private void executarEvento(Evento evento) {
      if (evento != null) {
         evento.iniciar();
      }
   }

   private void exibirMensagemInformativa(String sumario, String descricao, int tipo) {
      if (interfaceMensagemInformativa != null) {
         interfaceMensagemInformativa.exibirMensagem(sumario, descricao, tipo);
      } else {
         System.out.println(String.format("Sumario: %s", sumario));
         System.out.println(String.format("Descricao: %s", descricao));
         System.out.println(String.format("Tipo: %d", tipo));
      }
   }

   private boolean exibirMensagemConfirmacao(String sumario, String descricao) {
      if (interfaceMensagemConfirmacao != null) {
         return interfaceMensagemConfirmacao.exibirMensagem(sumario, descricao, Mensagem.TIPO_INFO);
      }
      return true;
   }

   public void setFazerDepoisRegistrarAgora(Evento fazerDepoisRegistrarAgora) {
      this.fazerDepoisRegistrarAgora = fazerDepoisRegistrarAgora;
   }

   public void setFazerDepoisRemoverRegistro(Evento fazerDepoisRemoverRegistro) {
      this.fazerDepoisRemoverRegistro = fazerDepoisRemoverRegistro;
   }

   public void setFazerDepoisSalvarRegistro(Evento fazerDepoisSalvarRegistro) {
      this.fazerDepoisSalvarRegistro = fazerDepoisSalvarRegistro;
   }

   public void setInterfaceMensagemInformativa(Mensagem mensagem) {
      this.interfaceMensagemInformativa = mensagem;
   }

   public void setInterfaceMensagemConfirmacao(Mensagem mensagem) {
      this.interfaceMensagemConfirmacao = mensagem;
   }

}
