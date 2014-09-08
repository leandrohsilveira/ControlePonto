/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.view;

import br.com.pontocontrol.controleponto.SessaoManager;
import br.com.pontocontrol.controleponto.controller.ControllerFactory;
import br.com.pontocontrol.controleponto.controller.IArquivoController;
import br.com.pontocontrol.controleponto.controller.IExportadorXLSController;
import br.com.pontocontrol.controleponto.controller.IFolhaPontoController;
import br.com.pontocontrol.controleponto.controller.json.impl.FolhaMensalPontoJSON;
import br.com.pontocontrol.controleponto.model.ConfiguracoesUsuario;
import br.com.pontocontrol.controleponto.model.FolhaMensalPonto;
import br.com.pontocontrol.controleponto.model.RegistroDiarioPonto;
import java.awt.Image;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

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
        SessaoManager.getInstance().registrarFrame(ID, this);
    }
    
    public static final String ID = "frame-principal";
    public static final String TITULO = "PontoController - Painel Principal";
    private static final Logger LOG = Logger.getLogger(PainelPrincipalFrame.class.getName());
    
    private FolhaMensalPontoJSON folhaMensal;
    private IFolhaPontoController folhaController;
    private IArquivoController arquivoController;
    private IExportadorXLSController exportadorController;
    private DefaultTableModel tableModel;
    private int mesSelecionado = Calendar.getInstance().get(Calendar.MONTH);
    private int anoSelecionado = Calendar.getInstance().get(Calendar.YEAR);
    private DateTimeFormatter timeFormater = DateTimeFormatter.ofPattern("HH:mm:ss");
    private final SimpleDateFormat FORMATO_COMBO_MESES = new SimpleDateFormat("MMMM");
    
    private void init() {
        tableModel = (DefaultTableModel) tabelaRegistros.getModel();
        atualizarTabelaRegistros(anoSelecionado, mesSelecionado);
        atualizarComboMeses();
        atualizarComboAno();
        cmpUsuario.setText(SessaoManager.getInstance().getUsuarioAutenticado().getLogin());
        Image img = SessaoManager.getInstance().getImageResource("icon.png");
        if(img != null) {
            setIconImage(img);
        }
        setTitle(TITULO);
    }
    
    private void atualizarComboMeses() {
        DefaultComboBoxModel<String> defaultComboBoxModel = new DefaultComboBoxModel<String>();
        List<Integer> meses = getArquivoController().getAvalableFileMonths(anoSelecionado);
        int mesAtual = Calendar.getInstance().get(Calendar.MONTH);
        if(meses.isEmpty() || !meses.contains(mesAtual)) {
            meses.add(mesAtual);
            Collections.sort(meses);
        }
        meses.stream().forEach((mes) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, mes);
            String mesFormatado = FORMATO_COMBO_MESES.format(calendar.getTime());
            defaultComboBoxModel.addElement(mesFormatado);
            if (mesAtual == mes) {
                defaultComboBoxModel.setSelectedItem(mesFormatado);
            }
        });
        comboMeses.setModel(defaultComboBoxModel);
    }
    
    private void atualizarComboAno() {
        DefaultComboBoxModel<String> defaultComboBoxModel = new DefaultComboBoxModel<String>();
        List<Integer> anos = getArquivoController().getAvalableYearFolders();
        int anoAtual = Calendar.getInstance().get(Calendar.YEAR);
        if(anos.isEmpty() || !anos.contains(anoAtual)) {
            anos.add(anoAtual);
            Collections.sort(anos);
        }
        anos.stream().forEach((ano) -> {
            defaultComboBoxModel.addElement(ano.toString());
            if(anoAtual == ano) {
                defaultComboBoxModel.setSelectedItem(ano.toString());
            }
        });
        comboAnos.setModel(defaultComboBoxModel);
    }
    
    private void limparTabela() {
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
    }
    
    public void atualizarTabelaRegistros(int ano, int mes) {
        limparTabela();
        folhaMensal = getFolhaPontoController().recuperarFolhaMensal(ano, mes);
        FolhaMensalPonto folha = folhaMensal.toModel();
        Double totalMensal = folha.calcularTotalMensal();
        Double totalVariacao = folha.calcularVariacaoMensal();
        NumberFormat formater = DecimalFormat.getNumberInstance();
        cmpTotalMes.setText(formater.format(totalMensal));
        cmpTotalMes.setToolTipText(String.format("Variação: %s", formater.format(totalVariacao)));
        folha.getRegistros().keySet().stream().map((dia) -> folha.getRegistros().get(dia)).forEach((reg) -> {
            LocalTime totalTime = reg.calcularTotalExpediente();
            String entrada = reg.getEntrada() != null ? timeFormater.format(reg.getEntrada()) : "Pendente";
            String almoco = reg.getAlmoco()!= null ? timeFormater.format(reg.getAlmoco()) : "Pendente";
            String retorno = reg.getRetorno()!= null ? timeFormater.format(reg.getRetorno()) : "Pendente";
            String saida = reg.getSaida()!= null ? timeFormater.format(reg.getSaida()) : "Pendente";
            String total = totalTime != null ? timeFormater.format(totalTime) : "-";
            String var = totalTime != null ? DecimalFormat.getNumberInstance().format(reg.calcularVariacaoExpediente()) : "-";
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
    
    private IFolhaPontoController getFolhaPontoController() {
        if (folhaController == null) {
            folhaController = ControllerFactory.localizar(IFolhaPontoController.class);
        }
        return folhaController;
    }
    
    private IArquivoController getArquivoController() {
        if(arquivoController == null) {
            arquivoController = ControllerFactory.localizar(IArquivoController.class);
        }
        return arquivoController;
    }
    
    private IExportadorXLSController getExportadorController() {
        if(exportadorController == null) {
            exportadorController = ControllerFactory.localizar(IExportadorXLSController.class);
        }
        return exportadorController;
    }
    
    private void atualizarBotoes() {
        btnRemoverRegistro.setEnabled(tabelaRegistros.getSelectedRowCount() > 0);
        btnEditarRegistro.setEnabled(tabelaRegistros.getSelectedRowCount() == 1);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        painelPrincipal = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelaRegistros = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        comboAnos = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        comboMeses = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        botaoRegistrar = new javax.swing.JButton();
        cmpTotalMes = new javax.swing.JTextField();
        cmpUsuario = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        btnRemoverRegistro = new javax.swing.JButton();
        btnEditarRegistro = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        btnExtrairXLS = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(704, 200));
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

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Ano"));

        comboAnos.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboAnos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboAnosActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(comboAnos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(comboAnos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Mês"));

        comboMeses.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comboMeses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comboMesesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(comboMeses, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(comboMeses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Agora"));

        botaoRegistrar.setText("Registrar Ponto");
        botaoRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botaoRegistrarregistrarPonto(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(botaoRegistrar, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
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

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Ações na tabela"));

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(btnEditarRegistro)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRemoverRegistro))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(btnRemoverRegistro)
                .addComponent(btnEditarRegistro))
        );

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Extrair Registros"));

        btnExtrairXLS.setText("XLS");
        btnExtrairXLS.setActionCommand("");
        btnExtrairXLS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExtrairXLSActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnExtrairXLS, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnExtrairXLS)
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
                        .addGroup(painelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(painelPrincipalLayout.createSequentialGroup()
                                .addComponent(cmpUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmpTotalMes, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(painelPrincipalLayout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        painelPrincipalLayout.setVerticalGroup(
            painelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelPrincipalLayout.createSequentialGroup()
                .addGroup(painelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmpTotalMes, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(painelPrincipalLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(painelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmpUsuario))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(painelPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(painelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(painelPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tabelaRegistrosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelaRegistrosMouseClicked
        atualizarBotoes();
    }//GEN-LAST:event_tabelaRegistrosMouseClicked

    private void btnRemoverRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverActionPerformed
        
    }//GEN-LAST:event_btnRemoverActionPerformed

    private void btnRemoverRegistroActionPerformed2(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverRegistroActionPerformed2
        if(tabelaRegistros.getSelectedRowCount() > 0) {
            int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja remover os registros selecionados?", "Remover registros", JOptionPane.YES_NO_OPTION);
            if(JOptionPane.OK_OPTION == confirm) {
                FolhaMensalPonto model = folhaMensal.toModel();
                for (Integer index : tabelaRegistros.getSelectedRows()) {
                    Integer dia = (Integer) tableModel.getValueAt(index, 0);
                    if (model.getRegistros().containsKey(dia)) {
                        model.getRegistros().remove(dia);
                    }
                }
                getFolhaPontoController().sincronizar(model);
                atualizarTabelaRegistros(anoSelecionado, mesSelecionado);
            }
        }
    }//GEN-LAST:event_btnRemoverRegistroActionPerformed2

    private void botaoRegistrarregistrarPonto(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botaoRegistrarregistrarPonto
        final Calendar calendar = Calendar.getInstance();
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        FolhaMensalPonto model = folhaMensal.toModel();
        RegistroDiarioPonto reg = model.getRegistros().get(dia);
        if(reg == null) {
            reg = new RegistroDiarioPonto();
            reg.setDia(dia);
        }
        reg.registrarProximoAgora();
        if(!model.getRegistros().containsKey(dia)) {
            model.getRegistros().put(dia, reg);
        }
        getFolhaPontoController().sincronizar(model);
        atualizarTabelaRegistros(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
    }//GEN-LAST:event_botaoRegistrarregistrarPonto

    private void btnEditarRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarRegistroActionPerformed
        if(tabelaRegistros.getSelectedColumnCount() == 1) {
            Integer dia = (Integer) tabelaRegistros.getValueAt(tabelaRegistros.getSelectedRow(), 0);
            if(folhaMensal.registros.containsKey(dia)) {
                final EditarRegistroFrame janelaEditar = new EditarRegistroFrame(dia, folhaMensal.toModel());
                janelaEditar.setVisible(true);
                this.setEnabled(false);
            }
        }
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

    private void comboAnosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboAnosActionPerformed
        anoSelecionado = Integer.valueOf((String) comboAnos.getSelectedItem());
        atualizarComboMeses();
        atualizarTabelaRegistros(anoSelecionado, mesSelecionado);
    }//GEN-LAST:event_comboAnosActionPerformed

    private void comboMesesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comboMesesActionPerformed
        String mesStr = (String) comboMeses.getSelectedItem();
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(FORMATO_COMBO_MESES.parse(mesStr));
            mesSelecionado = cal.get(Calendar.MONTH);
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, "Erro ao executar parse do valor \"%s\" para uma data válida.", ex);
        }
        atualizarTabelaRegistros(anoSelecionado, mesSelecionado);
    }//GEN-LAST:event_comboMesesActionPerformed

    private void btnExtrairXLSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExtrairXLSActionPerformed
        final ConfiguracoesUsuario usuarioAutenticado = SessaoManager.getInstance().getUsuarioAutenticado();
        if(usuarioAutenticado != null && folhaMensal != null) {
            boolean ok = getExportadorController().extrair(folhaMensal.toModel(), usuarioAutenticado.getPathUsuario());
            if(ok) {
                JOptionPane.showMessageDialog(this, "Folha de Ponto mensal extraída com sucesso!", "Extrair para XLS", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnExtrairXLSActionPerformed

                                        

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                PainelPrincipalFrame frame = new PainelPrincipalFrame();
                frame.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botaoRegistrar;
    private javax.swing.JButton btnEditarRegistro;
    private javax.swing.JButton btnExtrairXLS;
    private javax.swing.JButton btnRemoverRegistro;
    private javax.swing.JTextField cmpTotalMes;
    private javax.swing.JTextField cmpUsuario;
    private javax.swing.JComboBox comboAnos;
    private javax.swing.JComboBox comboMeses;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel painelPrincipal;
    private javax.swing.JTable tabelaRegistros;
    // End of variables declaration//GEN-END:variables
}
