package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.Mat22;
import net.diebuddies.jbox2d.common.Mat33;
import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.common.Vec3;
import net.diebuddies.jbox2d.dynamics.Body;
import net.diebuddies.jbox2d.dynamics.SolverData;
import net.diebuddies.jbox2d.pooling.IWorldPool;

public class RevoluteJoint extends Joint {
   protected final Vec2 m_localAnchorA = new Vec2();
   protected final Vec2 m_localAnchorB = new Vec2();
   private final Vec3 m_impulse = new Vec3();
   private float m_motorImpulse;
   private boolean m_enableMotor;
   private float m_maxMotorTorque;
   private float m_motorSpeed;
   private boolean m_enableLimit;
   protected float m_referenceAngle;
   private float m_lowerAngle;
   private float m_upperAngle;
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
   private final Mat33 m_mass = new Mat33();
   private float m_motorMass;
   private LimitState m_limitState;

   protected RevoluteJoint(IWorldPool argWorld, RevoluteJointDef def) {
      super(argWorld, def);
      this.m_localAnchorA.set(def.localAnchorA);
      this.m_localAnchorB.set(def.localAnchorB);
      this.m_referenceAngle = def.referenceAngle;
      this.m_motorImpulse = 0.0F;
      this.m_lowerAngle = def.lowerAngle;
      this.m_upperAngle = def.upperAngle;
      this.m_maxMotorTorque = def.maxMotorTorque;
      this.m_motorSpeed = def.motorSpeed;
      this.m_enableLimit = def.enableLimit;
      this.m_enableMotor = def.enableMotor;
      this.m_limitState = LimitState.INACTIVE;
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
      Rot qA = this.pool.popRot();
      Rot qB = this.pool.popRot();
      Vec2 temp = this.pool.popVec2();
      qA.set(aA);
      qB.set(aB);
      Rot.mulToOutUnsafe(qA, temp.set(this.m_localAnchorA).subLocal(this.m_localCenterA), this.m_rA);
      Rot.mulToOutUnsafe(qB, temp.set(this.m_localAnchorB).subLocal(this.m_localCenterB), this.m_rB);
      float mA = this.m_invMassA;
      float mB = this.m_invMassB;
      float iA = this.m_invIA;
      float iB = this.m_invIB;
      boolean fixedRotation = iA + iB == 0.0F;
      this.m_mass.ex.x = mA + mB + this.m_rA.y * this.m_rA.y * iA + this.m_rB.y * this.m_rB.y * iB;
      this.m_mass.ey.x = -this.m_rA.y * this.m_rA.x * iA - this.m_rB.y * this.m_rB.x * iB;
      this.m_mass.ez.x = -this.m_rA.y * iA - this.m_rB.y * iB;
      this.m_mass.ex.y = this.m_mass.ey.x;
      this.m_mass.ey.y = mA + mB + this.m_rA.x * this.m_rA.x * iA + this.m_rB.x * this.m_rB.x * iB;
      this.m_mass.ez.y = this.m_rA.x * iA + this.m_rB.x * iB;
      this.m_mass.ex.z = this.m_mass.ez.x;
      this.m_mass.ey.z = this.m_mass.ez.y;
      this.m_mass.ez.z = iA + iB;
      this.m_motorMass = iA + iB;
      if (this.m_motorMass > 0.0F) {
         this.m_motorMass = 1.0F / this.m_motorMass;
      }

      if (!this.m_enableMotor || fixedRotation) {
         this.m_motorImpulse = 0.0F;
      }

      if (this.m_enableLimit && !fixedRotation) {
         float jointAngle = aB - aA - this.m_referenceAngle;
         if (MathUtils.abs(this.m_upperAngle - this.m_lowerAngle) < 2.0F * Settings.angularSlop) {
            this.m_limitState = LimitState.EQUAL;
         } else if (jointAngle <= this.m_lowerAngle) {
            if (this.m_limitState != LimitState.AT_LOWER) {
               this.m_impulse.z = 0.0F;
            }

            this.m_limitState = LimitState.AT_LOWER;
         } else if (jointAngle >= this.m_upperAngle) {
            if (this.m_limitState != LimitState.AT_UPPER) {
               this.m_impulse.z = 0.0F;
            }

            this.m_limitState = LimitState.AT_UPPER;
         } else {
            this.m_limitState = LimitState.INACTIVE;
            this.m_impulse.z = 0.0F;
         }
      } else {
         this.m_limitState = LimitState.INACTIVE;
      }

      if (data.step.warmStarting) {
         Vec2 P = this.pool.popVec2();
         this.m_impulse.x = this.m_impulse.x * data.step.dtRatio;
         this.m_impulse.y = this.m_impulse.y * data.step.dtRatio;
         this.m_motorImpulse = this.m_motorImpulse * data.step.dtRatio;
         P.x = this.m_impulse.x;
         P.y = this.m_impulse.y;
         vA.x = vA.x - mA * P.x;
         vA.y = vA.y - mA * P.y;
         wA -= iA * (Vec2.cross(this.m_rA, P) + this.m_motorImpulse + this.m_impulse.z);
         vB.x = vB.x + mB * P.x;
         vB.y = vB.y + mB * P.y;
         wB += iB * (Vec2.cross(this.m_rB, P) + this.m_motorImpulse + this.m_impulse.z);
         this.pool.pushVec2(1);
      } else {
         this.m_impulse.setZero();
         this.m_motorImpulse = 0.0F;
      }

      data.velocities[this.m_indexA].w = wA;
      data.velocities[this.m_indexB].w = wB;
      this.pool.pushVec2(1);
      this.pool.pushRot(2);
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
      boolean fixedRotation = iA + iB == 0.0F;
      if (this.m_enableMotor && this.m_limitState != LimitState.EQUAL && !fixedRotation) {
         float Cdot = wB - wA - this.m_motorSpeed;
         float impulse = -this.m_motorMass * Cdot;
         float oldImpulse = this.m_motorImpulse;
         float maxImpulse = data.step.dt * this.m_maxMotorTorque;
         this.m_motorImpulse = MathUtils.clamp(this.m_motorImpulse + impulse, -maxImpulse, maxImpulse);
         impulse = this.m_motorImpulse - oldImpulse;
         wA -= iA * impulse;
         wB += iB * impulse;
      }

      Vec2 temp = this.pool.popVec2();
      if (this.m_enableLimit && this.m_limitState != LimitState.INACTIVE && !fixedRotation) {
         Vec2 Cdot1 = this.pool.popVec2();
         Vec3 Cdot = this.pool.popVec3();
         Vec2.crossToOutUnsafe(wA, this.m_rA, temp);
         Vec2.crossToOutUnsafe(wB, this.m_rB, Cdot1);
         Cdot1.addLocal(vB).subLocal(vA).subLocal(temp);
         float Cdot2 = wB - wA;
         Cdot.set(Cdot1.x, Cdot1.y, Cdot2);
         Vec3 impulse = this.pool.popVec3();
         this.m_mass.solve33ToOut(Cdot, impulse);
         impulse.negateLocal();
         if (this.m_limitState == LimitState.EQUAL) {
            this.m_impulse.addLocal(impulse);
         } else if (this.m_limitState == LimitState.AT_LOWER) {
            float newImpulse = this.m_impulse.z + impulse.z;
            if (newImpulse < 0.0F) {
               Vec2 rhs = this.pool.popVec2();
               rhs.set(this.m_mass.ez.x, this.m_mass.ez.y).mulLocal(this.m_impulse.z).subLocal(Cdot1);
               this.m_mass.solve22ToOut(rhs, temp);
               impulse.x = temp.x;
               impulse.y = temp.y;
               impulse.z = -this.m_impulse.z;
               this.m_impulse.x = this.m_impulse.x + temp.x;
               this.m_impulse.y = this.m_impulse.y + temp.y;
               this.m_impulse.z = 0.0F;
               this.pool.pushVec2(1);
            } else {
               this.m_impulse.addLocal(impulse);
            }
         } else if (this.m_limitState == LimitState.AT_UPPER) {
            float newImpulse = this.m_impulse.z + impulse.z;
            if (newImpulse > 0.0F) {
               Vec2 rhs = this.pool.popVec2();
               rhs.set(this.m_mass.ez.x, this.m_mass.ez.y).mulLocal(this.m_impulse.z).subLocal(Cdot1);
               this.m_mass.solve22ToOut(rhs, temp);
               impulse.x = temp.x;
               impulse.y = temp.y;
               impulse.z = -this.m_impulse.z;
               this.m_impulse.x = this.m_impulse.x + temp.x;
               this.m_impulse.y = this.m_impulse.y + temp.y;
               this.m_impulse.z = 0.0F;
               this.pool.pushVec2(1);
            } else {
               this.m_impulse.addLocal(impulse);
            }
         }

         Vec2 P = this.pool.popVec2();
         P.set(impulse.x, impulse.y);
         vA.x = vA.x - mA * P.x;
         vA.y = vA.y - mA * P.y;
         wA -= iA * (Vec2.cross(this.m_rA, P) + impulse.z);
         vB.x = vB.x + mB * P.x;
         vB.y = vB.y + mB * P.y;
         wB += iB * (Vec2.cross(this.m_rB, P) + impulse.z);
         this.pool.pushVec2(2);
         this.pool.pushVec3(2);
      } else {
         Vec2 Cdot = this.pool.popVec2();
         Vec2 impulse = this.pool.popVec2();
         Vec2.crossToOutUnsafe(wA, this.m_rA, temp);
         Vec2.crossToOutUnsafe(wB, this.m_rB, Cdot);
         Cdot.addLocal(vB).subLocal(vA).subLocal(temp);
         this.m_mass.solve22ToOut(Cdot.negateLocal(), impulse);
         this.m_impulse.x = this.m_impulse.x + impulse.x;
         this.m_impulse.y = this.m_impulse.y + impulse.y;
         vA.x = vA.x - mA * impulse.x;
         vA.y = vA.y - mA * impulse.y;
         wA -= iA * Vec2.cross(this.m_rA, impulse);
         vB.x = vB.x + mB * impulse.x;
         vB.y = vB.y + mB * impulse.y;
         wB += iB * Vec2.cross(this.m_rB, impulse);
         this.pool.pushVec2(2);
      }

      data.velocities[this.m_indexA].w = wA;
      data.velocities[this.m_indexB].w = wB;
      this.pool.pushVec2(1);
   }

