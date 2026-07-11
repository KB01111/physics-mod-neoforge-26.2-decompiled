package net.diebuddies.physics.wind;

import javax.swing.JFrame;

public class WindTest {
   public static void main(String[] args) {
      JFrame frame = new JFrame("Wind Test");
      frame.setDefaultCloseOperation(3);
      WindPanel panel = new WindPanel();
      frame.add(panel);
      frame.pack();
      frame.setLocationRelativeTo(null);
      frame.setVisible(true);

      while (true) {
         frame.repaint();

         try {
            Thread.sleep(16L);
         } catch (InterruptedException var4) {
            var4.printStackTrace();
         }
      }
   }
}
