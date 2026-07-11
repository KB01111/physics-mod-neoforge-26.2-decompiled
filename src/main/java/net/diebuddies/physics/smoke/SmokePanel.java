package net.diebuddies.physics.smoke;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;
import java.util.Objects;
import javax.swing.JPanel;

public class SmokePanel extends JPanel {
   private static final long serialVersionUID = 2820835971520532576L;
   public static final double SCALE = 100.0;
   private SmokeSimulation smokeSimulation = new SmokeSimulation();

   public SmokePanel() {
      this.setPreferredSize(new Dimension(1380, 720));
      this.addMouseMotionListener(new MouseMotionListener() {
         {
            Objects.requireNonNull(SmokePanel.this);
         }

         @Override
         public void mouseMoved(MouseEvent e) {
         }

         @Override
         public void mouseDragged(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            SmokePanel.this.smokeSimulation.addParticle(SmokePanel.this.new Particle((double)x / 100.0, (double)y / 100.0));
         }
      });
      this.addMouseListener(new MouseListener() {
         {
            Objects.requireNonNull(SmokePanel.this);
         }

         @Override
         public void mouseClicked(MouseEvent e) {
         }

         @Override
         public void mousePressed(MouseEvent e) {
            int x = e.getX();
            int y = e.getY();
            SmokePanel.this.smokeSimulation.addParticle(SmokePanel.this.new Particle((double)x / 100.0, (double)y / 100.0));
            if (e.getButton() == 3) {
               SmokePanel.this.smokeSimulation.clear();
            }
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

   @Override
   protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, this.getWidth(), this.getHeight());
      g.setColor(Color.WHITE);
      ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
      this.smokeSimulation.update(0.016666666666666666);
      List<SmokePanel.Particle> particles = this.smokeSimulation.getAllParticles();

      for (int i = 0; i < particles.size(); i++) {
         SmokePanel.Particle particle = particles.get(i);
         g.drawLine((int)(particle.x * 100.0), (int)(particle.y * 100.0), (int)(particle.x * 100.0), (int)(particle.y * 100.0));
      }
   }

   class Particle {
      public double x;
      public double y;
      public double vx;
      public double vy;
      private double gravity;
      private double airDrag;

      public Particle(double x, double y) {
         Objects.requireNonNull(SmokePanel.this);
         super();
         this.gravity = -6.867;
         this.airDrag = 0.93;
         this.x = x;
         this.y = y;
      }

      public void update(double time) {
         this.vy = this.vy + time * this.gravity;
         this.x = this.x + this.vx * time;
         this.y = this.y + this.vy * time;
         if (this.y < 0.0) {
            this.y = 0.0;
            if (this.vy < 0.0) {
               this.vy = -this.vy;
            }
         } else if (this.y > (double)SmokePanel.this.getHeight() / 100.0) {
            this.y = (double)SmokePanel.this.getHeight() / 100.0;
            if (this.vy > 0.0) {
               this.vy = -this.vy;
            }
         }

         if (this.x < 0.0) {
            this.x = 0.0;
            if (this.vx < 0.0) {
               this.vx = -this.vx;
            }
         } else if (this.x > (double)SmokePanel.this.getWidth() / 100.0) {
            this.x = (double)SmokePanel.this.getWidth() / 100.0;
            if (this.vx > 0.0) {
               this.vx = -this.vx;
            }
         }

         this.vx = this.vx * this.airDrag;
         this.vy = this.vy * this.airDrag;
      }
   }
}
