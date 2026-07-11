package net.diebuddies.jbox2d.collision;

import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Sweep;
import net.diebuddies.jbox2d.common.Transform;
import net.diebuddies.jbox2d.common.Vec2;

class SeparationFunction {
   public Distance.DistanceProxy m_proxyA;
   public Distance.DistanceProxy m_proxyB;
   public Type m_type;
   public final Vec2 m_localPoint = new Vec2();
   public final Vec2 m_axis = new Vec2();
   public Sweep m_sweepA;
   public Sweep m_sweepB;
   private final Vec2 localPointA = new Vec2();
   private final Vec2 localPointB = new Vec2();
   private final Vec2 pointA = new Vec2();
   private final Vec2 pointB = new Vec2();
   private final Vec2 localPointA1 = new Vec2();
   private final Vec2 localPointA2 = new Vec2();
   private final Vec2 normal = new Vec2();
   private final Vec2 localPointB1 = new Vec2();
   private final Vec2 localPointB2 = new Vec2();
   private final Vec2 temp = new Vec2();
   private final Transform xfa = new Transform();
   private final Transform xfb = new Transform();
   private final Vec2 axisA = new Vec2();
   private final Vec2 axisB = new Vec2();

   public float initialize(Distance.SimplexCache cache, Distance.DistanceProxy proxyA, Sweep sweepA, Distance.DistanceProxy proxyB, Sweep sweepB, float t1) {
      this.m_proxyA = proxyA;
      this.m_proxyB = proxyB;
      int count = cache.count;

      assert 0 < count && count < 3;

      this.m_sweepA = sweepA;
      this.m_sweepB = sweepB;
      this.m_sweepA.getTransform(this.xfa, t1);
      this.m_sweepB.getTransform(this.xfb, t1);
      if (count == 1) {
         this.m_type = Type.POINTS;
         this.localPointA.set(this.m_proxyA.getVertex(cache.indexA[0]));
         this.localPointB.set(this.m_proxyB.getVertex(cache.indexB[0]));
         Transform.mulToOutUnsafe(this.xfa, this.localPointA, this.pointA);
         Transform.mulToOutUnsafe(this.xfb, this.localPointB, this.pointB);
         this.m_axis.set(this.pointB).subLocal(this.pointA);
         return this.m_axis.normalize();
      } else if (cache.indexA[0] == cache.indexA[1]) {
         this.m_type = Type.FACE_B;
         this.localPointB1.set(this.m_proxyB.getVertex(cache.indexB[0]));
         this.localPointB2.set(this.m_proxyB.getVertex(cache.indexB[1]));
         this.temp.set(this.localPointB2).subLocal(this.localPointB1);
         Vec2.crossToOutUnsafe(this.temp, 1.0F, this.m_axis);
         this.m_axis.normalize();
         Rot.mulToOutUnsafe(this.xfb.q, this.m_axis, this.normal);
         this.m_localPoint.set(this.localPointB1).addLocal(this.localPointB2).mulLocal(0.5F);
         Transform.mulToOutUnsafe(this.xfb, this.m_localPoint, this.pointB);
         this.localPointA.set(proxyA.getVertex(cache.indexA[0]));
         Transform.mulToOutUnsafe(this.xfa, this.localPointA, this.pointA);
         this.temp.set(this.pointA).subLocal(this.pointB);
         float s = Vec2.dot(this.temp, this.normal);
         if (s < 0.0F) {
            this.m_axis.negateLocal();
            s = -s;
         }

         return s;
      } else {
         this.m_type = Type.FACE_A;
         this.localPointA1.set(this.m_proxyA.getVertex(cache.indexA[0]));
         this.localPointA2.set(this.m_proxyA.getVertex(cache.indexA[1]));
         this.temp.set(this.localPointA2).subLocal(this.localPointA1);
         Vec2.crossToOutUnsafe(this.temp, 1.0F, this.m_axis);
         this.m_axis.normalize();
         Rot.mulToOutUnsafe(this.xfa.q, this.m_axis, this.normal);
         this.m_localPoint.set(this.localPointA1).addLocal(this.localPointA2).mulLocal(0.5F);
         Transform.mulToOutUnsafe(this.xfa, this.m_localPoint, this.pointA);
         this.localPointB.set(this.m_proxyB.getVertex(cache.indexB[0]));
         Transform.mulToOutUnsafe(this.xfb, this.localPointB, this.pointB);
         this.temp.set(this.pointB).subLocal(this.pointA);
         float s = Vec2.dot(this.temp, this.normal);
         if (s < 0.0F) {
            this.m_axis.negateLocal();
            s = -s;
         }

         return s;
      }
   }

