package net.diebuddies.jbox2d.particle;

import java.lang.reflect.Array;
import java.util.Arrays;
import net.diebuddies.jbox2d.callbacks.ParticleDestructionListener;
import net.diebuddies.jbox2d.callbacks.ParticleQueryCallback;
import net.diebuddies.jbox2d.callbacks.ParticleRaycastCallback;
import net.diebuddies.jbox2d.callbacks.QueryCallback;
import net.diebuddies.jbox2d.collision.AABB;
import net.diebuddies.jbox2d.collision.RayCastInput;
import net.diebuddies.jbox2d.collision.RayCastOutput;
import net.diebuddies.jbox2d.collision.shapes.Shape;
import net.diebuddies.jbox2d.common.BufferUtils;
import net.diebuddies.jbox2d.common.MathUtils;
import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Transform;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Body;
import net.diebuddies.jbox2d.dynamics.Fixture;
import net.diebuddies.jbox2d.dynamics.TimeStep;
import net.diebuddies.jbox2d.dynamics.World;

public class ParticleSystem {
   private static final int k_pairFlags = 8;
   private static final int k_triadFlags = 16;
   private static final int k_noPressureFlags = 64;
   static final int xTruncBits = 12;
   static final int yTruncBits = 12;
   static final int tagBits = 31;
   static final long yOffset = 2048L;
   static final int yShift = 19;
   static final int xShift = 7;
   static final long xScale = 128L;
   static final long xOffset = 262144L;
   static final int xMask = 4095;
   static final int yMask = 4095;
   int m_timestamp;
   int m_allParticleFlags;
   int m_allGroupFlags;
   float m_density;
   float m_inverseDensity;
   float m_gravityScale;
   float m_particleDiameter;
   float m_inverseDiameter;
   float m_squaredDiameter;
   int m_count;
   int m_internalAllocatedCapacity;
   int m_maxCount;
   ParticleSystem.ParticleBufferInt m_flagsBuffer;
   ParticleSystem.ParticleBuffer<Vec2> m_positionBuffer;
   ParticleSystem.ParticleBuffer<Vec2> m_velocityBuffer;
   float[] m_accumulationBuffer;
   Vec2[] m_accumulation2Buffer;
   float[] m_depthBuffer;
   public ParticleSystem.ParticleBuffer<ParticleColor> m_colorBuffer;
   ParticleGroup[] m_groupBuffer;
   ParticleSystem.ParticleBuffer<Object> m_userDataBuffer;
   int m_proxyCount;
   int m_proxyCapacity;
   ParticleSystem.Proxy[] m_proxyBuffer;
   public int m_contactCount;
   int m_contactCapacity;
   public ParticleContact[] m_contactBuffer;
   public int m_bodyContactCount;
   int m_bodyContactCapacity;
   public ParticleBodyContact[] m_bodyContactBuffer;
   int m_pairCount;
   int m_pairCapacity;
   ParticleSystem.Pair[] m_pairBuffer;
   int m_triadCount;
   int m_triadCapacity;
   ParticleSystem.Triad[] m_triadBuffer;
   int m_groupCount;
   ParticleGroup m_groupList;
   float m_pressureStrength;
   float m_dampingStrength;
   float m_elasticStrength;
   float m_springStrength;
   float m_viscousStrength;
   float m_surfaceTensionStrengthA;
   float m_surfaceTensionStrengthB;
   float m_powderStrength;
   float m_ejectionStrength;
   float m_colorMixingStrength;
   World m_world;
   private final AABB temp = new AABB();
   private final ParticleSystem.DestroyParticlesInShapeCallback dpcallback = new ParticleSystem.DestroyParticlesInShapeCallback();
   private final AABB temp2 = new AABB();
   private final Vec2 tempVec = new Vec2();
   private final Transform tempTransform = new Transform();
   private final Transform tempTransform2 = new Transform();
   private ParticleSystem.CreateParticleGroupCallback createParticleGroupCallback = new ParticleSystem.CreateParticleGroupCallback();
   private final ParticleDef tempParticleDef = new ParticleDef();
   private final ParticleSystem.UpdateBodyContactsCallback ubccallback = new ParticleSystem.UpdateBodyContactsCallback();
   private ParticleSystem.SolveCollisionCallback sccallback = new ParticleSystem.SolveCollisionCallback();
   private final Vec2 tempVec2 = new Vec2();
   private final Rot tempRot = new Rot();
   private final Transform tempXf = new Transform();
   private final Transform tempXf2 = new Transform();
   private final ParticleSystem.NewIndices newIndices = new ParticleSystem.NewIndices();

   static long computeTag(float x, float y) {
      return ((long)(y + 2048.0F) << 19) + (long)(128.0F * x) + 262144L;
   }

   static long computeRelativeTag(long tag, int x, int y) {
      return tag + (long)(y << 19) + (long)(x << 7);
   }

   static int limitCapacity(int capacity, int maxCount) {
      return maxCount != 0 && capacity > maxCount ? maxCount : capacity;
   }

   public ParticleSystem(World world) {
      this.m_world = world;
      this.m_timestamp = 0;
      this.m_allParticleFlags = 0;
      this.m_allGroupFlags = 0;
      this.m_density = 1.0F;
      this.m_inverseDensity = 1.0F;
      this.m_gravityScale = 1.0F;
      this.m_particleDiameter = 1.0F;
      this.m_inverseDiameter = 1.0F;
      this.m_squaredDiameter = 1.0F;
      this.m_count = 0;
      this.m_internalAllocatedCapacity = 0;
      this.m_maxCount = 0;
      this.m_proxyCount = 0;
      this.m_proxyCapacity = 0;
      this.m_contactCount = 0;
      this.m_contactCapacity = 0;
      this.m_bodyContactCount = 0;
      this.m_bodyContactCapacity = 0;
      this.m_pairCount = 0;
      this.m_pairCapacity = 0;
      this.m_triadCount = 0;
      this.m_triadCapacity = 0;
      this.m_groupCount = 0;
      this.m_pressureStrength = 0.05F;
      this.m_dampingStrength = 1.0F;
      this.m_elasticStrength = 0.25F;
      this.m_springStrength = 0.25F;
      this.m_viscousStrength = 0.25F;
      this.m_surfaceTensionStrengthA = 0.1F;
      this.m_surfaceTensionStrengthB = 0.2F;
      this.m_powderStrength = 0.5F;
      this.m_ejectionStrength = 0.5F;
      this.m_colorMixingStrength = 0.5F;
      this.m_flagsBuffer = new ParticleSystem.ParticleBufferInt();
      this.m_positionBuffer = new ParticleSystem.ParticleBuffer<>(Vec2.class);
      this.m_velocityBuffer = new ParticleSystem.ParticleBuffer<>(Vec2.class);
      this.m_colorBuffer = new ParticleSystem.ParticleBuffer<>(ParticleColor.class);
      this.m_userDataBuffer = new ParticleSystem.ParticleBuffer<>(Object.class);
   }

   public int createParticle(ParticleDef def) {
      if (this.m_count >= this.m_internalAllocatedCapacity) {
         int capacity = this.m_count != 0 ? 2 * this.m_count : 256;
         capacity = limitCapacity(capacity, this.m_maxCount);
         capacity = limitCapacity(capacity, this.m_flagsBuffer.userSuppliedCapacity);
         capacity = limitCapacity(capacity, this.m_positionBuffer.userSuppliedCapacity);
         capacity = limitCapacity(capacity, this.m_velocityBuffer.userSuppliedCapacity);
         capacity = limitCapacity(capacity, this.m_colorBuffer.userSuppliedCapacity);
         capacity = limitCapacity(capacity, this.m_userDataBuffer.userSuppliedCapacity);
         if (this.m_internalAllocatedCapacity < capacity) {
            this.m_flagsBuffer.data = reallocateBuffer(this.m_flagsBuffer, this.m_internalAllocatedCapacity, capacity, false);
            this.m_positionBuffer.data = reallocateBuffer(this.m_positionBuffer, this.m_internalAllocatedCapacity, capacity, false);
            this.m_velocityBuffer.data = reallocateBuffer(this.m_velocityBuffer, this.m_internalAllocatedCapacity, capacity, false);
            this.m_accumulationBuffer = BufferUtils.reallocateBuffer(this.m_accumulationBuffer, 0, this.m_internalAllocatedCapacity, capacity, false);
            this.m_accumulation2Buffer = BufferUtils.reallocateBuffer(
               Vec2.class, this.m_accumulation2Buffer, 0, this.m_internalAllocatedCapacity, capacity, true
            );
            this.m_depthBuffer = BufferUtils.reallocateBuffer(this.m_depthBuffer, 0, this.m_internalAllocatedCapacity, capacity, true);
            this.m_colorBuffer.data = reallocateBuffer(this.m_colorBuffer, this.m_internalAllocatedCapacity, capacity, true);
            this.m_groupBuffer = BufferUtils.reallocateBuffer(ParticleGroup.class, this.m_groupBuffer, 0, this.m_internalAllocatedCapacity, capacity, false);
            this.m_userDataBuffer.data = reallocateBuffer(this.m_userDataBuffer, this.m_internalAllocatedCapacity, capacity, true);
            this.m_internalAllocatedCapacity = capacity;
         }
      }

      if (this.m_count >= this.m_internalAllocatedCapacity) {
         return -1;
      } else {
         int index = this.m_count++;
         this.m_flagsBuffer.data[index] = def.flags;
         this.m_positionBuffer.data[index].set(def.position);
         this.m_velocityBuffer.data[index].set(def.velocity);
         this.m_groupBuffer[index] = null;
         if (this.m_depthBuffer != null) {
            this.m_depthBuffer[index] = 0.0F;
         }

         if (this.m_colorBuffer.data != null || def.color != null) {
            this.m_colorBuffer.data = this.requestParticleBuffer(this.m_colorBuffer.dataClass, this.m_colorBuffer.data);
            this.m_colorBuffer.data[index].set(def.color);
         }

         if (this.m_userDataBuffer.data != null || def.userData != null) {
            this.m_userDataBuffer.data = this.requestParticleBuffer(this.m_userDataBuffer.dataClass, this.m_userDataBuffer.data);
            this.m_userDataBuffer.data[index] = def.userData;
         }

         if (this.m_proxyCount >= this.m_proxyCapacity) {
            int oldCapacity = this.m_proxyCapacity;
            int newCapacity = this.m_proxyCount != 0 ? 2 * this.m_proxyCount : 256;
            this.m_proxyBuffer = BufferUtils.reallocateBuffer(ParticleSystem.Proxy.class, this.m_proxyBuffer, oldCapacity, newCapacity);
            this.m_proxyCapacity = newCapacity;
         }

         this.m_proxyBuffer[this.m_proxyCount++].index = index;
         return index;
      }
   }

