/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pontocontrol.controleponto.swing;

import java.awt.Image;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import br.com.pontocontrol.controleponto.FrameManager;
import br.com.pontocontrol.controleponto.SessaoManager;
import br.com.pontocontrol.controleponto.model.RegistroDiarioPonto;
import br.com.pontocontrol.controleponto.swing.event.MensagemConfirmacao;
import br.com.pontocontrol.controleponto.swing.event.MensagemInformativa;
import br.com.pontocontrol.controleponto.swing.util.SwingUtils;
import br.com.pontocontrol.controleponto.util.TimeUtils;
import br.com.pontocontrol.controleponto.view.PontoControlViewState;

/**
 *
 * @author Leandro
 */
public class PainelPrincipalFrame extends javax.swing.JFrame {

   /**
    * Creates new form PainelPrincipalFrame
    */
   public PainelPrincipalFrame() {
      initComponents();
      init();
      FrameManager.getInstance().registrarFrame(ID, this);
   }

   public static final String ID = "frame-principal";
   public static final String TITULO = "PontoController - Painel Principal";
   private static final Logger LOG = Logger.getLogger(PainelPrincipalFrame.class.getName());
   private final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

   private DefaultTableModel tableModel;
   private PontoControlViewState viewState;

   private void init() {
      configurarViewState();
      tableModel = (DefaultTableModel) tabelaRegistros.getModel();
      atualizarValores();
      cmpUsuario.setText(SessaoManager.getInstance().getUsuarioAutenticado().getLogin());
      Image img = SwingUtils.getImage("icon.png");
      if (img != null) {
         setIconImage(img);
      }
      setTitle(TITULO);
   }

   private void configurarViewState() {
      viewState = new PontoControlViewState(SessaoManager.getInstance().getUsuarioAutenticado());
      viewState.setFazerDepoisRegistrarAgora(() -> {
         viewState.mesAtual();
         atualizarValores();
         atualizarBotoes();
      });
      viewState.setFazerDepoisRemoverRegistro(() -> {
         atualizarValores();
         atualizarBotoes();
      });

      viewState.setInterfaceMensagemInformativa(new MensagemInformativa(this));
      viewState.setInterfaceMensagemConfirmacao(new MensagemConfirmacao(this));
   }

   private void limparTabela() {
      while (tableModel.getRowCount() > 0) {
         tableModel.removeRow(0);
      }
   }

   public void atualizarValores() {
      limparTabela();
      long usrOffset = SessaoManager.getInstance().getUsuarioAutenticado().getOffset();
      cmpTotalMes.setText(String.format("%s - %s", viewState.getTotalMensal(), viewState.getTotalEsperadoMensal()));
      cmpTotalMes.setToolTipText(String.format("Variação: %s", viewState.getVariacaoMensal()));
      labelPeríodo.setText(viewState.getIntervaloSelecionadoAsString());
      viewState.getListaRegistros().forEach((entry) -> {
         RegistroDiarioPonto reg = entry.getValue();
         LocalTime totalTime = reg.calcularTotalExpediente();
         String entrada = reg.getEntrada() != null ? TIME_FORMATTER.format(reg.getEntrada()) : "Pendente";
         String almoco = reg.getAlmoco() != null ? TIME_FORMATTER.format(reg.getAlmoco()) : "Pendente";
         String retorno = reg.getRetorno() != null ? TIME_FORMATTER.format(reg.getRetorno()) : "Pendente";
         String saida = reg.getSaida() != null ? TIME_FORMATTER.format(reg.getSaida()) : "Pendente";
         String total = totalTime != null ? TIME_FORMATTER.format(totalTime) : "-";
         String var = totalTime != null ? TimeUtils.fromNumberLocalTimeFormatted(reg.calcularVariacaoExpediente(), usrOffset) : "-";
         addRow(tableModel,
                reg.getDia(),
                entrada,
                almoco,
                retorno,
                saida,
                total,
                var);
      });
   }

   private void addRow(DefaultTableModel model, Object... objects) {
      model.addRow(objects);
   }

   private void atualizarBotoes() {
      btnRemoverRegistro.setEnabled(tabelaRegistros.getSelectedRowCount() == 1);
      btnEditarRegistro.setEnabled(tabelaRegistros.getSelectedRowCount() == 1);
      menuBtnRemoverRegistro.setEnabled(tabelaRegistros.getSelectedRowCount() == 1);
      menuBtnEditarRegistro.setEnabled(tabelaRegistros.getSelectedRowCount() == 1);
   }

