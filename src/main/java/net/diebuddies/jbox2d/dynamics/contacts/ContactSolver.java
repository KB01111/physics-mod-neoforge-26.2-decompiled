package net.diebuddies.jbox2d.dynamics.contacts;

import net.diebuddies.jbox2d.collision.Manifold;
import net.diebuddies.jbox2d.collision.ManifoldPoint;
import net.diebuddies.jbox2d.collision.WorldManifold;
import net.diebuddies.jbox2d.collision.shapes.Shape;
import net.diebuddies.jbox2d.common.Mat22;
import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Transform;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Body;
import net.diebuddies.jbox2d.dynamics.Fixture;
import net.diebuddies.jbox2d.dynamics.TimeStep;

public class ContactSolver {
   public static final boolean DEBUG_SOLVER = false;
   public static final float k_errorTol = 0.001F;
   public static final int INITIAL_NUM_CONSTRAINTS = 256;
   public static final float k_maxConditionNumber = 100.0F;
   public TimeStep m_step;
   public Position[] m_positions;
   public Velocity[] m_velocities;
   public ContactPositionConstraint[] m_positionConstraints;
   public ContactVelocityConstraint[] m_velocityConstraints;
   public Contact[] m_contacts;
   public int m_count;
   private final Transform xfA = new Transform();
   private final Transform xfB = new Transform();
   private final WorldManifold worldManifold = new WorldManifold();
   private final PositionSolverManifold psolver = new PositionSolverManifold();

   public ContactSolver() {
      this.m_positionConstraints = new ContactPositionConstraint[256];
      this.m_velocityConstraints = new ContactVelocityConstraint[256];

      for (int i = 0; i < 256; i++) {
         this.m_positionConstraints[i] = new ContactPositionConstraint();
         this.m_velocityConstraints[i] = new ContactVelocityConstraint();
      }
   }

   public final void init(ContactSolver.ContactSolverDef def) {
      this.m_step = def.step;
      this.m_count = def.count;
      if (this.m_positionConstraints.length < this.m_count) {
         ContactPositionConstraint[] old = this.m_positionConstraints;
         this.m_positionConstraints = new ContactPositionConstraint[MathUtils.max(old.length * 2, this.m_count)];
         System.arraycopy(old, 0, this.m_positionConstraints, 0, old.length);

         for (int i = old.length; i < this.m_positionConstraints.length; i++) {
            this.m_positionConstraints[i] = new ContactPositionConstraint();
         }
      }

      if (this.m_velocityConstraints.length < this.m_count) {
         ContactVelocityConstraint[] old = this.m_velocityConstraints;
         this.m_velocityConstraints = new ContactVelocityConstraint[MathUtils.max(old.length * 2, this.m_count)];
         System.arraycopy(old, 0, this.m_velocityConstraints, 0, old.length);

         for (int i = old.length; i < this.m_velocityConstraints.length; i++) {
            this.m_velocityConstraints[i] = new ContactVelocityConstraint();
         }
      }

      this.m_positions = def.positions;
      this.m_velocities = def.velocities;
      this.m_contacts = def.contacts;

      for (int i = 0; i < this.m_count; i++) {
         Contact contact = this.m_contacts[i];
         Fixture fixtureA = contact.m_fixtureA;
         Fixture fixtureB = contact.m_fixtureB;
         Shape shapeA = fixtureA.getShape();
         Shape shapeB = fixtureB.getShape();
         float radiusA = shapeA.m_radius;
         float radiusB = shapeB.m_radius;
         Body bodyA = fixtureA.getBody();
         Body bodyB = fixtureB.getBody();
         Manifold manifold = contact.getManifold();
         int pointCount = manifold.pointCount;

         assert pointCount > 0;

         ContactVelocityConstraint vc = this.m_velocityConstraints[i];
         vc.friction = contact.m_friction;
         vc.restitution = contact.m_restitution;
         vc.tangentSpeed = contact.m_tangentSpeed;
         vc.indexA = bodyA.m_islandIndex;
         vc.indexB = bodyB.m_islandIndex;
         vc.invMassA = bodyA.m_invMass;
         vc.invMassB = bodyB.m_invMass;
         vc.invIA = bodyA.m_invI;
         vc.invIB = bodyB.m_invI;
         vc.contactIndex = i;
         vc.pointCount = pointCount;
         vc.K.setZero();
         vc.normalMass.setZero();
         ContactPositionConstraint pc = this.m_positionConstraints[i];
         pc.indexA = bodyA.m_islandIndex;
         pc.indexB = bodyB.m_islandIndex;
         pc.invMassA = bodyA.m_invMass;
         pc.invMassB = bodyB.m_invMass;
         pc.localCenterA.set(bodyA.m_sweep.localCenter);
         pc.localCenterB.set(bodyB.m_sweep.localCenter);
         pc.invIA = bodyA.m_invI;
         pc.invIB = bodyB.m_invI;
         pc.localNormal.set(manifold.localNormal);
         pc.localPoint.set(manifold.localPoint);
         pc.pointCount = pointCount;
         pc.radiusA = radiusA;
         pc.radiusB = radiusB;
         pc.type = manifold.type;

         for (int j = 0; j < pointCount; j++) {
            ManifoldPoint cp = manifold.points[j];
            ContactVelocityConstraint.VelocityConstraintPoint vcp = vc.points[j];
            if (this.m_step.warmStarting) {
               vcp.normalImpulse = this.m_step.dtRatio * cp.normalImpulse;
               vcp.tangentImpulse = this.m_step.dtRatio * cp.tangentImpulse;
            } else {
               vcp.normalImpulse = 0.0F;
               vcp.tangentImpulse = 0.0F;
            }

            vcp.rA.setZero();
            vcp.rB.setZero();
            vcp.normalMass = 0.0F;
            vcp.tangentMass = 0.0F;
            vcp.velocityBias = 0.0F;
            pc.localPoints[j].x = cp.localPoint.x;
            pc.localPoints[j].y = cp.localPoint.y;
         }
      }
   }

