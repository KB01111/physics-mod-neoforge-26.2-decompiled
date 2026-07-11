package net.diebuddies.jbox2d.particle;

import java.util.Objects;
import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.pooling.normal.MutableStack;

public class VoronoiDiagram {
   private VoronoiDiagram.Generator[] m_generatorBuffer;
   private int m_generatorCount;
   private int m_countX;
   private int m_countY;
   private VoronoiDiagram.Generator[] m_diagram;
   private final Vec2 lower = new Vec2();
   private final Vec2 upper = new Vec2();
   private MutableStack<VoronoiDiagram.VoronoiDiagramTask> taskPool = new MutableStack<VoronoiDiagram.VoronoiDiagramTask>(50) {
      {
         Objects.requireNonNull(VoronoiDiagram.this);
      }

      protected VoronoiDiagram.VoronoiDiagramTask newInstance() {
         return new VoronoiDiagram.VoronoiDiagramTask();
      }

      protected VoronoiDiagram.VoronoiDiagramTask[] newArray(int size) {
         return new VoronoiDiagram.VoronoiDiagramTask[size];
      }
   };
   private final StackQueue<VoronoiDiagram.VoronoiDiagramTask> queue = new StackQueue<>();

   public VoronoiDiagram(int generatorCapacity) {
      this.m_generatorBuffer = new VoronoiDiagram.Generator[generatorCapacity];

      for (int i = 0; i < generatorCapacity; i++) {
         this.m_generatorBuffer[i] = new VoronoiDiagram.Generator();
      }

      this.m_generatorCount = 0;
      this.m_countX = 0;
      this.m_countY = 0;
      this.m_diagram = null;
   }

   public void getNodes(VoronoiDiagram.VoronoiDiagramCallback callback) {
      for (int y = 0; y < this.m_countY - 1; y++) {
         for (int x = 0; x < this.m_countX - 1; x++) {
            int i = x + y * this.m_countX;
            VoronoiDiagram.Generator a = this.m_diagram[i];
            VoronoiDiagram.Generator b = this.m_diagram[i + 1];
            VoronoiDiagram.Generator c = this.m_diagram[i + this.m_countX];
            VoronoiDiagram.Generator d = this.m_diagram[i + 1 + this.m_countX];
            if (b != c) {
               if (a != b && a != c) {
                  callback.callback(a.tag, b.tag, c.tag);
               }

               if (d != b && d != c) {
                  callback.callback(b.tag, d.tag, c.tag);
               }
            }
         }
      }
   }

   public void addGenerator(Vec2 center, int tag) {
      VoronoiDiagram.Generator g = this.m_generatorBuffer[this.m_generatorCount++];
      g.center.x = center.x;
      g.center.y = center.y;
      g.tag = tag;
   }

