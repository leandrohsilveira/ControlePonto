/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.pontocontrol.controleponto;

import br.com.pontocontrol.controleponto.view.PainelPrincipalFrame;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author Leandro
 */
public class ControlePonto extends ExtObject {
    
    private static final Logger LOG = Logger.getLogger(ControlePonto.class.getName());
    
    private static Map<String, JFrame> frames = new HashMap<String, JFrame>();
    
    /**
     * Main execution
     * 
     * @param args 
     */
    public static void main(String[] args) {
        LOG.info(format("Inicializando aplicação no diretório \"%s\"", mainPath()));
        
        PainelPrincipalFrame.main(args);
        
        LOG.info(format("Finalizando aplicação do diretório \"%s\"", mainPath()));
    }
    
    public static void registrarFrame(String id, JFrame frame) {
        if(frames.containsKey(id)) {
            LOG.info(format("Atualizando frame de ID \"%s\"", id));
        } else {
            LOG.info(format("Registrando frame de ID \"%s\"", id));
        }
        frames.put(id, frame);
    }
    
    public static JFrame getFrame(String id) {
        if(frames.containsKey(id)) {
            LOG.info(format("Localizado frame com ID \"%s\"", id));
            return frames.get(id);
        } else {
            LOG.severe(format("O frame com ID \"%s\" não foi encontrado!", id));
            return null;
        }
    }
    
    public static void apagarFrame(String id) {
        if(frames.containsKey(id)) {
            LOG.info(format("Apagando frame com ID \"%s\"", id));
            frames.remove(id);
        } else {
            LOG.warning(format("O frame com ID \"%s\" não foi encontrado!", id));
        }
    }
    
}