   public void destroyParticle(int index, boolean callDestructionListener) {
      int flags = 2;
      if (callDestructionListener) {
         flags |= 512;
      }

      this.m_flagsBuffer.data[index] = this.m_flagsBuffer.data[index] | flags;
   }

   public int destroyParticlesInShape(Shape shape, Transform xf, boolean callDestructionListener) {
      this.dpcallback.init(this, shape, xf, callDestructionListener);
      shape.computeAABB(this.temp, xf, 0);
      this.m_world.queryAABB(this.dpcallback, this.temp);
      return this.dpcallback.destroyed;
   }

   public void destroyParticlesInGroup(ParticleGroup group, boolean callDestructionListener) {
      for (int i = group.m_firstIndex; i < group.m_lastIndex; i++) {
         this.destroyParticle(i, callDestructionListener);
      }
   }

   public ParticleGroup createParticleGroup(ParticleGroupDef groupDef) {
      float stride = this.getParticleStride();
      Transform identity = this.tempTransform;
      identity.setIdentity();
      Transform transform = this.tempTransform2;
      transform.setIdentity();
      int firstIndex = this.m_count;
      if (groupDef.shape != null) {
         ParticleDef particleDef = this.tempParticleDef;
         particleDef.flags = groupDef.flags;
         particleDef.color = groupDef.color;
         particleDef.userData = groupDef.userData;
         Shape shape = groupDef.shape;
         transform.set(groupDef.position, groupDef.angle);
         AABB aabb = this.temp;
         int childCount = shape.getChildCount();

         for (int childIndex = 0; childIndex < childCount; childIndex++) {
            if (childIndex == 0) {
               shape.computeAABB(aabb, identity, childIndex);
            } else {
               AABB childAABB = this.temp2;
               shape.computeAABB(childAABB, identity, childIndex);
               aabb.combine(childAABB);
            }
         }

         float upperBoundY = aabb.upperBound.y;
         float upperBoundX = aabb.upperBound.x;

         for (float y = (float)MathUtils.floor(aabb.lowerBound.y / stride) * stride; y < upperBoundY; y += stride) {
            for (float x = (float)MathUtils.floor(aabb.lowerBound.x / stride) * stride; x < upperBoundX; x += stride) {
               Vec2 p = this.tempVec;
               p.x = x;
               p.y = y;
               if (shape.testPoint(identity, p)) {
                  Transform.mulToOut(transform, p, p);
                  particleDef.position.x = p.x;
                  particleDef.position.y = p.y;
                  p.subLocal(groupDef.position);
                  Vec2.crossToOutUnsafe(groupDef.angularVelocity, p, particleDef.velocity);
                  particleDef.velocity.addLocal(groupDef.linearVelocity);
                  this.createParticle(particleDef);
               }
            }
         }
      }

      int lastIndex = this.m_count;
      ParticleGroup group = new ParticleGroup();
      group.m_system = this;
      group.m_firstIndex = firstIndex;
      group.m_lastIndex = lastIndex;
      group.m_groupFlags = groupDef.groupFlags;
      group.m_strength = groupDef.strength;
      group.m_userData = groupDef.userData;
      group.m_transform.set(transform);
      group.m_destroyAutomatically = groupDef.destroyAutomatically;
      group.m_prev = null;
      group.m_next = this.m_groupList;
      if (this.m_groupList != null) {
         this.m_groupList.m_prev = group;
      }

      this.m_groupList = group;
      this.m_groupCount++;

      for (int i = firstIndex; i < lastIndex; i++) {
         this.m_groupBuffer[i] = group;
      }

      this.updateContacts(true);
      if ((groupDef.flags & 8) != 0) {
         for (int k = 0; k < this.m_contactCount; k++) {
            ParticleContact contact = this.m_contactBuffer[k];
            int a = contact.indexA;
            int b = contact.indexB;
            if (a > b) {
               int temp = a;
               a = b;
               b = temp;
            }

            if (firstIndex <= a && b < lastIndex) {
               if (this.m_pairCount >= this.m_pairCapacity) {
                  int oldCapacity = this.m_pairCapacity;
                  int newCapacity = this.m_pairCount != 0 ? 2 * this.m_pairCount : 256;
                  this.m_pairBuffer = BufferUtils.reallocateBuffer(ParticleSystem.Pair.class, this.m_pairBuffer, oldCapacity, newCapacity);
                  this.m_pairCapacity = newCapacity;
               }

               ParticleSystem.Pair pair = this.m_pairBuffer[this.m_pairCount];
               pair.indexA = a;
               pair.indexB = b;
               pair.flags = contact.flags;
               pair.strength = groupDef.strength;
               pair.distance = MathUtils.distance(this.m_positionBuffer.data[a], this.m_positionBuffer.data[b]);
               this.m_pairCount++;
            }
         }
      }

      if ((groupDef.flags & 16) != 0) {
         VoronoiDiagram diagram = new VoronoiDiagram(lastIndex - firstIndex);

         for (int i = firstIndex; i < lastIndex; i++) {
            diagram.addGenerator(this.m_positionBuffer.data[i], i);
         }

         diagram.generate(stride / 2.0F);
         this.createParticleGroupCallback.system = this;
         this.createParticleGroupCallback.def = groupDef;
         this.createParticleGroupCallback.firstIndex = firstIndex;
         diagram.getNodes(this.createParticleGroupCallback);
      }

      if ((groupDef.groupFlags & 1) != 0) {
         this.computeDepthForGroup(group);
      }

      return group;
   }

   public void joinParticleGroups(ParticleGroup groupA, ParticleGroup groupB) {
      assert groupA != groupB;

      this.RotateBuffer(groupB.m_firstIndex, groupB.m_lastIndex, this.m_count);

      assert groupB.m_lastIndex == this.m_count;

      this.RotateBuffer(groupA.m_firstIndex, groupA.m_lastIndex, groupB.m_firstIndex);

      assert groupA.m_lastIndex == groupB.m_firstIndex;

      int particleFlags = 0;

      for (int i = groupA.m_firstIndex; i < groupB.m_lastIndex; i++) {
         particleFlags |= this.m_flagsBuffer.data[i];
      }

      this.updateContacts(true);
      if ((particleFlags & 8) != 0) {
         for (int k = 0; k < this.m_contactCount; k++) {
            ParticleContact contact = this.m_contactBuffer[k];
            int a = contact.indexA;
            int b = contact.indexB;
            if (a > b) {
               int temp = a;
               a = b;
               b = temp;
            }

            if (groupA.m_firstIndex <= a && a < groupA.m_lastIndex && groupB.m_firstIndex <= b && b < groupB.m_lastIndex) {
               if (this.m_pairCount >= this.m_pairCapacity) {
                  int oldCapacity = this.m_pairCapacity;
                  int newCapacity = this.m_pairCount != 0 ? 2 * this.m_pairCount : 256;
                  this.m_pairBuffer = BufferUtils.reallocateBuffer(ParticleSystem.Pair.class, this.m_pairBuffer, oldCapacity, newCapacity);
                  this.m_pairCapacity = newCapacity;
               }

               ParticleSystem.Pair pair = this.m_pairBuffer[this.m_pairCount];
               pair.indexA = a;
               pair.indexB = b;
               pair.flags = contact.flags;
               pair.strength = MathUtils.min(groupA.m_strength, groupB.m_strength);
               pair.distance = MathUtils.distance(this.m_positionBuffer.data[a], this.m_positionBuffer.data[b]);
               this.m_pairCount++;
            }
         }
      }

      if ((particleFlags & 16) != 0) {
         VoronoiDiagram diagram = new VoronoiDiagram(groupB.m_lastIndex - groupA.m_firstIndex);

         for (int i = groupA.m_firstIndex; i < groupB.m_lastIndex; i++) {
            if ((this.m_flagsBuffer.data[i] & 2) == 0) {
               diagram.addGenerator(this.m_positionBuffer.data[i], i);
            }
         }

         diagram.generate(this.getParticleStride() / 2.0F);
         ParticleSystem.JoinParticleGroupsCallback callback = new ParticleSystem.JoinParticleGroupsCallback();
         callback.system = this;
         callback.groupA = groupA;
         callback.groupB = groupB;
         diagram.getNodes(callback);
      }

      for (int ix = groupB.m_firstIndex; ix < groupB.m_lastIndex; ix++) {
         this.m_groupBuffer[ix] = groupA;
      }

      int groupFlags = groupA.m_groupFlags | groupB.m_groupFlags;
      groupA.m_groupFlags = groupFlags;
      groupA.m_lastIndex = groupB.m_lastIndex;
      groupB.m_firstIndex = groupB.m_lastIndex;
      this.destroyParticleGroup(groupB);
      if ((groupFlags & 1) != 0) {
         this.computeDepthForGroup(groupA);
      }
   }

   void destroyParticleGroup(ParticleGroup group) {
      assert this.m_groupCount > 0;

      assert group != null;

      if (this.m_world.getParticleDestructionListener() != null) {
         this.m_world.getParticleDestructionListener().sayGoodbye(group);
      }

      for (int i = group.m_firstIndex; i < group.m_lastIndex; i++) {
         this.m_groupBuffer[i] = null;
      }

      if (group.m_prev != null) {
         group.m_prev.m_next = group.m_next;
      }

      if (group.m_next != null) {
         group.m_next.m_prev = group.m_prev;
      }

      if (group == this.m_groupList) {
         this.m_groupList = group.m_next;
      }

      this.m_groupCount--;
   }

   public void computeDepthForGroup(ParticleGroup group) {
      for (int i = group.m_firstIndex; i < group.m_lastIndex; i++) {
         this.m_accumulationBuffer[i] = 0.0F;
      }

      for (int k = 0; k < this.m_contactCount; k++) {
         ParticleContact contact = this.m_contactBuffer[k];
         int a = contact.indexA;
         int b = contact.indexB;
         if (a >= group.m_firstIndex && a < group.m_lastIndex && b >= group.m_firstIndex && b < group.m_lastIndex) {
            float w = contact.weight;
            this.m_accumulationBuffer[a] = this.m_accumulationBuffer[a] + w;
            this.m_accumulationBuffer[b] = this.m_accumulationBuffer[b] + w;
         }
      }

      this.m_depthBuffer = this.requestParticleBuffer(this.m_depthBuffer);

      for (int i = group.m_firstIndex; i < group.m_lastIndex; i++) {
         float w = this.m_accumulationBuffer[i];
         this.m_depthBuffer[i] = w < 0.8F ? 0.0F : Float.MAX_VALUE;
      }

      int interationCount = group.getParticleCount();

      for (int t = 0; t < interationCount; t++) {
         boolean updated = false;

         for (int kx = 0; kx < this.m_contactCount; kx++) {
            ParticleContact contact = this.m_contactBuffer[kx];
            int a = contact.indexA;
            int b = contact.indexB;
            if (a >= group.m_firstIndex && a < group.m_lastIndex && b >= group.m_firstIndex && b < group.m_lastIndex) {
               float r = 1.0F - contact.weight;
               float ap0 = this.m_depthBuffer[a];
               float bp0 = this.m_depthBuffer[b];
               float ap1 = bp0 + r;
               float bp1 = ap0 + r;
               if (ap0 > ap1) {
                  this.m_depthBuffer[a] = ap1;
                  updated = true;
               }

               if (bp0 > bp1) {
                  this.m_depthBuffer[b] = bp1;
                  updated = true;
               }
            }
         }

         if (!updated) {
            break;
         }
      }

      for (int i = group.m_firstIndex; i < group.m_lastIndex; i++) {
         float p = this.m_depthBuffer[i];
         if (p < Float.MAX_VALUE) {
            this.m_depthBuffer[i] = this.m_depthBuffer[i] * this.m_particleDiameter;
         } else {
            this.m_depthBuffer[i] = 0.0F;
         }
      }
   }