   //** CRUD ACTIONS
   private void doNovo() {
      viewState.novoRegistroDiario();
      final EditarRegistroFrame janelaEditar = new EditarRegistroFrame(viewState);
      janelaEditar.setVisible(true);
      this.setEnabled(false);
      atualizarBotoes();
   }

   private void doEditar() {
      if (tabelaRegistros.getSelectedColumnCount() == 1 || tabelaRegistros.getSelectedRowCount() == 1) {
         Integer dia = (Integer) tabelaRegistros.getValueAt(tabelaRegistros.getSelectedRow(), 0);
         viewState.editarRegistroDiario(dia);
         final EditarRegistroFrame janelaEditar = new EditarRegistroFrame(viewState);
         janelaEditar.setVisible(true);
         this.setEnabled(false);
         atualizarBotoes();
      }
   }

   private void doRemover() {
      if (tabelaRegistros.getSelectedColumnCount() == 1 || tabelaRegistros.getSelectedRowCount() == 1) {
         for (Integer index : tabelaRegistros.getSelectedRows()) {
            Integer dia = (Integer) tableModel.getValueAt(index, 0);
            viewState.removerRegistroDiario(dia);
         }
      }
   }

   private void doExtrairXLS() {
      boolean ok = viewState.extrairParaXLS(SessaoManager.getInstance().getUsuarioAutenticado().getPathUsuario());
      if (ok) {
         JOptionPane.showMessageDialog(this, "Folha de Ponto mensal extraída com sucesso!", "Extrair para XLS", JOptionPane.INFORMATION_MESSAGE);
      } else {
         JOptionPane.showMessageDialog(this, "Ocorreu um problema ao extrair os registros para o formato XLS!", "Erro", JOptionPane.ERROR_MESSAGE);
      }
      atualizarBotoes();
   }

   private void doRegistrarAgora() {
      viewState.registrarAgora();
   }