   public void warmStart() {
      for (int i = 0; i < this.m_count; i++) {
         ContactVelocityConstraint vc = this.m_velocityConstraints[i];
         int indexA = vc.indexA;
         int indexB = vc.indexB;
         float mA = vc.invMassA;
         float iA = vc.invIA;
         float mB = vc.invMassB;
         float iB = vc.invIB;
         int pointCount = vc.pointCount;
         Vec2 vA = this.m_velocities[indexA].v;
         float wA = this.m_velocities[indexA].w;
         Vec2 vB = this.m_velocities[indexB].v;
         float wB = this.m_velocities[indexB].w;
         Vec2 normal = vc.normal;
         float tangentx = 1.0F * normal.y;
         float tangenty = -1.0F * normal.x;

         for (int j = 0; j < pointCount; j++) {
            ContactVelocityConstraint.VelocityConstraintPoint vcp = vc.points[j];
            float Px = tangentx * vcp.tangentImpulse + normal.x * vcp.normalImpulse;
            float Py = tangenty * vcp.tangentImpulse + normal.y * vcp.normalImpulse;
            wA -= iA * (vcp.rA.x * Py - vcp.rA.y * Px);
            vA.x -= Px * mA;
            vA.y -= Py * mA;
            wB += iB * (vcp.rB.x * Py - vcp.rB.y * Px);
            vB.x += Px * mB;
            vB.y += Py * mB;
         }

         this.m_velocities[indexA].w = wA;
         this.m_velocities[indexB].w = wB;
      }
   }

