/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.view;

import br.com.pontocontrol.controleponto.ControlePonto;
import br.com.pontocontrol.controleponto.controller.ControllerFactory;
import br.com.pontocontrol.controleponto.controller.IFolhaPontoController;
import br.com.pontocontrol.controleponto.model.FolhaMensalPonto;
import br.com.pontocontrol.controleponto.model.RegistroDiarioPonto;
import br.com.pontocontrol.controleponto.util.TimeUtils;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Leandro
 */
public class EditarRegistroFrame extends javax.swing.JFrame {

    public EditarRegistroFrame() {
        this(null, null);
    }

    public EditarRegistroFrame(Integer dia, FolhaMensalPonto folhaMensal) {
        initComponents();
        this.folhaMensal = folhaMensal;
        Calendar calendar = Calendar.getInstance();
        if(dia != null) {
            calendar.set(Calendar.DAY_OF_MONTH, dia);
            this.registro = this.folhaMensal.getRegistros().get(dia);
        }
        if(folhaMensal != null) {
            calendar.set(Calendar.MONTH, folhaMensal.getMes());
            calendar.set(Calendar.YEAR, folhaMensal.getAno());
        }
        data = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy").format(calendar.getTime());
        init();
        ControlePonto.registrarFrame(ID, this);
    }
    
    public static final String ID = "editar-registro-frame";
    private static final String TIME_PATTERN = "HH:mm:ss";
    
    private String data;
    private FolhaMensalPonto folhaMensal;
    private RegistroDiarioPonto registro;
    private final DateTimeFormatter timeFormater = DateTimeFormatter.ofPattern(TIME_PATTERN);
    
