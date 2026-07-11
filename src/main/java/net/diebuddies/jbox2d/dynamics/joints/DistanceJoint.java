package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.SolverData;
import net.diebuddies.jbox2d.pooling.IWorldPool;

public class DistanceJoint extends Joint {
   private float m_frequencyHz;
   private float m_dampingRatio;
   private float m_bias;
   private final Vec2 m_localAnchorA;
   private final Vec2 m_localAnchorB;
   private float m_gamma;
   private float m_impulse;
   private float m_length;
   private int m_indexA;
   private int m_indexB;
   private final Vec2 m_u = new Vec2();
   private final Vec2 m_rA = new Vec2();
   private final Vec2 m_rB = new Vec2();
   private final Vec2 m_localCenterA = new Vec2();
   private final Vec2 m_localCenterB = new Vec2();
   private float m_invMassA;
   private float m_invMassB;
   private float m_invIA;
   private float m_invIB;
   private float m_mass;

   protected DistanceJoint(IWorldPool argWorld, DistanceJointDef def) {
      super(argWorld, def);
      this.m_localAnchorA = def.localAnchorA.clone();
      this.m_localAnchorB = def.localAnchorB.clone();
      this.m_length = def.length;
      this.m_impulse = 0.0F;
      this.m_frequencyHz = def.frequencyHz;
      this.m_dampingRatio = def.dampingRatio;
      this.m_gamma = 0.0F;
      this.m_bias = 0.0F;
   }

   public void setFrequency(float hz) {
      this.m_frequencyHz = hz;
   }

   public float getFrequency() {
      return this.m_frequencyHz;
   }

   public float getLength() {
      return this.m_length;
   }

   public void setLength(float argLength) {
      this.m_length = argLength;
   }

   public void setDampingRatio(float damp) {
      this.m_dampingRatio = damp;
   }

   public float getDampingRatio() {
      return this.m_dampingRatio;
   }

   @Override
   public void getAnchorA(Vec2 argOut) {
      this.m_bodyA.getWorldPointToOut(this.m_localAnchorA, argOut);
   }

   @Override
   public void getAnchorB(Vec2 argOut) {
      this.m_bodyB.getWorldPointToOut(this.m_localAnchorB, argOut);
   }

   public Vec2 getLocalAnchorA() {
      return this.m_localAnchorA;
   }

   public Vec2 getLocalAnchorB() {
      return this.m_localAnchorB;
   }

   @Override
   public void getReactionForce(float inv_dt, Vec2 argOut) {
      argOut.x = this.m_impulse * this.m_u.x * inv_dt;
      argOut.y = this.m_impulse * this.m_u.y * inv_dt;
   }

   @Override
   public float getReactionTorque(float inv_dt) {
      return 0.0F;
   }

   @Override
   public void initVelocityConstraints(SolverData data) {
      this.m_indexA = this.m_bodyA.m_islandIndex;
      this.m_indexB = this.m_bodyB.m_islandIndex;
      this.m_localCenterA.set(this.m_bodyA.m_sweep.localCenter);
      this.m_localCenterB.set(this.m_bodyB.m_sweep.localCenter);
      this.m_invMassA = this.m_bodyA.m_invMass;
      this.m_invMassB = this.m_bodyB.m_invMass;
      this.m_invIA = this.m_bodyA.m_invI;
      this.m_invIB = this.m_bodyB.m_invI;
      Vec2 cA = data.positions[this.m_indexA].c;
      float aA = data.positions[this.m_indexA].a;
      Vec2 vA = data.velocities[this.m_indexA].v;
      float wA = data.velocities[this.m_indexA].w;
      Vec2 cB = data.positions[this.m_indexB].c;
      float aB = data.positions[this.m_indexB].a;
      Vec2 vB = data.velocities[this.m_indexB].v;
      float wB = data.velocities[this.m_indexB].w;
      Rot qA = this.pool.popRot();
      Rot qB = this.pool.popRot();
      qA.set(aA);
      qB.set(aB);
      Rot.mulToOutUnsafe(qA, this.m_u.set(this.m_localAnchorA).subLocal(this.m_localCenterA), this.m_rA);
      Rot.mulToOutUnsafe(qB, this.m_u.set(this.m_localAnchorB).subLocal(this.m_localCenterB), this.m_rB);
      this.m_u.set(cB).addLocal(this.m_rB).subLocal(cA).subLocal(this.m_rA);
      this.pool.pushRot(2);
      float length = this.m_u.length();
      if (length > Settings.linearSlop) {
         this.m_u.x *= 1.0F / length;
         this.m_u.y *= 1.0F / length;
      } else {
         this.m_u.set(0.0F, 0.0F);
      }

      float crAu = Vec2.cross(this.m_rA, this.m_u);
      float crBu = Vec2.cross(this.m_rB, this.m_u);
      float invMass = this.m_invMassA + this.m_invIA * crAu * crAu + this.m_invMassB + this.m_invIB * crBu * crBu;
      this.m_mass = invMass != 0.0F ? 1.0F / invMass : 0.0F;
      if (this.m_frequencyHz > 0.0F) {
         float C = length - this.m_length;
         float omega = (float) (Math.PI * 2) * this.m_frequencyHz;
         float d = 2.0F * this.m_mass * this.m_dampingRatio * omega;
         float k = this.m_mass * omega * omega;
         float h = data.step.dt;
         this.m_gamma = h * (d + h * k);
         this.m_gamma = this.m_gamma != 0.0F ? 1.0F / this.m_gamma : 0.0F;
         this.m_bias = C * h * k * this.m_gamma;
         invMass += this.m_gamma;
         this.m_mass = invMass != 0.0F ? 1.0F / invMass : 0.0F;
      } else {
         this.m_gamma = 0.0F;
         this.m_bias = 0.0F;
      }

      if (data.step.warmStarting) {
         this.m_impulse = this.m_impulse * data.step.dtRatio;
         Vec2 P = this.pool.popVec2();
         P.set(this.m_u).mulLocal(this.m_impulse);
         vA.x = vA.x - this.m_invMassA * P.x;
         vA.y = vA.y - this.m_invMassA * P.y;
         wA -= this.m_invIA * Vec2.cross(this.m_rA, P);
         vB.x = vB.x + this.m_invMassB * P.x;
         vB.y = vB.y + this.m_invMassB * P.y;
         wB += this.m_invIB * Vec2.cross(this.m_rB, P);
         this.pool.pushVec2(1);
      } else {
         this.m_impulse = 0.0F;
      }

      data.velocities[this.m_indexA].w = wA;
      data.velocities[this.m_indexB].w = wB;
   }

