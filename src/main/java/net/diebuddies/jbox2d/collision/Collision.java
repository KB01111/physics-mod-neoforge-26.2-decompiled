package net.diebuddies.jbox2d.collision;

import net.diebuddies.jbox2d.collision.shapes.CircleShape;
import net.diebuddies.jbox2d.collision.shapes.EdgeShape;
import net.diebuddies.jbox2d.collision.shapes.PolygonShape;
import net.diebuddies.jbox2d.collision.shapes.Shape;
import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Transform;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.pooling.IWorldPool;

public class Collision {
   public static final int NULL_FEATURE = Integer.MAX_VALUE;
   private final IWorldPool pool;
   private final DistanceInput input = new DistanceInput();
   private final Distance.SimplexCache cache = new Distance.SimplexCache();
   private final DistanceOutput output = new DistanceOutput();
   private static Vec2 d = new Vec2();
   private final Vec2 temp = new Vec2();
   private final Transform xf = new Transform();
   private final Vec2 n = new Vec2();
   private final Vec2 v1 = new Vec2();
   private final Collision.EdgeResults results1 = new Collision.EdgeResults();
   private final Collision.EdgeResults results2 = new Collision.EdgeResults();
   private final Collision.ClipVertex[] incidentEdge = new Collision.ClipVertex[2];
   private final Vec2 localTangent = new Vec2();
   private final Vec2 localNormal = new Vec2();
   private final Vec2 planePoint = new Vec2();
   private final Vec2 tangent = new Vec2();
   private final Vec2 v11 = new Vec2();
   private final Vec2 v12 = new Vec2();
   private final Collision.ClipVertex[] clipPoints1 = new Collision.ClipVertex[2];
   private final Collision.ClipVertex[] clipPoints2 = new Collision.ClipVertex[2];
   private final Vec2 Q = new Vec2();
   private final Vec2 e = new Vec2();
   private final ContactID cf = new ContactID();
   private final Vec2 e1 = new Vec2();
   private final Vec2 P = new Vec2();
   private final Collision.EPCollider collider = new Collision.EPCollider();

   public Collision(IWorldPool argPool) {
      this.incidentEdge[0] = new Collision.ClipVertex();
      this.incidentEdge[1] = new Collision.ClipVertex();
      this.clipPoints1[0] = new Collision.ClipVertex();
      this.clipPoints1[1] = new Collision.ClipVertex();
      this.clipPoints2[0] = new Collision.ClipVertex();
      this.clipPoints2[1] = new Collision.ClipVertex();
      this.pool = argPool;
   }

   public final boolean testOverlap(Shape shapeA, int indexA, Shape shapeB, int indexB, Transform xfA, Transform xfB) {
      this.input.proxyA.set(shapeA, indexA);
      this.input.proxyB.set(shapeB, indexB);
      this.input.transformA.set(xfA);
      this.input.transformB.set(xfB);
      this.input.useRadii = true;
      this.cache.count = 0;
      this.pool.getDistance().distance(this.output, this.cache, this.input);
      return this.output.distance < 1.1920929E-6F;
   }

   public static final void getPointStates(Collision.PointState[] state1, Collision.PointState[] state2, Manifold manifold1, Manifold manifold2) {
      for (int i = 0; i < Settings.maxManifoldPoints; i++) {
         state1[i] = Collision.PointState.NULL_STATE;
         state2[i] = Collision.PointState.NULL_STATE;
      }

      for (int i = 0; i < manifold1.pointCount; i++) {
         ContactID id = manifold1.points[i].id;
         state1[i] = Collision.PointState.REMOVE_STATE;

         for (int j = 0; j < manifold2.pointCount; j++) {
            if (manifold2.points[j].id.isEqual(id)) {
               state1[i] = Collision.PointState.PERSIST_STATE;
               break;
            }
         }
      }

      for (int i = 0; i < manifold2.pointCount; i++) {
         ContactID id = manifold2.points[i].id;
         state2[i] = Collision.PointState.ADD_STATE;

         for (int jx = 0; jx < manifold1.pointCount; jx++) {
            if (manifold1.points[jx].id.isEqual(id)) {
               state2[i] = Collision.PointState.PERSIST_STATE;
               break;
            }
         }
      }
   }

   public static final int clipSegmentToLine(Collision.ClipVertex[] vOut, Collision.ClipVertex[] vIn, Vec2 normal, float offset, int vertexIndexA) {
      int numOut = 0;
      Collision.ClipVertex vIn0 = vIn[0];
      Collision.ClipVertex vIn1 = vIn[1];
      Vec2 vIn0v = vIn0.v;
      Vec2 vIn1v = vIn1.v;
      float distance0 = Vec2.dot(normal, vIn0v) - offset;
      float distance1 = Vec2.dot(normal, vIn1v) - offset;
      if (distance0 <= 0.0F) {
         vOut[numOut++].set(vIn0);
      }

      if (distance1 <= 0.0F) {
         vOut[numOut++].set(vIn1);
      }

      if (distance0 * distance1 < 0.0F) {
         float interp = distance0 / (distance0 - distance1);
         Collision.ClipVertex vOutNO = vOut[numOut];
         vOutNO.v.x = vIn0v.x + interp * (vIn1v.x - vIn0v.x);
         vOutNO.v.y = vIn0v.y + interp * (vIn1v.y - vIn0v.y);
         vOutNO.id.indexA = (byte)vertexIndexA;
         vOutNO.id.indexB = vIn0.id.indexB;
         vOutNO.id.typeA = (byte)ContactID.Type.VERTEX.ordinal();
         vOutNO.id.typeB = (byte)ContactID.Type.FACE.ordinal();
         numOut++;
      }

      return numOut;
   }