   public void generate(float radius) {
      assert this.m_diagram == null;

      float inverseRadius = 1.0F / radius;
      this.lower.x = Float.MAX_VALUE;
      this.lower.y = Float.MAX_VALUE;
      this.upper.x = -Float.MAX_VALUE;
      this.upper.y = -Float.MAX_VALUE;

      for (int k = 0; k < this.m_generatorCount; k++) {
         VoronoiDiagram.Generator g = this.m_generatorBuffer[k];
         Vec2.minToOut(this.lower, g.center, this.lower);
         Vec2.maxToOut(this.upper, g.center, this.upper);
      }

      this.m_countX = 1 + (int)(inverseRadius * (this.upper.x - this.lower.x));
      this.m_countY = 1 + (int)(inverseRadius * (this.upper.y - this.lower.y));
      this.m_diagram = new VoronoiDiagram.Generator[this.m_countX * this.m_countY];
      this.queue.reset(new VoronoiDiagram.VoronoiDiagramTask[4 * this.m_countX * this.m_countX]);

      for (int k = 0; k < this.m_generatorCount; k++) {
         VoronoiDiagram.Generator g = this.m_generatorBuffer[k];
         g.center.x = inverseRadius * (g.center.x - this.lower.x);
         g.center.y = inverseRadius * (g.center.y - this.lower.y);
         int x = MathUtils.max(0, MathUtils.min((int)g.center.x, this.m_countX - 1));
         int y = MathUtils.max(0, MathUtils.min((int)g.center.y, this.m_countY - 1));
         this.queue.push(this.taskPool.pop().set(x, y, x + y * this.m_countX, g));
      }

      while (!this.queue.empty()) {
         VoronoiDiagram.VoronoiDiagramTask front = this.queue.pop();
         int x = front.m_x;
         int y = front.m_y;
         int i = front.m_i;
         VoronoiDiagram.Generator g = front.m_generator;
         if (this.m_diagram[i] == null) {
            this.m_diagram[i] = g;
            if (x > 0) {
               this.queue.push(this.taskPool.pop().set(x - 1, y, i - 1, g));
            }

            if (y > 0) {
               this.queue.push(this.taskPool.pop().set(x, y - 1, i - this.m_countX, g));
            }

            if (x < this.m_countX - 1) {
               this.queue.push(this.taskPool.pop().set(x + 1, y, i + 1, g));
            }

            if (y < this.m_countY - 1) {
               this.queue.push(this.taskPool.pop().set(x, y + 1, i + this.m_countX, g));
            }
         }

         this.taskPool.push(front);
      }

      int maxIteration = this.m_countX + this.m_countY;

      for (int iteration = 0; iteration < maxIteration; iteration++) {
         for (int y = 0; y < this.m_countY; y++) {
            for (int x = 0; x < this.m_countX - 1; x++) {
               int i = x + y * this.m_countX;
               VoronoiDiagram.Generator a = this.m_diagram[i];
               VoronoiDiagram.Generator b = this.m_diagram[i + 1];
               if (a != b) {
                  this.queue.push(this.taskPool.pop().set(x, y, i, b));
                  this.queue.push(this.taskPool.pop().set(x + 1, y, i + 1, a));
               }
            }
         }

         for (int y = 0; y < this.m_countY - 1; y++) {
            for (int xx = 0; xx < this.m_countX; xx++) {
               int i = xx + y * this.m_countX;
               VoronoiDiagram.Generator a = this.m_diagram[i];
               VoronoiDiagram.Generator b = this.m_diagram[i + this.m_countX];
               if (a != b) {
                  this.queue.push(this.taskPool.pop().set(xx, y, i, b));
                  this.queue.push(this.taskPool.pop().set(xx, y + 1, i + this.m_countX, a));
               }
            }
         }

         boolean updated = false;

         while (!this.queue.empty()) {
            VoronoiDiagram.VoronoiDiagramTask front = this.queue.pop();
            int xxx = front.m_x;
            int y = front.m_y;
            int i = front.m_i;
            VoronoiDiagram.Generator k = front.m_generator;
            VoronoiDiagram.Generator a = this.m_diagram[i];
            if (a != k) {
               float ax = a.center.x - (float)xxx;
               float ay = a.center.y - (float)y;
               float bx = k.center.x - (float)xxx;
               float by = k.center.y - (float)y;
               float a2 = ax * ax + ay * ay;
               float b2 = bx * bx + by * by;
               if (a2 > b2) {
                  this.m_diagram[i] = k;
                  if (xxx > 0) {
                     this.queue.push(this.taskPool.pop().set(xxx - 1, y, i - 1, k));
                  }

                  if (y > 0) {
                     this.queue.push(this.taskPool.pop().set(xxx, y - 1, i - this.m_countX, k));
                  }

                  if (xxx < this.m_countX - 1) {
                     this.queue.push(this.taskPool.pop().set(xxx + 1, y, i + 1, k));
                  }

                  if (y < this.m_countY - 1) {
                     this.queue.push(this.taskPool.pop().set(xxx, y + 1, i + this.m_countX, k));
                  }

                  updated = true;
               }
            }

            this.taskPool.push(front);
         }

         if (!updated) {
            break;
         }
      }
   }

   public static class Generator {
      final Vec2 center = new Vec2();
      int tag;
   }

   public interface VoronoiDiagramCallback {
      void callback(int var1, int var2, int var3);
   }

   public static class VoronoiDiagramTask {
      int m_x;
      int m_y;
      int m_i;
      VoronoiDiagram.Generator m_generator;

      public VoronoiDiagramTask() {
      }

      public VoronoiDiagramTask(int x, int y, int i, VoronoiDiagram.Generator g) {
         this.m_x = x;
         this.m_y = y;
         this.m_i = i;
         this.m_generator = g;
      }

      public VoronoiDiagram.VoronoiDiagramTask set(int x, int y, int i, VoronoiDiagram.Generator g) {
         this.m_x = x;
         this.m_y = y;
         this.m_i = i;
         this.m_generator = g;
         return this;
      }
   }
}
