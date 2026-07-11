package net.diebuddies.physics.verlet.test;

import javax.swing.JFrame;

public class VerletTest {
   public static void main(String[] args) {
      JFrame frame = new JFrame("ASCII Renderer");
      frame.setDefaultCloseOperation(3);
      VerletPanel panel = new VerletPanel();
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