   public final void collideCircles(Manifold manifold, CircleShape circle1, Transform xfA, CircleShape circle2, Transform xfB) {
      manifold.pointCount = 0;
      Vec2 circle1p = circle1.m_p;
      Vec2 circle2p = circle2.m_p;
      float pAx = xfA.q.c * circle1p.x - xfA.q.s * circle1p.y + xfA.p.x;
      float pAy = xfA.q.s * circle1p.x + xfA.q.c * circle1p.y + xfA.p.y;
      float pBx = xfB.q.c * circle2p.x - xfB.q.s * circle2p.y + xfB.p.x;
      float pBy = xfB.q.s * circle2p.x + xfB.q.c * circle2p.y + xfB.p.y;
      float dx = pBx - pAx;
      float dy = pBy - pAy;
      float distSqr = dx * dx + dy * dy;
      float radius = circle1.m_radius + circle2.m_radius;
      if (!(distSqr > radius * radius)) {
         manifold.type = Manifold.ManifoldType.CIRCLES;
         manifold.localPoint.set(circle1p);
         manifold.localNormal.setZero();
         manifold.pointCount = 1;
         manifold.points[0].localPoint.set(circle2p);
         manifold.points[0].id.zero();
      }
   }

   public final void collidePolygonAndCircle(Manifold manifold, PolygonShape polygon, Transform xfA, CircleShape circle, Transform xfB) {
      manifold.pointCount = 0;
      Vec2 circlep = circle.m_p;
      Rot xfBq = xfB.q;
      Rot xfAq = xfA.q;
      float cx = xfBq.c * circlep.x - xfBq.s * circlep.y + xfB.p.x;
      float cy = xfBq.s * circlep.x + xfBq.c * circlep.y + xfB.p.y;
      float px = cx - xfA.p.x;
      float py = cy - xfA.p.y;
      float cLocalx = xfAq.c * px + xfAq.s * py;
      float cLocaly = -xfAq.s * px + xfAq.c * py;
      int normalIndex = 0;
      float separation = -Float.MAX_VALUE;
      float radius = polygon.m_radius + circle.m_radius;
      int vertexCount = polygon.m_count;
      Vec2[] vertices = polygon.m_vertices;
      Vec2[] normals = polygon.m_normals;

      for (int i = 0; i < vertexCount; i++) {
         Vec2 vertex = vertices[i];
         float tempx = cLocalx - vertex.x;
         float tempy = cLocaly - vertex.y;
         float s = normals[i].x * tempx + normals[i].y * tempy;
         if (s > radius) {
            return;
         }

         if (s > separation) {
            separation = s;
            normalIndex = i;
         }
      }

      int vertIndex2 = normalIndex + 1 < vertexCount ? normalIndex + 1 : 0;
      Vec2 v1 = vertices[normalIndex];
      Vec2 v2 = vertices[vertIndex2];
      if (separation < 1.1920929E-7F) {
         manifold.pointCount = 1;
         manifold.type = Manifold.ManifoldType.FACE_A;
         Vec2 normal = normals[normalIndex];
         manifold.localNormal.x = normal.x;
         manifold.localNormal.y = normal.y;
         manifold.localPoint.x = (v1.x + v2.x) * 0.5F;
         manifold.localPoint.y = (v1.y + v2.y) * 0.5F;
         ManifoldPoint mpoint = manifold.points[0];
         mpoint.localPoint.x = circlep.x;
         mpoint.localPoint.y = circlep.y;
         mpoint.id.zero();
      } else {
         float tempX = cLocalx - v1.x;
         float tempY = cLocaly - v1.y;
         float temp2X = v2.x - v1.x;
         float temp2Y = v2.y - v1.y;
         float u1 = tempX * temp2X + tempY * temp2Y;
         float temp3X = cLocalx - v2.x;
         float temp3Y = cLocaly - v2.y;
         float temp4X = v1.x - v2.x;
         float temp4Y = v1.y - v2.y;
         float u2 = temp3X * temp4X + temp3Y * temp4Y;
         if (u1 <= 0.0F) {
            float dx = cLocalx - v1.x;
            float dy = cLocaly - v1.y;
            if (dx * dx + dy * dy > radius * radius) {
               return;
            }

            manifold.pointCount = 1;
            manifold.type = Manifold.ManifoldType.FACE_A;
            manifold.localNormal.x = cLocalx - v1.x;
            manifold.localNormal.y = cLocaly - v1.y;
            manifold.localNormal.normalize();
            manifold.localPoint.set(v1);
            manifold.points[0].localPoint.set(circlep);
            manifold.points[0].id.zero();
         } else if (u2 <= 0.0F) {
            float dx = cLocalx - v2.x;
            float dy = cLocaly - v2.y;
            if (dx * dx + dy * dy > radius * radius) {
               return;
            }

            manifold.pointCount = 1;
            manifold.type = Manifold.ManifoldType.FACE_A;
            manifold.localNormal.x = cLocalx - v2.x;
            manifold.localNormal.y = cLocaly - v2.y;
            manifold.localNormal.normalize();
            manifold.localPoint.set(v2);
            manifold.points[0].localPoint.set(circlep);
            manifold.points[0].id.zero();
         } else {
            float fcx = (v1.x + v2.x) * 0.5F;
            float fcy = (v1.y + v2.y) * 0.5F;
            float tx = cLocalx - fcx;
            float ty = cLocaly - fcy;
            Vec2 normal = normals[normalIndex];
            separation = tx * normal.x + ty * normal.y;
            if (separation > radius) {
               return;
            }

            manifold.pointCount = 1;
            manifold.type = Manifold.ManifoldType.FACE_A;
            manifold.localNormal.set(normals[normalIndex]);
            manifold.localPoint.x = fcx;
            manifold.localPoint.y = fcy;
            manifold.points[0].localPoint.set(circlep);
            manifold.points[0].id.zero();
         }
      }
   }

