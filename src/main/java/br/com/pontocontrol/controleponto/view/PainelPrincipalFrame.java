/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.view;

import br.com.pontocontrol.controleponto.ControlePonto;
import br.com.pontocontrol.controleponto.controller.ControllerFactory;
import br.com.pontocontrol.controleponto.controller.IArquivoController;
import br.com.pontocontrol.controleponto.controller.IFolhaPontoController;
import br.com.pontocontrol.controleponto.controller.json.impl.FolhaMensalPontoJSON;
import br.com.pontocontrol.controleponto.model.FolhaMensalPonto;
import br.com.pontocontrol.controleponto.model.RegistroDiarioPonto;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
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
        ControlePonto.registrarFrame(ID, this);
    }
    
    public static final String ID = "frame-principal";
    
    private FolhaMensalPontoJSON folhaMensal;
    private IFolhaPontoController folhaController;
    private IArquivoController arquivoController;
    private DefaultTableModel tableModel;
    private int mesSelecionado = Calendar.getInstance().get(Calendar.MONTH);
    private DateTimeFormatter timeFormater = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    private void init() {
        tableModel = (DefaultTableModel) tabelaRegistros.getModel();
        atualizarTabelaRegistros(mesSelecionado);
        atualizarComboMeses();
    }
    
    private void atualizarComboMeses() {
        DefaultComboBoxModel<String> defaultComboBoxModel = new DefaultComboBoxModel<String>();
        List<Integer> meses = getArquivoController().getAvalableFileMonths();
        int mesAtual = Calendar.getInstance().get(Calendar.MONTH);
        if(meses.isEmpty() || !meses.contains(mesAtual)) {
            meses.add(mesAtual);
            Collections.sort(meses);
        }
        for (Integer mes : meses) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MONTH, mes);
            String mesFormatado = new SimpleDateFormat("MMMM").format(calendar.getTime());
            defaultComboBoxModel.addElement(mesFormatado);
            if(mesAtual == mes) {
                defaultComboBoxModel.setSelectedItem(mesFormatado);
            }
        }
        comboMeses.setModel(defaultComboBoxModel);
    }
    
    private void limparTabela() {
        for(int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.removeRow(i);
        } 
    }
    
    private void atualizarTabelaRegistros(int mes) {
        limparTabela();
        folhaMensal = getFolhaPontoController().recuperarFolhaMensal(mes);
        FolhaMensalPonto folha = folhaMensal.toModel();
        Double totalMensal = folha.calcularTotalMensal();
        Double totalVariacao = folha.calcularVariacaoMensal();
        NumberFormat formater = DecimalFormat.getNumberInstance();
        String text = String.format("%s (%s)", formater.format(totalMensal), formater.format(totalVariacao));
        totalMes.setText(text);
        for (Integer dia :  folha.getRegistros().keySet()) {
            RegistroDiarioPonto reg = folha.getRegistros().get(dia);
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
        }
    }
    
    private void addRow(DefaultTableModel model, Object... objects) {
        model.addRow(objects);
    }
    
    private IFolhaPontoController getFolhaPontoController() {
        if (folhaController == null) {
            folhaController = (IFolhaPontoController) ControllerFactory.localizar(IFolhaPontoController.class);
        }
        return folhaController;
    }
    
    private IArquivoController getArquivoController() {
        if(arquivoController == null) {
            arquivoController = (IArquivoController) ControllerFactory.localizar(IArquivoController.class);
        }
        return arquivoController;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        comboMeses = new javax.swing.JComboBox();
        botaoRegistrar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabelaRegistros = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        totalMes = new javax.swing.JLabel();
        btnRemoverRegistro = new javax.swing.JButton();
        btnEditarRegistro = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(400, 400));

        comboMeses.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        botaoRegistrar.setText("Registrar Ponto");
        botaoRegistrar.setToolTipText("Pressione para Registrar o próximo ponto");
        botaoRegistrar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        botaoRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botaoRegistrarregistrarPonto(evt);
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

        jLabel2.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel2.setText("Controle Pessoal de Ponto");

        jLabel1.setText("Total do Mês:");
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        totalMes.setText("0,0 (0,0)");
        totalMes.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        btnRemoverRegistro.setText("Remover Registro");
        btnRemoverRegistro.setToolTipText("Remover o registro diário selecionado na tabela acima.");
        btnRemoverRegistro.setEnabled(false);
        btnRemoverRegistro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverRegistroActionPerformed(evt);
            }
        });

        btnEditarRegistro.setText("Editar Registro");
        btnEditarRegistro.setToolTipText("Editar o registro diário selecionado na tabela acima.");
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
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel2)
                .addGap(0, 504, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnRemoverRegistro)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnEditarRegistro))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(comboMeses, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(botaoRegistrar)
                                .addGap(4, 4, 4)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(totalMes)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(botaoRegistrar)
                    .addComponent(comboMeses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(totalMes))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 332, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRemoverRegistro)
                    .addComponent(btnEditarRegistro))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRemoverRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverRegistroActionPerformed
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
                atualizarTabelaRegistros(mesSelecionado);
            }
        }
    }//GEN-LAST:event_btnRemoverRegistroActionPerformed

    private void tabelaRegistrosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelaRegistrosMouseClicked
        btnRemoverRegistro.setEnabled(tabelaRegistros.getSelectedRowCount() > 0);
        btnEditarRegistro.setEnabled(tabelaRegistros.getSelectedRowCount() == 1);
    }//GEN-LAST:event_tabelaRegistrosMouseClicked

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
        atualizarTabelaRegistros(calendar.get(Calendar.MONTH));
    }//GEN-LAST:event_botaoRegistrarregistrarPonto

    private void btnEditarRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditarRegistroActionPerformed
        if(tabelaRegistros.getSelectedColumnCount() == 1) {
            Integer dia = (Integer) tabelaRegistros.getValueAt(tabelaRegistros.getSelectedRow(), 0);
            if(folhaMensal.registros.containsKey(dia)) {
                RegistroDiarioPonto reg = folhaMensal.registros.get(dia).toModel();
                final EditarRegistroFrame janelaEditar = new EditarRegistroFrame(reg, folhaMensal.mes, folhaMensal.ano);
                janelaEditar.setVisible(true);
                this.setEnabled(false);
            }
        }
    }//GEN-LAST:event_btnEditarRegistroActionPerformed

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
    private javax.swing.JButton btnRemoverRegistro;
    private javax.swing.JComboBox comboMeses;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tabelaRegistros;
    private javax.swing.JLabel totalMes;
    // End of variables declaration//GEN-END:variables
}
