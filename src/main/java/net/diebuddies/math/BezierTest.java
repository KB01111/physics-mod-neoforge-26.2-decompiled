package net.diebuddies.math;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.joml.Vector2f;

public class BezierTest {
   public static void main(String[] args) {
      JFrame frame = new JFrame("Bezier Test");
      frame.setDefaultCloseOperation(3);
      JPanel panel = new JPanel() {
         @Override
         protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(java.awt.Color.BLACK);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
            g.setColor(java.awt.Color.WHITE);
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

            for (int i = 0; i < 1000; i++) {
               Vector2f pos = new Vector2f();
               Bezier2D.EASE_IN_OUT_EXPO.get((float)i / 1000.0F, pos);
               System.out.println((float)i / 1000.0F + ", " + pos.y);
               pos.y *= 600.0F;
               pos.y = -pos.y + 600.0F;
               pos.x *= 600.0F;
               float val = Bezier.EASE_IN_OUT_EXPO.get((float)i / 1000.0F);
               val *= 600.0F;
               val = -val + 600.0F;
               g.drawLine((int)((double)i * 0.5) + 40, (int)val + 100, (int)((double)i * 0.5) + 40, (int)val + 100);
            }
         }
      };
      panel.setPreferredSize(new Dimension(1280, 720));
      frame.add(panel);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);
   }
}