   public void addContact(int a, int b) {
      assert a != b;

      Vec2 pa = this.m_positionBuffer.data[a];
      Vec2 pb = this.m_positionBuffer.data[b];
      float dx = pb.x - pa.x;
      float dy = pb.y - pa.y;
      float d2 = dx * dx + dy * dy;
      if (d2 < this.m_squaredDiameter) {
         if (this.m_contactCount >= this.m_contactCapacity) {
            int oldCapacity = this.m_contactCapacity;
            int newCapacity = this.m_contactCount != 0 ? 2 * this.m_contactCount : 256;
            this.m_contactBuffer = BufferUtils.reallocateBuffer(ParticleContact.class, this.m_contactBuffer, oldCapacity, newCapacity);
            this.m_contactCapacity = newCapacity;
         }

         float invD = d2 != 0.0F ? MathUtils.sqrt(1.0F / d2) : Float.MAX_VALUE;
         ParticleContact contact = this.m_contactBuffer[this.m_contactCount];
         contact.indexA = a;
         contact.indexB = b;
         contact.flags = this.m_flagsBuffer.data[a] | this.m_flagsBuffer.data[b];
         contact.weight = 1.0F - d2 * invD * this.m_inverseDiameter;
         contact.normal.x = invD * dx;
         contact.normal.y = invD * dy;
         this.m_contactCount++;
      }
   }

   public void updateContacts(boolean exceptZombie) {
      for (int p = 0; p < this.m_proxyCount; p++) {
         ParticleSystem.Proxy proxy = this.m_proxyBuffer[p];
         int i = proxy.index;
         Vec2 pos = this.m_positionBuffer.data[i];
         proxy.tag = computeTag(this.m_inverseDiameter * pos.x, this.m_inverseDiameter * pos.y);
      }

      Arrays.sort(this.m_proxyBuffer, 0, this.m_proxyCount);
      this.m_contactCount = 0;
      int c_index = 0;

      for (int i = 0; i < this.m_proxyCount; i++) {
         ParticleSystem.Proxy a = this.m_proxyBuffer[i];
         long rightTag = computeRelativeTag(a.tag, 1, 0);

         for (int j = i + 1; j < this.m_proxyCount; j++) {
            ParticleSystem.Proxy b = this.m_proxyBuffer[j];
            if (rightTag < b.tag) {
               break;
            }

            this.addContact(a.index, b.index);
         }

         for (long bottomLeftTag = computeRelativeTag(a.tag, -1, 1); c_index < this.m_proxyCount; c_index++) {
            ParticleSystem.Proxy c = this.m_proxyBuffer[c_index];
            if (bottomLeftTag <= c.tag) {
               break;
            }
         }

         long bottomRightTag = computeRelativeTag(a.tag, 1, 1);

         for (int b_index = c_index; b_index < this.m_proxyCount; b_index++) {
            ParticleSystem.Proxy b = this.m_proxyBuffer[b_index];
            if (bottomRightTag < b.tag) {
               break;
            }

            this.addContact(a.index, b.index);
         }
      }

      if (exceptZombie) {
         int j = this.m_contactCount;

         for (int i = 0; i < j; i++) {
            if ((this.m_contactBuffer[i].flags & 2) != 0) {
               ParticleContact temp = this.m_contactBuffer[--j];
               this.m_contactBuffer[j] = this.m_contactBuffer[i];
               this.m_contactBuffer[i] = temp;
               i--;
            }
         }

         this.m_contactCount = j;
      }
   }

   public void updateBodyContacts() {
      AABB aabb = this.temp;
      aabb.lowerBound.x = Float.MAX_VALUE;
      aabb.lowerBound.y = Float.MAX_VALUE;
      aabb.upperBound.x = -Float.MAX_VALUE;
      aabb.upperBound.y = -Float.MAX_VALUE;

      for (int i = 0; i < this.m_count; i++) {
         Vec2 p = this.m_positionBuffer.data[i];
         Vec2.minToOut(aabb.lowerBound, p, aabb.lowerBound);
         Vec2.maxToOut(aabb.upperBound, p, aabb.upperBound);
      }

      aabb.lowerBound.x = aabb.lowerBound.x - this.m_particleDiameter;
      aabb.lowerBound.y = aabb.lowerBound.y - this.m_particleDiameter;
      aabb.upperBound.x = aabb.upperBound.x + this.m_particleDiameter;
      aabb.upperBound.y = aabb.upperBound.y + this.m_particleDiameter;
      this.m_bodyContactCount = 0;
      this.ubccallback.system = this;
      this.m_world.queryAABB(this.ubccallback, aabb);
   }

   public void solveCollision(TimeStep step) {
      AABB aabb = this.temp;
      Vec2 lowerBound = aabb.lowerBound;
      Vec2 upperBound = aabb.upperBound;
      lowerBound.x = Float.MAX_VALUE;
      lowerBound.y = Float.MAX_VALUE;
      upperBound.x = -Float.MAX_VALUE;
      upperBound.y = -Float.MAX_VALUE;

      for (int i = 0; i < this.m_count; i++) {
         Vec2 v = this.m_velocityBuffer.data[i];
         Vec2 p1 = this.m_positionBuffer.data[i];
         float p1x = p1.x;
         float p1y = p1.y;
         float p2x = p1x + step.dt * v.x;
         float p2y = p1y + step.dt * v.y;
         float bx = p1x < p2x ? p1x : p2x;
         float by = p1y < p2y ? p1y : p2y;
         lowerBound.x = lowerBound.x < bx ? lowerBound.x : bx;
         lowerBound.y = lowerBound.y < by ? lowerBound.y : by;
         float b1x = p1x > p2x ? p1x : p2x;
         float b1y = p1y > p2y ? p1y : p2y;
         upperBound.x = upperBound.x > b1x ? upperBound.x : b1x;
         upperBound.y = upperBound.y > b1y ? upperBound.y : b1y;
      }

      this.sccallback.step = step;
      this.sccallback.system = this;
      this.m_world.queryAABB(this.sccallback, aabb);
   }

   public void solve(TimeStep step) {
      this.m_timestamp++;
      if (this.m_count != 0) {
         this.m_allParticleFlags = 0;

         for (int i = 0; i < this.m_count; i++) {
            this.m_allParticleFlags = this.m_allParticleFlags | this.m_flagsBuffer.data[i];
         }

         if ((this.m_allParticleFlags & 2) != 0) {
            this.solveZombie();
         }

         if (this.m_count != 0) {
            this.m_allGroupFlags = 0;

            for (ParticleGroup group = this.m_groupList; group != null; group = group.getNext()) {
               this.m_allGroupFlags = this.m_allGroupFlags | group.m_groupFlags;
            }

            float gravityx = step.dt * this.m_gravityScale * this.m_world.getGravity().x;
            float gravityy = step.dt * this.m_gravityScale * this.m_world.getGravity().y;
            float criticalVelocytySquared = this.getCriticalVelocitySquared(step);

            for (int i = 0; i < this.m_count; i++) {
               Vec2 v = this.m_velocityBuffer.data[i];
               v.x += gravityx;
               v.y += gravityy;
               float v2 = v.x * v.x + v.y * v.y;
               if (v2 > criticalVelocytySquared) {
                  float a = v2 == 0.0F ? Float.MAX_VALUE : MathUtils.sqrt(criticalVelocytySquared / v2);
                  v.x *= a;
                  v.y *= a;
               }
            }

            this.solveCollision(step);
            if ((this.m_allGroupFlags & 2) != 0) {
               this.solveRigid(step);
            }

            if ((this.m_allParticleFlags & 4) != 0) {
               this.solveWall(step);
            }

            for (int ix = 0; ix < this.m_count; ix++) {
               Vec2 pos = this.m_positionBuffer.data[ix];
               Vec2 vel = this.m_velocityBuffer.data[ix];
               pos.x = pos.x + step.dt * vel.x;
               pos.y = pos.y + step.dt * vel.y;
            }

            this.updateBodyContacts();
            this.updateContacts(false);
            if ((this.m_allParticleFlags & 32) != 0) {
               this.solveViscous(step);
            }

            if ((this.m_allParticleFlags & 64) != 0) {
               this.solvePowder(step);
            }

            if ((this.m_allParticleFlags & 128) != 0) {
               this.solveTensile(step);
            }

            if ((this.m_allParticleFlags & 16) != 0) {
               this.solveElastic(step);
            }

            if ((this.m_allParticleFlags & 8) != 0) {
               this.solveSpring(step);
            }

            if ((this.m_allGroupFlags & 1) != 0) {
               this.solveSolid(step);
            }

            if ((this.m_allParticleFlags & 256) != 0) {
               this.solveColorMixing(step);
            }

            this.solvePressure(step);
            this.solveDamping(step);
         }
      }
   }