   @Override
   public boolean solvePositionConstraints(SolverData data) {
      Rot qA = this.pool.popRot();
      Rot qB = this.pool.popRot();
      Vec2 cA = data.positions[this.m_indexA].c;
      float aA = data.positions[this.m_indexA].a;
      Vec2 cB = data.positions[this.m_indexB].c;
      float aB = data.positions[this.m_indexB].a;
      qA.set(aA);
      qB.set(aB);
      float angularError = 0.0F;
      float positionError = 0.0F;
      boolean fixedRotation = this.m_invIA + this.m_invIB == 0.0F;
      if (this.m_enableLimit && this.m_limitState != LimitState.INACTIVE && !fixedRotation) {
         float angle = aB - aA - this.m_referenceAngle;
         float limitImpulse = 0.0F;
         if (this.m_limitState == LimitState.EQUAL) {
            float C = MathUtils.clamp(angle - this.m_lowerAngle, -Settings.maxAngularCorrection, Settings.maxAngularCorrection);
            limitImpulse = -this.m_motorMass * C;
            angularError = MathUtils.abs(C);
         } else if (this.m_limitState == LimitState.AT_LOWER) {
            float C = angle - this.m_lowerAngle;
            angularError = -C;
            C = MathUtils.clamp(C + Settings.angularSlop, -Settings.maxAngularCorrection, 0.0F);
            limitImpulse = -this.m_motorMass * C;
         } else if (this.m_limitState == LimitState.AT_UPPER) {
            float C = angle - this.m_upperAngle;
            angularError = C;
            C = MathUtils.clamp(C - Settings.angularSlop, 0.0F, Settings.maxAngularCorrection);
            limitImpulse = -this.m_motorMass * C;
         }

         aA -= this.m_invIA * limitImpulse;
         aB += this.m_invIB * limitImpulse;
      }

      qA.set(aA);
      qB.set(aB);
      Vec2 rA = this.pool.popVec2();
      Vec2 rB = this.pool.popVec2();
      Vec2 C = this.pool.popVec2();
      Vec2 impulse = this.pool.popVec2();
      Rot.mulToOutUnsafe(qA, C.set(this.m_localAnchorA).subLocal(this.m_localCenterA), rA);
      Rot.mulToOutUnsafe(qB, C.set(this.m_localAnchorB).subLocal(this.m_localCenterB), rB);
      C.set(cB).addLocal(rB).subLocal(cA).subLocal(rA);
      positionError = C.length();
      float mA = this.m_invMassA;
      float mB = this.m_invMassB;
      float iA = this.m_invIA;
      float iB = this.m_invIB;
      Mat22 K = this.pool.popMat22();
      K.ex.x = mA + mB + iA * rA.y * rA.y + iB * rB.y * rB.y;
      K.ex.y = -iA * rA.x * rA.y - iB * rB.x * rB.y;
      K.ey.x = K.ex.y;
      K.ey.y = mA + mB + iA * rA.x * rA.x + iB * rB.x * rB.x;
      K.solveToOut(C, impulse);
      impulse.negateLocal();
      cA.x = cA.x - mA * impulse.x;
      cA.y = cA.y - mA * impulse.y;
      aA -= iA * Vec2.cross(rA, impulse);
      cB.x = cB.x + mB * impulse.x;
      cB.y = cB.y + mB * impulse.y;
      aB += iB * Vec2.cross(rB, impulse);
      this.pool.pushVec2(4);
      this.pool.pushMat22(1);
      data.positions[this.m_indexA].a = aA;
      data.positions[this.m_indexB].a = aB;
      this.pool.pushRot(2);
      return positionError <= Settings.linearSlop && angularError <= Settings.angularSlop;
   }

