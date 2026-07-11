package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.Mat22;
import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.SolverData;
import net.diebuddies.jbox2d.pooling.IWorldPool;

public class MotorJoint extends Joint {
   private final Vec2 m_linearOffset = new Vec2();
   private float m_angularOffset;
   private final Vec2 m_linearImpulse = new Vec2();
   private float m_angularImpulse;
   private float m_maxForce;
   private float m_maxTorque;
   private float m_correctionFactor;
   private int m_indexA;
   private int m_indexB;
   private final Vec2 m_rA = new Vec2();
   private final Vec2 m_rB = new Vec2();
   private final Vec2 m_localCenterA = new Vec2();
   private final Vec2 m_localCenterB = new Vec2();
   private final Vec2 m_linearError = new Vec2();
   private float m_angularError;
   private float m_invMassA;
   private float m_invMassB;
   private float m_invIA;
   private float m_invIB;
   private final Mat22 m_linearMass = new Mat22();
   private float m_angularMass;

   public MotorJoint(IWorldPool pool, MotorJointDef def) {
      super(pool, def);
      this.m_linearOffset.set(def.linearOffset);
      this.m_angularOffset = def.angularOffset;
      this.m_angularImpulse = 0.0F;
      this.m_maxForce = def.maxForce;
      this.m_maxTorque = def.maxTorque;
      this.m_correctionFactor = def.correctionFactor;
   }

   @Override
   public void getAnchorA(Vec2 out) {
      out.set(this.m_bodyA.getPosition());
   }

   @Override
   public void getAnchorB(Vec2 out) {
      out.set(this.m_bodyB.getPosition());
   }

   @Override
   public void getReactionForce(float inv_dt, Vec2 out) {
      out.set(this.m_linearImpulse).mulLocal(inv_dt);
   }

   @Override
   public float getReactionTorque(float inv_dt) {
      return this.m_angularImpulse * inv_dt;
   }

   public float getCorrectionFactor() {
      return this.m_correctionFactor;
   }

   public void setCorrectionFactor(float correctionFactor) {
      this.m_correctionFactor = correctionFactor;
   }

   public void setLinearOffset(Vec2 linearOffset) {
      if (linearOffset.x != this.m_linearOffset.x || linearOffset.y != this.m_linearOffset.y) {
         this.m_bodyA.setAwake(true);
         this.m_bodyB.setAwake(true);
         this.m_linearOffset.set(linearOffset);
      }
   }

   public void getLinearOffset(Vec2 out) {
      out.set(this.m_linearOffset);
   }

   public Vec2 getLinearOffset() {
      return this.m_linearOffset;
   }

   public void setAngularOffset(float angularOffset) {
      if (angularOffset != this.m_angularOffset) {
         this.m_bodyA.setAwake(true);
         this.m_bodyB.setAwake(true);
         this.m_angularOffset = angularOffset;
      }
   }