   void solvePressure(TimeStep step) {
      for (int i = 0; i < this.m_count; i++) {
         this.m_accumulationBuffer[i] = 0.0F;
      }

      for (int k = 0; k < this.m_bodyContactCount; k++) {
         ParticleBodyContact contact = this.m_bodyContactBuffer[k];
         int a = contact.index;
         float w = contact.weight;
         this.m_accumulationBuffer[a] = this.m_accumulationBuffer[a] + w;
      }

      for (int k = 0; k < this.m_contactCount; k++) {
         ParticleContact contact = this.m_contactBuffer[k];
         int a = contact.indexA;
         int b = contact.indexB;
         float w = contact.weight;
         this.m_accumulationBuffer[a] = this.m_accumulationBuffer[a] + w;
         this.m_accumulationBuffer[b] = this.m_accumulationBuffer[b] + w;
      }

      if ((this.m_allParticleFlags & 64) != 0) {
         for (int i = 0; i < this.m_count; i++) {
            if ((this.m_flagsBuffer.data[i] & 64) != 0) {
               this.m_accumulationBuffer[i] = 0.0F;
            }
         }
      }

      float pressurePerWeight = this.m_pressureStrength * this.getCriticalPressure(step);

      for (int ix = 0; ix < this.m_count; ix++) {
         float w = this.m_accumulationBuffer[ix];
         float h = pressurePerWeight * MathUtils.max(0.0F, MathUtils.min(w, 5.0F) - 1.0F);
         this.m_accumulationBuffer[ix] = h;
      }

      float velocityPerPressure = step.dt / (this.m_density * this.m_particleDiameter);

      for (int k = 0; k < this.m_bodyContactCount; k++) {
         ParticleBodyContact contact = this.m_bodyContactBuffer[k];
         int a = contact.index;
         Body b = contact.body;
         float w = contact.weight;
         float m = contact.mass;
         Vec2 n = contact.normal;
         Vec2 p = this.m_positionBuffer.data[a];
         float h = this.m_accumulationBuffer[a] + pressurePerWeight * w;
         Vec2 f = this.tempVec;
         float coef = velocityPerPressure * w * m * h;
         f.x = coef * n.x;
         f.y = coef * n.y;
         Vec2 velData = this.m_velocityBuffer.data[a];
         float particleInvMass = this.getParticleInvMass();
         velData.x = velData.x - particleInvMass * f.x;
         velData.y = velData.y - particleInvMass * f.y;
         b.applyLinearImpulse(f, p, true);
      }

      for (int k = 0; k < this.m_contactCount; k++) {
         ParticleContact contact = this.m_contactBuffer[k];
         int a = contact.indexA;
         int b = contact.indexB;
         float w = contact.weight;
         Vec2 n = contact.normal;
         float h = this.m_accumulationBuffer[a] + this.m_accumulationBuffer[b];
         float fx = velocityPerPressure * w * h * n.x;
         float fy = velocityPerPressure * w * h * n.y;
         Vec2 velDataA = this.m_velocityBuffer.data[a];
         Vec2 velDataB = this.m_velocityBuffer.data[b];
         velDataA.x -= fx;
         velDataA.y -= fy;
         velDataB.x += fx;
         velDataB.y += fy;
      }
   }

   void solveDamping(TimeStep step) {
      float damping = this.m_dampingStrength;

      for (int k = 0; k < this.m_bodyContactCount; k++) {
         ParticleBodyContact contact = this.m_bodyContactBuffer[k];
         int a = contact.index;
         Body b = contact.body;
         float w = contact.weight;
         float m = contact.mass;
         Vec2 n = contact.normal;
         Vec2 p = this.m_positionBuffer.data[a];
         float tempX = p.x - b.m_sweep.c.x;
         float tempY = p.y - b.m_sweep.c.y;
         Vec2 velA = this.m_velocityBuffer.data[a];
         float vx = -b.m_angularVelocity * tempY + b.m_linearVelocity.x - velA.x;
         float vy = b.m_angularVelocity * tempX + b.m_linearVelocity.y - velA.y;
         float vn = vx * n.x + vy * n.y;
         if (vn < 0.0F) {
            Vec2 f = this.tempVec;
            f.x = damping * w * m * vn * n.x;
            f.y = damping * w * m * vn * n.y;
            float invMass = this.getParticleInvMass();
            velA.x = velA.x + invMass * f.x;
            velA.y = velA.y + invMass * f.y;
            f.x = -f.x;
            f.y = -f.y;
            b.applyLinearImpulse(f, p, true);
         }
      }

      for (int kx = 0; kx < this.m_contactCount; kx++) {
         ParticleContact contact = this.m_contactBuffer[kx];
         int a = contact.indexA;
         int b = contact.indexB;
         float w = contact.weight;
         Vec2 n = contact.normal;
         Vec2 velA = this.m_velocityBuffer.data[a];
         Vec2 velB = this.m_velocityBuffer.data[b];
         float vx = velB.x - velA.x;
         float vy = velB.y - velA.y;
         float vn = vx * n.x + vy * n.y;
         if (vn < 0.0F) {
            float fx = damping * w * vn * n.x;
            float fy = damping * w * vn * n.y;
            velA.x += fx;
            velA.y += fy;
            velB.x -= fx;
            velB.y -= fy;
         }
      }
   }

   public void solveWall(TimeStep step) {
      for (int i = 0; i < this.m_count; i++) {
         if ((this.m_flagsBuffer.data[i] & 4) != 0) {
            Vec2 r = this.m_velocityBuffer.data[i];
            r.x = 0.0F;
            r.y = 0.0F;
         }
      }
   }

   void solveRigid(TimeStep step) {
      for (ParticleGroup group = this.m_groupList; group != null; group = group.getNext()) {
         if ((group.m_groupFlags & 2) != 0) {
            group.updateStatistics();
            Vec2 temp = this.tempVec;
            Vec2 cross = this.tempVec2;
            Rot rotation = this.tempRot;
            rotation.set(step.dt * group.m_angularVelocity);
            Rot.mulToOutUnsafe(rotation, group.m_center, cross);
            temp.set(group.m_linearVelocity).mulLocal(step.dt).addLocal(group.m_center).subLocal(cross);
            this.tempXf.p.set(temp);
            this.tempXf.q.set(rotation);
            Transform.mulToOut(this.tempXf, group.m_transform, group.m_transform);
            Transform velocityTransform = this.tempXf2;
            velocityTransform.p.x = step.inv_dt * this.tempXf.p.x;
            velocityTransform.p.y = step.inv_dt * this.tempXf.p.y;
            velocityTransform.q.s = step.inv_dt * this.tempXf.q.s;
            velocityTransform.q.c = step.inv_dt * (this.tempXf.q.c - 1.0F);

            for (int i = group.m_firstIndex; i < group.m_lastIndex; i++) {
               Transform.mulToOutUnsafe(velocityTransform, this.m_positionBuffer.data[i], this.m_velocityBuffer.data[i]);
            }
         }
      }
   }

   void solveElastic(TimeStep step) {
      float elasticStrength = step.inv_dt * this.m_elasticStrength;

      for (int k = 0; k < this.m_triadCount; k++) {
         ParticleSystem.Triad triad = this.m_triadBuffer[k];
         if ((triad.flags & 16) != 0) {
            int a = triad.indexA;
            int b = triad.indexB;
            int c = triad.indexC;
            Vec2 oa = triad.pa;
            Vec2 ob = triad.pb;
            Vec2 oc = triad.pc;
            Vec2 pa = this.m_positionBuffer.data[a];
            Vec2 pb = this.m_positionBuffer.data[b];
            Vec2 pc = this.m_positionBuffer.data[c];
            float px = 0.33333334F * (pa.x + pb.x + pc.x);
            float py = 0.33333334F * (pa.y + pb.y + pc.y);
            float rs = Vec2.cross(oa, pa) + Vec2.cross(ob, pb) + Vec2.cross(oc, pc);
            float rc = Vec2.dot(oa, pa) + Vec2.dot(ob, pb) + Vec2.dot(oc, pc);
            float r2 = rs * rs + rc * rc;
            float invR = r2 == 0.0F ? Float.MAX_VALUE : MathUtils.sqrt(1.0F / r2);
            rs *= invR;
            rc *= invR;
            float strength = elasticStrength * triad.strength;
            float roax = rc * oa.x - rs * oa.y;
            float roay = rs * oa.x + rc * oa.y;
            float robx = rc * ob.x - rs * ob.y;
            float roby = rs * ob.x + rc * ob.y;
            float rocx = rc * oc.x - rs * oc.y;
            float rocy = rs * oc.x + rc * oc.y;
            Vec2 va = this.m_velocityBuffer.data[a];
            Vec2 vb = this.m_velocityBuffer.data[b];
            Vec2 vc = this.m_velocityBuffer.data[c];
            va.x = va.x + strength * (roax - (pa.x - px));
            va.y = va.y + strength * (roay - (pa.y - py));
            vb.x = vb.x + strength * (robx - (pb.x - px));
            vb.y = vb.y + strength * (roby - (pb.y - py));
            vc.x = vc.x + strength * (rocx - (pc.x - px));
            vc.y = vc.y + strength * (rocy - (pc.y - py));
         }
      }
   }

   void solveSpring(TimeStep step) {
      float springStrength = step.inv_dt * this.m_springStrength;

      for (int k = 0; k < this.m_pairCount; k++) {
         ParticleSystem.Pair pair = this.m_pairBuffer[k];
         if ((pair.flags & 8) != 0) {
            int a = pair.indexA;
            int b = pair.indexB;
            Vec2 pa = this.m_positionBuffer.data[a];
            Vec2 pb = this.m_positionBuffer.data[b];
            float dx = pb.x - pa.x;
            float dy = pb.y - pa.y;
            float r0 = pair.distance;
            float r1 = MathUtils.sqrt(dx * dx + dy * dy);
            if (r1 == 0.0F) {
               r1 = Float.MAX_VALUE;
            }

            float strength = springStrength * pair.strength;
            float fx = strength * (r0 - r1) / r1 * dx;
            float fy = strength * (r0 - r1) / r1 * dy;
            Vec2 va = this.m_velocityBuffer.data[a];
            Vec2 vb = this.m_velocityBuffer.data[b];
            va.x -= fx;
            va.y -= fy;
            vb.x += fx;
            vb.y += fy;
         }
      }
   }

   void solveTensile(TimeStep step) {
      this.m_accumulation2Buffer = this.requestParticleBuffer(Vec2.class, this.m_accumulation2Buffer);

      for (int i = 0; i < this.m_count; i++) {
         this.m_accumulationBuffer[i] = 0.0F;
         this.m_accumulation2Buffer[i].setZero();
      }

      for (int k = 0; k < this.m_contactCount; k++) {
         ParticleContact contact = this.m_contactBuffer[k];
         if ((contact.flags & 128) != 0) {
            int a = contact.indexA;
            int b = contact.indexB;
            float w = contact.weight;
            Vec2 n = contact.normal;
            this.m_accumulationBuffer[a] = this.m_accumulationBuffer[a] + w;
            this.m_accumulationBuffer[b] = this.m_accumulationBuffer[b] + w;
            Vec2 a2A = this.m_accumulation2Buffer[a];
            Vec2 a2B = this.m_accumulation2Buffer[b];
            float inter = (1.0F - w) * w;
            a2A.x = a2A.x - inter * n.x;
            a2A.y = a2A.y - inter * n.y;
            a2B.x = a2B.x + inter * n.x;
            a2B.y = a2B.y + inter * n.y;
         }
      }

      float strengthA = this.m_surfaceTensionStrengthA * this.getCriticalVelocity(step);
      float strengthB = this.m_surfaceTensionStrengthB * this.getCriticalVelocity(step);

      for (int kx = 0; kx < this.m_contactCount; kx++) {
         ParticleContact contact = this.m_contactBuffer[kx];
         if ((contact.flags & 128) != 0) {
            int a = contact.indexA;
            int b = contact.indexB;
            float w = contact.weight;
            Vec2 n = contact.normal;
            Vec2 a2A = this.m_accumulation2Buffer[a];
            Vec2 a2B = this.m_accumulation2Buffer[b];
            float h = this.m_accumulationBuffer[a] + this.m_accumulationBuffer[b];
            float sx = a2B.x - a2A.x;
            float sy = a2B.y - a2A.y;
            float fn = (strengthA * (h - 2.0F) + strengthB * (sx * n.x + sy * n.y)) * w;
            float fx = fn * n.x;
            float fy = fn * n.y;
            Vec2 va = this.m_velocityBuffer.data[a];
            Vec2 vb = this.m_velocityBuffer.data[b];
            va.x -= fx;
            va.y -= fy;
            vb.x += fx;
            vb.y += fy;
         }
      }
   }

