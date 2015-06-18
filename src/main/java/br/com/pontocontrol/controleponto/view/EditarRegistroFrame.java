/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto.view;

import br.com.pontocontrol.controleponto.SessaoManager;
import br.com.pontocontrol.controleponto.controller.ControllerFactory;
import br.com.pontocontrol.controleponto.controller.IFolhaPontoController;
import br.com.pontocontrol.controleponto.controller.json.impl.FolhaMensalPontoJSON;
import br.com.pontocontrol.controleponto.model.FolhaMensalPonto;
import br.com.pontocontrol.controleponto.model.RegistroDiarioPonto;
import br.com.pontocontrol.controleponto.util.SwingUtils;
import br.com.pontocontrol.controleponto.util.TimeUtils;
import java.awt.Image;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;
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
            data = new SimpleDateFormat(DATE_PATTERN).format(calendar.getTime());
        }
        init();
        SessaoManager.getInstance().registrarFrame(ID, this);
    }
    
    public static final String ID = "editar-registro-frame";
    public static final String TITULO = "PontoController - Formulário de Registro";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATE_PATTERN = "dd/MM/yyyy";
    private static final Logger LOG = Logger.getLogger(EditarRegistroFrame.class.getName());
    
    private String data;
    private FolhaMensalPonto folhaMensal;
    private RegistroDiarioPonto registro;
    //ESTE REGISTRO E APENAS PARA VISUALIZAÇÃO, NÃO USAR PARA SALVAR REGISTROS.
    private RegistroDiarioPonto regVisualizacao;
    private IFolhaPontoController folhaPontoController;
    
    private void init() {
        regVisualizacao = new RegistroDiarioPonto();
        SwingUtils.setTimeMasks(cmpEntrada, cmpAlmoco, cmpRetorno, cmpSaida);
        SwingUtils.setDateMasks(cmpData);
        
        if(StringUtils.isNotBlank(data)) {
            cmpData.setText(data);
        }
        atualizarCampos();
        atualizarTotais();
        
        Image img = SessaoManager.getInstance().getImageResource("icon.png");
        if(img != null) {
            setIconImage(img);
        }
        
        setTitle(TITULO);
    }
    
    
    private IFolhaPontoController getFolhaPontoController() {
        if(folhaPontoController == null) {
            folhaPontoController = ControllerFactory.localizar(IFolhaPontoController.class);
        }
        return folhaPontoController;
    }
    
    private void fecharJanela() {
        this.setVisible(false);
        formWindowClosed(null);
        this.invalidate();
    }
    
    
    private void atualizarCampos() {
        cmpEntrada.setText("");
        cmpAlmoco.setText("");
        cmpRetorno.setText("");
        cmpSaida.setText("");
        
        if(registro != null) {
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
        }
    }
    
    private void atualizarTotais() {
        regVisualizacao.setEntrada(SwingUtils.getLocalTimeValueFromField(cmpEntrada));
        regVisualizacao.setAlmoco(SwingUtils.getLocalTimeValueFromField(cmpAlmoco));
        regVisualizacao.setRetorno(SwingUtils.getLocalTimeValueFromField(cmpRetorno));
        regVisualizacao.setSaida(SwingUtils.getLocalTimeValueFromField(cmpSaida));
        
        long usrOffset = SessaoManager.getInstance().getUsuarioAutenticado().getOffset();
        long usrExp = usrOffset / TimeUtils.OFFSET_1_HORA;
        Double totAlmoco = regVisualizacao.calcularTotalAlmocoAsNumber();
        Double totExp = regVisualizacao.calcularTotalExpedienteAsNumber();
        Double var = regVisualizacao.calcularVariacaoExpediente();
        
        cmpTotalAlmoco.setText("-");
        cmpTotalExpediente.setText("-");
        cmpTotalVariacao.setText("-");
        if(totAlmoco > 0) {
            cmpTotalAlmoco.setText(String.format("%s (%.3f)", TimeUtils.fromNumberLocalTimeFormatted(totAlmoco, usrOffset), totAlmoco * usrExp));
        } 
        if(totExp > 0) {
            cmpTotalExpediente.setText(String.format("%s (%.3f)", TimeUtils.fromNumberLocalTimeFormatted(totExp, usrOffset), totExp * usrExp));
        }
        if(totExp > 0) {
            cmpTotalVariacao.setText(String.format("%s (%.3f)", TimeUtils.fromNumberLocalTimeFormatted(var, usrOffset), var * usrExp));
        }
    }
    
    private void doSalvar() {
        Date dataValue = SwingUtils.getDateValueFromField(cmpData);
        if(dataValue == null) {
            JOptionPane.showMessageDialog(this, "O campo de data é obrigatório!\nPor favor, informe uma data do registro.", "Campo obrigatório", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean cadastrar = registro == null;
        if(cadastrar) {
            registro = new RegistroDiarioPonto();
            Calendar cal = Calendar.getInstance();
            cal.setTime(dataValue);
            registro.setDia(cal.get(Calendar.DAY_OF_MONTH));
        }
        
        registro.setEntrada(SwingUtils.getLocalTimeValueFromField(cmpEntrada));
        registro.setAlmoco(SwingUtils.getLocalTimeValueFromField(cmpAlmoco));
        registro.setRetorno(SwingUtils.getLocalTimeValueFromField(cmpRetorno));
        registro.setSaida(SwingUtils.getLocalTimeValueFromField(cmpSaida));
        if(cadastrar) {
            if(folhaMensal == null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(dataValue);
                folhaMensal = new FolhaMensalPonto(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
            }
            folhaMensal.getRegistros().put(registro.getDia(), registro);
        }
        getFolhaPontoController().sincronizar(folhaMensal);
        JOptionPane.showMessageDialog(this, String.format("O registro do dia %s foi atualizado com sucesso.", cmpData.getText()), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        
        fecharJanela();
    }
    
    private void doAltararData() {
        SwingUtils.validateDateFields(cmpData);
        Date dt = SwingUtils.getDateValueFromField(cmpData);
        if(dt != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dt);
            int dia = cal.get(Calendar.DAY_OF_MONTH);
            int mes = cal.get(Calendar.MONTH);
            int ano = cal.get(Calendar.YEAR);
            FolhaMensalPontoJSON folha = getFolhaPontoController().recuperarFolhaMensal(ano, mes);
            if(folha != null) {
                folhaMensal = folha.toModel();
                registro = folhaMensal.getRegistros().get(dia);
            } else {
                folhaMensal = null;
                registro = null;
            }
            if(registro != null) {
                regVisualizacao = registro;
            } else {
                regVisualizacao = new RegistroDiarioPonto();
            }
        }
        atualizarCampos();
        atualizarTotais();
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
        cmpEntrada = new javax.swing.JFormattedTextField();
        cmpAlmoco = new javax.swing.JFormattedTextField();
        cmpRetorno = new javax.swing.JFormattedTextField();
        cmpSaida = new javax.swing.JFormattedTextField();
        jSeparator1 = new javax.swing.JSeparator();
        cmpTotalExpediente = new javax.swing.JTextField();
        cmpTotalAlmoco = new javax.swing.JTextField();
        cmpTotalVariacao = new javax.swing.JTextField();
        cmpData = new javax.swing.JFormattedTextField();
        btnSalvar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        menuRegistro = new javax.swing.JMenu();
        menuBtnSalvarRegistro = new javax.swing.JMenuItem();
        menuBtnCancelar = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Formulário de Registro", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 24))); // NOI18N

        cmpEntrada.setBorder(javax.swing.BorderFactory.createTitledBorder("Entrada"));
        cmpEntrada.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmpEntradaFocusLost(evt);
            }
        });

        cmpAlmoco.setBorder(javax.swing.BorderFactory.createTitledBorder("Saída para Intervalo"));
        cmpAlmoco.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmpAlmocoFocusLost(evt);
            }
        });

        cmpRetorno.setBorder(javax.swing.BorderFactory.createTitledBorder("Retorno do Intervalo"));
        cmpRetorno.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmpRetornoFocusLost(evt);
            }
        });

        cmpSaida.setBorder(javax.swing.BorderFactory.createTitledBorder("Saída"));
        cmpSaida.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmpSaidaFocusLost(evt);
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

        cmpData.setBorder(javax.swing.BorderFactory.createTitledBorder("Data"));
        cmpData.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cmpDataFocusLost(evt);
            }
        });

        btnSalvar.setText("Salvar");
        btnSalvar.setToolTipText("Salvar o registro do dia.");
        btnSalvar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        btnCancelar.setText("Cancelar");
        btnCancelar.setToolTipText("Cancelar todas as alterações e retornar ao Painel principal");
        btnCancelar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmpData)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cmpEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmpAlmoco, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(cmpRetorno, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmpSaida, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cmpTotalExpediente, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                                    .addComponent(btnSalvar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cmpTotalVariacao, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                                    .addComponent(cmpTotalAlmoco, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                                    .addComponent(btnCancelar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cmpTotalVariacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                        .addComponent(btnCancelar))
                    .addComponent(btnSalvar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        menuRegistro.setText("Registro");

        menuBtnSalvarRegistro.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
        menuBtnSalvarRegistro.setText("Salvar");
        menuBtnSalvarRegistro.setToolTipText("Salvar registro");
        menuBtnSalvarRegistro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBtnSalvarRegistroActionPerformed(evt);
            }
        });
        menuRegistro.add(menuBtnSalvarRegistro);

        menuBtnCancelar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        menuBtnCancelar.setText("Cancelar");
        menuBtnCancelar.setToolTipText("Cancelar todas as alterações e retornar ao Painel Principal");
        menuBtnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuBtnCancelarActionPerformed(evt);
            }
        });
        menuRegistro.add(menuBtnCancelar);

        jMenuBar1.add(menuRegistro);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        SessaoManager.getInstance().apagarFrame(ID);
        PainelPrincipalFrame main = (PainelPrincipalFrame) SessaoManager.getInstance().getFrame(PainelPrincipalFrame.ID);
        main.setEnabled(true);
        main.requestFocus();
        if(folhaMensal != null) {
            main.atualizarTabelaRegistros(folhaMensal.getAno(), folhaMensal.getMes());
        }
        main.atualizarComboAno();
        main.atualizarComboMeses();
    }//GEN-LAST:event_formWindowClosed

    private void cmpEntradaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmpEntradaFocusLost
        SwingUtils.validateTimeFields(cmpEntrada);
        atualizarTotais();
    }//GEN-LAST:event_cmpEntradaFocusLost

    private void cmpAlmocoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmpAlmocoFocusLost
        SwingUtils.validateTimeFields(cmpAlmoco);
        atualizarTotais();
    }//GEN-LAST:event_cmpAlmocoFocusLost

    private void cmpRetornoFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmpRetornoFocusLost
        SwingUtils.validateTimeFields(cmpRetorno);
        atualizarTotais();
    }//GEN-LAST:event_cmpRetornoFocusLost

    private void cmpSaidaFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmpSaidaFocusLost
        SwingUtils.validateTimeFields(cmpSaida);
        atualizarTotais();
    }//GEN-LAST:event_cmpSaidaFocusLost

    private void cmpDataFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cmpDataFocusLost
        doAltararData();
    }//GEN-LAST:event_cmpDataFocusLost

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        doSalvar();
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        fecharJanela();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void menuBtnSalvarRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnSalvarRegistroActionPerformed
        doSalvar();
    }//GEN-LAST:event_menuBtnSalvarRegistroActionPerformed

    private void menuBtnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuBtnCancelarActionPerformed
        fecharJanela();
    }//GEN-LAST:event_menuBtnCancelarActionPerformed

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
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JFormattedTextField cmpAlmoco;
    private javax.swing.JFormattedTextField cmpData;
    private javax.swing.JFormattedTextField cmpEntrada;
    private javax.swing.JFormattedTextField cmpRetorno;
    private javax.swing.JFormattedTextField cmpSaida;
    private javax.swing.JTextField cmpTotalAlmoco;
    private javax.swing.JTextField cmpTotalExpediente;
    private javax.swing.JTextField cmpTotalVariacao;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JMenuItem menuBtnCancelar;
    private javax.swing.JMenuItem menuBtnSalvarRegistro;
    private javax.swing.JMenu menuRegistro;
    // End of variables declaration//GEN-END:variables

    public FolhaMensalPonto getFolhaMensal() {
        return folhaMensal;
    }

    public void setFolhaMensal(FolhaMensalPonto folhaMensal) {
        this.folhaMensal = folhaMensal;
    }
}
