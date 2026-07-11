package net.diebuddies.jbox2d.collision.shapes;

import net.diebuddies.jbox2d.collision.AABB;
import net.diebuddies.jbox2d.collision.RayCastInput;
import net.diebuddies.jbox2d.collision.RayCastOutput;
import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Transform;
import net.diebuddies.jbox2d.common.Vec2;

public class CircleShape extends Shape {
   public final Vec2 m_p = new Vec2();

   public CircleShape() {
      super(ShapeType.CIRCLE);
      this.m_radius = 0.0F;
   }

   @Override
   public final Shape clone() {
      CircleShape shape = new CircleShape();
      shape.m_p.x = this.m_p.x;
      shape.m_p.y = this.m_p.y;
      shape.m_radius = this.m_radius;
      return shape;
   }

   @Override
   public final int getChildCount() {
      return 1;
   }

   public final int getSupport(Vec2 d) {
      return 0;
   }

   public final Vec2 getSupportVertex(Vec2 d) {
      return this.m_p;
   }

   public final int getVertexCount() {
      return 1;
   }

   public final Vec2 getVertex(int index) {
      assert index == 0;

      return this.m_p;
   }

   @Override
   public final boolean testPoint(Transform transform, Vec2 p) {
      Rot q = transform.q;
      Vec2 tp = transform.p;
      float centerx = -(q.c * this.m_p.x - q.s * this.m_p.y + tp.x - p.x);
      float centery = -(q.s * this.m_p.x + q.c * this.m_p.y + tp.y - p.y);
      return centerx * centerx + centery * centery <= this.m_radius * this.m_radius;
   }

   @Override
   public float computeDistanceToOut(Transform xf, Vec2 p, int childIndex, Vec2 normalOut) {
      Rot xfq = xf.q;
      float centerx = xfq.c * this.m_p.x - xfq.s * this.m_p.y + xf.p.x;
      float centery = xfq.s * this.m_p.x + xfq.c * this.m_p.y + xf.p.y;
      float dx = p.x - centerx;
      float dy = p.y - centery;
      float d1 = MathUtils.sqrt(dx * dx + dy * dy);
      normalOut.x = dx * 1.0F / d1;
      normalOut.y = dy * 1.0F / d1;
      return d1 - this.m_radius;
   }

   @Override
   public final boolean raycast(RayCastOutput output, RayCastInput input, Transform transform, int childIndex) {
      Vec2 inputp1 = input.p1;
      Vec2 inputp2 = input.p2;
      Rot tq = transform.q;
      Vec2 tp = transform.p;
      float positionx = tq.c * this.m_p.x - tq.s * this.m_p.y + tp.x;
      float positiony = tq.s * this.m_p.x + tq.c * this.m_p.y + tp.y;
      float sx = inputp1.x - positionx;
      float sy = inputp1.y - positiony;
      float b = sx * sx + sy * sy - this.m_radius * this.m_radius;
      float rx = inputp2.x - inputp1.x;
      float ry = inputp2.y - inputp1.y;
      float c = sx * rx + sy * ry;
      float rr = rx * rx + ry * ry;
      float sigma = c * c - rr * b;
      if (!(sigma < 0.0F) && !(rr < 1.1920929E-7F)) {
         float a = -(c + MathUtils.sqrt(sigma));
         if (0.0F <= a && a <= input.maxFraction * rr) {
            a /= rr;
            output.fraction = a;
            output.normal.x = rx * a + sx;
            output.normal.y = ry * a + sy;
            output.normal.normalize();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   @Override
   public final void computeAABB(AABB aabb, Transform transform, int childIndex) {
      Rot tq = transform.q;
      Vec2 tp = transform.p;
      float px = tq.c * this.m_p.x - tq.s * this.m_p.y + tp.x;
      float py = tq.s * this.m_p.x + tq.c * this.m_p.y + tp.y;
      aabb.lowerBound.x = px - this.m_radius;
      aabb.lowerBound.y = py - this.m_radius;
      aabb.upperBound.x = px + this.m_radius;
      aabb.upperBound.y = py + this.m_radius;
   }

   @Override
   public final void computeMass(MassData massData, float density) {
      massData.mass = density * (float) Math.PI * this.m_radius * this.m_radius;
      massData.center.x = this.m_p.x;
      massData.center.y = this.m_p.y;
      massData.I = massData.mass * (0.5F * this.m_radius * this.m_radius + this.m_p.x * this.m_p.x + this.m_p.y * this.m_p.y);
   }
}
