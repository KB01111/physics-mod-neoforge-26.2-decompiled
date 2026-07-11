package net.diebuddies.physics.ocean.test;

import javax.swing.JFrame;

public class OceanTest {
   public static void main(String[] args) {
      JFrame frame = new JFrame("Ocean Test");
      frame.setDefaultCloseOperation(3);
      OceanPanel panel = new OceanPanel();
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
