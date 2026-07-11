package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.Mat22;
import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.SolverData;
import net.diebuddies.jbox2d.pooling.IWorldPool;

public class FrictionJoint extends Joint {
   private final Vec2 m_localAnchorA;
   private final Vec2 m_localAnchorB;
   private final Vec2 m_linearImpulse;
   private float m_angularImpulse;
   private float m_maxForce;
   private float m_maxTorque;
   private int m_indexA;
   private int m_indexB;
   private final Vec2 m_rA = new Vec2();
   private final Vec2 m_rB = new Vec2();
   private final Vec2 m_localCenterA = new Vec2();
   private final Vec2 m_localCenterB = new Vec2();
   private float m_invMassA;
   private float m_invMassB;
   private float m_invIA;
   private float m_invIB;
   private final Mat22 m_linearMass = new Mat22();
   private float m_angularMass;

   protected FrictionJoint(IWorldPool argWorldPool, FrictionJointDef def) {
      super(argWorldPool, def);
      this.m_localAnchorA = new Vec2(def.localAnchorA);
      this.m_localAnchorB = new Vec2(def.localAnchorB);
      this.m_linearImpulse = new Vec2();
      this.m_angularImpulse = 0.0F;
      this.m_maxForce = def.maxForce;
      this.m_maxTorque = def.maxTorque;
   }

   public Vec2 getLocalAnchorA() {
      return this.m_localAnchorA;
   }

   public Vec2 getLocalAnchorB() {
      return this.m_localAnchorB;
   }

   @Override
   public void getAnchorA(Vec2 argOut) {
      this.m_bodyA.getWorldPointToOut(this.m_localAnchorA, argOut);
   }

   @Override
   public void getAnchorB(Vec2 argOut) {
      this.m_bodyB.getWorldPointToOut(this.m_localAnchorB, argOut);
   }

   @Override
   public void getReactionForce(float inv_dt, Vec2 argOut) {
      argOut.set(this.m_linearImpulse).mulLocal(inv_dt);
   }

   @Override
   public float getReactionTorque(float inv_dt) {
      return inv_dt * this.m_angularImpulse;
   }

   public void setMaxForce(float force) {
      assert force >= 0.0F;

      this.m_maxForce = force;
   }

   public float getMaxForce() {
      return this.m_maxForce;
   }

   public void setMaxTorque(float torque) {
      assert torque >= 0.0F;

      this.m_maxTorque = torque;
   }

   public float getMaxTorque() {
      return this.m_maxTorque;
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
      float aA = data.positions[this.m_indexA].a;
      Vec2 vA = data.velocities[this.m_indexA].v;
      float wA = data.velocities[this.m_indexA].w;
      float aB = data.positions[this.m_indexB].a;
      Vec2 vB = data.velocities[this.m_indexB].v;
      float wB = data.velocities[this.m_indexB].w;
      Vec2 temp = this.pool.popVec2();
      Rot qA = this.pool.popRot();
      Rot qB = this.pool.popRot();
      qA.set(aA);
      qB.set(aB);
      Rot.mulToOutUnsafe(qA, temp.set(this.m_localAnchorA).subLocal(this.m_localCenterA), this.m_rA);
      Rot.mulToOutUnsafe(qB, temp.set(this.m_localAnchorB).subLocal(this.m_localCenterB), this.m_rB);
      float mA = this.m_invMassA;
      float mB = this.m_invMassB;
      float iA = this.m_invIA;
      float iB = this.m_invIB;
      Mat22 K = this.pool.popMat22();
      K.ex.x = mA + mB + iA * this.m_rA.y * this.m_rA.y + iB * this.m_rB.y * this.m_rB.y;
      K.ex.y = -iA * this.m_rA.x * this.m_rA.y - iB * this.m_rB.x * this.m_rB.y;
      K.ey.x = K.ex.y;
      K.ey.y = mA + mB + iA * this.m_rA.x * this.m_rA.x + iB * this.m_rB.x * this.m_rB.x;
      K.invertToOut(this.m_linearMass);
      this.m_angularMass = iA + iB;
      if (this.m_angularMass > 0.0F) {
         this.m_angularMass = 1.0F / this.m_angularMass;
      }

      if (data.step.warmStarting) {
         this.m_linearImpulse.mulLocal(data.step.dtRatio);
         this.m_angularImpulse = this.m_angularImpulse * data.step.dtRatio;
         Vec2 P = this.pool.popVec2();
         P.set(this.m_linearImpulse);
         temp.set(P).mulLocal(mA);
         vA.subLocal(temp);
         wA -= iA * (Vec2.cross(this.m_rA, P) + this.m_angularImpulse);
         temp.set(P).mulLocal(mB);
         vB.addLocal(temp);
         wB += iB * (Vec2.cross(this.m_rB, P) + this.m_angularImpulse);
         this.pool.pushVec2(1);
      } else {
         this.m_linearImpulse.setZero();
         this.m_angularImpulse = 0.0F;
      }

      assert data.velocities[this.m_indexA].w == wA || data.velocities[this.m_indexA].w != wA;

      data.velocities[this.m_indexA].w = wA;
      data.velocities[this.m_indexB].w = wB;
      this.pool.pushRot(2);
      this.pool.pushVec2(1);
      this.pool.pushMat22(1);
   }

