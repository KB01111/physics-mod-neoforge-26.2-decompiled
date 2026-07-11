package net.diebuddies.physics.wind;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.joml.Vector2f;

public class WindPanel extends JPanel {
   private static final long serialVersionUID = 2820835971520532576L;
   private WindSimulation windSimulation = new WindSimulation();

   public WindPanel() {
      this.setPreferredSize(new Dimension(1280, 720));
      this.addMouseMotionListener(new MouseMotionListener() {
         {
            Objects.requireNonNull(WindPanel.this);
         }

         @Override
         public void mouseMoved(MouseEvent e) {
         }

         @Override
         public void mouseDragged(MouseEvent e) {
            WindPanel.this.setSkyLight(e);
         }
      });
      this.addMouseListener(new MouseListener() {
         {
            Objects.requireNonNull(WindPanel.this);
         }

         @Override
         public void mouseClicked(MouseEvent e) {
            WindPanel.this.setSkyLight(e);
         }

         @Override
         public void mousePressed(MouseEvent e) {
            WindPanel.this.setSkyLight(e);
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

   public void setSkyLight(MouseEvent e) {
      int x = e.getX();
      int y = e.getY();
      float scale = this.getScale();
      int xi = (int)((float)x / scale);
      int yi = (int)((float)y / scale);
      if (SwingUtilities.isLeftMouseButton(e)) {
         this.windSimulation.setSkyLight(xi, 0);
      } else if (SwingUtilities.isRightMouseButton(e)) {
         if (WindPanel.Keyboard.isKeyPressed(16)) {
            this.windSimulation.removeSolid(xi, yi);
         } else {
            this.windSimulation.removeSkyLight(xi, 0);
         }
      }

      if (SwingUtilities.isMiddleMouseButton(e)) {
         this.windSimulation.setSolid(xi, yi);
      }
   }

   @Override
   protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, this.getWidth(), this.getHeight());
      g.setColor(Color.WHITE);
      ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
      this.windSimulation.update(0.016666666666666666);
      int[][] map = this.windSimulation.map;
      int[][] light = this.windSimulation.light;
      float scale = this.getScale();

      for (int x = 0; x < map.length; x++) {
         for (int y = 0; y < map[0].length; y++) {
            if (map[x][y] == 1) {
               if (light[x][y] == 60) {
                  g.setColor(Color.RED);
               } else {
                  g.setColor(Color.YELLOW);
               }
            } else {
               float brightness = (float)light[x][y] / 60.0F;
               g.setColor(new Color(brightness, brightness, brightness, 1.0F));
            }

            g.fillRect((int)((float)x * scale), (int)((float)y * scale), (int)scale, (int)scale);
            g.setColor(Color.CYAN);
            g.drawRect((int)((float)x * scale), (int)((float)y * scale), (int)scale, (int)scale);
            int currentLight = this.windSimulation.getLightData(x, y);
            int windX = 0;
            int windY = 0;
            if (!this.windSimulation.isSolid(x, y)) {
               if (this.windSimulation.isSolid(x + 1, y)) {
                  if (this.windSimulation.isSolid(x - 1, y)) {
                     windX = 0;
                  } else {
                     windX = this.windSimulation.getLightData(x - 1, y) - this.windSimulation.getLightData(x, y);
                  }
               } else {
                  windX = this.windSimulation.getLightData(x, y) - this.windSimulation.getLightData(x + 1, y);
               }

               if (this.windSimulation.isSolid(x, y + 1)) {
                  if (this.windSimulation.isSolid(x, y - 1)) {
                     windY = 0;
                  } else {
                     windY = this.windSimulation.getLightData(x, y - 1) - this.windSimulation.getLightData(x, y);
                  }
               } else {
                  windY = this.windSimulation.getLightData(x, y) - this.windSimulation.getLightData(x, y + 1);
               }
            }

            Vector2f wind = new Vector2f((float)(-windX), (float)(-windY));
            float lengthSquared = wind.lengthSquared();
            if (lengthSquared != 0.0F) {
               wind.mul(1.0F / (float)Math.sqrt((double)lengthSquared));
            } else if (currentLight == 60) {
               wind.set(0.0F, -1.0F);
            } else {
               wind.set(0.0F);
            }

            int middleX = (int)((float)x * scale + scale * 0.5F);
            int middleY = (int)((float)y * scale + scale * 0.5F);
            GradientPaint gradient = new GradientPaint(
               (float)middleX,
               (float)middleY,
               Color.MAGENTA,
               (float)(middleX + (int)(wind.x * scale * 0.4F)),
               (float)(middleY + (int)(wind.y * scale * 0.4F)),
               Color.GREEN
            );
            Graphics2D g2d = (Graphics2D)g;
            g2d.setPaint(gradient);
            g2d.drawLine(middleX, middleY, middleX + (int)(wind.x * scale * 0.4F), middleY + (int)(wind.y * scale * 0.4F));
         }
      }
   }

   public float getScale() {
      return Math.min((float)this.getWidth() / (float)this.windSimulation.map.length, (float)this.getHeight() / (float)this.windSimulation.map[0].length);
   }

   public class Keyboard {
      private static final Map<Integer, Boolean> pressedKeys = new HashMap<>();

      public Keyboard() {
         Objects.requireNonNull(WindPanel.this);
         super();
      }

      public static boolean isKeyPressed(int keyCode) {
         return pressedKeys.getOrDefault(keyCode, false);
      }

      static {
         KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(event -> {
            synchronized (WindPanel.Keyboard.class) {
               if (event.getID() == 401) {
                  pressedKeys.put(event.getKeyCode(), true);
               } else if (event.getID() == 402) {
                  pressedKeys.put(event.getKeyCode(), false);
               }

               return false;
            }
         });
      }
   }
}
