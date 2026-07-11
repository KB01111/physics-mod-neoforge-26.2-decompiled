package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.Mat33;
import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.common.Vec3;
import net.diebuddies.jbox2d.dynamics.SolverData;
import net.diebuddies.jbox2d.pooling.IWorldPool;

public class WeldJoint extends Joint {
   private float m_frequencyHz;
   private float m_dampingRatio;
   private float m_bias;
   private final Vec2 m_localAnchorA;
   private final Vec2 m_localAnchorB;
   private float m_referenceAngle;
   private float m_gamma;
   private final Vec3 m_impulse;
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

   protected WeldJoint(IWorldPool argWorld, WeldJointDef def) {
      super(argWorld, def);
      this.m_localAnchorA = new Vec2(def.localAnchorA);
      this.m_localAnchorB = new Vec2(def.localAnchorB);
      this.m_referenceAngle = def.referenceAngle;
      this.m_frequencyHz = def.frequencyHz;
      this.m_dampingRatio = def.dampingRatio;
      this.m_impulse = new Vec3();
      this.m_impulse.setZero();
   }

   public float getReferenceAngle() {
      return this.m_referenceAngle;
   }

   public Vec2 getLocalAnchorA() {
      return this.m_localAnchorA;
   }

   public Vec2 getLocalAnchorB() {
      return this.m_localAnchorB;
   }

   public float getFrequency() {
      return this.m_frequencyHz;
   }

   public void setFrequency(float frequencyHz) {
      this.m_frequencyHz = frequencyHz;
   }

   public float getDampingRatio() {
      return this.m_dampingRatio;
   }

   public void setDampingRatio(float dampingRatio) {
      this.m_dampingRatio = dampingRatio;
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
      argOut.set(this.m_impulse.x, this.m_impulse.y);
      argOut.mulLocal(inv_dt);
   }