   public final void findMaxSeparation(Collision.EdgeResults results, PolygonShape poly1, Transform xf1, PolygonShape poly2, Transform xf2) {
      int count1 = poly1.m_count;
      int count2 = poly2.m_count;
      Vec2[] n1s = poly1.m_normals;
      Vec2[] v1s = poly1.m_vertices;
      Vec2[] v2s = poly2.m_vertices;
      Transform.mulTransToOutUnsafe(xf2, xf1, this.xf);
      Rot xfq = this.xf.q;
      int bestIndex = 0;
      float maxSeparation = -Float.MAX_VALUE;

      for (int i = 0; i < count1; i++) {
         Rot.mulToOutUnsafe(xfq, n1s[i], this.n);
         Transform.mulToOutUnsafe(this.xf, v1s[i], this.v1);
         float si = Float.MAX_VALUE;

         for (int j = 0; j < count2; j++) {
            Vec2 v2sj = v2s[j];
            float sij = this.n.x * (v2sj.x - this.v1.x) + this.n.y * (v2sj.y - this.v1.y);
            if (sij < si) {
               si = sij;
            }
         }

         if (si > maxSeparation) {
            maxSeparation = si;
            bestIndex = i;
         }
      }

      results.edgeIndex = bestIndex;
      results.separation = maxSeparation;
   }

   public final void findIncidentEdge(Collision.ClipVertex[] c, PolygonShape poly1, Transform xf1, int edge1, PolygonShape poly2, Transform xf2) {
      int count1 = poly1.m_count;
      Vec2[] normals1 = poly1.m_normals;
      int count2 = poly2.m_count;
      Vec2[] vertices2 = poly2.m_vertices;
      Vec2[] normals2 = poly2.m_normals;

      assert 0 <= edge1 && edge1 < count1;

      Collision.ClipVertex c0 = c[0];
      Collision.ClipVertex c1 = c[1];
      Rot xf1q = xf1.q;
      Rot xf2q = xf2.q;
      Vec2 v = normals1[edge1];
      float tempx = xf1q.c * v.x - xf1q.s * v.y;
      float tempy = xf1q.s * v.x + xf1q.c * v.y;
      float normal1x = xf2q.c * tempx + xf2q.s * tempy;
      float normal1y = -xf2q.s * tempx + xf2q.c * tempy;
      int index = 0;
      float minDot = Float.MAX_VALUE;

      for (int i = 0; i < count2; i++) {
         Vec2 b = normals2[i];
         float dot = normal1x * b.x + normal1y * b.y;
         if (dot < minDot) {
            minDot = dot;
            index = i;
         }
      }

      int i2 = index + 1 < count2 ? index + 1 : 0;
      Vec2 v1 = vertices2[index];
      Vec2 out = c0.v;
      out.x = xf2q.c * v1.x - xf2q.s * v1.y + xf2.p.x;
      out.y = xf2q.s * v1.x + xf2q.c * v1.y + xf2.p.y;
      c0.id.indexA = (byte)edge1;
      c0.id.indexB = (byte)index;
      c0.id.typeA = (byte)ContactID.Type.FACE.ordinal();
      c0.id.typeB = (byte)ContactID.Type.VERTEX.ordinal();
      Vec2 v2 = vertices2[i2];
      Vec2 out1 = c1.v;
      out1.x = xf2q.c * v2.x - xf2q.s * v2.y + xf2.p.x;
      out1.y = xf2q.s * v2.x + xf2q.c * v2.y + xf2.p.y;
      c1.id.indexA = (byte)edge1;
      c1.id.indexB = (byte)i2;
      c1.id.typeA = (byte)ContactID.Type.FACE.ordinal();
      c1.id.typeB = (byte)ContactID.Type.VERTEX.ordinal();
   }

   public final void collidePolygons(Manifold manifold, PolygonShape polyA, Transform xfA, PolygonShape polyB, Transform xfB) {
      manifold.pointCount = 0;
      float totalRadius = polyA.m_radius + polyB.m_radius;
      this.findMaxSeparation(this.results1, polyA, xfA, polyB, xfB);
      if (!(this.results1.separation > totalRadius)) {
         this.findMaxSeparation(this.results2, polyB, xfB, polyA, xfA);
         if (!(this.results2.separation > totalRadius)) {
            float k_tol = 0.1F * Settings.linearSlop;
            PolygonShape poly1;
            PolygonShape poly2;
            Transform xf1;
            Transform xf2;
            int edge1;
            boolean flip;
            if (this.results2.separation > this.results1.separation + k_tol) {
               poly1 = polyB;
               poly2 = polyA;
               xf1 = xfB;
               xf2 = xfA;
               edge1 = this.results2.edgeIndex;
               manifold.type = Manifold.ManifoldType.FACE_B;
               flip = true;
            } else {
               poly1 = polyA;
               poly2 = polyB;
               xf1 = xfA;
               xf2 = xfB;
               edge1 = this.results1.edgeIndex;
               manifold.type = Manifold.ManifoldType.FACE_A;
               flip = false;
            }

            Rot xf1q = xf1.q;
            this.findIncidentEdge(this.incidentEdge, poly1, xf1, edge1, poly2, xf2);
            int count1 = poly1.m_count;
            Vec2[] vertices1 = poly1.m_vertices;
            int iv2 = edge1 + 1 < count1 ? edge1 + 1 : 0;
            this.v11.set(vertices1[edge1]);
            this.v12.set(vertices1[iv2]);
            this.localTangent.x = this.v12.x - this.v11.x;
            this.localTangent.y = this.v12.y - this.v11.y;
            this.localTangent.normalize();
            this.localNormal.x = 1.0F * this.localTangent.y;
            this.localNormal.y = -1.0F * this.localTangent.x;
            this.planePoint.x = (this.v11.x + this.v12.x) * 0.5F;
            this.planePoint.y = (this.v11.y + this.v12.y) * 0.5F;
            this.tangent.x = xf1q.c * this.localTangent.x - xf1q.s * this.localTangent.y;
            this.tangent.y = xf1q.s * this.localTangent.x + xf1q.c * this.localTangent.y;
            float normalx = 1.0F * this.tangent.y;
            float normaly = -1.0F * this.tangent.x;
            Transform.mulToOut(xf1, this.v11, this.v11);
            Transform.mulToOut(xf1, this.v12, this.v12);
            float frontOffset = normalx * this.v11.x + normaly * this.v11.y;
            float sideOffset1 = -(this.tangent.x * this.v11.x + this.tangent.y * this.v11.y) + totalRadius;
            float sideOffset2 = this.tangent.x * this.v12.x + this.tangent.y * this.v12.y + totalRadius;
            this.tangent.negateLocal();
            int np = clipSegmentToLine(this.clipPoints1, this.incidentEdge, this.tangent, sideOffset1, edge1);
            this.tangent.negateLocal();
            if (np >= 2) {
               np = clipSegmentToLine(this.clipPoints2, this.clipPoints1, this.tangent, sideOffset2, iv2);
               if (np >= 2) {
                  manifold.localNormal.set(this.localNormal);
                  manifold.localPoint.set(this.planePoint);
                  int pointCount = 0;

                  for (int i = 0; i < Settings.maxManifoldPoints; i++) {
                     float separation = normalx * this.clipPoints2[i].v.x + normaly * this.clipPoints2[i].v.y - frontOffset;
                     if (separation <= totalRadius) {
                        ManifoldPoint cp = manifold.points[pointCount];
                        Vec2 out = cp.localPoint;
                        float px = this.clipPoints2[i].v.x - xf2.p.x;
                        float py = this.clipPoints2[i].v.y - xf2.p.y;
                        out.x = xf2.q.c * px + xf2.q.s * py;
                        out.y = -xf2.q.s * px + xf2.q.c * py;
                        cp.id.set(this.clipPoints2[i].id);
                        if (flip) {
                           cp.id.flip();
                        }

                        pointCount++;
                     }
                  }

                  manifold.pointCount = pointCount;
               }
            }
         }
      }
   }

