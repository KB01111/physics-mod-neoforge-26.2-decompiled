package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.Mat22;
import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Transform;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.SolverData;
import net.diebuddies.jbox2d.pooling.IWorldPool;

public class MouseJoint extends Joint {
   private final Vec2 m_localAnchorB = new Vec2();
   private final Vec2 m_targetA = new Vec2();
   private float m_frequencyHz;
   private float m_dampingRatio;
   private float m_beta;
   private final Vec2 m_impulse = new Vec2();
   private float m_maxForce;
   private float m_gamma;
   private int m_indexB;
   private final Vec2 m_rB = new Vec2();
   private final Vec2 m_localCenterB = new Vec2();
   private float m_invMassB;
   private float m_invIB;
   private final Mat22 m_mass = new Mat22();
   private final Vec2 m_C = new Vec2();

   protected MouseJoint(IWorldPool argWorld, MouseJointDef def) {
      super(argWorld, def);

      assert def.target.isValid();

      assert def.maxForce >= 0.0F;

      assert def.frequencyHz >= 0.0F;

      assert def.dampingRatio >= 0.0F;

      this.m_targetA.set(def.target);
      Transform.mulTransToOutUnsafe(this.m_bodyB.getTransform(), this.m_targetA, this.m_localAnchorB);
      this.m_maxForce = def.maxForce;
      this.m_impulse.setZero();
      this.m_frequencyHz = def.frequencyHz;
      this.m_dampingRatio = def.dampingRatio;
      this.m_beta = 0.0F;
      this.m_gamma = 0.0F;
   }

   @Override
   public void getAnchorA(Vec2 argOut) {
      argOut.set(this.m_targetA);
   }

   @Override
   public void getAnchorB(Vec2 argOut) {
      this.m_bodyB.getWorldPointToOut(this.m_localAnchorB, argOut);
   }

   @Override
   public void getReactionForce(float invDt, Vec2 argOut) {
      argOut.set(this.m_impulse).mulLocal(invDt);
   }

   @Override
   public float getReactionTorque(float invDt) {
      return invDt * 0.0F;
   }

   public void setTarget(Vec2 target) {
      if (!this.m_bodyB.isAwake()) {
         this.m_bodyB.setAwake(true);
      }

      this.m_targetA.set(target);
   }

   public Vec2 getTarget() {
      return this.m_targetA;
   }

   public void setMaxForce(float force) {
      this.m_maxForce = force;
   }

   public float getMaxForce() {
      return this.m_maxForce;
   }

   public void setFrequency(float hz) {
      this.m_frequencyHz = hz;
   }

   public float getFrequency() {
      return this.m_frequencyHz;
   }

   public void setDampingRatio(float ratio) {
      this.m_dampingRatio = ratio;
   }

   public float getDampingRatio() {
      return this.m_dampingRatio;
   }

   @Override
   public void initVelocityConstraints(SolverData data) {
      this.m_indexB = this.m_bodyB.m_islandIndex;
      this.m_localCenterB.set(this.m_bodyB.m_sweep.localCenter);
      this.m_invMassB = this.m_bodyB.m_invMass;
      this.m_invIB = this.m_bodyB.m_invI;
      Vec2 cB = data.positions[this.m_indexB].c;
      float aB = data.positions[this.m_indexB].a;
      Vec2 vB = data.velocities[this.m_indexB].v;
      float wB = data.velocities[this.m_indexB].w;
      Rot qB = this.pool.popRot();
      qB.set(aB);
      float mass = this.m_bodyB.getMass();
      float omega = (float) (Math.PI * 2) * this.m_frequencyHz;
      float d = 2.0F * mass * this.m_dampingRatio * omega;
      float k = mass * omega * omega;
      float h = data.step.dt;

      assert d + h * k > 1.1920929E-7F;

      this.m_gamma = h * (d + h * k);
      if (this.m_gamma != 0.0F) {
         this.m_gamma = 1.0F / this.m_gamma;
      }

      this.m_beta = h * k * this.m_gamma;
      Vec2 temp = this.pool.popVec2();
      Rot.mulToOutUnsafe(qB, temp.set(this.m_localAnchorB).subLocal(this.m_localCenterB), this.m_rB);
      Mat22 K = this.pool.popMat22();
      K.ex.x = this.m_invMassB + this.m_invIB * this.m_rB.y * this.m_rB.y + this.m_gamma;
      K.ex.y = -this.m_invIB * this.m_rB.x * this.m_rB.y;
      K.ey.x = K.ex.y;
      K.ey.y = this.m_invMassB + this.m_invIB * this.m_rB.x * this.m_rB.x + this.m_gamma;
      K.invertToOut(this.m_mass);
      this.m_C.set(cB).addLocal(this.m_rB).subLocal(this.m_targetA);
      this.m_C.mulLocal(this.m_beta);
      wB *= 0.98F;
      if (data.step.warmStarting) {
         this.m_impulse.mulLocal(data.step.dtRatio);
         vB.x = vB.x + this.m_invMassB * this.m_impulse.x;
         vB.y = vB.y + this.m_invMassB * this.m_impulse.y;
         wB += this.m_invIB * Vec2.cross(this.m_rB, this.m_impulse);
      } else {
         this.m_impulse.setZero();
      }

      data.velocities[this.m_indexB].w = wB;
      this.pool.pushVec2(1);
      this.pool.pushMat22(1);
      this.pool.pushRot(1);
   }

   @Override
   public boolean solvePositionConstraints(SolverData data) {
      return true;
   }

   @Override
   public void solveVelocityConstraints(SolverData data) {
      Vec2 vB = data.velocities[this.m_indexB].v;
      float wB = data.velocities[this.m_indexB].w;
      Vec2 Cdot = this.pool.popVec2();
      Vec2.crossToOutUnsafe(wB, this.m_rB, Cdot);
      Cdot.addLocal(vB);
      Vec2 impulse = this.pool.popVec2();
      Vec2 temp = this.pool.popVec2();
      temp.set(this.m_impulse).mulLocal(this.m_gamma).addLocal(this.m_C).addLocal(Cdot).negateLocal();
      Mat22.mulToOutUnsafe(this.m_mass, temp, impulse);
      temp.set(this.m_impulse);
      this.m_impulse.addLocal(impulse);
      float maxImpulse = data.step.dt * this.m_maxForce;
      if (this.m_impulse.lengthSquared() > maxImpulse * maxImpulse) {
         this.m_impulse.mulLocal(maxImpulse / this.m_impulse.length());
      }

      impulse.set(this.m_impulse).subLocal(temp);
      vB.x = vB.x + this.m_invMassB * impulse.x;
      vB.y = vB.y + this.m_invMassB * impulse.y;
      wB += this.m_invIB * Vec2.cross(this.m_rB, impulse);
      data.velocities[this.m_indexB].w = wB;
      this.pool.pushVec2(3);
   }
}
