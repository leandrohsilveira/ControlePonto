/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto;

import br.com.pontocontrol.controleponto.model.ConfiguracoesUsuario;
import br.com.pontocontrol.controleponto.view.PainelPrincipalFrame;
import java.awt.Image;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Leandro
 */
public class ControlePonto extends ExtObject {
    
    private static final Logger LOG = Logger.getLogger(ControlePonto.class.getName());
    
    /**
     * Main execution
     * 
     * @param args 
     */
    public static void main(String[] args) {
        LOG.info(format("Inicializando aplicação no diretório \"%s\"", projectDataPath()));
        solicitarLogin();
        if(SessaoManager.getInstance().getUsuarioAutenticado() != null) {
            PainelPrincipalFrame.main(args);
        }
        LOG.info(format("Finalizando aplicação do diretório \"%s\"", projectDataPath()));
    }
    
    public static void solicitarLogin() {
        String usuario;
        do {
            usuario = JOptionPane.showInputDialog(null, "Informe seu usuário:", "Identificação", JOptionPane.INFORMATION_MESSAGE);
            if(StringUtils.isBlank(usuario)) {
                JOptionPane.showMessageDialog(null, "Informe um login de usuário.", "Validação falhou.", JOptionPane.ERROR_MESSAGE);
            }
        } while (StringUtils.isBlank(usuario));
        switch(SessaoManager.getInstance().autenticar(usuario)) {
            case SessaoManager.LOGIN_STATUS.OK:
                
                break;
            case SessaoManager.LOGIN_STATUS.USUARIO_NAO_EXISTE:
                int opt = JOptionPane.showConfirmDialog(null, 
                                                format("O usuário com o login informado \"%s\" não existe, deseja criar um novo usuário?", usuario), 
                                                "Usuário não encontrado.", 
                                                JOptionPane.YES_NO_OPTION, 
                                                JOptionPane.WARNING_MESSAGE);
                if(JOptionPane.YES_OPTION == opt) {
                    ConfiguracoesUsuario configuracoesUsuario = new ConfiguracoesUsuario(usuario);
                    SessaoManager.getInstance().criarUsuario(configuracoesUsuario);
                    SessaoManager.getInstance().autenticar(usuario);
                }
                break;
            default:
                
                break;
        }
    }
    
}
