package net.diebuddies.physics.smoke;

import javax.swing.JFrame;

public class SmokeTest {
   public static void main(String[] args) {
      JFrame frame = new JFrame("Smoke Test");
      frame.setDefaultCloseOperation(3);
      SmokePanel panel = new SmokePanel();
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