   @Override
   public void solveVelocityConstraints(SolverData data) {
      Vec2 vA = data.velocities[this.m_indexA].v;
      float wA = data.velocities[this.m_indexA].w;
      Vec2 vB = data.velocities[this.m_indexB].v;
      float wB = data.velocities[this.m_indexB].w;
      float mA = this.m_invMassA;
      float mB = this.m_invMassB;
      float iA = this.m_invIA;
      float iB = this.m_invIB;
      float h = data.step.dt;
      float Cdot = wB - wA;
      float impulse = -this.m_angularMass * Cdot;
      float oldImpulse = this.m_angularImpulse;
      float maxImpulse = h * this.m_maxTorque;
      this.m_angularImpulse = MathUtils.clamp(this.m_angularImpulse + impulse, -maxImpulse, maxImpulse);
      impulse = this.m_angularImpulse - oldImpulse;
      wA -= iA * impulse;
      wB += iB * impulse;
      Vec2 Cdotx = this.pool.popVec2();
      Vec2 temp = this.pool.popVec2();
      Vec2.crossToOutUnsafe(wA, this.m_rA, temp);
      Vec2.crossToOutUnsafe(wB, this.m_rB, Cdotx);
      Cdotx.addLocal(vB).subLocal(vA).subLocal(temp);
      Vec2 impulsex = this.pool.popVec2();
      Mat22.mulToOutUnsafe(this.m_linearMass, Cdotx, impulsex);
      impulsex.negateLocal();
      Vec2 oldImpulsex = this.pool.popVec2();
      oldImpulsex.set(this.m_linearImpulse);
      this.m_linearImpulse.addLocal(impulsex);
      float maxImpulsex = h * this.m_maxForce;
      if (this.m_linearImpulse.lengthSquared() > maxImpulsex * maxImpulsex) {
         this.m_linearImpulse.normalize();
         this.m_linearImpulse.mulLocal(maxImpulsex);
      }

      impulsex.set(this.m_linearImpulse).subLocal(oldImpulsex);
      temp.set(impulsex).mulLocal(mA);
      vA.subLocal(temp);
      wA -= iA * Vec2.cross(this.m_rA, impulsex);
      temp.set(impulsex).mulLocal(mB);
      vB.addLocal(temp);
      wB += iB * Vec2.cross(this.m_rB, impulsex);

      assert data.velocities[this.m_indexA].w == wA || data.velocities[this.m_indexA].w != wA;

      data.velocities[this.m_indexA].w = wA;
      data.velocities[this.m_indexB].w = wB;
      this.pool.pushVec2(4);
   }

   @Override
   public boolean solvePositionConstraints(SolverData data) {
      return true;
   }
}
