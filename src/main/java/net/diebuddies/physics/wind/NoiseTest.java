package net.diebuddies.physics.wind;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;
import net.diebuddies.math.Math;
import net.diebuddies.math.PerlinNoise;

public class NoiseTest {
   public static void main(String[] args) {
      System.setProperty("joml.fastmath", "true");
      System.setProperty("joml.sinLookup", "true");
      System.setProperty("joml.useMathFma", "true");
      JFrame frame = new JFrame("Noise Test");
      frame.setDefaultCloseOperation(3);
      frame.add(new NoiseTest.CustomPanel());
      frame.setSize(1280, 720);
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);

      while (true) {
         frame.repaint();

         try {
            Thread.sleep(16L);
         } catch (InterruptedException var3) {
            var3.printStackTrace();
         }
      }
   }

   public static class CustomPanel extends JPanel {
      private static final long serialVersionUID = 2700078076395182431L;
      private long startTime = 0L;
      private double total = 0.0;
      private int count = 0;

      public CustomPanel() {
         this.startTime = System.currentTimeMillis();
      }

      @Override
      protected void paintComponent(Graphics g) {
         super.paintComponent(g);
         g.setColor(Color.WHITE);
         g.fillRect(0, 0, this.getWidth(), this.getHeight());
         PerlinNoise perlin = new PerlinNoise(new Random(0L));
         long delta = System.currentTimeMillis() - this.startTime;
         long time = System.currentTimeMillis();

         for (int x = 0; x < this.getWidth(); x++) {
            for (int y = 0; y < this.getHeight(); y++) {
               double wind = perlin.noise(
                  perlin.noise((double)x / 45.0, (double)y / 45.0, (double)delta / 4000.0) * 1.0 + (double)x / 50.0,
                  perlin.noise((double)x / 35.0, (double)y / 35.0, (double)delta / 4000.0) * 1.0 + (double)y / 50.0
               );
               double scale = 0.1;
               double bigWind = perlin.noise(
                  perlin.noise((double)x / 45.0 * scale, (double)y / 45.0 * scale, (double)delta / 4000.0) * 4.0 + (double)x / 50.0 * scale,
                  perlin.noise((double)x / 35.0 * scale, (double)y / 35.0 * scale, (double)delta / 4000.0) * 4.0 + (double)y / 50.0 * scale
               );
               double jitter = perlin.noise(
                  perlin.noise((double)x / 25.0, (double)y / 25.0, (double)delta / 100.0) * 1.0 + (double)x / 20.0,
                  perlin.noise((double)x / 15.0, (double)y / 15.0, (double)delta / 100.0) * 1.0 + (double)y / 20.0
               );
               float greyscale = Math.clamp(
                  (float)(bigWind * 0.5 + 0.5) * 0.7F + (float)(wind * 0.5 + 0.5) * 0.25F + ((float)jitter * 0.5F + 0.5F) * 0.05F, 0.0F, 1.0F
               );
               g.setColor(new Color(greyscale, greyscale, greyscale));
               g.drawLine(x, y, x, y);
            }
         }

         long took = System.currentTimeMillis() - time;
         System.out.println("took: " + took);
      }
   }
}
