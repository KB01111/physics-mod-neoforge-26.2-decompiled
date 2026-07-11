package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Body;
import net.diebuddies.jbox2d.dynamics.SolverData;
import net.diebuddies.jbox2d.pooling.IWorldPool;

public class WheelJoint extends Joint {
   private float m_frequencyHz;
   private float m_dampingRatio;
   private final Vec2 m_localAnchorA = new Vec2();
   private final Vec2 m_localAnchorB = new Vec2();
   private final Vec2 m_localXAxisA = new Vec2();
   private final Vec2 m_localYAxisA = new Vec2();
   private float m_impulse;
   private float m_motorImpulse;
   private float m_springImpulse;
   private float m_maxMotorTorque;
   private float m_motorSpeed;
   private boolean m_enableMotor;
   private int m_indexA;
   private int m_indexB;
   private final Vec2 m_localCenterA = new Vec2();
   private final Vec2 m_localCenterB = new Vec2();
   private float m_invMassA;
   private float m_invMassB;
   private float m_invIA;
   private float m_invIB;
   private final Vec2 m_ax = new Vec2();
   private final Vec2 m_ay = new Vec2();
   private float m_sAx;
   private float m_sBx;
   private float m_sAy;
   private float m_sBy;
   private float m_mass;
   private float m_motorMass;
   private float m_springMass;
   private float m_bias;
   private float m_gamma;
   private final Vec2 rA = new Vec2();
   private final Vec2 rB = new Vec2();
   private final Vec2 d = new Vec2();

   protected WheelJoint(IWorldPool argPool, WheelJointDef def) {
      super(argPool, def);
      this.m_localAnchorA.set(def.localAnchorA);
      this.m_localAnchorB.set(def.localAnchorB);
      this.m_localXAxisA.set(def.localAxisA);
      Vec2.crossToOutUnsafe(1.0F, this.m_localXAxisA, this.m_localYAxisA);
      this.m_motorMass = 0.0F;
      this.m_motorImpulse = 0.0F;
      this.m_maxMotorTorque = def.maxMotorTorque;
      this.m_motorSpeed = def.motorSpeed;
      this.m_enableMotor = def.enableMotor;
      this.m_frequencyHz = def.frequencyHz;
      this.m_dampingRatio = def.dampingRatio;
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
      Vec2 temp = this.pool.popVec2();
      temp.set(this.m_ay).mulLocal(this.m_impulse);
      argOut.set(this.m_ax).mulLocal(this.m_springImpulse).addLocal(temp).mulLocal(inv_dt);
      this.pool.pushVec2(1);
   }

   @Override
   public float getReactionTorque(float inv_dt) {
      return inv_dt * this.m_motorImpulse;
   }

   public float getJointTranslation() {
      Body b1 = this.m_bodyA;
      Body b2 = this.m_bodyB;
      Vec2 p1 = this.pool.popVec2();
      Vec2 p2 = this.pool.popVec2();
      Vec2 axis = this.pool.popVec2();
      b1.getWorldPointToOut(this.m_localAnchorA, p1);
      b2.getWorldPointToOut(this.m_localAnchorA, p2);
      p2.subLocal(p1);
      b1.getWorldVectorToOut(this.m_localXAxisA, axis);
      float translation = Vec2.dot(p2, axis);
      this.pool.pushVec2(3);
      return translation;
   }

   public Vec2 getLocalAxisA() {
      return this.m_localXAxisA;
   }

   public float getJointSpeed() {
      return this.m_bodyA.m_angularVelocity - this.m_bodyB.m_angularVelocity;
   }

   public boolean isMotorEnabled() {
      return this.m_enableMotor;
   }

   public void enableMotor(boolean flag) {
      this.m_bodyA.setAwake(true);
      this.m_bodyB.setAwake(true);
      this.m_enableMotor = flag;
   }

   public void setMotorSpeed(float speed) {
      this.m_bodyA.setAwake(true);
      this.m_bodyB.setAwake(true);
      this.m_motorSpeed = speed;
   }

   public float getMotorSpeed() {
      return this.m_motorSpeed;
   }

   public float getMaxMotorTorque() {
      return this.m_maxMotorTorque;
   }

   public void setMaxMotorTorque(float torque) {
      this.m_bodyA.setAwake(true);
      this.m_bodyB.setAwake(true);
      this.m_maxMotorTorque = torque;
   }

   public float getMotorTorque(float inv_dt) {
      return this.m_motorImpulse * inv_dt;
   }

   public void setSpringFrequencyHz(float hz) {
      this.m_frequencyHz = hz;
   }

   public float getSpringFrequencyHz() {
      return this.m_frequencyHz;
   }

