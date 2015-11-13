/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.pontocontrol.controleponto;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.JFrame;

/**
 *
 * @author silveira
 */
public class FrameManager {

   private static final Logger LOG = Logger.getLogger(FrameManager.class.getName());

   private FrameManager() {
      frames = new HashMap<>();
   }

   private static FrameManager instance;

   public static FrameManager getInstance() {
      if (instance == null) {
         instance = new FrameManager();
      }
      return instance;
   }

   private final Map<String, JFrame> frames;

   public void registrarFrame(String id, JFrame frame) {
      if (frames.containsKey(id)) {
         LOG.info(String.format("Atualizando frame de ID \"%s\"", id));
      } else {
         LOG.info(String.format("Registrando frame de ID \"%s\"", id));
      }
      frames.put(id, frame);
   }

   public JFrame getFrame(String id) {
      if (frames.containsKey(id)) {
         LOG.info(String.format("Localizado frame com ID \"%s\"", id));
         return frames.get(id);
      } else {
         LOG.severe(String.format("O frame com ID \"%s\" não foi encontrado!", id));
         return null;
      }
   }

   public void apagarFrame(String id) {
      if (frames.containsKey(id)) {
         LOG.info(String.format("Apagando frame com ID \"%s\"", id));
         frames.remove(id);
      } else {
         LOG.warning(String.format("O frame com ID \"%s\" não foi encontrado!", id));
      }
   }

}