   public final void initializeVelocityConstraints() {
      for (int i = 0; i < this.m_count; i++) {
         ContactVelocityConstraint vc = this.m_velocityConstraints[i];
         ContactPositionConstraint pc = this.m_positionConstraints[i];
         float radiusA = pc.radiusA;
         float radiusB = pc.radiusB;
         Manifold manifold = this.m_contacts[vc.contactIndex].getManifold();
         int indexA = vc.indexA;
         int indexB = vc.indexB;
         float mA = vc.invMassA;
         float mB = vc.invMassB;
         float iA = vc.invIA;
         float iB = vc.invIB;
         Vec2 localCenterA = pc.localCenterA;
         Vec2 localCenterB = pc.localCenterB;
         Vec2 cA = this.m_positions[indexA].c;
         float aA = this.m_positions[indexA].a;
         Vec2 vA = this.m_velocities[indexA].v;
         float wA = this.m_velocities[indexA].w;
         Vec2 cB = this.m_positions[indexB].c;
         float aB = this.m_positions[indexB].a;
         Vec2 vB = this.m_velocities[indexB].v;
         float wB = this.m_velocities[indexB].w;

         assert manifold.pointCount > 0;

         Rot xfAq = this.xfA.q;
         Rot xfBq = this.xfB.q;
         xfAq.set(aA);
         xfBq.set(aB);
         this.xfA.p.x = cA.x - (xfAq.c * localCenterA.x - xfAq.s * localCenterA.y);
         this.xfA.p.y = cA.y - (xfAq.s * localCenterA.x + xfAq.c * localCenterA.y);
         this.xfB.p.x = cB.x - (xfBq.c * localCenterB.x - xfBq.s * localCenterB.y);
         this.xfB.p.y = cB.y - (xfBq.s * localCenterB.x + xfBq.c * localCenterB.y);
         this.worldManifold.initialize(manifold, this.xfA, radiusA, this.xfB, radiusB);
         Vec2 vcnormal = vc.normal;
         vcnormal.x = this.worldManifold.normal.x;
         vcnormal.y = this.worldManifold.normal.y;
         int pointCount = vc.pointCount;

         for (int j = 0; j < pointCount; j++) {
            ContactVelocityConstraint.VelocityConstraintPoint vcp = vc.points[j];
            Vec2 wmPj = this.worldManifold.points[j];
            Vec2 vcprA = vcp.rA;
            Vec2 vcprB = vcp.rB;
            vcprA.x = wmPj.x - cA.x;
            vcprA.y = wmPj.y - cA.y;
            vcprB.x = wmPj.x - cB.x;
            vcprB.y = wmPj.y - cB.y;
            float rnA = vcprA.x * vcnormal.y - vcprA.y * vcnormal.x;
            float rnB = vcprB.x * vcnormal.y - vcprB.y * vcnormal.x;
            float kNormal = mA + mB + iA * rnA * rnA + iB * rnB * rnB;
            vcp.normalMass = kNormal > 0.0F ? 1.0F / kNormal : 0.0F;
            float tangentx = 1.0F * vcnormal.y;
            float tangenty = -1.0F * vcnormal.x;
            float rtA = vcprA.x * tangenty - vcprA.y * tangentx;
            float rtB = vcprB.x * tangenty - vcprB.y * tangentx;
            float kTangent = mA + mB + iA * rtA * rtA + iB * rtB * rtB;
            vcp.tangentMass = kTangent > 0.0F ? 1.0F / kTangent : 0.0F;
            vcp.velocityBias = 0.0F;
            float tempx = vB.x + -wB * vcprB.y - vA.x - -wA * vcprA.y;
            float tempy = vB.y + wB * vcprB.x - vA.y - wA * vcprA.x;
            float vRel = vcnormal.x * tempx + vcnormal.y * tempy;
            if (vRel < -Settings.velocityThreshold) {
               vcp.velocityBias = -vc.restitution * vRel;
            }
         }

         if (vc.pointCount == 2) {
            ContactVelocityConstraint.VelocityConstraintPoint vcp1 = vc.points[0];
            ContactVelocityConstraint.VelocityConstraintPoint vcp2 = vc.points[1];
            float rn1A = vcp1.rA.x * vcnormal.y - vcp1.rA.y * vcnormal.x;
            float rn1B = vcp1.rB.x * vcnormal.y - vcp1.rB.y * vcnormal.x;
            float rn2A = vcp2.rA.x * vcnormal.y - vcp2.rA.y * vcnormal.x;
            float rn2B = vcp2.rB.x * vcnormal.y - vcp2.rB.y * vcnormal.x;
            float k11 = mA + mB + iA * rn1A * rn1A + iB * rn1B * rn1B;
            float k22 = mA + mB + iA * rn2A * rn2A + iB * rn2B * rn2B;
            float k12 = mA + mB + iA * rn1A * rn2A + iB * rn1B * rn2B;
            if (k11 * k11 < 100.0F * (k11 * k22 - k12 * k12)) {
               vc.K.ex.x = k11;
               vc.K.ex.y = k12;
               vc.K.ey.x = k12;
               vc.K.ey.y = k22;
               vc.K.invertToOut(vc.normalMass);
            } else {
               vc.pointCount = 1;
            }
         }
      }
   }