   public void setSpringDampingRatio(float ratio) {
      this.m_dampingRatio = ratio;
   }

   public float getSpringDampingRatio() {
      return this.m_dampingRatio;
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
      float mA = this.m_invMassA;
      float mB = this.m_invMassB;
      float iA = this.m_invIA;
      float iB = this.m_invIB;
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
      qA.set(aA);
      qB.set(aB);
      Rot.mulToOutUnsafe(qA, temp.set(this.m_localAnchorA).subLocal(this.m_localCenterA), this.rA);
      Rot.mulToOutUnsafe(qB, temp.set(this.m_localAnchorB).subLocal(this.m_localCenterB), this.rB);
      this.d.set(cB).addLocal(this.rB).subLocal(cA).subLocal(this.rA);
      Rot.mulToOut(qA, this.m_localYAxisA, this.m_ay);
      this.m_sAy = Vec2.cross(temp.set(this.d).addLocal(this.rA), this.m_ay);
      this.m_sBy = Vec2.cross(this.rB, this.m_ay);
      this.m_mass = mA + mB + iA * this.m_sAy * this.m_sAy + iB * this.m_sBy * this.m_sBy;
      if (this.m_mass > 0.0F) {
         this.m_mass = 1.0F / this.m_mass;
      }

      this.m_springMass = 0.0F;
      this.m_bias = 0.0F;
      this.m_gamma = 0.0F;
      if (this.m_frequencyHz > 0.0F) {
         Rot.mulToOut(qA, this.m_localXAxisA, this.m_ax);
         this.m_sAx = Vec2.cross(temp.set(this.d).addLocal(this.rA), this.m_ax);
         this.m_sBx = Vec2.cross(this.rB, this.m_ax);
         float invMass = mA + mB + iA * this.m_sAx * this.m_sAx + iB * this.m_sBx * this.m_sBx;
         if (invMass > 0.0F) {
            this.m_springMass = 1.0F / invMass;
            float C = Vec2.dot(this.d, this.m_ax);
            float omega = (float) (Math.PI * 2) * this.m_frequencyHz;
            float d = 2.0F * this.m_springMass * this.m_dampingRatio * omega;
            float k = this.m_springMass * omega * omega;
            float h = data.step.dt;
            this.m_gamma = h * (d + h * k);
            if (this.m_gamma > 0.0F) {
               this.m_gamma = 1.0F / this.m_gamma;
            }

            this.m_bias = C * h * k * this.m_gamma;
            this.m_springMass = invMass + this.m_gamma;
            if (this.m_springMass > 0.0F) {
               this.m_springMass = 1.0F / this.m_springMass;
            }
         }
      } else {
         this.m_springImpulse = 0.0F;
      }

      if (this.m_enableMotor) {
         this.m_motorMass = iA + iB;
         if (this.m_motorMass > 0.0F) {
            this.m_motorMass = 1.0F / this.m_motorMass;
         }
      } else {
         this.m_motorMass = 0.0F;
         this.m_motorImpulse = 0.0F;
      }

      if (data.step.warmStarting) {
         Vec2 P = this.pool.popVec2();
         this.m_impulse = this.m_impulse * data.step.dtRatio;
         this.m_springImpulse = this.m_springImpulse * data.step.dtRatio;
         this.m_motorImpulse = this.m_motorImpulse * data.step.dtRatio;
         P.x = this.m_impulse * this.m_ay.x + this.m_springImpulse * this.m_ax.x;
         P.y = this.m_impulse * this.m_ay.y + this.m_springImpulse * this.m_ax.y;
         float LA = this.m_impulse * this.m_sAy + this.m_springImpulse * this.m_sAx + this.m_motorImpulse;
         float LB = this.m_impulse * this.m_sBy + this.m_springImpulse * this.m_sBx + this.m_motorImpulse;
         vA.x = vA.x - this.m_invMassA * P.x;
         vA.y = vA.y - this.m_invMassA * P.y;
         wA -= this.m_invIA * LA;
         vB.x = vB.x + this.m_invMassB * P.x;
         vB.y = vB.y + this.m_invMassB * P.y;
         wB += this.m_invIB * LB;
         this.pool.pushVec2(1);
      } else {
         this.m_impulse = 0.0F;
         this.m_springImpulse = 0.0F;
         this.m_motorImpulse = 0.0F;
      }

      this.pool.pushRot(2);
      this.pool.pushVec2(1);
      data.velocities[this.m_indexA].w = wA;
      data.velocities[this.m_indexB].w = wB;
   }