   @Override
   public void solveVelocityConstraints(SolverData data) {
      Vec2 vA = data.velocities[this.m_indexA].v;
      float wA = data.velocities[this.m_indexA].w;
      Vec2 vB = data.velocities[this.m_indexB].v;
      float wB = data.velocities[this.m_indexB].w;
      Vec2 vpA = this.pool.popVec2();
      Vec2 vpB = this.pool.popVec2();
      Vec2.crossToOutUnsafe(wA, this.m_rA, vpA);
      vpA.addLocal(vA);
      Vec2.crossToOutUnsafe(wB, this.m_rB, vpB);
      vpB.addLocal(vB);
      float Cdot = Vec2.dot(this.m_u, vpB.subLocal(vpA));
      float impulse = -this.m_mass * (Cdot + this.m_bias + this.m_gamma * this.m_impulse);
      this.m_impulse += impulse;
      float Px = impulse * this.m_u.x;
      float Py = impulse * this.m_u.y;
      vA.x = vA.x - this.m_invMassA * Px;
      vA.y = vA.y - this.m_invMassA * Py;
      wA -= this.m_invIA * (this.m_rA.x * Py - this.m_rA.y * Px);
      vB.x = vB.x + this.m_invMassB * Px;
      vB.y = vB.y + this.m_invMassB * Py;
      wB += this.m_invIB * (this.m_rB.x * Py - this.m_rB.y * Px);
      data.velocities[this.m_indexA].w = wA;
      data.velocities[this.m_indexB].w = wB;
      this.pool.pushVec2(2);
   }

   @Override
   public boolean solvePositionConstraints(SolverData data) {
      if (this.m_frequencyHz > 0.0F) {
         return true;
      } else {
         Rot qA = this.pool.popRot();
         Rot qB = this.pool.popRot();
         Vec2 rA = this.pool.popVec2();
         Vec2 rB = this.pool.popVec2();
         Vec2 u = this.pool.popVec2();
         Vec2 cA = data.positions[this.m_indexA].c;
         float aA = data.positions[this.m_indexA].a;
         Vec2 cB = data.positions[this.m_indexB].c;
         float aB = data.positions[this.m_indexB].a;
         qA.set(aA);
         qB.set(aB);
         Rot.mulToOutUnsafe(qA, u.set(this.m_localAnchorA).subLocal(this.m_localCenterA), rA);
         Rot.mulToOutUnsafe(qB, u.set(this.m_localAnchorB).subLocal(this.m_localCenterB), rB);
         u.set(cB).addLocal(rB).subLocal(cA).subLocal(rA);
         float length = u.normalize();
         float C = length - this.m_length;
         C = MathUtils.clamp(C, -Settings.maxLinearCorrection, Settings.maxLinearCorrection);
         float impulse = -this.m_mass * C;
         float Px = impulse * u.x;
         float Py = impulse * u.y;
         cA.x = cA.x - this.m_invMassA * Px;
         cA.y = cA.y - this.m_invMassA * Py;
         aA -= this.m_invIA * (rA.x * Py - rA.y * Px);
         cB.x = cB.x + this.m_invMassB * Px;
         cB.y = cB.y + this.m_invMassB * Py;
         aB += this.m_invIB * (rB.x * Py - rB.y * Px);
         data.positions[this.m_indexA].a = aA;
         data.positions[this.m_indexB].a = aB;
         this.pool.pushVec2(3);
         this.pool.pushRot(2);
         return MathUtils.abs(C) < Settings.linearSlop;
      }
   }
}