   public void collideEdgeAndCircle(Manifold manifold, EdgeShape edgeA, Transform xfA, CircleShape circleB, Transform xfB) {
      manifold.pointCount = 0;
      Transform.mulToOutUnsafe(xfB, circleB.m_p, this.temp);
      Transform.mulTransToOutUnsafe(xfA, this.temp, this.Q);
      Vec2 A = edgeA.m_vertex1;
      Vec2 B = edgeA.m_vertex2;
      this.e.set(B).subLocal(A);
      float u = Vec2.dot(this.e, this.temp.set(B).subLocal(this.Q));
      float v = Vec2.dot(this.e, this.temp.set(this.Q).subLocal(A));
      float radius = edgeA.m_radius + circleB.m_radius;
      this.cf.indexB = 0;
      this.cf.typeB = (byte)ContactID.Type.VERTEX.ordinal();
      if (v <= 0.0F) {
         d.set(this.Q).subLocal(A);
         float dd = Vec2.dot(d, d);
         if (!(dd > radius * radius)) {
            if (edgeA.m_hasVertex0) {
               Vec2 A1 = edgeA.m_vertex0;
               this.e1.set(A).subLocal(A1);
               float u1 = Vec2.dot(this.e1, this.temp.set(A).subLocal(this.Q));
               if (u1 > 0.0F) {
                  return;
               }
            }

            this.cf.indexA = 0;
            this.cf.typeA = (byte)ContactID.Type.VERTEX.ordinal();
            manifold.pointCount = 1;
            manifold.type = Manifold.ManifoldType.CIRCLES;
            manifold.localNormal.setZero();
            manifold.localPoint.set(A);
            manifold.points[0].id.set(this.cf);
            manifold.points[0].localPoint.set(circleB.m_p);
         }
      } else if (u <= 0.0F) {
         d.set(this.Q).subLocal(B);
         float dd = Vec2.dot(d, d);
         if (!(dd > radius * radius)) {
            if (edgeA.m_hasVertex3) {
               Vec2 B2 = edgeA.m_vertex3;
               Vec2 e2 = this.e1;
               e2.set(B2).subLocal(B);
               float v2 = Vec2.dot(e2, this.temp.set(this.Q).subLocal(B));
               if (v2 > 0.0F) {
                  return;
               }
            }

            this.cf.indexA = 1;
            this.cf.typeA = (byte)ContactID.Type.VERTEX.ordinal();
            manifold.pointCount = 1;
            manifold.type = Manifold.ManifoldType.CIRCLES;
            manifold.localNormal.setZero();
            manifold.localPoint.set(B);
            manifold.points[0].id.set(this.cf);
            manifold.points[0].localPoint.set(circleB.m_p);
         }
      } else {
         float den = Vec2.dot(this.e, this.e);

         assert den > 0.0F;

         this.P.set(A).mulLocal(u).addLocal(this.temp.set(B).mulLocal(v));
         this.P.mulLocal(1.0F / den);
         d.set(this.Q).subLocal(this.P);
         float dd = Vec2.dot(d, d);
         if (!(dd > radius * radius)) {
            this.n.x = -this.e.y;
            this.n.y = this.e.x;
            if (Vec2.dot(this.n, this.temp.set(this.Q).subLocal(A)) < 0.0F) {
               this.n.set(-this.n.x, -this.n.y);
            }

            this.n.normalize();
            this.cf.indexA = 0;
            this.cf.typeA = (byte)ContactID.Type.FACE.ordinal();
            manifold.pointCount = 1;
            manifold.type = Manifold.ManifoldType.FACE_A;
            manifold.localNormal.set(this.n);
            manifold.localPoint.set(A);
            manifold.points[0].id.set(this.cf);
            manifold.points[0].localPoint.set(circleB.m_p);
         }
      }
   }