   @Override
   public void solveVelocityConstraints(SolverData data) {
      float mA = this.m_invMassA;
      float mB = this.m_invMassB;
      float iA = this.m_invIA;
      float iB = this.m_invIB;
      Vec2 vA = data.velocities[this.m_indexA].v;
      float wA = data.velocities[this.m_indexA].w;
      Vec2 vB = data.velocities[this.m_indexB].v;
      float wB = data.velocities[this.m_indexB].w;
      Vec2 temp = this.pool.popVec2();
      Vec2 P = this.pool.popVec2();
      float Cdot = Vec2.dot(this.m_ax, temp.set(vB).subLocal(vA)) + this.m_sBx * wB - this.m_sAx * wA;
      float impulse = -this.m_springMass * (Cdot + this.m_bias + this.m_gamma * this.m_springImpulse);
      this.m_springImpulse += impulse;
      P.x = impulse * this.m_ax.x;
      P.y = impulse * this.m_ax.y;
      float LA = impulse * this.m_sAx;
      float LB = impulse * this.m_sBx;
      vA.x = vA.x - mA * P.x;
      vA.y = vA.y - mA * P.y;
      wA -= iA * LA;
      vB.x = vB.x + mB * P.x;
      vB.y = vB.y + mB * P.y;
      wB += iB * LB;
      Cdot = wB - wA - this.m_motorSpeed;
      impulse = -this.m_motorMass * Cdot;
      LA = this.m_motorImpulse;
      LB = data.step.dt * this.m_maxMotorTorque;
      this.m_motorImpulse = MathUtils.clamp(this.m_motorImpulse + impulse, -LB, LB);
      impulse = this.m_motorImpulse - LA;
      wA -= iA * impulse;
      wB += iB * impulse;
      Cdot = Vec2.dot(this.m_ay, temp.set(vB).subLocal(vA)) + this.m_sBy * wB - this.m_sAy * wA;
      impulse = -this.m_mass * Cdot;
      this.m_impulse += impulse;
      P.x = impulse * this.m_ay.x;
      P.y = impulse * this.m_ay.y;
      LA = impulse * this.m_sAy;
      LB = impulse * this.m_sBy;
      vA.x = vA.x - mA * P.x;
      vA.y = vA.y - mA * P.y;
      wA -= iA * LA;
      vB.x = vB.x + mB * P.x;
      vB.y = vB.y + mB * P.y;
      wB += iB * LB;
      this.pool.pushVec2(2);
      data.velocities[this.m_indexA].w = wA;
      data.velocities[this.m_indexB].w = wB;
   }

   @Override
   public boolean solvePositionConstraints(SolverData data) {
      Vec2 cA = data.positions[this.m_indexA].c;
      float aA = data.positions[this.m_indexA].a;
      Vec2 cB = data.positions[this.m_indexB].c;
      float aB = data.positions[this.m_indexB].a;
      Rot qA = this.pool.popRot();
      Rot qB = this.pool.popRot();
      Vec2 temp = this.pool.popVec2();
      qA.set(aA);
      qB.set(aB);
      Rot.mulToOut(qA, temp.set(this.m_localAnchorA).subLocal(this.m_localCenterA), this.rA);
      Rot.mulToOut(qB, temp.set(this.m_localAnchorB).subLocal(this.m_localCenterB), this.rB);
      this.d.set(cB).subLocal(cA).addLocal(this.rB).subLocal(this.rA);
      Vec2 ay = this.pool.popVec2();
      Rot.mulToOut(qA, this.m_localYAxisA, ay);
      float sAy = Vec2.cross(temp.set(this.d).addLocal(this.rA), ay);
      float sBy = Vec2.cross(this.rB, ay);
      float C = Vec2.dot(this.d, ay);
      float k = this.m_invMassA + this.m_invMassB + this.m_invIA * this.m_sAy * this.m_sAy + this.m_invIB * this.m_sBy * this.m_sBy;
      float impulse;
      if (k != 0.0F) {
         impulse = -C / k;
      } else {
         impulse = 0.0F;
      }

      Vec2 P = this.pool.popVec2();
      P.x = impulse * ay.x;
      P.y = impulse * ay.y;
      float LA = impulse * sAy;
      float LB = impulse * sBy;
      cA.x = cA.x - this.m_invMassA * P.x;
      cA.y = cA.y - this.m_invMassA * P.y;
      aA -= this.m_invIA * LA;
      cB.x = cB.x + this.m_invMassB * P.x;
      cB.y = cB.y + this.m_invMassB * P.y;
      aB += this.m_invIB * LB;
      this.pool.pushVec2(3);
      this.pool.pushRot(2);
      data.positions[this.m_indexA].a = aA;
      data.positions[this.m_indexB].a = aB;
      return MathUtils.abs(C) <= Settings.linearSlop;
   }
}