   public final void solveVelocityConstraints() {
      for (int i = 0; i < this.m_count; i++) {
         ContactVelocityConstraint vc = this.m_velocityConstraints[i];
         int indexA = vc.indexA;
         int indexB = vc.indexB;
         float mA = vc.invMassA;
         float mB = vc.invMassB;
         float iA = vc.invIA;
         float iB = vc.invIB;
         int pointCount = vc.pointCount;
         Vec2 vA = this.m_velocities[indexA].v;
         float wA = this.m_velocities[indexA].w;
         Vec2 vB = this.m_velocities[indexB].v;
         float wB = this.m_velocities[indexB].w;
         Vec2 normal = vc.normal;
         float normalx = normal.x;
         float normaly = normal.y;
         float tangentx = 1.0F * vc.normal.y;
         float tangenty = -1.0F * vc.normal.x;
         float friction = vc.friction;

         assert pointCount == 1 || pointCount == 2;

         for (int j = 0; j < pointCount; j++) {
            ContactVelocityConstraint.VelocityConstraintPoint vcp = vc.points[j];
            Vec2 a = vcp.rA;
            float dvx = -wB * vcp.rB.y + vB.x - vA.x + wA * a.y;
            float dvy = wB * vcp.rB.x + vB.y - vA.y - wA * a.x;
            float vt = dvx * tangentx + dvy * tangenty - vc.tangentSpeed;
            float lambda = vcp.tangentMass * -vt;
            float maxFriction = friction * vcp.normalImpulse;
            float newImpulse = MathUtils.clamp(vcp.tangentImpulse + lambda, -maxFriction, maxFriction);
            lambda = newImpulse - vcp.tangentImpulse;
            vcp.tangentImpulse = newImpulse;
            float Px = tangentx * lambda;
            float Py = tangenty * lambda;
            vA.x -= Px * mA;
            vA.y -= Py * mA;
            wA -= iA * (vcp.rA.x * Py - vcp.rA.y * Px);
            vB.x += Px * mB;
            vB.y += Py * mB;
            wB += iB * (vcp.rB.x * Py - vcp.rB.y * Px);
         }

         if (vc.pointCount == 1) {
            ContactVelocityConstraint.VelocityConstraintPoint vcp = vc.points[0];
            float dvx = -wB * vcp.rB.y + vB.x - vA.x + wA * vcp.rA.y;
            float dvy = wB * vcp.rB.x + vB.y - vA.y - wA * vcp.rA.x;
            float vn = dvx * normalx + dvy * normaly;
            float lambda = -vcp.normalMass * (vn - vcp.velocityBias);
            float a = vcp.normalImpulse + lambda;
            float newImpulse = a > 0.0F ? a : 0.0F;
            lambda = newImpulse - vcp.normalImpulse;
            vcp.normalImpulse = newImpulse;
            float Px = normalx * lambda;
            float Py = normaly * lambda;
            vA.x -= Px * mA;
            vA.y -= Py * mA;
            wA -= iA * (vcp.rA.x * Py - vcp.rA.y * Px);
            vB.x += Px * mB;
            vB.y += Py * mB;
            wB += iB * (vcp.rB.x * Py - vcp.rB.y * Px);
         } else {
            ContactVelocityConstraint.VelocityConstraintPoint cp1 = vc.points[0];
            ContactVelocityConstraint.VelocityConstraintPoint cp2 = vc.points[1];
            Vec2 cp1rA = cp1.rA;
            Vec2 cp1rB = cp1.rB;
            Vec2 cp2rA = cp2.rA;
            Vec2 cp2rB = cp2.rB;
            float ax = cp1.normalImpulse;
            float ay = cp2.normalImpulse;

            assert ax >= 0.0F && ay >= 0.0F;

            float dv1x = -wB * cp1rB.y + vB.x - vA.x + wA * cp1rA.y;
            float dv1y = wB * cp1rB.x + vB.y - vA.y - wA * cp1rA.x;
            float dv2x = -wB * cp2rB.y + vB.x - vA.x + wA * cp2rA.y;
            float dv2y = wB * cp2rB.x + vB.y - vA.y - wA * cp2rA.x;
            float vn1 = dv1x * normalx + dv1y * normaly;
            float vn2 = dv2x * normalx + dv2y * normaly;
            float bx = vn1 - cp1.velocityBias;
            float by = vn2 - cp2.velocityBias;
            Mat22 R = vc.K;
            bx -= R.ex.x * ax + R.ey.x * ay;
            by -= R.ex.y * ax + R.ey.y * ay;
            Mat22 R1 = vc.normalMass;
            float xx = R1.ex.x * bx + R1.ey.x * by;
            float xy = R1.ex.y * bx + R1.ey.y * by;
            xx *= -1.0F;
            xy *= -1.0F;
            if (xx >= 0.0F && xy >= 0.0F) {
               float dx = xx - ax;
               float dy = xy - ay;
               float P1x = dx * normalx;
               float P1y = dx * normaly;
               float P2x = dy * normalx;
               float P2y = dy * normaly;
               vA.x -= mA * (P1x + P2x);
               vA.y -= mA * (P1y + P2y);
               vB.x += mB * (P1x + P2x);
               vB.y += mB * (P1y + P2y);
               wA -= iA * (cp1rA.x * P1y - cp1rA.y * P1x + (cp2rA.x * P2y - cp2rA.y * P2x));
               wB += iB * (cp1rB.x * P1y - cp1rB.y * P1x + (cp2rB.x * P2y - cp2rB.y * P2x));
               cp1.normalImpulse = xx;
               cp2.normalImpulse = xy;
            } else {
               xx = -cp1.normalMass * bx;
               xy = 0.0F;
               vn1 = 0.0F;
               vn2 = vc.K.ex.y * xx + by;
               if (xx >= 0.0F && vn2 >= 0.0F) {
                  float dx = xx - ax;
                  float dy = xy - ay;
                  float P1x = normalx * dx;
                  float P1y = normaly * dx;
                  float P2x = normalx * dy;
                  float P2y = normaly * dy;
                  vA.x -= mA * (P1x + P2x);
                  vA.y -= mA * (P1y + P2y);
                  vB.x += mB * (P1x + P2x);
                  vB.y += mB * (P1y + P2y);
                  wA -= iA * (cp1rA.x * P1y - cp1rA.y * P1x + (cp2rA.x * P2y - cp2rA.y * P2x));
                  wB += iB * (cp1rB.x * P1y - cp1rB.y * P1x + (cp2rB.x * P2y - cp2rB.y * P2x));
                  cp1.normalImpulse = xx;
                  cp2.normalImpulse = xy;
               } else {
                  xx = 0.0F;
                  xy = -cp2.normalMass * by;
                  vn1 = vc.K.ey.x * xy + bx;
                  vn2 = 0.0F;
                  if (xy >= 0.0F && vn1 >= 0.0F) {
                     float dx = xx - ax;
                     float dy = xy - ay;
                     float P1x = normalx * dx;
                     float P1y = normaly * dx;
                     float P2x = normalx * dy;
                     float P2y = normaly * dy;
                     vA.x -= mA * (P1x + P2x);
                     vA.y -= mA * (P1y + P2y);
                     vB.x += mB * (P1x + P2x);
                     vB.y += mB * (P1y + P2y);
                     wA -= iA * (cp1rA.x * P1y - cp1rA.y * P1x + (cp2rA.x * P2y - cp2rA.y * P2x));
                     wB += iB * (cp1rB.x * P1y - cp1rB.y * P1x + (cp2rB.x * P2y - cp2rB.y * P2x));
                     cp1.normalImpulse = xx;
                     cp2.normalImpulse = xy;
                  } else {
                     xx = 0.0F;
                     xy = 0.0F;
                     if (bx >= 0.0F && by >= 0.0F) {
                        float dx = xx - ax;
                        float dy = xy - ay;
                        float P1x = normalx * dx;
                        float P1y = normaly * dx;
                        float P2x = normalx * dy;
                        float P2y = normaly * dy;
                        vA.x -= mA * (P1x + P2x);
                        vA.y -= mA * (P1y + P2y);
                        vB.x += mB * (P1x + P2x);
                        vB.y += mB * (P1y + P2y);
                        wA -= iA * (cp1rA.x * P1y - cp1rA.y * P1x + (cp2rA.x * P2y - cp2rA.y * P2x));
                        wB += iB * (cp1rB.x * P1y - cp1rB.y * P1x + (cp2rB.x * P2y - cp2rB.y * P2x));
                        cp1.normalImpulse = xx;
                        cp2.normalImpulse = xy;
                     }
                  }
               }
            }
         }

         this.m_velocities[indexA].w = wA;
         this.m_velocities[indexB].w = wB;
      }
   }

