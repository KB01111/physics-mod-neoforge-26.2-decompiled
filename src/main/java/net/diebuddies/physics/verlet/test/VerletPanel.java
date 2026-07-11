package net.diebuddies.physics.verlet.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Objects;
import javax.swing.JPanel;
import net.diebuddies.physics.snow.math.AABB3D;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletStick;
import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;

public class VerletPanel extends JPanel {
   private static final long serialVersionUID = 2820835971520532576L;
   private VerletSimulationTest simulation;

   public VerletPanel() {
      this.setPreferredSize(new Dimension(1380, 720));
      this.simulation = new VerletSimulationTest(new Vector3d(0.0, 58.81, 0.0), 45, 1.0, new Vector3d());
      this.simulation
         .addConstraint(
            new BoxConstraint(new AABB3D(new Vector3d(-100.0, -200.0, -10.0), new Vector3d(50.0, 200.0, 10.0)), new Matrix4d().translation(600.0, 400.0, 0.0))
         );
      VerletPoint[][] points = new VerletPoint[12][25];
      double xOffset = 420.0;
      double yOffset = 40.0;

      for (int x = 0; x < points.length; x++) {
         for (int y = 0; y < points[0].length; y++) {
            VerletPoint point = new VerletPoint(new Vector3d((double)(x * 30) + xOffset, (double)(y * 30) + yOffset, 0.0));
            if (y == 0) {
               point.locked = true;
            }

            points[x][y] = point;
            if (x == 0 && y == 4) {
               points[x][y].prevPosition.x -= 15.0;
            }

            this.simulation.addPoint(points[x][y]);
         }
      }

      for (int x = 0; x < points.length; x++) {
         for (int y = 0; y < points[0].length; y++) {
            if (x < points.length - 1) {
               this.simulation.addStick(new VerletStick(points[x][y], points[x + 1][y]));
            }

            if (y < points[0].length - 1) {
               this.simulation.addStick(new VerletStick(points[x][y], points[x][y + 1]));
            }
         }
      }

      this.addMouseListener(new MouseListener() {
         {
            Objects.requireNonNull(VerletPanel.this);
         }

         @Override
         public void mouseClicked(MouseEvent e) {
         }

         @Override
         public void mousePressed(MouseEvent e) {
            double distance = Double.MAX_VALUE;
            VerletStick closestStick = null;

            for (int i = 0; i < VerletPanel.this.simulation.getSticks().size(); i++) {
               VerletStick stick = VerletPanel.this.simulation.getSticks().get(i);
               int x = e.getX();
               int y = e.getY();
               double d1 = Vector2d.distanceSquared(stick.pointA.position.x, stick.pointA.position.y, (double)x, (double)y);
               double d2 = Vector2d.distanceSquared(stick.pointB.position.x, stick.pointB.position.y, (double)x, (double)y);
               if (d1 + d2 < distance) {
                  distance = d1 + d2;
                  closestStick = stick;
               }
            }

            if (closestStick != null) {
               VerletPanel.this.simulation.removeStick(closestStick);
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
      long time = System.nanoTime();
      this.simulation.update(null, 0.1);
      System.out.println("took: " + (double)(System.nanoTime() - time) / 1000000.0);
      List<VerletStick> sticks = this.simulation.getSticks();

      for (VerletTestConstraint constraint : this.simulation.getConstraints()) {
         if (constraint instanceof BoxConstraint box) {
            Vector3d p1 = box.getTransformation(1.0).transformPosition(box.aabb.start, new Vector3d());
            Vector3d p2 = box.getTransformation(1.0).transformPosition(box.aabb.start.x, box.aabb.end.y, box.aabb.start.z, new Vector3d());
            Vector3d p3 = box.getTransformation(1.0).transformPosition(box.aabb.end.x, box.aabb.start.y, box.aabb.start.z, new Vector3d());
            Vector3d p4 = box.getTransformation(1.0).transformPosition(box.aabb.end, new Vector3d());
            g.drawLine((int)p1.x, (int)p1.y, (int)p3.x, (int)p3.y);
            g.drawLine((int)p3.x, (int)p3.y, (int)p4.x, (int)p4.y);
            g.drawLine((int)p2.x, (int)p2.y, (int)p4.x, (int)p4.y);
            g.drawLine((int)p2.x, (int)p2.y, (int)p1.x, (int)p1.y);
         }
      }

      for (int i = 0; i < sticks.size(); i++) {
         VerletStick stick = sticks.get(i);
         g.drawLine(
            (int)stick.pointA.bufferPosition.x, (int)stick.pointA.bufferPosition.y, (int)stick.pointB.bufferPosition.x, (int)stick.pointB.bufferPosition.y
         );
      }
   }
}
