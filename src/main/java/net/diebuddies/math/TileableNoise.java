package net.diebuddies.math;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TileableNoise {
   public static void main(String[] args) {
      JFrame window = new JFrame("Tileable noise");
      window.setSize(400, 400);
      window.setLocationRelativeTo(null);
      window.setDefaultCloseOperation(3);
      window.getRootPane().setLayout(new BorderLayout());
      window.getRootPane().add(new TileableNoise.CustomPanel(), "Center");
      window.setVisible(true);

      while (true) {
         window.repaint();

         try {
            Thread.sleep(16L);
         } catch (InterruptedException var3) {
            var3.printStackTrace();
         }
      }
   }

   static class CustomPanel extends JPanel {
      private int tiles = 16;
      private int repeatableAfter = 64;
      private double divider = (double)this.repeatableAfter / (double)this.tiles;
      private PerlinNoise perlin = new PerlinNoise(new Random(0L), this.tiles);

      @Override
      protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         long time = System.currentTimeMillis();

         for (int x = 0; x < 64; x++) {
            for (int y = 0; y < 64; y++) {
               for (int z = 0; z < 64; z++) {
                  this.perlin.noise((double)x / 32.0, (double)y / 32.0, (double)z / 32.0);
               }
            }
         }

         System.out.println("took: " + (System.currentTimeMillis() - time));

         for (int x = 0; x < this.repeatableAfter; x++) {
            for (int y = 0; y < this.repeatableAfter; y++) {
               double val = (this.perlin.noise((double)x / this.divider, (double)y / this.divider) * 0.5 + 0.5) * 255.0;
               g.setColor(new java.awt.Color((int)val, (int)val, (int)val));
               g.drawLine(x, y, x, y);
            }
         }
      }
   }
}
