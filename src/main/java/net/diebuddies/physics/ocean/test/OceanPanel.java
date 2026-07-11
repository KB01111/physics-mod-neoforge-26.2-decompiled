package net.diebuddies.physics.ocean.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Objects;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class OceanPanel extends JPanel {
   private static final long serialVersionUID = 2820835971520532576L;
   private OceanSimulation oceanSimulation = new OceanSimulation();

   public OceanPanel() {
      this.setPreferredSize(new Dimension(1280, 720));
      this.addMouseMotionListener(new MouseMotionListener() {
         {
            Objects.requireNonNull(OceanPanel.this);
         }

         @Override
         public void mouseMoved(MouseEvent e) {
         }

         @Override
         public void mouseDragged(MouseEvent e) {
            OceanPanel.this.setOcean(e);
         }
      });
      this.addMouseListener(new MouseListener() {
         {
            Objects.requireNonNull(OceanPanel.this);
         }

         @Override
         public void mouseClicked(MouseEvent e) {
            OceanPanel.this.setOcean(e);
         }

         @Override
         public void mousePressed(MouseEvent e) {
            OceanPanel.this.setOcean(e);
         }

         @Override
         public void mouseReleased(MouseEvent e) {
         }

         @Override
         public void mouseEntered(MouseEvent e) {
         }

         @Override
         public void mouseExited(MouseEvent e) {
         }
      });
   }

   public void setOcean(MouseEvent e) {
      int x = e.getX();
      int y = e.getY();
      float scale = this.getScale();
      int xi = (int)((float)x / scale);
      int yi = (int)((float)y / scale);
      if (SwingUtilities.isLeftMouseButton(e)) {
         this.oceanSimulation.setSolid(xi, yi);
      } else if (SwingUtilities.isRightMouseButton(e)) {
         this.oceanSimulation.setOcean(xi, yi);
      } else if (SwingUtilities.isMiddleMouseButton(e)) {
         this.oceanSimulation.setOceanDepth(xi, yi, 3);
      }
   }

   @Override
   protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, this.getWidth(), this.getHeight());
      g.setColor(Color.WHITE);
      ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
      this.oceanSimulation.update(0.016666666666666666);
      int[][] map = this.oceanSimulation.map;
      int[][] waves = this.oceanSimulation.waves;
      int[][] depth = this.oceanSimulation.depth;
      float scale = this.getScale();

      for (int x = 0; x < map.length; x++) {
         for (int y = 0; y < map[0].length; y++) {
            if (map[x][y] == 1) {
               g.setColor(Color.YELLOW);
            } else {
               float brightness = Math.max(0.0F, Math.min(1.0F, (float)waves[x][y] / 16.0F));
               g.setColor(new Color(0.0F, brightness * 0.5F, brightness, 1.0F));
            }

            g.fillRect((int)((float)x * scale), (int)((float)y * scale), (int)scale, (int)scale);
            float brightness = Math.max(0.0F, Math.min(1.0F, (float)depth[x][y] / 16.0F));
            g.setColor(new Color(1.0F - brightness, 0.5F, 1.0F, 1.0F));
            g.drawRect((int)((float)x * scale), (int)((float)y * scale), (int)scale, (int)scale);
         }
      }
   }

   public float getScale() {
      return Math.min((float)this.getWidth() / (float)this.oceanSimulation.map.length, (float)this.getHeight() / (float)this.oceanSimulation.map[0].length);
   }
}