   public void storeImpulses() {
      for (int i = 0; i < this.m_count; i++) {
         ContactVelocityConstraint vc = this.m_velocityConstraints[i];
         Manifold manifold = this.m_contacts[vc.contactIndex].getManifold();

         for (int j = 0; j < vc.pointCount; j++) {
            manifold.points[j].normalImpulse = vc.points[j].normalImpulse;
            manifold.points[j].tangentImpulse = vc.points[j].tangentImpulse;
         }
      }
   }

   public final boolean solvePositionConstraints() {
      float minSeparation = 0.0F;

      for (int i = 0; i < this.m_count; i++) {
         ContactPositionConstraint pc = this.m_positionConstraints[i];
         int indexA = pc.indexA;
         int indexB = pc.indexB;
         float mA = pc.invMassA;
         float iA = pc.invIA;
         Vec2 localCenterA = pc.localCenterA;
         float localCenterAx = localCenterA.x;
         float localCenterAy = localCenterA.y;
         float mB = pc.invMassB;
         float iB = pc.invIB;
         Vec2 localCenterB = pc.localCenterB;
         float localCenterBx = localCenterB.x;
         float localCenterBy = localCenterB.y;
         int pointCount = pc.pointCount;
         Vec2 cA = this.m_positions[indexA].c;
         float aA = this.m_positions[indexA].a;
         Vec2 cB = this.m_positions[indexB].c;
         float aB = this.m_positions[indexB].a;

         for (int j = 0; j < pointCount; j++) {
            Rot xfAq = this.xfA.q;
            Rot xfBq = this.xfB.q;
            xfAq.set(aA);
            xfBq.set(aB);
            this.xfA.p.x = cA.x - xfAq.c * localCenterAx + xfAq.s * localCenterAy;
            this.xfA.p.y = cA.y - xfAq.s * localCenterAx - xfAq.c * localCenterAy;
            this.xfB.p.x = cB.x - xfBq.c * localCenterBx + xfBq.s * localCenterBy;
            this.xfB.p.y = cB.y - xfBq.s * localCenterBx - xfBq.c * localCenterBy;
            PositionSolverManifold psm = this.psolver;
            psm.initialize(pc, this.xfA, this.xfB, j);
            Vec2 normal = psm.normal;
            Vec2 point = psm.point;
            float separation = psm.separation;
            float rAx = point.x - cA.x;
            float rAy = point.y - cA.y;
            float rBx = point.x - cB.x;
            float rBy = point.y - cB.y;
            minSeparation = MathUtils.min(minSeparation, separation);
            float C = MathUtils.clamp(Settings.baumgarte * (separation + Settings.linearSlop), -Settings.maxLinearCorrection, 0.0F);
            float rnA = rAx * normal.y - rAy * normal.x;
            float rnB = rBx * normal.y - rBy * normal.x;
            float K = mA + mB + iA * rnA * rnA + iB * rnB * rnB;
            float impulse = K > 0.0F ? -C / K : 0.0F;
            float Px = normal.x * impulse;
            float Py = normal.y * impulse;
            cA.x -= Px * mA;
            cA.y -= Py * mA;
            aA -= iA * (rAx * Py - rAy * Px);
            cB.x += Px * mB;
            cB.y += Py * mB;
            aB += iB * (rBx * Py - rBy * Px);
         }

         this.m_positions[indexA].a = aA;
         this.m_positions[indexB].a = aB;
      }

      return minSeparation >= -3.0F * Settings.linearSlop;
   }