    private void init() {
        cmpData.setText(data);
        if(registro.getEntrada() != null) {
            cmpEntrada.setText(TimeUtils.fromLocalTime(registro.getEntrada(), TIME_PATTERN));
        }
        if(registro.getAlmoco()!= null) {
            cmpAlmoco.setText(TimeUtils.fromLocalTime(registro.getAlmoco(), TIME_PATTERN));
        }
        if(registro.getRetorno()!= null) {
            cmpRetorno.setText(TimeUtils.fromLocalTime(registro.getRetorno(), TIME_PATTERN));
        }
        if(registro.getSaida()!= null) {
            cmpSaida.setText(TimeUtils.fromLocalTime(registro.getSaida(), TIME_PATTERN));
        }
        Double totAlmoco = registro.calcularTotalAlmocoAsNumber();
        Double totExp = registro.calcularTotalExpedienteAsNumber();
        Double var = registro.calcularVariacaoExpediente();
        if(totAlmoco > 0) {
            cmpTotalAlmoco.setText(String.format("%s (%.3f)", TimeUtils.fromNumberLocalTimeFormatted(totAlmoco, TimeUtils.OFFSET_8_HORAS), totAlmoco));
        }
        if(totExp > 0) {
            cmpTotalExpediente.setText(String.format("%s (%.3f)", TimeUtils.fromNumberLocalTimeFormatted(totExp, TimeUtils.OFFSET_8_HORAS), totExp));
        }
        if(totExp > 0) {
            cmpTotalVariacao.setText(String.format("%s (%.3f)", TimeUtils.fromNumberLocalTimeFormatted(var, TimeUtils.OFFSET_8_HORAS), var));
        }
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
        cmpData = new javax.swing.JTextField();
        cmpEntrada = new javax.swing.JFormattedTextField();
        cmpAlmoco = new javax.swing.JFormattedTextField();
        cmpRetorno = new javax.swing.JFormattedTextField();
        cmpSaida = new javax.swing.JFormattedTextField();
        jSeparator1 = new javax.swing.JSeparator();
        cmpTotalExpediente = new javax.swing.JTextField();
        cmpTotalAlmoco = new javax.swing.JTextField();
        cmpTotalVariacao = new javax.swing.JTextField();
        btnCancelar = new javax.swing.JToggleButton();
        btnSalvar = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Editar Registro", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 24))); // NOI18N

        cmpData.setEditable(false);
        cmpData.setForeground(new java.awt.Color(204, 204, 204));
        cmpData.setText("-");
        cmpData.setBorder(javax.swing.BorderFactory.createTitledBorder("Data"));

        cmpEntrada.setBorder(javax.swing.BorderFactory.createTitledBorder("Entrada"));
        cmpEntrada.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance())));
        cmpEntrada.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmpEntradaActionPerformed(evt);
            }
        });

        cmpAlmoco.setBorder(javax.swing.BorderFactory.createTitledBorder("Saída para Intervalo"));
        cmpAlmoco.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance())));
        cmpAlmoco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmpAlmocoActionPerformed(evt);
            }
        });

        cmpRetorno.setBorder(javax.swing.BorderFactory.createTitledBorder("Retorno do Intervalo"));
        cmpRetorno.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance())));
        cmpRetorno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmpRetornoActionPerformed(evt);
            }
        });

        cmpSaida.setBorder(javax.swing.BorderFactory.createTitledBorder("Saída"));
        cmpSaida.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance())));
        cmpSaida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmpSaidaActionPerformed(evt);
            }
        });

        cmpTotalExpediente.setEditable(false);
        cmpTotalExpediente.setForeground(new java.awt.Color(204, 204, 204));
        cmpTotalExpediente.setText("-");
        cmpTotalExpediente.setBorder(javax.swing.BorderFactory.createTitledBorder("Total de Expediente"));

        cmpTotalAlmoco.setEditable(false);
        cmpTotalAlmoco.setForeground(new java.awt.Color(204, 204, 204));
        cmpTotalAlmoco.setText("-");
        cmpTotalAlmoco.setBorder(javax.swing.BorderFactory.createTitledBorder("Total de Intervalo"));

        cmpTotalVariacao.setEditable(false);
        cmpTotalVariacao.setForeground(new java.awt.Color(204, 204, 204));
        cmpTotalVariacao.setText("-");
        cmpTotalVariacao.setBorder(javax.swing.BorderFactory.createTitledBorder("Variação"));

        btnCancelar.setText("Cancelar");
        btnCancelar.setToolTipText("Cancelar todas alterações.");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        btnSalvar.setText("Salvar");
        btnSalvar.setToolTipText("Salvar registro diário.");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(cmpData, javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addComponent(cmpEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(cmpAlmoco, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cmpRetorno, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmpSaida, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btnSalvar, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                            .addComponent(cmpTotalExpediente))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmpTotalAlmoco, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cmpTotalVariacao, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                                    .addComponent(btnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(cmpData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmpEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmpAlmoco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmpRetorno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmpSaida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmpTotalExpediente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmpTotalAlmoco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cmpTotalVariacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCancelar))
                    .addComponent(btnSalvar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    IFolhaPontoController folhaPontoController;
    
    private IFolhaPontoController getFolhaPontoController() {
        if(folhaPontoController == null) {
            folhaPontoController = (IFolhaPontoController) ControllerFactory.localizar(IFolhaPontoController.class);
        }
        return folhaPontoController;
    }
    
    private void fecharJanela() {
        this.setVisible(false);
        formWindowClosed(null);
        this.invalidate();
    }
    
    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        ControlePonto.apagarFrame(ID);
        PainelPrincipalFrame main = (PainelPrincipalFrame) ControlePonto.getFrame(PainelPrincipalFrame.ID);
        main.setEnabled(true);
        main.requestFocus();
        main.atualizarTabelaRegistros(folhaMensal.getMes());
    }//GEN-LAST:event_formWindowClosed

    private void cmpEntradaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmpEntradaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmpEntradaActionPerformed

    private void cmpAlmocoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmpAlmocoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmpAlmocoActionPerformed

    private void cmpRetornoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmpRetornoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmpRetornoActionPerformed

    private void cmpSaidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmpSaidaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmpSaidaActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        fecharJanela();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        String entrada = cmpEntrada.getText();
        String almoco = cmpAlmoco.getText();
        String retorno = cmpRetorno.getText();
        String saida = cmpSaida.getText();
        
        boolean cadastrar = registro == null;
        if(cadastrar) {
            registro = new RegistroDiarioPonto();
            registro.setDia(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        }
        
        if(StringUtils.isNotBlank(entrada)) {
            registro.setEntrada(LocalTime.from(timeFormater.parse(entrada)));
        }
        if(StringUtils.isNotBlank(almoco)) {
            registro.setAlmoco(LocalTime.from(timeFormater.parse(almoco)));
        }
        if(StringUtils.isNotBlank(retorno)) {
            registro.setRetorno(LocalTime.from(timeFormater.parse(retorno)));
        }
        if(StringUtils.isNotBlank(saida)) {
            registro.setEntrada(LocalTime.from(timeFormater.parse(saida)));
        }
        if(cadastrar) {
            folhaMensal.getRegistros().put(registro.getDia(), registro);
        }
        getFolhaPontoController().sincronizar(folhaMensal);
        JOptionPane.showMessageDialog(this, String.format("O registro do dia %s foi atualizado com sucesso.", data), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        
        fecharJanela();
    }//GEN-LAST:event_btnSalvarActionPerformed

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
            java.util.logging.Logger.getLogger(EditarRegistroFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EditarRegistroFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EditarRegistroFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EditarRegistroFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                EditarRegistroFrame frame = new EditarRegistroFrame();
                frame.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnCancelar;
    private javax.swing.JToggleButton btnSalvar;
    private javax.swing.JFormattedTextField cmpAlmoco;
    private javax.swing.JTextField cmpData;
    private javax.swing.JFormattedTextField cmpEntrada;
    private javax.swing.JFormattedTextField cmpRetorno;
    private javax.swing.JFormattedTextField cmpSaida;
    private javax.swing.JTextField cmpTotalAlmoco;
    private javax.swing.JTextField cmpTotalExpediente;
    private javax.swing.JTextField cmpTotalVariacao;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables

    public FolhaMensalPonto getFolhaMensal() {
        return folhaMensal;
    }

    public void setFolhaMensal(FolhaMensalPonto folhaMensal) {
        this.folhaMensal = folhaMensal;
    }
}
