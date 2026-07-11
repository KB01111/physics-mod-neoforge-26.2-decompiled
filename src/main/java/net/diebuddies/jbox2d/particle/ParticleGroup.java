package net.diebuddies.jbox2d.particle;

import net.diebuddies.jbox2d.common.Transform;
import net.diebuddies.jbox2d.common.Vec2;

public class ParticleGroup {
   ParticleSystem m_system;
   int m_firstIndex;
   int m_lastIndex;
   int m_groupFlags;
   float m_strength;
   ParticleGroup m_prev;
   ParticleGroup m_next;
   int m_timestamp;
   float m_mass;
   float m_inertia;
   final Vec2 m_center = new Vec2();
   final Vec2 m_linearVelocity = new Vec2();
   float m_angularVelocity;
   final Transform m_transform = new Transform();
   boolean m_destroyAutomatically;
   boolean m_toBeDestroyed;
   boolean m_toBeSplit;
   Object m_userData;

   public ParticleGroup() {
      this.m_firstIndex = 0;
      this.m_lastIndex = 0;
      this.m_groupFlags = 0;
      this.m_strength = 1.0F;
      this.m_timestamp = -1;
      this.m_mass = 0.0F;
      this.m_inertia = 0.0F;
      this.m_angularVelocity = 0.0F;
      this.m_transform.setIdentity();
      this.m_destroyAutomatically = true;
      this.m_toBeDestroyed = false;
      this.m_toBeSplit = false;
   }

   public ParticleGroup getNext() {
      return this.m_next;
   }

   public int getParticleCount() {
      return this.m_lastIndex - this.m_firstIndex;
   }

   public int getBufferIndex() {
      return this.m_firstIndex;
   }

   public int getGroupFlags() {
      return this.m_groupFlags;
   }

   public void setGroupFlags(int flags) {
      this.m_groupFlags = flags;
   }

   public float getMass() {
      this.updateStatistics();
      return this.m_mass;
   }

   public float getInertia() {
      this.updateStatistics();
      return this.m_inertia;
   }

   public Vec2 getCenter() {
      this.updateStatistics();
      return this.m_center;
   }

   public Vec2 getLinearVelocity() {
      this.updateStatistics();
      return this.m_linearVelocity;
   }

   public float getAngularVelocity() {
      this.updateStatistics();
      return this.m_angularVelocity;
   }

   public Transform getTransform() {
      return this.m_transform;
   }

   public Vec2 getPosition() {
      return this.m_transform.p;
   }

   public float getAngle() {
      return this.m_transform.q.getAngle();
   }

   public Object getUserData() {
      return this.m_userData;
   }

   public void setUserData(Object data) {
      this.m_userData = data;
   }

   public void updateStatistics() {
      if (this.m_timestamp != this.m_system.m_timestamp) {
         float m = this.m_system.getParticleMass();
         this.m_mass = 0.0F;
         this.m_center.setZero();
         this.m_linearVelocity.setZero();

         for (int i = this.m_firstIndex; i < this.m_lastIndex; i++) {
            this.m_mass += m;
            Vec2 pos = this.m_system.m_positionBuffer.data[i];
            this.m_center.x = this.m_center.x + m * pos.x;
            this.m_center.y = this.m_center.y + m * pos.y;
            Vec2 vel = this.m_system.m_velocityBuffer.data[i];
            this.m_linearVelocity.x = this.m_linearVelocity.x + m * vel.x;
            this.m_linearVelocity.y = this.m_linearVelocity.y + m * vel.y;
         }

         if (this.m_mass > 0.0F) {
            this.m_center.x = this.m_center.x * (1.0F / this.m_mass);
            this.m_center.y = this.m_center.y * (1.0F / this.m_mass);
            this.m_linearVelocity.x = this.m_linearVelocity.x * (1.0F / this.m_mass);
            this.m_linearVelocity.y = this.m_linearVelocity.y * (1.0F / this.m_mass);
         }

         this.m_inertia = 0.0F;
         this.m_angularVelocity = 0.0F;

         for (int i = this.m_firstIndex; i < this.m_lastIndex; i++) {
            Vec2 pos = this.m_system.m_positionBuffer.data[i];
            Vec2 vel = this.m_system.m_velocityBuffer.data[i];
            float px = pos.x - this.m_center.x;
            float py = pos.y - this.m_center.y;
            float vx = vel.x - this.m_linearVelocity.x;
            float vy = vel.y - this.m_linearVelocity.y;
            this.m_inertia += m * (px * px + py * py);
            this.m_angularVelocity += m * (px * vy - py * vx);
         }

         if (this.m_inertia > 0.0F) {
            this.m_angularVelocity = this.m_angularVelocity * (1.0F / this.m_inertia);
         }

         this.m_timestamp = this.m_system.m_timestamp;
      }
   }
}