   void solveViscous(TimeStep step) {
      float viscousStrength = this.m_viscousStrength;

      for (int k = 0; k < this.m_bodyContactCount; k++) {
         ParticleBodyContact contact = this.m_bodyContactBuffer[k];
         int a = contact.index;
         if ((this.m_flagsBuffer.data[a] & 32) != 0) {
            Body b = contact.body;
            float w = contact.weight;
            float m = contact.mass;
            Vec2 p = this.m_positionBuffer.data[a];
            Vec2 va = this.m_velocityBuffer.data[a];
            float tempX = p.x - b.m_sweep.c.x;
            float tempY = p.y - b.m_sweep.c.y;
            float vx = -b.m_angularVelocity * tempY + b.m_linearVelocity.x - va.x;
            float vy = b.m_angularVelocity * tempX + b.m_linearVelocity.y - va.y;
            Vec2 f = this.tempVec;
            float pInvMass = this.getParticleInvMass();
            f.x = viscousStrength * m * w * vx;
            f.y = viscousStrength * m * w * vy;
            va.x = va.x + pInvMass * f.x;
            va.y = va.y + pInvMass * f.y;
            f.x = -f.x;
            f.y = -f.y;
            b.applyLinearImpulse(f, p, true);
         }
      }

      for (int kx = 0; kx < this.m_contactCount; kx++) {
         ParticleContact contact = this.m_contactBuffer[kx];
         if ((contact.flags & 32) != 0) {
            int a = contact.indexA;
            int b = contact.indexB;
            float w = contact.weight;
            Vec2 va = this.m_velocityBuffer.data[a];
            Vec2 vb = this.m_velocityBuffer.data[b];
            float vx = vb.x - va.x;
            float vy = vb.y - va.y;
            float fx = viscousStrength * w * vx;
            float fy = viscousStrength * w * vy;
            va.x += fx;
            va.y += fy;
            vb.x -= fx;
            vb.y -= fy;
         }
      }
   }

   void solvePowder(TimeStep step) {
      float powderStrength = this.m_powderStrength * this.getCriticalVelocity(step);
      float minWeight = 0.25F;

      for (int k = 0; k < this.m_bodyContactCount; k++) {
         ParticleBodyContact contact = this.m_bodyContactBuffer[k];
         int a = contact.index;
         if ((this.m_flagsBuffer.data[a] & 64) != 0) {
            float w = contact.weight;
            if (w > minWeight) {
               Body b = contact.body;
               float m = contact.mass;
               Vec2 p = this.m_positionBuffer.data[a];
               Vec2 n = contact.normal;
               Vec2 f = this.tempVec;
               Vec2 va = this.m_velocityBuffer.data[a];
               float inter = powderStrength * m * (w - minWeight);
               float pInvMass = this.getParticleInvMass();
               f.x = inter * n.x;
               f.y = inter * n.y;
               va.x = va.x - pInvMass * f.x;
               va.y = va.y - pInvMass * f.y;
               b.applyLinearImpulse(f, p, true);
            }
         }
      }

      for (int kx = 0; kx < this.m_contactCount; kx++) {
         ParticleContact contact = this.m_contactBuffer[kx];
         if ((contact.flags & 64) != 0) {
            float w = contact.weight;
            if (w > minWeight) {
               int a = contact.indexA;
               int b = contact.indexB;
               Vec2 n = contact.normal;
               Vec2 va = this.m_velocityBuffer.data[a];
               Vec2 vb = this.m_velocityBuffer.data[b];
               float inter = powderStrength * (w - minWeight);
               float fx = inter * n.x;
               float fy = inter * n.y;
               va.x -= fx;
               va.y -= fy;
               vb.x += fx;
               vb.y += fy;
            }
         }
      }
   }

   void solveSolid(TimeStep step) {
      this.m_depthBuffer = this.requestParticleBuffer(this.m_depthBuffer);
      float ejectionStrength = step.inv_dt * this.m_ejectionStrength;

      for (int k = 0; k < this.m_contactCount; k++) {
         ParticleContact contact = this.m_contactBuffer[k];
         int a = contact.indexA;
         int b = contact.indexB;
         if (this.m_groupBuffer[a] != this.m_groupBuffer[b]) {
            float w = contact.weight;
            Vec2 n = contact.normal;
            float h = this.m_depthBuffer[a] + this.m_depthBuffer[b];
            Vec2 va = this.m_velocityBuffer.data[a];
            Vec2 vb = this.m_velocityBuffer.data[b];
            float inter = ejectionStrength * h * w;
            float fx = inter * n.x;
            float fy = inter * n.y;
            va.x -= fx;
            va.y -= fy;
            vb.x += fx;
            vb.y += fy;
         }
      }
   }

   void solveColorMixing(TimeStep step) {
      this.m_colorBuffer.data = this.requestParticleBuffer(ParticleColor.class, this.m_colorBuffer.data);
      int colorMixing256 = (int)(256.0F * this.m_colorMixingStrength);

      for (int k = 0; k < this.m_contactCount; k++) {
         ParticleContact contact = this.m_contactBuffer[k];
         int a = contact.indexA;
         int b = contact.indexB;
         if ((this.m_flagsBuffer.data[a] & this.m_flagsBuffer.data[b] & 256) != 0) {
            ParticleColor colorA = this.m_colorBuffer.data[a];
            ParticleColor colorB = this.m_colorBuffer.data[b];
            int dr = colorMixing256 * ((colorB.r & 255) - (colorA.r & 255)) >> 8;
            int dg = colorMixing256 * ((colorB.g & 255) - (colorA.g & 255)) >> 8;
            int db = colorMixing256 * ((colorB.b & 255) - (colorA.b & 255)) >> 8;
            int da = colorMixing256 * ((colorB.a & 255) - (colorA.a & 255)) >> 8;
            colorA.r = (byte)(colorA.r + dr);
            colorA.g = (byte)(colorA.g + dg);
            colorA.b = (byte)(colorA.b + db);
            colorA.a = (byte)(colorA.a + da);
            colorB.r = (byte)(colorB.r - dr);
            colorB.g = (byte)(colorB.g - dg);
            colorB.b = (byte)(colorB.b - db);
            colorB.a = (byte)(colorB.a - da);
         }
      }
   }

   void solveZombie() {
      int newCount = 0;
      int[] newIndices = new int[this.m_count];

      for (int i = 0; i < this.m_count; i++) {
         int flags = this.m_flagsBuffer.data[i];
         if ((flags & 2) != 0) {
            ParticleDestructionListener destructionListener = this.m_world.getParticleDestructionListener();
            if ((flags & 512) != 0 && destructionListener != null) {
               destructionListener.sayGoodbye(i);
            }

            newIndices[i] = -1;
         } else {
            newIndices[i] = newCount;
            if (i != newCount) {
               this.m_flagsBuffer.data[newCount] = this.m_flagsBuffer.data[i];
               this.m_positionBuffer.data[newCount].set(this.m_positionBuffer.data[i]);
               this.m_velocityBuffer.data[newCount].set(this.m_velocityBuffer.data[i]);
               this.m_groupBuffer[newCount] = this.m_groupBuffer[i];
               if (this.m_depthBuffer != null) {
                  this.m_depthBuffer[newCount] = this.m_depthBuffer[i];
               }

               if (this.m_colorBuffer.data != null) {
                  this.m_colorBuffer.data[newCount].set(this.m_colorBuffer.data[i]);
               }

               if (this.m_userDataBuffer.data != null) {
                  this.m_userDataBuffer.data[newCount] = this.m_userDataBuffer.data[i];
               }
            }

            newCount++;
         }
      }

      for (int k = 0; k < this.m_proxyCount; k++) {
         ParticleSystem.Proxy proxy = this.m_proxyBuffer[k];
         proxy.index = newIndices[proxy.index];
      }

      int j = this.m_proxyCount;

      for (int ix = 0; ix < j; ix++) {
         if (ParticleSystem.Test.IsProxyInvalid(this.m_proxyBuffer[ix])) {
            ParticleSystem.Proxy temp = this.m_proxyBuffer[--j];
            this.m_proxyBuffer[j] = this.m_proxyBuffer[ix];
            this.m_proxyBuffer[ix] = temp;
            ix--;
         }
      }

      this.m_proxyCount = j;

      for (int k = 0; k < this.m_contactCount; k++) {
         ParticleContact contact = this.m_contactBuffer[k];
         contact.indexA = newIndices[contact.indexA];
         contact.indexB = newIndices[contact.indexB];
      }

      j = this.m_contactCount;

      for (int ixx = 0; ixx < j; ixx++) {
         if (ParticleSystem.Test.IsContactInvalid(this.m_contactBuffer[ixx])) {
            ParticleContact temp = this.m_contactBuffer[--j];
            this.m_contactBuffer[j] = this.m_contactBuffer[ixx];
            this.m_contactBuffer[ixx] = temp;
            ixx--;
         }
      }

      this.m_contactCount = j;

      for (int k = 0; k < this.m_bodyContactCount; k++) {
         ParticleBodyContact contact = this.m_bodyContactBuffer[k];
         contact.index = newIndices[contact.index];
      }

      j = this.m_bodyContactCount;

      for (int ixxx = 0; ixxx < j; ixxx++) {
         if (ParticleSystem.Test.IsBodyContactInvalid(this.m_bodyContactBuffer[ixxx])) {
            ParticleBodyContact temp = this.m_bodyContactBuffer[--j];
            this.m_bodyContactBuffer[j] = this.m_bodyContactBuffer[ixxx];
            this.m_bodyContactBuffer[ixxx] = temp;
            ixxx--;
         }
      }

      this.m_bodyContactCount = j;

      for (int k = 0; k < this.m_pairCount; k++) {
         ParticleSystem.Pair pair = this.m_pairBuffer[k];
         pair.indexA = newIndices[pair.indexA];
         pair.indexB = newIndices[pair.indexB];
      }

      j = this.m_pairCount;

      for (int ixxxx = 0; ixxxx < j; ixxxx++) {
         if (ParticleSystem.Test.IsPairInvalid(this.m_pairBuffer[ixxxx])) {
            ParticleSystem.Pair temp = this.m_pairBuffer[--j];
            this.m_pairBuffer[j] = this.m_pairBuffer[ixxxx];
            this.m_pairBuffer[ixxxx] = temp;
            ixxxx--;
         }
      }

      this.m_pairCount = j;

      for (int k = 0; k < this.m_triadCount; k++) {
         ParticleSystem.Triad triad = this.m_triadBuffer[k];
         triad.indexA = newIndices[triad.indexA];
         triad.indexB = newIndices[triad.indexB];
         triad.indexC = newIndices[triad.indexC];
      }

      j = this.m_triadCount;

      for (int ixxxxx = 0; ixxxxx < j; ixxxxx++) {
         if (ParticleSystem.Test.IsTriadInvalid(this.m_triadBuffer[ixxxxx])) {
            ParticleSystem.Triad temp = this.m_triadBuffer[--j];
            this.m_triadBuffer[j] = this.m_triadBuffer[ixxxxx];
            this.m_triadBuffer[ixxxxx] = temp;
            ixxxxx--;
         }
      }

      this.m_triadCount = j;

      for (ParticleGroup group = this.m_groupList; group != null; group = group.getNext()) {
         int firstIndex = newCount;
         int lastIndex = 0;
         boolean modified = false;

         for (int ixxxxxx = group.m_firstIndex; ixxxxxx < group.m_lastIndex; ixxxxxx++) {
            j = newIndices[ixxxxxx];
            if (j >= 0) {
               firstIndex = MathUtils.min(firstIndex, j);
               lastIndex = MathUtils.max(lastIndex, j + 1);
            } else {
               modified = true;
            }
         }

         if (firstIndex < lastIndex) {
            group.m_firstIndex = firstIndex;
            group.m_lastIndex = lastIndex;
            if (modified && (group.m_groupFlags & 2) != 0) {
               group.m_toBeSplit = true;
            }
         } else {
            group.m_firstIndex = 0;
            group.m_lastIndex = 0;
            if (group.m_destroyAutomatically) {
               group.m_toBeDestroyed = true;
            }
         }
      }

      this.m_count = newCount;
      ParticleGroup group = this.m_groupList;

      while (group != null) {
         ParticleGroup next = group.getNext();
         if (group.m_toBeDestroyed) {
            this.destroyParticleGroup(group);
         } else if (group.m_toBeSplit) {
         }

         group = next;
      }
   }