   @Override
   public float getReactionTorque(float inv_dt) {
      return inv_dt * this.m_impulse.z;
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
      Mat33 K = this.pool.popMat33();
      K.ex.x = mA + mB + this.m_rA.y * this.m_rA.y * iA + this.m_rB.y * this.m_rB.y * iB;
      K.ey.x = -this.m_rA.y * this.m_rA.x * iA - this.m_rB.y * this.m_rB.x * iB;
      K.ez.x = -this.m_rA.y * iA - this.m_rB.y * iB;
      K.ex.y = K.ey.x;
      K.ey.y = mA + mB + this.m_rA.x * this.m_rA.x * iA + this.m_rB.x * this.m_rB.x * iB;
      K.ez.y = this.m_rA.x * iA + this.m_rB.x * iB;
      K.ex.z = K.ez.x;
      K.ey.z = K.ez.y;
      K.ez.z = iA + iB;
      if (this.m_frequencyHz > 0.0F) {
         K.getInverse22(this.m_mass);
         float invM = iA + iB;
         float m = invM > 0.0F ? 1.0F / invM : 0.0F;
         float C = aB - aA - this.m_referenceAngle;
         float omega = (float) (Math.PI * 2) * this.m_frequencyHz;
         float d = 2.0F * m * this.m_dampingRatio * omega;
         float k = m * omega * omega;
         float h = data.step.dt;
         this.m_gamma = h * (d + h * k);
         this.m_gamma = this.m_gamma != 0.0F ? 1.0F / this.m_gamma : 0.0F;
         this.m_bias = C * h * k * this.m_gamma;
         invM += this.m_gamma;
         this.m_mass.ez.z = invM != 0.0F ? 1.0F / invM : 0.0F;
      } else {
         K.getSymInverse33(this.m_mass);
         this.m_gamma = 0.0F;
         this.m_bias = 0.0F;
      }

      if (data.step.warmStarting) {
         Vec2 P = this.pool.popVec2();
         this.m_impulse.mulLocal(data.step.dtRatio);
         P.set(this.m_impulse.x, this.m_impulse.y);
         vA.x = vA.x - mA * P.x;
         vA.y = vA.y - mA * P.y;
         wA -= iA * (Vec2.cross(this.m_rA, P) + this.m_impulse.z);
         vB.x = vB.x + mB * P.x;
         vB.y = vB.y + mB * P.y;
         wB += iB * (Vec2.cross(this.m_rB, P) + this.m_impulse.z);
         this.pool.pushVec2(1);
      } else {
         this.m_impulse.setZero();
      }

      data.velocities[this.m_indexA].w = wA;
      data.velocities[this.m_indexB].w = wB;
      this.pool.pushVec2(1);
      this.pool.pushRot(2);
      this.pool.pushMat33(1);
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
      Vec2 Cdot1 = this.pool.popVec2();
      Vec2 P = this.pool.popVec2();
      Vec2 temp = this.pool.popVec2();
      if (this.m_frequencyHz > 0.0F) {
         float Cdot2 = wB - wA;
         float impulse2 = -this.m_mass.ez.z * (Cdot2 + this.m_bias + this.m_gamma * this.m_impulse.z);
         this.m_impulse.z += impulse2;
         wA -= iA * impulse2;
         wB += iB * impulse2;
         Vec2.crossToOutUnsafe(wB, this.m_rB, Cdot1);
         Vec2.crossToOutUnsafe(wA, this.m_rA, temp);
         Cdot1.addLocal(vB).subLocal(vA).subLocal(temp);
         Mat33.mul22ToOutUnsafe(this.m_mass, Cdot1, P);
         P.negateLocal();
         this.m_impulse.x = this.m_impulse.x + P.x;
         this.m_impulse.y = this.m_impulse.y + P.y;
         vA.x = vA.x - mA * P.x;
         vA.y = vA.y - mA * P.y;
         wA -= iA * Vec2.cross(this.m_rA, P);
         vB.x = vB.x + mB * P.x;
         vB.y = vB.y + mB * P.y;
         wB += iB * Vec2.cross(this.m_rB, P);
      } else {
         Vec2.crossToOutUnsafe(wA, this.m_rA, temp);
         Vec2.crossToOutUnsafe(wB, this.m_rB, Cdot1);
         Cdot1.addLocal(vB).subLocal(vA).subLocal(temp);
         float Cdot2 = wB - wA;
         Vec3 Cdot = this.pool.popVec3();
         Cdot.set(Cdot1.x, Cdot1.y, Cdot2);
         Vec3 impulse = this.pool.popVec3();
         Mat33.mulToOutUnsafe(this.m_mass, Cdot, impulse);
         impulse.negateLocal();
         this.m_impulse.addLocal(impulse);
         P.set(impulse.x, impulse.y);
         vA.x = vA.x - mA * P.x;
         vA.y = vA.y - mA * P.y;
         wA -= iA * (Vec2.cross(this.m_rA, P) + impulse.z);
         vB.x = vB.x + mB * P.x;
         vB.y = vB.y + mB * P.y;
         wB += iB * (Vec2.cross(this.m_rB, P) + impulse.z);
         this.pool.pushVec3(2);
      }

      data.velocities[this.m_indexA].w = wA;
      data.velocities[this.m_indexB].w = wB;
      this.pool.pushVec2(3);
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
      Vec2 rA = this.pool.popVec2();
      Vec2 rB = this.pool.popVec2();
      qA.set(aA);
      qB.set(aB);
      float mA = this.m_invMassA;
      float mB = this.m_invMassB;
      float iA = this.m_invIA;
      float iB = this.m_invIB;
      Rot.mulToOutUnsafe(qA, temp.set(this.m_localAnchorA).subLocal(this.m_localCenterA), rA);
      Rot.mulToOutUnsafe(qB, temp.set(this.m_localAnchorB).subLocal(this.m_localCenterB), rB);
      Mat33 K = this.pool.popMat33();
      Vec2 C1 = this.pool.popVec2();
      Vec2 P = this.pool.popVec2();
      K.ex.x = mA + mB + rA.y * rA.y * iA + rB.y * rB.y * iB;
      K.ey.x = -rA.y * rA.x * iA - rB.y * rB.x * iB;
      K.ez.x = -rA.y * iA - rB.y * iB;
      K.ex.y = K.ey.x;
      K.ey.y = mA + mB + rA.x * rA.x * iA + rB.x * rB.x * iB;
      K.ez.y = rA.x * iA + rB.x * iB;
      K.ex.z = K.ez.x;
      K.ey.z = K.ez.y;
      K.ez.z = iA + iB;
      float positionError;
      float angularError;
      if (this.m_frequencyHz > 0.0F) {
         C1.set(cB).addLocal(rB).subLocal(cA).subLocal(rA);
         positionError = C1.length();
         angularError = 0.0F;
         K.solve22ToOut(C1, P);
         P.negateLocal();
         cA.x = cA.x - mA * P.x;
         cA.y = cA.y - mA * P.y;
         aA -= iA * Vec2.cross(rA, P);
         cB.x = cB.x + mB * P.x;
         cB.y = cB.y + mB * P.y;
         aB += iB * Vec2.cross(rB, P);
      } else {
         C1.set(cB).addLocal(rB).subLocal(cA).subLocal(rA);
         float C2 = aB - aA - this.m_referenceAngle;
         positionError = C1.length();
         angularError = MathUtils.abs(C2);
         Vec3 C = this.pool.popVec3();
         Vec3 impulse = this.pool.popVec3();
         C.set(C1.x, C1.y, C2);
         K.solve33ToOut(C, impulse);
         impulse.negateLocal();
         P.set(impulse.x, impulse.y);
         cA.x = cA.x - mA * P.x;
         cA.y = cA.y - mA * P.y;
         aA -= iA * (Vec2.cross(rA, P) + impulse.z);
         cB.x = cB.x + mB * P.x;
         cB.y = cB.y + mB * P.y;
         aB += iB * (Vec2.cross(rB, P) + impulse.z);
         this.pool.pushVec3(2);
      }

      data.positions[this.m_indexA].a = aA;
      data.positions[this.m_indexB].a = aB;
      this.pool.pushVec2(5);
      this.pool.pushRot(2);
      this.pool.pushMat33(1);
      return positionError <= Settings.linearSlop && angularError <= Settings.angularSlop;
   }
}