   public void collideEdgeAndPolygon(Manifold manifold, EdgeShape edgeA, Transform xfA, PolygonShape polygonB, Transform xfB) {
      this.collider.collide(manifold, edgeA, xfA, polygonB, xfB);
   }

   public static class ClipVertex {
      public final Vec2 v = new Vec2();
      public final ContactID id = new ContactID();

      public void set(Collision.ClipVertex cv) {
         Vec2 v1 = cv.v;
         this.v.x = v1.x;
         this.v.y = v1.y;
         ContactID c = cv.id;
         this.id.indexA = c.indexA;
         this.id.indexB = c.indexB;
         this.id.typeA = c.typeA;
         this.id.typeB = c.typeB;
      }
   }

   static class EPAxis {
      Collision.EPAxis.Type type;
      int index;
      float separation;

      static enum Type {
         UNKNOWN,
         EDGE_A,
         EDGE_B;
      }
   }

   static class EPCollider {
      final Collision.TempPolygon m_polygonB = new Collision.TempPolygon();
      final Transform m_xf = new Transform();
      final Vec2 m_centroidB = new Vec2();
      Vec2 m_v0 = new Vec2();
      Vec2 m_v1 = new Vec2();
      Vec2 m_v2 = new Vec2();
      Vec2 m_v3 = new Vec2();
      final Vec2 m_normal0 = new Vec2();
      final Vec2 m_normal1 = new Vec2();
      final Vec2 m_normal2 = new Vec2();
      final Vec2 m_normal = new Vec2();
      Collision.EPCollider.VertexType m_type1;
      Collision.EPCollider.VertexType m_type2;
      final Vec2 m_lowerLimit = new Vec2();
      final Vec2 m_upperLimit = new Vec2();
      float m_radius;
      boolean m_front;
      private final Vec2 edge1 = new Vec2();
      private final Vec2 temp = new Vec2();
      private final Vec2 edge0 = new Vec2();
      private final Vec2 edge2 = new Vec2();
      private final Collision.ClipVertex[] ie = new Collision.ClipVertex[2];
      private final Collision.ClipVertex[] clipPoints1 = new Collision.ClipVertex[2];
      private final Collision.ClipVertex[] clipPoints2 = new Collision.ClipVertex[2];
      private final Collision.ReferenceFace rf = new Collision.ReferenceFace();
      private final Collision.EPAxis edgeAxis = new Collision.EPAxis();
      private final Collision.EPAxis polygonAxis = new Collision.EPAxis();
      private final Vec2 perp = new Vec2();
      private final Vec2 n = new Vec2();

      public EPCollider() {
         for (int i = 0; i < 2; i++) {
            this.ie[i] = new Collision.ClipVertex();
            this.clipPoints1[i] = new Collision.ClipVertex();
            this.clipPoints2[i] = new Collision.ClipVertex();
         }
      }