   void RotateBuffer(int start, int mid, int end) {
      if (start != mid && mid != end) {
         this.newIndices.start = start;
         this.newIndices.mid = mid;
         this.newIndices.end = end;
         BufferUtils.rotate(this.m_flagsBuffer.data, start, mid, end);
         BufferUtils.rotate(this.m_positionBuffer.data, start, mid, end);
         BufferUtils.rotate(this.m_velocityBuffer.data, start, mid, end);
         BufferUtils.rotate(this.m_groupBuffer, start, mid, end);
         if (this.m_depthBuffer != null) {
            BufferUtils.rotate(this.m_depthBuffer, start, mid, end);
         }

         if (this.m_colorBuffer.data != null) {
            BufferUtils.rotate(this.m_colorBuffer.data, start, mid, end);
         }

         if (this.m_userDataBuffer.data != null) {
            BufferUtils.rotate(this.m_userDataBuffer.data, start, mid, end);
         }

         for (int k = 0; k < this.m_proxyCount; k++) {
            ParticleSystem.Proxy proxy = this.m_proxyBuffer[k];
            proxy.index = this.newIndices.getIndex(proxy.index);
         }

         for (int k = 0; k < this.m_contactCount; k++) {
            ParticleContact contact = this.m_contactBuffer[k];
            contact.indexA = this.newIndices.getIndex(contact.indexA);
            contact.indexB = this.newIndices.getIndex(contact.indexB);
         }

         for (int k = 0; k < this.m_bodyContactCount; k++) {
            ParticleBodyContact contact = this.m_bodyContactBuffer[k];
            contact.index = this.newIndices.getIndex(contact.index);
         }

         for (int k = 0; k < this.m_pairCount; k++) {
            ParticleSystem.Pair pair = this.m_pairBuffer[k];
            pair.indexA = this.newIndices.getIndex(pair.indexA);
            pair.indexB = this.newIndices.getIndex(pair.indexB);
         }

         for (int k = 0; k < this.m_triadCount; k++) {
            ParticleSystem.Triad triad = this.m_triadBuffer[k];
            triad.indexA = this.newIndices.getIndex(triad.indexA);
            triad.indexB = this.newIndices.getIndex(triad.indexB);
            triad.indexC = this.newIndices.getIndex(triad.indexC);
         }

         for (ParticleGroup group = this.m_groupList; group != null; group = group.getNext()) {
            group.m_firstIndex = this.newIndices.getIndex(group.m_firstIndex);
            group.m_lastIndex = this.newIndices.getIndex(group.m_lastIndex - 1) + 1;
         }
      }
   }

   public void setParticleRadius(float radius) {
      this.m_particleDiameter = 2.0F * radius;
      this.m_squaredDiameter = this.m_particleDiameter * this.m_particleDiameter;
      this.m_inverseDiameter = 1.0F / this.m_particleDiameter;
   }

   public void setParticleDensity(float density) {
      this.m_density = density;
      this.m_inverseDensity = 1.0F / this.m_density;
   }

   public float getParticleDensity() {
      return this.m_density;
   }

   public void setParticleGravityScale(float gravityScale) {
      this.m_gravityScale = gravityScale;
   }

   public float getParticleGravityScale() {
      return this.m_gravityScale;
   }

   public void setParticleDamping(float damping) {
      this.m_dampingStrength = damping;
   }

   public float getParticleDamping() {
      return this.m_dampingStrength;
   }

   public float getParticleRadius() {
      return this.m_particleDiameter / 2.0F;
   }

   float getCriticalVelocity(TimeStep step) {
      return this.m_particleDiameter * step.inv_dt;
   }

   float getCriticalVelocitySquared(TimeStep step) {
      float velocity = this.getCriticalVelocity(step);
      return velocity * velocity;
   }

   float getCriticalPressure(TimeStep step) {
      return this.m_density * this.getCriticalVelocitySquared(step);
   }

   float getParticleStride() {
      return 0.75F * this.m_particleDiameter;
   }

   float getParticleMass() {
      float stride = this.getParticleStride();
      return this.m_density * stride * stride;
   }

   float getParticleInvMass() {
      return 1.777777F * this.m_inverseDensity * this.m_inverseDiameter * this.m_inverseDiameter;
   }

   public int[] getParticleFlagsBuffer() {
      return this.m_flagsBuffer.data;
   }

   public Vec2[] getParticlePositionBuffer() {
      return this.m_positionBuffer.data;
   }

   public Vec2[] getParticleVelocityBuffer() {
      return this.m_velocityBuffer.data;
   }

   public ParticleColor[] getParticleColorBuffer() {
      this.m_colorBuffer.data = this.requestParticleBuffer(ParticleColor.class, this.m_colorBuffer.data);
      return this.m_colorBuffer.data;
   }

   public Object[] getParticleUserDataBuffer() {
      this.m_userDataBuffer.data = this.requestParticleBuffer(Object.class, this.m_userDataBuffer.data);
      return this.m_userDataBuffer.data;
   }

   public int getParticleMaxCount() {
      return this.m_maxCount;
   }

   public void setParticleMaxCount(int count) {
      assert this.m_count <= count;

      this.m_maxCount = count;
   }

   void setParticleBuffer(ParticleSystem.ParticleBufferInt buffer, int[] newData, int newCapacity) {
      assert newData != null && newCapacity != 0 || newData == null && newCapacity == 0;

      if (buffer.userSuppliedCapacity != 0) {
      }

      buffer.data = newData;
      buffer.userSuppliedCapacity = newCapacity;
   }

   <T> void setParticleBuffer(ParticleSystem.ParticleBuffer<T> buffer, T[] newData, int newCapacity) {
      assert newData != null && newCapacity != 0 || newData == null && newCapacity == 0;

      if (buffer.userSuppliedCapacity != 0) {
      }

      buffer.data = newData;
      buffer.userSuppliedCapacity = newCapacity;
   }

   public void setParticleFlagsBuffer(int[] buffer, int capacity) {
      this.setParticleBuffer(this.m_flagsBuffer, buffer, capacity);
   }

   public void setParticlePositionBuffer(Vec2[] buffer, int capacity) {
      this.setParticleBuffer(this.m_positionBuffer, buffer, capacity);
   }

   public void setParticleVelocityBuffer(Vec2[] buffer, int capacity) {
      this.setParticleBuffer(this.m_velocityBuffer, buffer, capacity);
   }

   public void setParticleColorBuffer(ParticleColor[] buffer, int capacity) {
      this.setParticleBuffer(this.m_colorBuffer, buffer, capacity);
   }

   public ParticleGroup[] getParticleGroupBuffer() {
      return this.m_groupBuffer;
   }

   public int getParticleGroupCount() {
      return this.m_groupCount;
   }

   public ParticleGroup[] getParticleGroupList() {
      return this.m_groupBuffer;
   }

   public int getParticleCount() {
      return this.m_count;
   }

   public void setParticleUserDataBuffer(Object[] buffer, int capacity) {
      this.setParticleBuffer(this.m_userDataBuffer, buffer, capacity);
   }

   private static final int lowerBound(ParticleSystem.Proxy[] ray, int length, long tag) {
      int left = 0;

      while (length > 0) {
         int step = length / 2;
         int curr = left + step;
         if (ray[curr].tag < tag) {
            left = curr + 1;
            length -= step + 1;
         } else {
            length = step;
         }
      }

      return left;
   }

   private static final int upperBound(ParticleSystem.Proxy[] ray, int length, long tag) {
      int left = 0;

      while (length > 0) {
         int step = length / 2;
         int curr = left + step;
         if (ray[curr].tag <= tag) {
            left = curr + 1;
            length -= step + 1;
         } else {
            length = step;
         }
      }

      return left;
   }