   /**
    * This method is called from within the constructor to initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is always
    * regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jScrollPane2 = new javax.swing.JScrollPane();
      jEditorPane1 = new javax.swing.JEditorPane();
      painelPrincipal = new javax.swing.JPanel();
      jScrollPane1 = new javax.swing.JScrollPane();
      tabelaRegistros = new javax.swing.JTable();
      painelPeriodo = new javax.swing.JPanel();
      labelPeríodo = new javax.swing.JLabel();
      botaoProximoAno = new javax.swing.JButton();
      botaoProximoMes = new javax.swing.JButton();
      botaoMesAnterior = new javax.swing.JButton();
      botaoAnoAnterior = new javax.swing.JButton();
      botaoMesAtual = new javax.swing.JButton();
      painelBordaBtnAgora = new javax.swing.JPanel();
      botaoRegistrar = new javax.swing.JButton();
      cmpTotalMes = new javax.swing.JTextField();
      cmpUsuario = new javax.swing.JTextField();
      painelBordaBotoesTabela = new javax.swing.JPanel();
      btnRemoverRegistro = new javax.swing.JButton();
      btnEditarRegistro = new javax.swing.JButton();
      btnNovoRegistro = new javax.swing.JButton();
      barraMenu = new javax.swing.JMenuBar();
      menuUsuario = new javax.swing.JMenu();
      menuBtnLogoff = new javax.swing.JMenuItem();
      menuBtnSair = new javax.swing.JMenuItem();
      menuRegistros = new javax.swing.JMenu();
      menuBtnRegistrarAgora = new javax.swing.JMenuItem();
      menuBtnNovoRegistro = new javax.swing.JMenuItem();
      menuBtnEditarRegistro = new javax.swing.JMenuItem();
      menuBtnRemoverRegistro = new javax.swing.JMenuItem();
      submenuExportar = new javax.swing.JMenu();
      menuBtnExportarXLS = new javax.swing.JMenuItem();

      jScrollPane2.setViewportView(jEditorPane1);

      setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
      setMinimumSize(new java.awt.Dimension(800, 400));
      addFocusListener(new java.awt.event.FocusAdapter() {
         public void focusGained(java.awt.event.FocusEvent evt) {
            formFocusGained(evt);
         }
         public void focusLost(java.awt.event.FocusEvent evt) {
            formFocusLost(evt);
         }
      });

      painelPrincipal.setBackground(new java.awt.Color(255, 255, 255));
      painelPrincipal.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Controle de Ponto [PontoController]", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 24))); // NOI18N
      painelPrincipal.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            painelPrincipalMouseClicked(evt);
         }
      });

      tabelaRegistros.setModel(new javax.swing.table.DefaultTableModel(
         new Object [][] {

         },
         new String [] {
            "Dia", "Entrada", "Almoço", "Retorno", "Saida", "Expediente", "Variação"
         }
      ) {
         boolean[] canEdit = new boolean [] {
            false, false, false, false, false, false, false
         };

         public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
         }
      });
      tabelaRegistros.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            tabelaRegistrosMouseClicked(evt);
         }
      });
      jScrollPane1.setViewportView(tabelaRegistros);

      painelPeriodo.setBackground(new java.awt.Color(255, 255, 255));
      painelPeriodo.setBorder(javax.swing.BorderFactory.createTitledBorder("Período"));

      labelPeríodo.setText("Junho 2015");

      botaoProximoAno.setText(">>");
      botaoProximoAno.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            botaoProximoAnoActionPerformed(evt);
         }
      });

      botaoProximoMes.setText(">");
      botaoProximoMes.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            botaoProximoMesActionPerformed(evt);
         }
      });

      botaoMesAnterior.setText("<");
      botaoMesAnterior.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            botaoMesAnteriorActionPerformed(evt);
         }
      });

      botaoAnoAnterior.setText("<<");
      botaoAnoAnterior.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            botaoAnoAnteriorActionPerformed(evt);
         }
      });

      botaoMesAtual.setText("Atual");
      botaoMesAtual.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            botaoMesAtualActionPerformed(evt);
         }
      });

      javax.swing.GroupLayout painelPeriodoLayout = new javax.swing.GroupLayout(painelPeriodo);
      painelPeriodo.setLayout(painelPeriodoLayout);
      painelPeriodoLayout.setHorizontalGroup(
         painelPeriodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(painelPeriodoLayout.createSequentialGroup()
            .addContainerGap()
            .addComponent(labelPeríodo)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
            .addComponent(botaoAnoAnterior)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(botaoMesAnterior)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(botaoMesAtual)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(botaoProximoMes)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(botaoProximoAno))
      );
      painelPeriodoLayout.setVerticalGroup(
         painelPeriodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(painelPeriodoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(labelPeríodo)
            .addComponent(botaoProximoAno)
            .addComponent(botaoProximoMes)
            .addComponent(botaoMesAnterior)
            .addComponent(botaoAnoAnterior)
            .addComponent(botaoMesAtual))
      );

      painelBordaBtnAgora.setBackground(new java.awt.Color(255, 255, 255));
      painelBordaBtnAgora.setBorder(javax.swing.BorderFactory.createTitledBorder("Agora"));

      botaoRegistrar.setText("Registrar Ponto");
      botaoRegistrar.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            botaoRegistrarregistrarPonto(evt);
         }
      });

      javax.swing.GroupLayout painelBordaBtnAgoraLayout = new javax.swing.GroupLayout(painelBordaBtnAgora);
      painelBordaBtnAgora.setLayout(painelBordaBtnAgoraLayout);
      painelBordaBtnAgoraLayout.setHorizontalGroup(
         painelBordaBtnAgoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(botaoRegistrar, javax.swing.GroupLayout.Alignment.TRAILING)
      );
      painelBordaBtnAgoraLayout.setVerticalGroup(
         painelBordaBtnAgoraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, painelBordaBtnAgoraLayout.createSequentialGroup()
            .addGap(0, 0, Short.MAX_VALUE)
            .addComponent(botaoRegistrar))
      );

      cmpTotalMes.setEditable(false);
      cmpTotalMes.setText("0,00");
      cmpTotalMes.setToolTipText("0,00");
      cmpTotalMes.setBorder(javax.swing.BorderFactory.createTitledBorder("Total Mês"));
      cmpTotalMes.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            cmpTotalMesActionPerformed(evt);
         }
      });

      cmpUsuario.setEditable(false);
      cmpUsuario.setToolTipText("");
      cmpUsuario.setBorder(javax.swing.BorderFactory.createTitledBorder("Usuário"));
      cmpUsuario.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            cmpUsuarioActionPerformed(evt);
         }
      });

      painelBordaBotoesTabela.setBackground(new java.awt.Color(255, 255, 255));
      painelBordaBotoesTabela.setBorder(javax.swing.BorderFactory.createTitledBorder("Ações na tabela"));

      btnRemoverRegistro.setText("Remover Registro");
      btnRemoverRegistro.setEnabled(false);
      btnRemoverRegistro.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnRemoverRegistroActionPerformed2(evt);
         }
      });

      btnEditarRegistro.setText("Editar Registro");
      btnEditarRegistro.setActionCommand("");
      btnEditarRegistro.setEnabled(false);
      btnEditarRegistro.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnEditarRegistroActionPerformed(evt);
         }
      });

      btnNovoRegistro.setText("Novo Registro");
      btnNovoRegistro.setToolTipText("Cadastrar um novo registro");
      btnNovoRegistro.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            btnNovoRegistroActionPerformed(evt);
         }
      });

      javax.swing.GroupLayout painelBordaBotoesTabelaLayout = new javax.swing.GroupLayout(painelBordaBotoesTabela);
      painelBordaBotoesTabela.setLayout(painelBordaBotoesTabelaLayout);
      painelBordaBotoesTabelaLayout.setHorizontalGroup(
         painelBordaBotoesTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, painelBordaBotoesTabelaLayout.createSequentialGroup()
            .addComponent(btnNovoRegistro)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(btnEditarRegistro)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(btnRemoverRegistro))
      );
      painelBordaBotoesTabelaLayout.setVerticalGroup(
         painelBordaBotoesTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(painelBordaBotoesTabelaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(btnRemoverRegistro)
            .addComponent(btnEditarRegistro)
            .addComponent(btnNovoRegistro))
      );

      javax.swing.GroupLayout painelPrincipalLayout = new javax.swing.GroupLayout(painelPrincipal);
      painelPrincipal.setLayout(painelPrincipalLayout);
      painelPrincipalLayout.setHorizontalGroup(
         painelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(painelPrincipalLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(painelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(jScrollPane1)
               .addGroup(painelPrincipalLayout.createSequentialGroup()
                  .addComponent(painelBordaBtnAgora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addGap(4, 4, 4)
                  .addComponent(painelBordaBotoesTabela, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addGap(0, 0, Short.MAX_VALUE))
               .addGroup(painelPrincipalLayout.createSequentialGroup()
                  .addComponent(cmpUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(painelPeriodo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(cmpTotalMes, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addContainerGap())
      );
      painelPrincipalLayout.setVerticalGroup(
         painelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(painelPrincipalLayout.createSequentialGroup()
            .addContainerGap()
            .addGroup(painelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
               .addComponent(cmpUsuario, javax.swing.GroupLayout.Alignment.LEADING)
               .addComponent(painelPeriodo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
               .addComponent(cmpTotalMes))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(painelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
               .addComponent(painelBordaBotoesTabela, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(painelBordaBtnAgora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
      );

      menuUsuario.setText("Usuário");

      menuBtnLogoff.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
      menuBtnLogoff.setText("Logoff");
      menuBtnLogoff.setToolTipText("Função atualmente indisponível.");
      menuBtnLogoff.setEnabled(false);
      menuBtnLogoff.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            menuBtnLogoffActionPerformed(evt);
         }
      });
      menuUsuario.add(menuBtnLogoff);

      menuBtnSair.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
      menuBtnSair.setText("Sair");
      menuBtnSair.setToolTipText("Sair do sistema");
      menuBtnSair.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            menuBtnSairActionPerformed(evt);
         }
      });
      menuUsuario.add(menuBtnSair);

      barraMenu.add(menuUsuario);

      menuRegistros.setText("Registros");

      menuBtnRegistrarAgora.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
      menuBtnRegistrarAgora.setText("Registrar Agora");
      menuBtnRegistrarAgora.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            menuBtnRegistrarAgoraActionPerformed(evt);
         }
      });
      menuRegistros.add(menuBtnRegistrarAgora);

      menuBtnNovoRegistro.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
      menuBtnNovoRegistro.setText("Novo");
      menuBtnNovoRegistro.setToolTipText("");
      menuBtnNovoRegistro.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            menuBtnNovoRegistroActionPerformed(evt);
         }
      });
      menuRegistros.add(menuBtnNovoRegistro);

      menuBtnEditarRegistro.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
      menuBtnEditarRegistro.setText("Editar");
      menuBtnEditarRegistro.setEnabled(false);
      menuBtnEditarRegistro.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            menuBtnEditarRegistroActionPerformed(evt);
         }
      });
      menuRegistros.add(menuBtnEditarRegistro);

      menuBtnRemoverRegistro.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
      menuBtnRemoverRegistro.setText("Remover");
      menuBtnRemoverRegistro.setToolTipText("");
      menuBtnRemoverRegistro.setEnabled(false);
      menuBtnRemoverRegistro.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            menuBtnRemoverRegistroActionPerformed(evt);
         }
      });
      menuRegistros.add(menuBtnRemoverRegistro);

      submenuExportar.setText("Exportar");

      menuBtnExportarXLS.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
      menuBtnExportarXLS.setText("Planilha XLS (Office)");
      menuBtnExportarXLS.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            menuBtnExportarXLSActionPerformed(evt);
         }
      });
      submenuExportar.add(menuBtnExportarXLS);

      menuRegistros.add(submenuExportar);

      barraMenu.add(menuRegistros);

      setJMenuBar(barraMenu);

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(painelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(painelPrincipal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      );

      pack();
   }// </editor-fold>//GEN-END:initComponents

    private void tabelaRegistrosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelaRegistrosMouseClicked
      atualizarBotoes();
    }//GEN-LAST:event_tabelaRegistrosMouseClicked

    private void btnRemoverRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverActionPerformed

    }//GEN-LAST:event_btnRemoverActionPerformed

    private void btnRemoverRegistroActionPerformed2(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverRegistroActionPerformed2
      doRemover();
    }//GEN-LAST:event_btnRemoverRegistroActionPerformed2

    private void botaoRegistrarregistrarPonto(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoRegistrarregistrarPonto
      doRegistrarAgora();
    }//GEN-LAST:event_botaoRegistrarregistrarPonto

    private void btnEditarRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarRegistroActionPerformed
      doEditar();
    }//GEN-LAST:event_btnEditarRegistroActionPerformed

    private void cmpTotalMesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmpTotalMesActionPerformed
      // TODO add your handling code here:
    }//GEN-LAST:event_cmpTotalMesActionPerformed

    private void formFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusGained
      atualizarBotoes();
    }//GEN-LAST:event_formFocusGained

    private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost
      atualizarBotoes();
    }//GEN-LAST:event_formFocusLost

    private void painelPrincipalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_painelPrincipalMouseClicked
      atualizarBotoes();
    }//GEN-LAST:event_painelPrincipalMouseClicked

    private void cmpUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmpUsuarioActionPerformed
      // TODO add your handling code here:
    }//GEN-LAST:event_cmpUsuarioActionPerformed

    private void menuBtnNovoRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnNovoRegistroActionPerformed
      doNovo();
    }//GEN-LAST:event_menuBtnNovoRegistroActionPerformed

    private void menuBtnLogoffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnLogoffActionPerformed
      JOptionPane.showMessageDialog(this, "Função atualmente indisponível!", "Função indisponível", JOptionPane.WARNING_MESSAGE);
    }//GEN-LAST:event_menuBtnLogoffActionPerformed

    private void menuBtnEditarRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnEditarRegistroActionPerformed
      doEditar();
    }//GEN-LAST:event_menuBtnEditarRegistroActionPerformed

    private void menuBtnRemoverRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnRemoverRegistroActionPerformed
      doRemover();
    }//GEN-LAST:event_menuBtnRemoverRegistroActionPerformed

    private void menuBtnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnSairActionPerformed
      dispose();
      invalidate();
      System.exit(0);
    }//GEN-LAST:event_menuBtnSairActionPerformed

    private void menuBtnRegistrarAgoraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnRegistrarAgoraActionPerformed
      doRegistrarAgora();
    }//GEN-LAST:event_menuBtnRegistrarAgoraActionPerformed

    private void btnNovoRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNovoRegistroActionPerformed
      doNovo();
    }//GEN-LAST:event_btnNovoRegistroActionPerformed

    private void menuBtnExportarXLSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnExportarXLSActionPerformed
      doExtrairXLS();
    }//GEN-LAST:event_menuBtnExportarXLSActionPerformed

   private void botaoAnoAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoAnoAnteriorActionPerformed
      viewState.anoAnterior();
      atualizarValores();
      atualizarBotoes();
   }//GEN-LAST:event_botaoAnoAnteriorActionPerformed

   private void botaoMesAnteriorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoMesAnteriorActionPerformed
      viewState.mesAnterior();
      atualizarValores();
      atualizarBotoes();
   }//GEN-LAST:event_botaoMesAnteriorActionPerformed

   private void botaoProximoMesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoProximoMesActionPerformed
      viewState.proximoMes();
      atualizarValores();
      atualizarBotoes();
   }//GEN-LAST:event_botaoProximoMesActionPerformed

   private void botaoProximoAnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoProximoAnoActionPerformed
      viewState.proximoAno();
      atualizarValores();
      atualizarBotoes();
   }//GEN-LAST:event_botaoProximoAnoActionPerformed

   private void botaoMesAtualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoMesAtualActionPerformed
      viewState.mesAtual();
      atualizarValores();
      atualizarBotoes();
   }//GEN-LAST:event_botaoMesAtualActionPerformed

   /**
    * @param args the command line arguments
    */
   public static void main(String args[]) {
      /*
       * Set the Nimbus look and feel
       */
      //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /*
       * If Nimbus (introduced in Java SE 6) is not available, stay with the
       * default look and feel.
       * For details see
       * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
       */
      try {
         for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
               javax.swing.UIManager.setLookAndFeel(info.getClassName());
               break;
            }
         }
      } catch (ClassNotFoundException ex) {
         java.util.logging.Logger.getLogger(PainelPrincipalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (InstantiationException ex) {
         java.util.logging.Logger.getLogger(PainelPrincipalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (IllegalAccessException ex) {
         java.util.logging.Logger.getLogger(PainelPrincipalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (javax.swing.UnsupportedLookAndFeelException ex) {
         java.util.logging.Logger.getLogger(PainelPrincipalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      }
      //</editor-fold>

      /*
       * Create and display the form
       */
      java.awt.EventQueue.invokeLater(new Runnable() {
         public void run() {
            PainelPrincipalFrame frame = new PainelPrincipalFrame();
            frame.setVisible(true);
         }
      });
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JMenuBar barraMenu;
   private javax.swing.JButton botaoAnoAnterior;
   private javax.swing.JButton botaoMesAnterior;
   private javax.swing.JButton botaoMesAtual;
   private javax.swing.JButton botaoProximoAno;
   private javax.swing.JButton botaoProximoMes;
   private javax.swing.JButton botaoRegistrar;
   private javax.swing.JButton btnEditarRegistro;
   private javax.swing.JButton btnNovoRegistro;
   private javax.swing.JButton btnRemoverRegistro;
   private javax.swing.JTextField cmpTotalMes;
   private javax.swing.JTextField cmpUsuario;
   private javax.swing.JEditorPane jEditorPane1;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JScrollPane jScrollPane2;
   private javax.swing.JLabel labelPeríodo;
   private javax.swing.JMenuItem menuBtnEditarRegistro;
   private javax.swing.JMenuItem menuBtnExportarXLS;
   private javax.swing.JMenuItem menuBtnLogoff;
   private javax.swing.JMenuItem menuBtnNovoRegistro;
   private javax.swing.JMenuItem menuBtnRegistrarAgora;
   private javax.swing.JMenuItem menuBtnRemoverRegistro;
   private javax.swing.JMenuItem menuBtnSair;
   private javax.swing.JMenu menuRegistros;
   private javax.swing.JMenu menuUsuario;
   private javax.swing.JPanel painelBordaBotoesTabela;
   private javax.swing.JPanel painelBordaBtnAgora;
   private javax.swing.JPanel painelPeriodo;
   private javax.swing.JPanel painelPrincipal;
   private javax.swing.JMenu submenuExportar;
   private javax.swing.JTable tabelaRegistros;
   // End of variables declaration//GEN-END:variables
}