   public float findMinSeparation(int[] indexes, float t) {
      this.m_sweepA.getTransform(this.xfa, t);
      this.m_sweepB.getTransform(this.xfb, t);
      switch (this.m_type) {
         case POINTS:
            Rot.mulTransUnsafe(this.xfa.q, this.m_axis, this.axisA);
            Rot.mulTransUnsafe(this.xfb.q, this.m_axis.negateLocal(), this.axisB);
            this.m_axis.negateLocal();
            indexes[0] = this.m_proxyA.getSupport(this.axisA);
            indexes[1] = this.m_proxyB.getSupport(this.axisB);
            this.localPointA.set(this.m_proxyA.getVertex(indexes[0]));
            this.localPointB.set(this.m_proxyB.getVertex(indexes[1]));
            Transform.mulToOutUnsafe(this.xfa, this.localPointA, this.pointA);
            Transform.mulToOutUnsafe(this.xfb, this.localPointB, this.pointB);
            return Vec2.dot(this.pointB.subLocal(this.pointA), this.m_axis);
         case FACE_A:
            Rot.mulToOutUnsafe(this.xfa.q, this.m_axis, this.normal);
            Transform.mulToOutUnsafe(this.xfa, this.m_localPoint, this.pointA);
            Rot.mulTransUnsafe(this.xfb.q, this.normal.negateLocal(), this.axisB);
            this.normal.negateLocal();
            indexes[0] = -1;
            indexes[1] = this.m_proxyB.getSupport(this.axisB);
            this.localPointB.set(this.m_proxyB.getVertex(indexes[1]));
            Transform.mulToOutUnsafe(this.xfb, this.localPointB, this.pointB);
            return Vec2.dot(this.pointB.subLocal(this.pointA), this.normal);
         case FACE_B:
            Rot.mulToOutUnsafe(this.xfb.q, this.m_axis, this.normal);
            Transform.mulToOutUnsafe(this.xfb, this.m_localPoint, this.pointB);
            Rot.mulTransUnsafe(this.xfa.q, this.normal.negateLocal(), this.axisA);
            this.normal.negateLocal();
            indexes[1] = -1;
            indexes[0] = this.m_proxyA.getSupport(this.axisA);
            this.localPointA.set(this.m_proxyA.getVertex(indexes[0]));
            Transform.mulToOutUnsafe(this.xfa, this.localPointA, this.pointA);
            return Vec2.dot(this.pointA.subLocal(this.pointB), this.normal);
         default:
            assert false;

            indexes[0] = -1;
            indexes[1] = -1;
            return 0.0F;
      }
   }

   public float evaluate(int indexA, int indexB, float t) {
      this.m_sweepA.getTransform(this.xfa, t);
      this.m_sweepB.getTransform(this.xfb, t);
      switch (this.m_type) {
         case POINTS:
            this.localPointA.set(this.m_proxyA.getVertex(indexA));
            this.localPointB.set(this.m_proxyB.getVertex(indexB));
            Transform.mulToOutUnsafe(this.xfa, this.localPointA, this.pointA);
            Transform.mulToOutUnsafe(this.xfb, this.localPointB, this.pointB);
            return Vec2.dot(this.pointB.subLocal(this.pointA), this.m_axis);
         case FACE_A:
            Rot.mulToOutUnsafe(this.xfa.q, this.m_axis, this.normal);
            Transform.mulToOutUnsafe(this.xfa, this.m_localPoint, this.pointA);
            this.localPointB.set(this.m_proxyB.getVertex(indexB));
            Transform.mulToOutUnsafe(this.xfb, this.localPointB, this.pointB);
            return Vec2.dot(this.pointB.subLocal(this.pointA), this.normal);
         case FACE_B:
            Rot.mulToOutUnsafe(this.xfb.q, this.m_axis, this.normal);
            Transform.mulToOutUnsafe(this.xfb, this.m_localPoint, this.pointB);
            this.localPointA.set(this.m_proxyA.getVertex(indexA));
            Transform.mulToOutUnsafe(this.xfa, this.localPointA, this.pointA);
            return Vec2.dot(this.pointA.subLocal(this.pointB), this.normal);
         default:
            assert false;

            return 0.0F;
      }
   }
}
