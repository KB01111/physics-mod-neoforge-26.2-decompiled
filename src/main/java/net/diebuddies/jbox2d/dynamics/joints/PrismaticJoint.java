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

public class PrismaticJoint extends Joint {
   protected final Vec2 m_localAnchorA;
   protected final Vec2 m_localAnchorB;
   protected final Vec2 m_localXAxisA;
   protected final Vec2 m_localYAxisA;
   protected float m_referenceAngle;
   private final Vec3 m_impulse;
   private float m_motorImpulse;
   private float m_lowerTranslation;
   private float m_upperTranslation;
   private float m_maxMotorForce;
   private float m_motorSpeed;
   private boolean m_enableLimit;
   private boolean m_enableMotor;
   private LimitState m_limitState;
   private int m_indexA;
   private int m_indexB;
   private final Vec2 m_localCenterA = new Vec2();
   private final Vec2 m_localCenterB = new Vec2();
   private float m_invMassA;
   private float m_invMassB;
   private float m_invIA;
   private float m_invIB;
   private final Vec2 m_axis;
   private final Vec2 m_perp;
   private float m_s1;
   private float m_s2;
   private float m_a1;
   private float m_a2;
   private final Mat33 m_K;
   private float m_motorMass;

   protected PrismaticJoint(IWorldPool argWorld, PrismaticJointDef def) {
      super(argWorld, def);
      this.m_localAnchorA = new Vec2(def.localAnchorA);
      this.m_localAnchorB = new Vec2(def.localAnchorB);
      this.m_localXAxisA = new Vec2(def.localAxisA);
      this.m_localXAxisA.normalize();
      this.m_localYAxisA = new Vec2();
      Vec2.crossToOutUnsafe(1.0F, this.m_localXAxisA, this.m_localYAxisA);
      this.m_referenceAngle = def.referenceAngle;
      this.m_impulse = new Vec3();
      this.m_motorMass = 0.0F;
      this.m_motorImpulse = 0.0F;
      this.m_lowerTranslation = def.lowerTranslation;
      this.m_upperTranslation = def.upperTranslation;
      this.m_maxMotorForce = def.maxMotorForce;
      this.m_motorSpeed = def.motorSpeed;
      this.m_enableLimit = def.enableLimit;
      this.m_enableMotor = def.enableMotor;
      this.m_limitState = LimitState.INACTIVE;
      this.m_K = new Mat33();
      this.m_axis = new Vec2();
      this.m_perp = new Vec2();
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
      temp.set(this.m_axis).mulLocal(this.m_motorImpulse + this.m_impulse.z);
      argOut.set(this.m_perp).mulLocal(this.m_impulse.x).addLocal(temp).mulLocal(inv_dt);
      this.pool.pushVec2(1);
   }

   @Override
   public float getReactionTorque(float inv_dt) {
      return inv_dt * this.m_impulse.y;
   }

   public float getJointSpeed() {
      Body bA = this.m_bodyA;
      Body bB = this.m_bodyB;
      Vec2 temp = this.pool.popVec2();
      Vec2 rA = this.pool.popVec2();
      Vec2 rB = this.pool.popVec2();
      Vec2 p1 = this.pool.popVec2();
      Vec2 p2 = this.pool.popVec2();
      Vec2 d = this.pool.popVec2();
      Vec2 axis = this.pool.popVec2();
      Vec2 temp2 = this.pool.popVec2();
      Vec2 temp3 = this.pool.popVec2();
      temp.set(this.m_localAnchorA).subLocal(bA.m_sweep.localCenter);
      Rot.mulToOutUnsafe(bA.m_xf.q, temp, rA);
      temp.set(this.m_localAnchorB).subLocal(bB.m_sweep.localCenter);
      Rot.mulToOutUnsafe(bB.m_xf.q, temp, rB);
      p1.set(bA.m_sweep.c).addLocal(rA);
      p2.set(bB.m_sweep.c).addLocal(rB);
      d.set(p2).subLocal(p1);
      Rot.mulToOutUnsafe(bA.m_xf.q, this.m_localXAxisA, axis);
      Vec2 vA = bA.m_linearVelocity;
      Vec2 vB = bB.m_linearVelocity;
      float wA = bA.m_angularVelocity;
      float wB = bB.m_angularVelocity;
      Vec2.crossToOutUnsafe(wA, axis, temp);
      Vec2.crossToOutUnsafe(wB, rB, temp2);
      Vec2.crossToOutUnsafe(wA, rA, temp3);
      temp2.addLocal(vB).subLocal(vA).subLocal(temp3);
      float speed = Vec2.dot(d, temp) + Vec2.dot(axis, temp2);
      this.pool.pushVec2(9);
      return speed;
   }