      public void collide(Manifold manifold, EdgeShape edgeA, Transform xfA, PolygonShape polygonB, Transform xfB) {
         Transform.mulTransToOutUnsafe(xfA, xfB, this.m_xf);
         Transform.mulToOutUnsafe(this.m_xf, polygonB.m_centroid, this.m_centroidB);
         this.m_v0 = edgeA.m_vertex0;
         this.m_v1 = edgeA.m_vertex1;
         this.m_v2 = edgeA.m_vertex2;
         this.m_v3 = edgeA.m_vertex3;
         boolean hasVertex0 = edgeA.m_hasVertex0;
         boolean hasVertex3 = edgeA.m_hasVertex3;
         this.edge1.set(this.m_v2).subLocal(this.m_v1);
         this.edge1.normalize();
         this.m_normal1.set(this.edge1.y, -this.edge1.x);
         float offset1 = Vec2.dot(this.m_normal1, this.temp.set(this.m_centroidB).subLocal(this.m_v1));
         float offset0 = 0.0F;
         float offset2 = 0.0F;
         boolean convex1 = false;
         boolean convex2 = false;
         if (hasVertex0) {
            this.edge0.set(this.m_v1).subLocal(this.m_v0);
            this.edge0.normalize();
            this.m_normal0.set(this.edge0.y, -this.edge0.x);
            convex1 = Vec2.cross(this.edge0, this.edge1) >= 0.0F;
            offset0 = Vec2.dot(this.m_normal0, this.temp.set(this.m_centroidB).subLocal(this.m_v0));
         }

         if (hasVertex3) {
            this.edge2.set(this.m_v3).subLocal(this.m_v2);
            this.edge2.normalize();
            this.m_normal2.set(this.edge2.y, -this.edge2.x);
            convex2 = Vec2.cross(this.edge1, this.edge2) > 0.0F;
            offset2 = Vec2.dot(this.m_normal2, this.temp.set(this.m_centroidB).subLocal(this.m_v2));
         }

         if (hasVertex0 && hasVertex3) {
            if (convex1 && convex2) {
               this.m_front = offset0 >= 0.0F || offset1 >= 0.0F || offset2 >= 0.0F;
               if (this.m_front) {
                  this.m_normal.x = this.m_normal1.x;
                  this.m_normal.y = this.m_normal1.y;
                  this.m_lowerLimit.x = this.m_normal0.x;
                  this.m_lowerLimit.y = this.m_normal0.y;
                  this.m_upperLimit.x = this.m_normal2.x;
                  this.m_upperLimit.y = this.m_normal2.y;
               } else {
                  this.m_normal.x = -this.m_normal1.x;
                  this.m_normal.y = -this.m_normal1.y;
                  this.m_lowerLimit.x = -this.m_normal1.x;
                  this.m_lowerLimit.y = -this.m_normal1.y;
                  this.m_upperLimit.x = -this.m_normal1.x;
                  this.m_upperLimit.y = -this.m_normal1.y;
               }
            } else if (convex1) {
               this.m_front = offset0 >= 0.0F || offset1 >= 0.0F && offset2 >= 0.0F;
               if (this.m_front) {
                  this.m_normal.x = this.m_normal1.x;
                  this.m_normal.y = this.m_normal1.y;
                  this.m_lowerLimit.x = this.m_normal0.x;
                  this.m_lowerLimit.y = this.m_normal0.y;
                  this.m_upperLimit.x = this.m_normal1.x;
                  this.m_upperLimit.y = this.m_normal1.y;
               } else {
                  this.m_normal.x = -this.m_normal1.x;
                  this.m_normal.y = -this.m_normal1.y;
                  this.m_lowerLimit.x = -this.m_normal2.x;
                  this.m_lowerLimit.y = -this.m_normal2.y;
                  this.m_upperLimit.x = -this.m_normal1.x;
                  this.m_upperLimit.y = -this.m_normal1.y;
               }
            } else if (convex2) {
               this.m_front = offset2 >= 0.0F || offset0 >= 0.0F && offset1 >= 0.0F;
               if (this.m_front) {
                  this.m_normal.x = this.m_normal1.x;
                  this.m_normal.y = this.m_normal1.y;
                  this.m_lowerLimit.x = this.m_normal1.x;
                  this.m_lowerLimit.y = this.m_normal1.y;
                  this.m_upperLimit.x = this.m_normal2.x;
                  this.m_upperLimit.y = this.m_normal2.y;
               } else {
                  this.m_normal.x = -this.m_normal1.x;
                  this.m_normal.y = -this.m_normal1.y;
                  this.m_lowerLimit.x = -this.m_normal1.x;
                  this.m_lowerLimit.y = -this.m_normal1.y;
                  this.m_upperLimit.x = -this.m_normal0.x;
                  this.m_upperLimit.y = -this.m_normal0.y;
               }
            } else {
               this.m_front = offset0 >= 0.0F && offset1 >= 0.0F && offset2 >= 0.0F;
               if (this.m_front) {
                  this.m_normal.x = this.m_normal1.x;
                  this.m_normal.y = this.m_normal1.y;
                  this.m_lowerLimit.x = this.m_normal1.x;
                  this.m_lowerLimit.y = this.m_normal1.y;
                  this.m_upperLimit.x = this.m_normal1.x;
                  this.m_upperLimit.y = this.m_normal1.y;
               } else {
                  this.m_normal.x = -this.m_normal1.x;
                  this.m_normal.y = -this.m_normal1.y;
                  this.m_lowerLimit.x = -this.m_normal2.x;
                  this.m_lowerLimit.y = -this.m_normal2.y;
                  this.m_upperLimit.x = -this.m_normal0.x;
                  this.m_upperLimit.y = -this.m_normal0.y;
               }
            }
         } else if (hasVertex0) {
            if (convex1) {
               this.m_front = offset0 >= 0.0F || offset1 >= 0.0F;
               if (this.m_front) {
                  this.m_normal.x = this.m_normal1.x;
                  this.m_normal.y = this.m_normal1.y;
                  this.m_lowerLimit.x = this.m_normal0.x;
                  this.m_lowerLimit.y = this.m_normal0.y;
                  this.m_upperLimit.x = -this.m_normal1.x;
                  this.m_upperLimit.y = -this.m_normal1.y;
               } else {
                  this.m_normal.x = -this.m_normal1.x;
                  this.m_normal.y = -this.m_normal1.y;
                  this.m_lowerLimit.x = this.m_normal1.x;
                  this.m_lowerLimit.y = this.m_normal1.y;
                  this.m_upperLimit.x = -this.m_normal1.x;
                  this.m_upperLimit.y = -this.m_normal1.y;
               }
            } else {
               this.m_front = offset0 >= 0.0F && offset1 >= 0.0F;
               if (this.m_front) {
                  this.m_normal.x = this.m_normal1.x;
                  this.m_normal.y = this.m_normal1.y;
                  this.m_lowerLimit.x = this.m_normal1.x;
                  this.m_lowerLimit.y = this.m_normal1.y;
                  this.m_upperLimit.x = -this.m_normal1.x;
                  this.m_upperLimit.y = -this.m_normal1.y;
               } else {
                  this.m_normal.x = -this.m_normal1.x;
                  this.m_normal.y = -this.m_normal1.y;
                  this.m_lowerLimit.x = this.m_normal1.x;
                  this.m_lowerLimit.y = this.m_normal1.y;
                  this.m_upperLimit.x = -this.m_normal0.x;
                  this.m_upperLimit.y = -this.m_normal0.y;
               }
            }
         } else if (hasVertex3) {
            if (convex2) {
               this.m_front = offset1 >= 0.0F || offset2 >= 0.0F;
               if (this.m_front) {
                  this.m_normal.x = this.m_normal1.x;
                  this.m_normal.y = this.m_normal1.y;
                  this.m_lowerLimit.x = -this.m_normal1.x;
                  this.m_lowerLimit.y = -this.m_normal1.y;
                  this.m_upperLimit.x = this.m_normal2.x;
                  this.m_upperLimit.y = this.m_normal2.y;
               } else {
                  this.m_normal.x = -this.m_normal1.x;
                  this.m_normal.y = -this.m_normal1.y;
                  this.m_lowerLimit.x = -this.m_normal1.x;
                  this.m_lowerLimit.y = -this.m_normal1.y;
                  this.m_upperLimit.x = this.m_normal1.x;
                  this.m_upperLimit.y = this.m_normal1.y;
               }
            } else {
               this.m_front = offset1 >= 0.0F && offset2 >= 0.0F;
               if (this.m_front) {
                  this.m_normal.x = this.m_normal1.x;
                  this.m_normal.y = this.m_normal1.y;
                  this.m_lowerLimit.x = -this.m_normal1.x;
                  this.m_lowerLimit.y = -this.m_normal1.y;
                  this.m_upperLimit.x = this.m_normal1.x;
                  this.m_upperLimit.y = this.m_normal1.y;
               } else {
                  this.m_normal.x = -this.m_normal1.x;
                  this.m_normal.y = -this.m_normal1.y;
                  this.m_lowerLimit.x = -this.m_normal2.x;
                  this.m_lowerLimit.y = -this.m_normal2.y;
                  this.m_upperLimit.x = this.m_normal1.x;
                  this.m_upperLimit.y = this.m_normal1.y;
               }
            }
         } else {
            this.m_front = offset1 >= 0.0F;
            if (this.m_front) {
               this.m_normal.x = this.m_normal1.x;
               this.m_normal.y = this.m_normal1.y;
               this.m_lowerLimit.x = -this.m_normal1.x;
               this.m_lowerLimit.y = -this.m_normal1.y;
               this.m_upperLimit.x = -this.m_normal1.x;
               this.m_upperLimit.y = -this.m_normal1.y;
            } else {
               this.m_normal.x = -this.m_normal1.x;
               this.m_normal.y = -this.m_normal1.y;
               this.m_lowerLimit.x = this.m_normal1.x;
               this.m_lowerLimit.y = this.m_normal1.y;
               this.m_upperLimit.x = this.m_normal1.x;
               this.m_upperLimit.y = this.m_normal1.y;
            }
         }

         this.m_polygonB.count = polygonB.m_count;

         for (int i = 0; i < polygonB.m_count; i++) {
            Transform.mulToOutUnsafe(this.m_xf, polygonB.m_vertices[i], this.m_polygonB.vertices[i]);
            Rot.mulToOutUnsafe(this.m_xf.q, polygonB.m_normals[i], this.m_polygonB.normals[i]);
         }

         this.m_radius = 2.0F * Settings.polygonRadius;
         manifold.pointCount = 0;
         this.computeEdgeSeparation(this.edgeAxis);
         if (this.edgeAxis.type != Collision.EPAxis.Type.UNKNOWN) {
            if (!(this.edgeAxis.separation > this.m_radius)) {
               this.computePolygonSeparation(this.polygonAxis);
               if (this.polygonAxis.type == Collision.EPAxis.Type.UNKNOWN || !(this.polygonAxis.separation > this.m_radius)) {
                  float k_relativeTol = 0.98F;
                  float k_absoluteTol = 0.001F;
                  Collision.EPAxis primaryAxis;
                  if (this.polygonAxis.type == Collision.EPAxis.Type.UNKNOWN) {
                     primaryAxis = this.edgeAxis;
                  } else if (this.polygonAxis.separation > 0.98F * this.edgeAxis.separation + 0.001F) {
                     primaryAxis = this.polygonAxis;
                  } else {
                     primaryAxis = this.edgeAxis;
                  }

                  Collision.ClipVertex ie0 = this.ie[0];
                  Collision.ClipVertex ie1 = this.ie[1];
                  if (primaryAxis.type == Collision.EPAxis.Type.EDGE_A) {
                     manifold.type = Manifold.ManifoldType.FACE_A;
                     int bestIndex = 0;
                     float bestValue = Vec2.dot(this.m_normal, this.m_polygonB.normals[0]);

                     for (int i = 1; i < this.m_polygonB.count; i++) {
                        float value = Vec2.dot(this.m_normal, this.m_polygonB.normals[i]);
                        if (value < bestValue) {
                           bestValue = value;
                           bestIndex = i;
                        }
                     }

                     int i2 = bestIndex + 1 < this.m_polygonB.count ? bestIndex + 1 : 0;
                     ie0.v.set(this.m_polygonB.vertices[bestIndex]);
                     ie0.id.indexA = 0;
                     ie0.id.indexB = (byte)bestIndex;
                     ie0.id.typeA = (byte)ContactID.Type.FACE.ordinal();
                     ie0.id.typeB = (byte)ContactID.Type.VERTEX.ordinal();
                     ie1.v.set(this.m_polygonB.vertices[i2]);
                     ie1.id.indexA = 0;
                     ie1.id.indexB = (byte)i2;
                     ie1.id.typeA = (byte)ContactID.Type.FACE.ordinal();
                     ie1.id.typeB = (byte)ContactID.Type.VERTEX.ordinal();
                     if (this.m_front) {
                        this.rf.i1 = 0;
                        this.rf.i2 = 1;
                        this.rf.v1.set(this.m_v1);
                        this.rf.v2.set(this.m_v2);
                        this.rf.normal.set(this.m_normal1);
                     } else {
                        this.rf.i1 = 1;
                        this.rf.i2 = 0;
                        this.rf.v1.set(this.m_v2);
                        this.rf.v2.set(this.m_v1);
                        this.rf.normal.set(this.m_normal1).negateLocal();
                     }
                  } else {
                     manifold.type = Manifold.ManifoldType.FACE_B;
                     ie0.v.set(this.m_v1);
                     ie0.id.indexA = 0;
                     ie0.id.indexB = (byte)primaryAxis.index;
                     ie0.id.typeA = (byte)ContactID.Type.VERTEX.ordinal();
                     ie0.id.typeB = (byte)ContactID.Type.FACE.ordinal();
                     ie1.v.set(this.m_v2);
                     ie1.id.indexA = 0;
                     ie1.id.indexB = (byte)primaryAxis.index;
                     ie1.id.typeA = (byte)ContactID.Type.VERTEX.ordinal();
                     ie1.id.typeB = (byte)ContactID.Type.FACE.ordinal();
                     this.rf.i1 = primaryAxis.index;
                     this.rf.i2 = this.rf.i1 + 1 < this.m_polygonB.count ? this.rf.i1 + 1 : 0;
                     this.rf.v1.set(this.m_polygonB.vertices[this.rf.i1]);
                     this.rf.v2.set(this.m_polygonB.vertices[this.rf.i2]);
                     this.rf.normal.set(this.m_polygonB.normals[this.rf.i1]);
                  }

                  this.rf.sideNormal1.set(this.rf.normal.y, -this.rf.normal.x);
                  this.rf.sideNormal2.set(this.rf.sideNormal1).negateLocal();
                  this.rf.sideOffset1 = Vec2.dot(this.rf.sideNormal1, this.rf.v1);
                  this.rf.sideOffset2 = Vec2.dot(this.rf.sideNormal2, this.rf.v2);
                  int np = Collision.clipSegmentToLine(this.clipPoints1, this.ie, this.rf.sideNormal1, this.rf.sideOffset1, this.rf.i1);
                  if (np >= Settings.maxManifoldPoints) {
                     np = Collision.clipSegmentToLine(this.clipPoints2, this.clipPoints1, this.rf.sideNormal2, this.rf.sideOffset2, this.rf.i2);
                     if (np >= Settings.maxManifoldPoints) {
                        if (primaryAxis.type == Collision.EPAxis.Type.EDGE_A) {
                           manifold.localNormal.set(this.rf.normal);
                           manifold.localPoint.set(this.rf.v1);
                        } else {
                           manifold.localNormal.set(polygonB.m_normals[this.rf.i1]);
                           manifold.localPoint.set(polygonB.m_vertices[this.rf.i1]);
                        }

                        int pointCount = 0;

                        for (int ix = 0; ix < Settings.maxManifoldPoints; ix++) {
                           float separation = Vec2.dot(this.rf.normal, this.temp.set(this.clipPoints2[ix].v).subLocal(this.rf.v1));
                           if (separation <= this.m_radius) {
                              ManifoldPoint cp = manifold.points[pointCount];
                              if (primaryAxis.type == Collision.EPAxis.Type.EDGE_A) {
                                 Transform.mulTransToOutUnsafe(this.m_xf, this.clipPoints2[ix].v, cp.localPoint);
                                 cp.id.set(this.clipPoints2[ix].id);
                              } else {
                                 cp.localPoint.set(this.clipPoints2[ix].v);
                                 cp.id.typeA = this.clipPoints2[ix].id.typeB;
                                 cp.id.typeB = this.clipPoints2[ix].id.typeA;
                                 cp.id.indexA = this.clipPoints2[ix].id.indexB;
                                 cp.id.indexB = this.clipPoints2[ix].id.indexA;
                              }

                              pointCount++;
                           }
                        }

                        manifold.pointCount = pointCount;
                     }
                  }
               }
            }
         }
      }