   public float getAngularOffset() {
      return this.m_angularOffset;
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
      Vec2 temp = this.pool.popVec2();
      Mat22 K = this.pool.popMat22();
      qA.set(aA);
      qB.set(aB);
      this.m_rA.x = qA.c * -this.m_localCenterA.x - qA.s * -this.m_localCenterA.y;
      this.m_rA.y = qA.s * -this.m_localCenterA.x + qA.c * -this.m_localCenterA.y;
      this.m_rB.x = qB.c * -this.m_localCenterB.x - qB.s * -this.m_localCenterB.y;
      this.m_rB.y = qB.s * -this.m_localCenterB.x + qB.c * -this.m_localCenterB.y;
      float mA = this.m_invMassA;
      float mB = this.m_invMassB;
      float iA = this.m_invIA;
      float iB = this.m_invIB;
      K.ex.x = mA + mB + iA * this.m_rA.y * this.m_rA.y + iB * this.m_rB.y * this.m_rB.y;
      K.ex.y = -iA * this.m_rA.x * this.m_rA.y - iB * this.m_rB.x * this.m_rB.y;
      K.ey.x = K.ex.y;
      K.ey.y = mA + mB + iA * this.m_rA.x * this.m_rA.x + iB * this.m_rB.x * this.m_rB.x;
      K.invertToOut(this.m_linearMass);
      this.m_angularMass = iA + iB;
      if (this.m_angularMass > 0.0F) {
         this.m_angularMass = 1.0F / this.m_angularMass;
      }

      Rot.mulToOutUnsafe(qA, this.m_linearOffset, temp);
      this.m_linearError.x = cB.x + this.m_rB.x - cA.x - this.m_rA.x - temp.x;
      this.m_linearError.y = cB.y + this.m_rB.y - cA.y - this.m_rA.y - temp.y;
      this.m_angularError = aB - aA - this.m_angularOffset;
      if (data.step.warmStarting) {
         this.m_linearImpulse.x = this.m_linearImpulse.x * data.step.dtRatio;
         this.m_linearImpulse.y = this.m_linearImpulse.y * data.step.dtRatio;
         this.m_angularImpulse = this.m_angularImpulse * data.step.dtRatio;
         Vec2 P = this.m_linearImpulse;
         vA.x = vA.x - mA * P.x;
         vA.y = vA.y - mA * P.y;
         wA -= iA * (this.m_rA.x * P.y - this.m_rA.y * P.x + this.m_angularImpulse);
         vB.x = vB.x + mB * P.x;
         vB.y = vB.y + mB * P.y;
         wB += iB * (this.m_rB.x * P.y - this.m_rB.y * P.x + this.m_angularImpulse);
      } else {
         this.m_linearImpulse.setZero();
         this.m_angularImpulse = 0.0F;
      }

      this.pool.pushVec2(1);
      this.pool.pushMat22(1);
      this.pool.pushRot(2);
      data.velocities[this.m_indexA].w = wA;
      data.velocities[this.m_indexB].w = wB;
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
      float inv_h = data.step.inv_dt;
      Vec2 temp = this.pool.popVec2();
      float Cdot = wB - wA + inv_h * this.m_correctionFactor * this.m_angularError;
      float impulse = -this.m_angularMass * Cdot;
      float oldImpulse = this.m_angularImpulse;
      float maxImpulse = h * this.m_maxTorque;
      this.m_angularImpulse = MathUtils.clamp(this.m_angularImpulse + impulse, -maxImpulse, maxImpulse);
      impulse = this.m_angularImpulse - oldImpulse;
      wA -= iA * impulse;
      wB += iB * impulse;
      Vec2 Cdotx = this.pool.popVec2();
      Cdotx.x = vB.x + -wB * this.m_rB.y - vA.x - -wA * this.m_rA.y + inv_h * this.m_correctionFactor * this.m_linearError.x;
      Cdotx.y = vB.y + wB * this.m_rB.x - vA.y - wA * this.m_rA.x + inv_h * this.m_correctionFactor * this.m_linearError.y;
      Mat22.mulToOutUnsafe(this.m_linearMass, Cdotx, temp);
      temp.negateLocal();
      Vec2 oldImpulsex = this.pool.popVec2();
      oldImpulsex.set(this.m_linearImpulse);
      this.m_linearImpulse.addLocal(temp);
      maxImpulse = h * this.m_maxForce;
      if (this.m_linearImpulse.lengthSquared() > maxImpulse * maxImpulse) {
         this.m_linearImpulse.normalize();
         this.m_linearImpulse.mulLocal(maxImpulse);
      }

      temp.x = this.m_linearImpulse.x - oldImpulsex.x;
      temp.y = this.m_linearImpulse.y - oldImpulsex.y;
      vA.x = vA.x - mA * temp.x;
      vA.y = vA.y - mA * temp.y;
      wA -= iA * (this.m_rA.x * temp.y - this.m_rA.y * temp.x);
      vB.x = vB.x + mB * temp.x;
      vB.y = vB.y + mB * temp.y;
      wB += iB * (this.m_rB.x * temp.y - this.m_rB.y * temp.x);
      this.pool.pushVec2(3);
      data.velocities[this.m_indexA].w = wA;
      data.velocities[this.m_indexB].w = wB;
   }

   @Override
   public boolean solvePositionConstraints(SolverData data) {
      return true;
   }
}