   public void queryAABB(ParticleQueryCallback callback, AABB aabb) {
      if (this.m_proxyCount != 0) {
         float lowerBoundX = aabb.lowerBound.x;
         float lowerBoundY = aabb.lowerBound.y;
         float upperBoundX = aabb.upperBound.x;
         float upperBoundY = aabb.upperBound.y;
         int firstProxy = lowerBound(
            this.m_proxyBuffer, this.m_proxyCount, computeTag(this.m_inverseDiameter * lowerBoundX, this.m_inverseDiameter * lowerBoundY)
         );
         int lastProxy = upperBound(
            this.m_proxyBuffer, this.m_proxyCount, computeTag(this.m_inverseDiameter * upperBoundX, this.m_inverseDiameter * upperBoundY)
         );

         for (int proxy = firstProxy; proxy < lastProxy; proxy++) {
            int i = this.m_proxyBuffer[proxy].index;
            Vec2 p = this.m_positionBuffer.data[i];
            if (lowerBoundX < p.x && p.x < upperBoundX && lowerBoundY < p.y && p.y < upperBoundY && !callback.reportParticle(i)) {
               break;
            }
         }
      }
   }

   public void raycast(ParticleRaycastCallback callback, Vec2 point1, Vec2 point2) {
      if (this.m_proxyCount != 0) {
         int firstProxy = lowerBound(
            this.m_proxyBuffer,
            this.m_proxyCount,
            computeTag(this.m_inverseDiameter * MathUtils.min(point1.x, point2.x) - 1.0F, this.m_inverseDiameter * MathUtils.min(point1.y, point2.y) - 1.0F)
         );
         int lastProxy = upperBound(
            this.m_proxyBuffer,
            this.m_proxyCount,
            computeTag(this.m_inverseDiameter * MathUtils.max(point1.x, point2.x) + 1.0F, this.m_inverseDiameter * MathUtils.max(point1.y, point2.y) + 1.0F)
         );
         float fraction = 1.0F;
         float vx = point2.x - point1.x;
         float vy = point2.y - point1.y;
         float v2 = vx * vx + vy * vy;
         if (v2 == 0.0F) {
            v2 = Float.MAX_VALUE;
         }

         for (int proxy = firstProxy; proxy < lastProxy; proxy++) {
            int i = this.m_proxyBuffer[proxy].index;
            Vec2 posI = this.m_positionBuffer.data[i];
            float px = point1.x - posI.x;
            float py = point1.y - posI.y;
            float pv = px * vx + py * vy;
            float p2 = px * px + py * py;
            float determinant = pv * pv - v2 * (p2 - this.m_squaredDiameter);
            if (determinant >= 0.0F) {
               float sqrtDeterminant = MathUtils.sqrt(determinant);
               float t = (-pv - sqrtDeterminant) / v2;
               if (!(t > fraction)) {
                  if (t < 0.0F) {
                     t = (-pv + sqrtDeterminant) / v2;
                     if (t < 0.0F || t > fraction) {
                        continue;
                     }
                  }

                  Vec2 n = this.tempVec;
                  this.tempVec.x = px + t * vx;
                  this.tempVec.y = py + t * vy;
                  n.normalize();
                  Vec2 point = this.tempVec2;
                  point.x = point1.x + t * vx;
                  point.y = point1.y + t * vy;
                  float f = callback.reportParticle(i, point, n, t);
                  fraction = MathUtils.min(fraction, f);
                  if (fraction <= 0.0F) {
                     break;
                  }
               }
            }
         }
      }
   }

   public float computeParticleCollisionEnergy() {
      float sum_v2 = 0.0F;

      for (int k = 0; k < this.m_contactCount; k++) {
         ParticleContact contact = this.m_contactBuffer[k];
         int a = contact.indexA;
         int b = contact.indexB;
         Vec2 n = contact.normal;
         Vec2 va = this.m_velocityBuffer.data[a];
         Vec2 vb = this.m_velocityBuffer.data[b];
         float vx = vb.x - va.x;
         float vy = vb.y - va.y;
         float vn = vx * n.x + vy * n.y;
         if (vn < 0.0F) {
            sum_v2 += vn * vn;
         }
      }

      return 0.5F * this.getParticleMass() * sum_v2;
   }

   static <T> T[] reallocateBuffer(ParticleSystem.ParticleBuffer<T> buffer, int oldCapacity, int newCapacity, boolean deferred) {
      assert newCapacity > oldCapacity;

      return BufferUtils.reallocateBuffer(buffer.dataClass, buffer.data, buffer.userSuppliedCapacity, oldCapacity, newCapacity, deferred);
   }

   static int[] reallocateBuffer(ParticleSystem.ParticleBufferInt buffer, int oldCapacity, int newCapacity, boolean deferred) {
      assert newCapacity > oldCapacity;

      return BufferUtils.reallocateBuffer(buffer.data, buffer.userSuppliedCapacity, oldCapacity, newCapacity, deferred);
   }

   <T> T[] requestParticleBuffer(Class<T> klass, T[] buffer) {
      if (buffer == null) {
         buffer = (T[])Array.newInstance(klass, this.m_internalAllocatedCapacity);

         for (int i = 0; i < this.m_internalAllocatedCapacity; i++) {
            try {
               buffer[i] = klass.newInstance();
            } catch (Exception var5) {
               throw new RuntimeException(var5);
            }
         }
      }

      return buffer;
   }

   float[] requestParticleBuffer(float[] buffer) {
      if (buffer == null) {
         buffer = new float[this.m_internalAllocatedCapacity];
      }

      return buffer;
   }

   static class CreateParticleGroupCallback implements VoronoiDiagram.VoronoiDiagramCallback {
      ParticleSystem system;
      ParticleGroupDef def;
      int firstIndex;

      @Override
      public void callback(int a, int b, int c) {
         Vec2 pa = this.system.m_positionBuffer.data[a];
         Vec2 pb = this.system.m_positionBuffer.data[b];
         Vec2 pc = this.system.m_positionBuffer.data[c];
         float dabx = pa.x - pb.x;
         float daby = pa.y - pb.y;
         float dbcx = pb.x - pc.x;
         float dbcy = pb.y - pc.y;
         float dcax = pc.x - pa.x;
         float dcay = pc.y - pa.y;
         float maxDistanceSquared = 4.0F * this.system.m_squaredDiameter;
         if (dabx * dabx + daby * daby < maxDistanceSquared && dbcx * dbcx + dbcy * dbcy < maxDistanceSquared && dcax * dcax + dcay * dcay < maxDistanceSquared
            )
          {
            if (this.system.m_triadCount >= this.system.m_triadCapacity) {
               int oldCapacity = this.system.m_triadCapacity;
               int newCapacity = this.system.m_triadCount != 0 ? 2 * this.system.m_triadCount : 256;
               this.system.m_triadBuffer = BufferUtils.reallocateBuffer(ParticleSystem.Triad.class, this.system.m_triadBuffer, oldCapacity, newCapacity);
               this.system.m_triadCapacity = newCapacity;
            }

            ParticleSystem.Triad triad = this.system.m_triadBuffer[this.system.m_triadCount];
            triad.indexA = a;
            triad.indexB = b;
            triad.indexC = c;
            triad.flags = this.system.m_flagsBuffer.data[a] | this.system.m_flagsBuffer.data[b] | this.system.m_flagsBuffer.data[c];
            triad.strength = this.def.strength;
            float midPointx = 0.33333334F * (pa.x + pb.x + pc.x);
            float midPointy = 0.33333334F * (pa.y + pb.y + pc.y);
            triad.pa.x = pa.x - midPointx;
            triad.pa.y = pa.y - midPointy;
            triad.pb.x = pb.x - midPointx;
            triad.pb.y = pb.y - midPointy;
            triad.pc.x = pc.x - midPointx;
            triad.pc.y = pc.y - midPointy;
            triad.ka = -(dcax * dabx + dcay * daby);
            triad.kb = -(dabx * dbcx + daby * dbcy);
            triad.kc = -(dbcx * dcax + dbcy * dcay);
            triad.s = Vec2.cross(pa, pb) + Vec2.cross(pb, pc) + Vec2.cross(pc, pa);
            this.system.m_triadCount++;
         }
      }
   }

   static class DestroyParticlesInShapeCallback implements ParticleQueryCallback {
      ParticleSystem system;
      Shape shape;
      Transform xf;
      boolean callDestructionListener;
      int destroyed;

      public DestroyParticlesInShapeCallback() {
      }

      public void init(ParticleSystem system, Shape shape, Transform xf, boolean callDestructionListener) {
         this.system = system;
         this.shape = shape;
         this.xf = xf;
         this.destroyed = 0;
         this.callDestructionListener = callDestructionListener;
      }

      @Override
      public boolean reportParticle(int index) {
         assert index >= 0 && index < this.system.m_count;

         if (this.shape.testPoint(this.xf, this.system.m_positionBuffer.data[index])) {
            this.system.destroyParticle(index, this.callDestructionListener);
            this.destroyed++;
         }

         return true;
      }
   }

   static class JoinParticleGroupsCallback implements VoronoiDiagram.VoronoiDiagramCallback {
      ParticleSystem system;
      ParticleGroup groupA;
      ParticleGroup groupB;

      @Override
      public void callback(int a, int b, int c) {
         int countA = (a < this.groupB.m_firstIndex ? 1 : 0) + (b < this.groupB.m_firstIndex ? 1 : 0) + (c < this.groupB.m_firstIndex ? 1 : 0);
         if (countA > 0 && countA < 3) {
            int af = this.system.m_flagsBuffer.data[a];
            int bf = this.system.m_flagsBuffer.data[b];
            int cf = this.system.m_flagsBuffer.data[c];
            if ((af & bf & cf & 16) != 0) {
               Vec2 pa = this.system.m_positionBuffer.data[a];
               Vec2 pb = this.system.m_positionBuffer.data[b];
               Vec2 pc = this.system.m_positionBuffer.data[c];
               float dabx = pa.x - pb.x;
               float daby = pa.y - pb.y;
               float dbcx = pb.x - pc.x;
               float dbcy = pb.y - pc.y;
               float dcax = pc.x - pa.x;
               float dcay = pc.y - pa.y;
               float maxDistanceSquared = 4.0F * this.system.m_squaredDiameter;
               if (dabx * dabx + daby * daby < maxDistanceSquared
                  && dbcx * dbcx + dbcy * dbcy < maxDistanceSquared
                  && dcax * dcax + dcay * dcay < maxDistanceSquared) {
                  if (this.system.m_triadCount >= this.system.m_triadCapacity) {
                     int oldCapacity = this.system.m_triadCapacity;
                     int newCapacity = this.system.m_triadCount != 0 ? 2 * this.system.m_triadCount : 256;
                     this.system.m_triadBuffer = BufferUtils.reallocateBuffer(ParticleSystem.Triad.class, this.system.m_triadBuffer, oldCapacity, newCapacity);
                     this.system.m_triadCapacity = newCapacity;
                  }

                  ParticleSystem.Triad triad = this.system.m_triadBuffer[this.system.m_triadCount];
                  triad.indexA = a;
                  triad.indexB = b;
                  triad.indexC = c;
                  triad.flags = af | bf | cf;
                  triad.strength = MathUtils.min(this.groupA.m_strength, this.groupB.m_strength);
                  float midPointx = 0.33333334F * (pa.x + pb.x + pc.x);
                  float midPointy = 0.33333334F * (pa.y + pb.y + pc.y);
                  triad.pa.x = pa.x - midPointx;
                  triad.pa.y = pa.y - midPointy;
                  triad.pb.x = pb.x - midPointx;
                  triad.pb.y = pb.y - midPointy;
                  triad.pc.x = pc.x - midPointx;
                  triad.pc.y = pc.y - midPointy;
                  triad.ka = -(dcax * dabx + dcay * daby);
                  triad.kb = -(dabx * dbcx + daby * dbcy);
                  triad.kc = -(dbcx * dcax + dbcy * dcay);
                  triad.s = Vec2.cross(pa, pb) + Vec2.cross(pb, pc) + Vec2.cross(pc, pa);
                  this.system.m_triadCount++;
               }
            }
         }
      }
   }