      public void computeEdgeSeparation(Collision.EPAxis axis) {
         axis.type = Collision.EPAxis.Type.EDGE_A;
         axis.index = this.m_front ? 0 : 1;
         axis.separation = Float.MAX_VALUE;
         float nx = this.m_normal.x;
         float ny = this.m_normal.y;

         for (int i = 0; i < this.m_polygonB.count; i++) {
            Vec2 v = this.m_polygonB.vertices[i];
            float tempx = v.x - this.m_v1.x;
            float tempy = v.y - this.m_v1.y;
            float s = nx * tempx + ny * tempy;
            if (s < axis.separation) {
               axis.separation = s;
            }
         }
      }

      public void computePolygonSeparation(Collision.EPAxis axis) {
         axis.type = Collision.EPAxis.Type.UNKNOWN;
         axis.index = -1;
         axis.separation = -Float.MAX_VALUE;
         this.perp.x = -this.m_normal.y;
         this.perp.y = this.m_normal.x;

         for (int i = 0; i < this.m_polygonB.count; i++) {
            Vec2 normalB = this.m_polygonB.normals[i];
            Vec2 vB = this.m_polygonB.vertices[i];
            this.n.x = -normalB.x;
            this.n.y = -normalB.y;
            float tempx = vB.x - this.m_v1.x;
            float tempy = vB.y - this.m_v1.y;
            float s1 = this.n.x * tempx + this.n.y * tempy;
            tempx = vB.x - this.m_v2.x;
            tempy = vB.y - this.m_v2.y;
            float s2 = this.n.x * tempx + this.n.y * tempy;
            float s = MathUtils.min(s1, s2);
            if (s > this.m_radius) {
               axis.type = Collision.EPAxis.Type.EDGE_B;
               axis.index = i;
               axis.separation = s;
               return;
            }

            if ((
                  this.n.x * this.perp.x + this.n.y * this.perp.y >= 0.0F
                     ? !(Vec2.dot(this.temp.set(this.n).subLocal(this.m_upperLimit), this.m_normal) < -Settings.angularSlop)
                     : !(Vec2.dot(this.temp.set(this.n).subLocal(this.m_lowerLimit), this.m_normal) < -Settings.angularSlop)
               )
               && s > axis.separation) {
               axis.type = Collision.EPAxis.Type.EDGE_B;
               axis.index = i;
               axis.separation = s;
            }
         }
      }

      static enum VertexType {
         ISOLATED,
         CONCAVE,
         CONVEX;
      }
   }

   private static class EdgeResults {
      public float separation;
      public int edgeIndex;
   }

   public static enum PointState {
      NULL_STATE,
      ADD_STATE,
      PERSIST_STATE,
      REMOVE_STATE;
   }

   static class ReferenceFace {
      int i1;
      int i2;
      final Vec2 v1 = new Vec2();
      final Vec2 v2 = new Vec2();
      final Vec2 normal = new Vec2();
      final Vec2 sideNormal1 = new Vec2();
      float sideOffset1;
      final Vec2 sideNormal2 = new Vec2();
      float sideOffset2;
   }

   static class TempPolygon {
      final Vec2[] vertices = new Vec2[Settings.maxPolygonVertices];
      final Vec2[] normals = new Vec2[Settings.maxPolygonVertices];
      int count;

      public TempPolygon() {
         for (int i = 0; i < this.vertices.length; i++) {
            this.vertices[i] = new Vec2();
            this.normals[i] = new Vec2();
         }
      }
   }
}