   public boolean solveTOIPositionConstraints(int toiIndexA, int toiIndexB) {
      float minSeparation = 0.0F;

      for (int i = 0; i < this.m_count; i++) {
         ContactPositionConstraint pc = this.m_positionConstraints[i];
         int indexA = pc.indexA;
         int indexB = pc.indexB;
         Vec2 localCenterA = pc.localCenterA;
         Vec2 localCenterB = pc.localCenterB;
         float localCenterAx = localCenterA.x;
         float localCenterAy = localCenterA.y;
         float localCenterBx = localCenterB.x;
         float localCenterBy = localCenterB.y;
         int pointCount = pc.pointCount;
         float mA = 0.0F;
         float iA = 0.0F;
         if (indexA == toiIndexA || indexA == toiIndexB) {
            mA = pc.invMassA;
            iA = pc.invIA;
         }

         float mB = 0.0F;
         float iB = 0.0F;
         if (indexB == toiIndexA || indexB == toiIndexB) {
            mB = pc.invMassB;
            iB = pc.invIB;
         }

         Vec2 cA = this.m_positions[indexA].c;
         float aA = this.m_positions[indexA].a;
         Vec2 cB = this.m_positions[indexB].c;
         float aB = this.m_positions[indexB].a;

         for (int j = 0; j < pointCount; j++) {
            Rot xfAq = this.xfA.q;
            Rot xfBq = this.xfB.q;
            xfAq.set(aA);
            xfBq.set(aB);
            this.xfA.p.x = cA.x - xfAq.c * localCenterAx + xfAq.s * localCenterAy;
            this.xfA.p.y = cA.y - xfAq.s * localCenterAx - xfAq.c * localCenterAy;
            this.xfB.p.x = cB.x - xfBq.c * localCenterBx + xfBq.s * localCenterBy;
            this.xfB.p.y = cB.y - xfBq.s * localCenterBx - xfBq.c * localCenterBy;
            PositionSolverManifold psm = this.psolver;
            psm.initialize(pc, this.xfA, this.xfB, j);
            Vec2 normal = psm.normal;
            Vec2 point = psm.point;
            float separation = psm.separation;
            float rAx = point.x - cA.x;
            float rAy = point.y - cA.y;
            float rBx = point.x - cB.x;
            float rBy = point.y - cB.y;
            minSeparation = MathUtils.min(minSeparation, separation);
            float C = MathUtils.clamp(Settings.toiBaugarte * (separation + Settings.linearSlop), -Settings.maxLinearCorrection, 0.0F);
            float rnA = rAx * normal.y - rAy * normal.x;
            float rnB = rBx * normal.y - rBy * normal.x;
            float K = mA + mB + iA * rnA * rnA + iB * rnB * rnB;
            float impulse = K > 0.0F ? -C / K : 0.0F;
            float Px = normal.x * impulse;
            float Py = normal.y * impulse;
            cA.x -= Px * mA;
            cA.y -= Py * mA;
            aA -= iA * (rAx * Py - rAy * Px);
            cB.x += Px * mB;
            cB.y += Py * mB;
            aB += iB * (rBx * Py - rBy * Px);
         }

         this.m_positions[indexA].a = aA;
         this.m_positions[indexB].a = aB;
      }

      return minSeparation >= -1.5F * Settings.linearSlop;
   }

   public static class ContactSolverDef {
      public TimeStep step;
      public Contact[] contacts;
      public int count;
      public Position[] positions;
      public Velocity[] velocities;
   }
}