   public float getJointTranslation() {
      Vec2 pA = this.pool.popVec2();
      Vec2 pB = this.pool.popVec2();
      Vec2 axis = this.pool.popVec2();
      this.m_bodyA.getWorldPointToOut(this.m_localAnchorA, pA);
      this.m_bodyB.getWorldPointToOut(this.m_localAnchorB, pB);
      this.m_bodyA.getWorldVectorToOutUnsafe(this.m_localXAxisA, axis);
      pB.subLocal(pA);
      float translation = Vec2.dot(pB, axis);
      this.pool.pushVec2(3);
      return translation;
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
      return this.m_lowerTranslation;
   }

   public float getUpperLimit() {
      return this.m_upperTranslation;
   }

   public void setLimits(float lower, float upper) {
      assert lower <= upper;

      if (lower != this.m_lowerTranslation || upper != this.m_upperTranslation) {
         this.m_bodyA.setAwake(true);
         this.m_bodyB.setAwake(true);
         this.m_lowerTranslation = lower;
         this.m_upperTranslation = upper;
         this.m_impulse.z = 0.0F;
      }
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

   public void setMaxMotorForce(float force) {
      this.m_bodyA.setAwake(true);
      this.m_bodyB.setAwake(true);
      this.m_maxMotorForce = force;
   }

   public float getMotorForce(float inv_dt) {
      return this.m_motorImpulse * inv_dt;
   }

   public float getMaxMotorForce() {
      return this.m_maxMotorForce;
   }

   public float getReferenceAngle() {
      return this.m_referenceAngle;
   }

   public Vec2 getLocalAxisA() {
      return this.m_localXAxisA;
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
      Vec2 d = this.pool.popVec2();
      Vec2 temp = this.pool.popVec2();
      Vec2 rA = this.pool.popVec2();
      Vec2 rB = this.pool.popVec2();
      qA.set(aA);
      qB.set(aB);
      Rot.mulToOutUnsafe(qA, d.set(this.m_localAnchorA).subLocal(this.m_localCenterA), rA);
      Rot.mulToOutUnsafe(qB, d.set(this.m_localAnchorB).subLocal(this.m_localCenterB), rB);
      d.set(cB).subLocal(cA).addLocal(rB).subLocal(rA);
      float mA = this.m_invMassA;
      float mB = this.m_invMassB;
      float iA = this.m_invIA;
      float iB = this.m_invIB;
      Rot.mulToOutUnsafe(qA, this.m_localXAxisA, this.m_axis);
      temp.set(d).addLocal(rA);
      this.m_a1 = Vec2.cross(temp, this.m_axis);
      this.m_a2 = Vec2.cross(rB, this.m_axis);
      this.m_motorMass = mA + mB + iA * this.m_a1 * this.m_a1 + iB * this.m_a2 * this.m_a2;
      if (this.m_motorMass > 0.0F) {
         this.m_motorMass = 1.0F / this.m_motorMass;
      }

      Rot.mulToOutUnsafe(qA, this.m_localYAxisA, this.m_perp);
      temp.set(d).addLocal(rA);
      this.m_s1 = Vec2.cross(temp, this.m_perp);
      this.m_s2 = Vec2.cross(rB, this.m_perp);
      float k11 = mA + mB + iA * this.m_s1 * this.m_s1 + iB * this.m_s2 * this.m_s2;
      float k12 = iA * this.m_s1 + iB * this.m_s2;
      float k13 = iA * this.m_s1 * this.m_a1 + iB * this.m_s2 * this.m_a2;
      float k22 = iA + iB;
      if (k22 == 0.0F) {
         k22 = 1.0F;
      }

      float k23 = iA * this.m_a1 + iB * this.m_a2;
      float k33 = mA + mB + iA * this.m_a1 * this.m_a1 + iB * this.m_a2 * this.m_a2;
      this.m_K.ex.set(k11, k12, k13);
      this.m_K.ey.set(k12, k22, k23);
      this.m_K.ez.set(k13, k23, k33);
      if (this.m_enableLimit) {
         k11 = Vec2.dot(this.m_axis, d);
         if (MathUtils.abs(this.m_upperTranslation - this.m_lowerTranslation) < 2.0F * Settings.linearSlop) {
            this.m_limitState = LimitState.EQUAL;
         } else if (k11 <= this.m_lowerTranslation) {
            if (this.m_limitState != LimitState.AT_LOWER) {
               this.m_limitState = LimitState.AT_LOWER;
               this.m_impulse.z = 0.0F;
            }
         } else if (k11 >= this.m_upperTranslation) {
            if (this.m_limitState != LimitState.AT_UPPER) {
               this.m_limitState = LimitState.AT_UPPER;
               this.m_impulse.z = 0.0F;
            }
         } else {
            this.m_limitState = LimitState.INACTIVE;
            this.m_impulse.z = 0.0F;
         }
      } else {
         this.m_limitState = LimitState.INACTIVE;
         this.m_impulse.z = 0.0F;
      }

      if (!this.m_enableMotor) {
         this.m_motorImpulse = 0.0F;
      }

      if (data.step.warmStarting) {
         this.m_impulse.mulLocal(data.step.dtRatio);
         this.m_motorImpulse = this.m_motorImpulse * data.step.dtRatio;
         Vec2 P = this.pool.popVec2();
         temp.set(this.m_axis).mulLocal(this.m_motorImpulse + this.m_impulse.z);
         P.set(this.m_perp).mulLocal(this.m_impulse.x).addLocal(temp);
         k12 = this.m_impulse.x * this.m_s1 + this.m_impulse.y + (this.m_motorImpulse + this.m_impulse.z) * this.m_a1;
         k13 = this.m_impulse.x * this.m_s2 + this.m_impulse.y + (this.m_motorImpulse + this.m_impulse.z) * this.m_a2;
         vA.x = vA.x - mA * P.x;
         vA.y = vA.y - mA * P.y;
         wA -= iA * k12;
         vB.x = vB.x + mB * P.x;
         vB.y = vB.y + mB * P.y;
         wB += iB * k13;
         this.pool.pushVec2(1);
      } else {
         this.m_impulse.setZero();
         this.m_motorImpulse = 0.0F;
      }

      data.velocities[this.m_indexA].w = wA;
      data.velocities[this.m_indexB].w = wB;
      this.pool.pushRot(2);
      this.pool.pushVec2(4);
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
      Vec2 temp = this.pool.popVec2();
      if (this.m_enableMotor && this.m_limitState != LimitState.EQUAL) {
         temp.set(vB).subLocal(vA);
         float Cdot = Vec2.dot(this.m_axis, temp) + this.m_a2 * wB - this.m_a1 * wA;
         float impulse = this.m_motorMass * (this.m_motorSpeed - Cdot);
         float oldImpulse = this.m_motorImpulse;
         float maxImpulse = data.step.dt * this.m_maxMotorForce;
         this.m_motorImpulse = MathUtils.clamp(this.m_motorImpulse + impulse, -maxImpulse, maxImpulse);
         impulse = this.m_motorImpulse - oldImpulse;
         Vec2 P = this.pool.popVec2();
         P.set(this.m_axis).mulLocal(impulse);
         float LA = impulse * this.m_a1;
         float LB = impulse * this.m_a2;
         vA.x = vA.x - mA * P.x;
         vA.y = vA.y - mA * P.y;
         wA -= iA * LA;
         vB.x = vB.x + mB * P.x;
         vB.y = vB.y + mB * P.y;
         wB += iB * LB;
         this.pool.pushVec2(1);
      }

      Vec2 Cdot1 = this.pool.popVec2();
      temp.set(vB).subLocal(vA);
      Cdot1.x = Vec2.dot(this.m_perp, temp) + this.m_s2 * wB - this.m_s1 * wA;
      Cdot1.y = wB - wA;
      if (this.m_enableLimit && this.m_limitState != LimitState.INACTIVE) {
         temp.set(vB).subLocal(vA);
         float Cdot2 = Vec2.dot(this.m_axis, temp) + this.m_a2 * wB - this.m_a1 * wA;
         Vec3 Cdot = this.pool.popVec3();
         Cdot.set(Cdot1.x, Cdot1.y, Cdot2);
         Vec3 f1 = this.pool.popVec3();
         Vec3 df = this.pool.popVec3();
         f1.set(this.m_impulse);
         this.m_K.solve33ToOut(Cdot.negateLocal(), df);
         this.m_impulse.addLocal(df);
         if (this.m_limitState == LimitState.AT_LOWER) {
            this.m_impulse.z = MathUtils.max(this.m_impulse.z, 0.0F);
         } else if (this.m_limitState == LimitState.AT_UPPER) {
            this.m_impulse.z = MathUtils.min(this.m_impulse.z, 0.0F);
         }

         Vec2 b = this.pool.popVec2();
         Vec2 f2r = this.pool.popVec2();
         temp.set(this.m_K.ez.x, this.m_K.ez.y).mulLocal(this.m_impulse.z - f1.z);
         b.set(Cdot1).negateLocal().subLocal(temp);
         this.m_K.solve22ToOut(b, f2r);
         f2r.addLocal(f1.x, f1.y);
         this.m_impulse.x = f2r.x;
         this.m_impulse.y = f2r.y;
         df.set(this.m_impulse).subLocal(f1);
         Vec2 P = this.pool.popVec2();
         temp.set(this.m_axis).mulLocal(df.z);
         P.set(this.m_perp).mulLocal(df.x).addLocal(temp);
         float LA = df.x * this.m_s1 + df.y + df.z * this.m_a1;
         float LB = df.x * this.m_s2 + df.y + df.z * this.m_a2;
         vA.x = vA.x - mA * P.x;
         vA.y = vA.y - mA * P.y;
         wA -= iA * LA;
         vB.x = vB.x + mB * P.x;
         vB.y = vB.y + mB * P.y;
         wB += iB * LB;
         this.pool.pushVec2(3);
         this.pool.pushVec3(3);
      } else {
         Vec2 df = this.pool.popVec2();
         this.m_K.solve22ToOut(Cdot1.negateLocal(), df);
         Cdot1.negateLocal();
         this.m_impulse.x = this.m_impulse.x + df.x;
         this.m_impulse.y = this.m_impulse.y + df.y;
         Vec2 P = this.pool.popVec2();
         P.set(this.m_perp).mulLocal(df.x);
         float LA = df.x * this.m_s1 + df.y;
         float LB = df.x * this.m_s2 + df.y;
         vA.x = vA.x - mA * P.x;
         vA.y = vA.y - mA * P.y;
         wA -= iA * LA;
         vB.x = vB.x + mB * P.x;
         vB.y = vB.y + mB * P.y;
         wB += iB * LB;
         this.pool.pushVec2(2);
      }

      data.velocities[this.m_indexA].w = wA;
      data.velocities[this.m_indexB].w = wB;
      this.pool.pushVec2(2);
   }

   @Override
   public boolean solvePositionConstraints(SolverData data) {
      Rot qA = this.pool.popRot();
      Rot qB = this.pool.popRot();
      Vec2 rA = this.pool.popVec2();
      Vec2 rB = this.pool.popVec2();
      Vec2 d = this.pool.popVec2();
      Vec2 axis = this.pool.popVec2();
      Vec2 perp = this.pool.popVec2();
      Vec2 temp = this.pool.popVec2();
      Vec2 C1 = this.pool.popVec2();
      Vec3 impulse = this.pool.popVec3();
      Vec2 cA = data.positions[this.m_indexA].c;
      float aA = data.positions[this.m_indexA].a;
      Vec2 cB = data.positions[this.m_indexB].c;
      float aB = data.positions[this.m_indexB].a;
      qA.set(aA);
      qB.set(aB);
      float mA = this.m_invMassA;
      float mB = this.m_invMassB;
      float iA = this.m_invIA;
      float iB = this.m_invIB;
      Rot.mulToOutUnsafe(qA, temp.set(this.m_localAnchorA).subLocal(this.m_localCenterA), rA);
      Rot.mulToOutUnsafe(qB, temp.set(this.m_localAnchorB).subLocal(this.m_localCenterB), rB);
      d.set(cB).addLocal(rB).subLocal(cA).subLocal(rA);
      Rot.mulToOutUnsafe(qA, this.m_localXAxisA, axis);
      float a1 = Vec2.cross(temp.set(d).addLocal(rA), axis);
      float a2 = Vec2.cross(rB, axis);
      Rot.mulToOutUnsafe(qA, this.m_localYAxisA, perp);
      float s1 = Vec2.cross(temp.set(d).addLocal(rA), perp);
      float s2 = Vec2.cross(rB, perp);
      C1.x = Vec2.dot(perp, d);
      C1.y = aB - aA - this.m_referenceAngle;
      float linearError = MathUtils.abs(C1.x);
      float angularError = MathUtils.abs(C1.y);
      boolean active = false;
      float C2 = 0.0F;
      if (this.m_enableLimit) {
         float translation = Vec2.dot(axis, d);
         if (MathUtils.abs(this.m_upperTranslation - this.m_lowerTranslation) < 2.0F * Settings.linearSlop) {
            C2 = MathUtils.clamp(translation, -Settings.maxLinearCorrection, Settings.maxLinearCorrection);
            linearError = MathUtils.max(linearError, MathUtils.abs(translation));
            active = true;
         } else if (translation <= this.m_lowerTranslation) {
            C2 = MathUtils.clamp(translation - this.m_lowerTranslation + Settings.linearSlop, -Settings.maxLinearCorrection, 0.0F);
            linearError = MathUtils.max(linearError, this.m_lowerTranslation - translation);
            active = true;
         } else if (translation >= this.m_upperTranslation) {
            C2 = MathUtils.clamp(translation - this.m_upperTranslation - Settings.linearSlop, 0.0F, Settings.maxLinearCorrection);
            linearError = MathUtils.max(linearError, translation - this.m_upperTranslation);
            active = true;
         }
      }

      if (active) {
         float k11 = mA + mB + iA * s1 * s1 + iB * s2 * s2;
         float k12 = iA * s1 + iB * s2;
         float k13 = iA * s1 * a1 + iB * s2 * a2;
         float k22 = iA + iB;
         if (k22 == 0.0F) {
            k22 = 1.0F;
         }

         float k23 = iA * a1 + iB * a2;
         float k33 = mA + mB + iA * a1 * a1 + iB * a2 * a2;
         Mat33 K = this.pool.popMat33();
         K.ex.set(k11, k12, k13);
         K.ey.set(k12, k22, k23);
         K.ez.set(k13, k23, k33);
         Vec3 C = this.pool.popVec3();
         C.x = C1.x;
         C.y = C1.y;
         C.z = C2;
         K.solve33ToOut(C.negateLocal(), impulse);
         this.pool.pushVec3(1);
         this.pool.pushMat33(1);
      } else {
         float k11 = mA + mB + iA * s1 * s1 + iB * s2 * s2;
         float k12 = iA * s1 + iB * s2;
         float k22 = iA + iB;
         if (k22 == 0.0F) {
            k22 = 1.0F;
         }

         Mat22 K = this.pool.popMat22();
         K.ex.set(k11, k12);
         K.ey.set(k12, k22);
         K.solveToOut(C1.negateLocal(), temp);
         C1.negateLocal();
         impulse.x = temp.x;
         impulse.y = temp.y;
         impulse.z = 0.0F;
         this.pool.pushMat22(1);
      }

      float Px = impulse.x * perp.x + impulse.z * axis.x;
      float Py = impulse.x * perp.y + impulse.z * axis.y;
      float LA = impulse.x * s1 + impulse.y + impulse.z * a1;
      float LB = impulse.x * s2 + impulse.y + impulse.z * a2;
      cA.x -= mA * Px;
      cA.y -= mA * Py;
      aA -= iA * LA;
      cB.x += mB * Px;
      cB.y += mB * Py;
      aB += iB * LB;
      data.positions[this.m_indexA].a = aA;
      data.positions[this.m_indexB].a = aB;
      this.pool.pushVec2(7);
      this.pool.pushVec3(1);
      this.pool.pushRot(2);
      return linearError <= Settings.linearSlop && angularError <= Settings.angularSlop;
   }
}