   public Vec2 getLocalAnchorA() {
      return this.m_localAnchorA;
   }

   public Vec2 getLocalAnchorB() {
      return this.m_localAnchorB;
   }

   public float getReferenceAngle() {
      return this.m_referenceAngle;
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
      argOut.set(this.m_impulse.x, this.m_impulse.y).mulLocal(inv_dt);
   }

   @Override
   public float getReactionTorque(float inv_dt) {
      return inv_dt * this.m_impulse.z;
   }

   public float getJointAngle() {
      Body b1 = this.m_bodyA;
      Body b2 = this.m_bodyB;
      return b2.m_sweep.a - b1.m_sweep.a - this.m_referenceAngle;
   }

   public float getJointSpeed() {
      Body b1 = this.m_bodyA;
      Body b2 = this.m_bodyB;
      return b2.m_angularVelocity - b1.m_angularVelocity;
   }

   public boolean isMotorEnabled() {
      return this.m_enableMotor;
   }

   public void enableMotor(boolean flag) {
      this.m_bodyA.setAwake(true);
      this.m_bodyB.setAwake(true);
      this.m_enableMotor = flag;
   }

   public float getMotorTorque(float inv_dt) {
      return this.m_motorImpulse * inv_dt;
   }

   public void setMotorSpeed(float speed) {
      this.m_bodyA.setAwake(true);
      this.m_bodyB.setAwake(true);
      this.m_motorSpeed = speed;
   }

   public void setMaxMotorTorque(float torque) {
      this.m_bodyA.setAwake(true);
      this.m_bodyB.setAwake(true);
      this.m_maxMotorTorque = torque;
   }

   public float getMotorSpeed() {
      return this.m_motorSpeed;
   }

   public float getMaxMotorTorque() {
      return this.m_maxMotorTorque;
   }

   public boolean isLimitEnabled() {
      return this.m_enableLimit;
   }

   public void enableLimit(boolean flag) {
      if (flag != this.m_enableLimit) {
         this.m_bodyA.setAwake(true);
         this.m_bodyB.setAwake(true);
         this.m_enableLimit = flag;
         this.m_impulse.z = 0.0F;
      }
   }

   public float getLowerLimit() {
      return this.m_lowerAngle;
   }

   public float getUpperLimit() {
      return this.m_upperAngle;
   }

   public void setLimits(float lower, float upper) {
      assert lower <= upper;

      if (lower != this.m_lowerAngle || upper != this.m_upperAngle) {
         this.m_bodyA.setAwake(true);
         this.m_bodyB.setAwake(true);
         this.m_impulse.z = 0.0F;
         this.m_lowerAngle = lower;
         this.m_upperAngle = upper;
      }
   }
}