   private static class NewIndices {
      int start;
      int mid;
      int end;

      final int getIndex(int i) {
         if (i < this.start) {
            return i;
         } else if (i < this.mid) {
            return i + this.end - this.mid;
         } else {
            return i < this.end ? i + this.start - this.mid : i;
         }
      }
   }

   public static class Pair {
      int indexA;
      int indexB;
      int flags;
      float strength;
      float distance;
   }

   public static class ParticleBuffer<T> {
      public T[] data;
      final Class<T> dataClass;
      int userSuppliedCapacity;

      public ParticleBuffer(Class<T> dataClass) {
         this.dataClass = dataClass;
      }
   }

   static class ParticleBufferInt {
      int[] data;
      int userSuppliedCapacity;
   }

   public static class Proxy implements Comparable<ParticleSystem.Proxy> {
      int index;
      long tag;

      public int compareTo(ParticleSystem.Proxy o) {
         return this.tag - o.tag < 0L ? -1 : (o.tag == this.tag ? 0 : 1);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            ParticleSystem.Proxy other = (ParticleSystem.Proxy)obj;
            return this.tag == other.tag;
         }
      }
   }

   static class SolveCollisionCallback implements QueryCallback {
      ParticleSystem system;
      TimeStep step;
      private final RayCastInput input = new RayCastInput();
      private final RayCastOutput output = new RayCastOutput();
      private final Vec2 tempVec = new Vec2();
      private final Vec2 tempVec2 = new Vec2();

      @Override
      public boolean reportFixture(Fixture fixture) {
         if (fixture.isSensor()) {
            return true;
         } else {
            Shape shape = fixture.getShape();
            Body body = fixture.getBody();
            int childCount = shape.getChildCount();

            for (int childIndex = 0; childIndex < childCount; childIndex++) {
               AABB aabb = fixture.getAABB(childIndex);
               float aabblowerBoundx = aabb.lowerBound.x - this.system.m_particleDiameter;
               float aabblowerBoundy = aabb.lowerBound.y - this.system.m_particleDiameter;
               float aabbupperBoundx = aabb.upperBound.x + this.system.m_particleDiameter;
               float aabbupperBoundy = aabb.upperBound.y + this.system.m_particleDiameter;
               int firstProxy = ParticleSystem.lowerBound(
                  this.system.m_proxyBuffer,
                  this.system.m_proxyCount,
                  ParticleSystem.computeTag(this.system.m_inverseDiameter * aabblowerBoundx, this.system.m_inverseDiameter * aabblowerBoundy)
               );
               int lastProxy = ParticleSystem.upperBound(
                  this.system.m_proxyBuffer,
                  this.system.m_proxyCount,
                  ParticleSystem.computeTag(this.system.m_inverseDiameter * aabbupperBoundx, this.system.m_inverseDiameter * aabbupperBoundy)
               );

               for (int proxy = firstProxy; proxy != lastProxy; proxy++) {
                  int a = this.system.m_proxyBuffer[proxy].index;
                  Vec2 ap = this.system.m_positionBuffer.data[a];
                  if (aabblowerBoundx <= ap.x && ap.x <= aabbupperBoundx && aabblowerBoundy <= ap.y && ap.y <= aabbupperBoundy) {
                     Vec2 av = this.system.m_velocityBuffer.data[a];
                     Vec2 temp = this.tempVec;
                     Transform.mulTransToOutUnsafe(body.m_xf0, ap, temp);
                     Transform.mulToOutUnsafe(body.m_xf, temp, this.input.p1);
                     this.input.p2.x = ap.x + this.step.dt * av.x;
                     this.input.p2.y = ap.y + this.step.dt * av.y;
                     this.input.maxFraction = 1.0F;
                     if (fixture.raycast(this.output, this.input, childIndex)) {
                        Vec2 p = this.tempVec;
                        p.x = (1.0F - this.output.fraction) * this.input.p1.x
                           + this.output.fraction * this.input.p2.x
                           + Settings.linearSlop * this.output.normal.x;
                        p.y = (1.0F - this.output.fraction) * this.input.p1.y
                           + this.output.fraction * this.input.p2.y
                           + Settings.linearSlop * this.output.normal.y;
                        float vx = this.step.inv_dt * (p.x - ap.x);
                        float vy = this.step.inv_dt * (p.y - ap.y);
                        av.x = vx;
                        av.y = vy;
                        float particleMass = this.system.getParticleMass();
                        float ax = particleMass * (av.x - vx);
                        float ay = particleMass * (av.y - vy);
                        Vec2 b = this.output.normal;
                        float fdn = ax * b.x + ay * b.y;
                        Vec2 f = this.tempVec2;
                        f.x = fdn * b.x;
                        f.y = fdn * b.y;
                        body.applyLinearImpulse(f, p, true);
                     }
                  }
               }
            }

            return true;
         }
      }
   }

   static class Test {
      static boolean IsProxyInvalid(ParticleSystem.Proxy proxy) {
         return proxy.index < 0;
      }

      static boolean IsContactInvalid(ParticleContact contact) {
         return contact.indexA < 0 || contact.indexB < 0;
      }

      static boolean IsBodyContactInvalid(ParticleBodyContact contact) {
         return contact.index < 0;
      }

      static boolean IsPairInvalid(ParticleSystem.Pair pair) {
         return pair.indexA < 0 || pair.indexB < 0;
      }

      static boolean IsTriadInvalid(ParticleSystem.Triad triad) {
         return triad.indexA < 0 || triad.indexB < 0 || triad.indexC < 0;
      }
   }

   public static class Triad {
      int indexA;
      int indexB;
      int indexC;
      int flags;
      float strength;
      final Vec2 pa = new Vec2();
      final Vec2 pb = new Vec2();
      final Vec2 pc = new Vec2();
      float ka;
      float kb;
      float kc;
      float s;
   }

   static class UpdateBodyContactsCallback implements QueryCallback {
      ParticleSystem system;
      private final Vec2 tempVec = new Vec2();

      @Override
      public boolean reportFixture(Fixture fixture) {
         if (fixture.isSensor()) {
            return true;
         } else {
            Shape shape = fixture.getShape();
            Body b = fixture.getBody();
            Vec2 bp = b.getWorldCenter();
            float bm = b.getMass();
            float bI = b.getInertia() - bm * b.getLocalCenter().lengthSquared();
            float invBm = bm > 0.0F ? 1.0F / bm : 0.0F;
            float invBI = bI > 0.0F ? 1.0F / bI : 0.0F;
            int childCount = shape.getChildCount();

            for (int childIndex = 0; childIndex < childCount; childIndex++) {
               AABB aabb = fixture.getAABB(childIndex);
               float aabblowerBoundx = aabb.lowerBound.x - this.system.m_particleDiameter;
               float aabblowerBoundy = aabb.lowerBound.y - this.system.m_particleDiameter;
               float aabbupperBoundx = aabb.upperBound.x + this.system.m_particleDiameter;
               float aabbupperBoundy = aabb.upperBound.y + this.system.m_particleDiameter;
               int firstProxy = ParticleSystem.lowerBound(
                  this.system.m_proxyBuffer,
                  this.system.m_proxyCount,
                  ParticleSystem.computeTag(this.system.m_inverseDiameter * aabblowerBoundx, this.system.m_inverseDiameter * aabblowerBoundy)
               );
               int lastProxy = ParticleSystem.upperBound(
                  this.system.m_proxyBuffer,
                  this.system.m_proxyCount,
                  ParticleSystem.computeTag(this.system.m_inverseDiameter * aabbupperBoundx, this.system.m_inverseDiameter * aabbupperBoundy)
               );

               for (int proxy = firstProxy; proxy != lastProxy; proxy++) {
                  int a = this.system.m_proxyBuffer[proxy].index;
                  Vec2 ap = this.system.m_positionBuffer.data[a];
                  if (aabblowerBoundx <= ap.x && ap.x <= aabbupperBoundx && aabblowerBoundy <= ap.y && ap.y <= aabbupperBoundy) {
                     Vec2 n = this.tempVec;
                     float d = fixture.computeDistance(ap, childIndex, n);
                     if (d < this.system.m_particleDiameter) {
                        float invAm = (this.system.m_flagsBuffer.data[a] & 4) != 0 ? 0.0F : this.system.getParticleInvMass();
                        float rpx = ap.x - bp.x;
                        float rpy = ap.y - bp.y;
                        float rpn = rpx * n.y - rpy * n.x;
                        if (this.system.m_bodyContactCount >= this.system.m_bodyContactCapacity) {
                           int oldCapacity = this.system.m_bodyContactCapacity;
                           int newCapacity = this.system.m_bodyContactCount != 0 ? 2 * this.system.m_bodyContactCount : 256;
                           this.system.m_bodyContactBuffer = BufferUtils.reallocateBuffer(
                              ParticleBodyContact.class, this.system.m_bodyContactBuffer, oldCapacity, newCapacity
                           );
                           this.system.m_bodyContactCapacity = newCapacity;
                        }

                        ParticleBodyContact contact = this.system.m_bodyContactBuffer[this.system.m_bodyContactCount];
                        contact.index = a;
                        contact.body = b;
                        contact.weight = 1.0F - d * this.system.m_inverseDiameter;
                        contact.normal.x = -n.x;
                        contact.normal.y = -n.y;
                        contact.mass = 1.0F / (invAm + invBm + invBI * rpn * rpn);
                        this.system.m_bodyContactCount++;
                     }
                  }
               }
            }

            return true;
         }
      }
   }
}
