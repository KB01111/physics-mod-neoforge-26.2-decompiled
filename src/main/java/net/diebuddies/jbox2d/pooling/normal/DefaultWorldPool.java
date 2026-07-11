package net.diebuddies.jbox2d.pooling.normal;

import java.util.HashMap;
import java.util.Objects;
import net.diebuddies.jbox2d.collision.AABB;
import net.diebuddies.jbox2d.collision.Collision;
import net.diebuddies.jbox2d.collision.Distance;
import net.diebuddies.jbox2d.collision.TimeOfImpact;
import net.diebuddies.jbox2d.common.Mat22;
import net.diebuddies.jbox2d.common.Mat33;
import net.diebuddies.jbox2d.common.Rot;
import net.diebuddies.jbox2d.common.Settings;
import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.common.Vec3;
import net.diebuddies.jbox2d.dynamics.contacts.ChainAndCircleContact;
import net.diebuddies.jbox2d.dynamics.contacts.ChainAndPolygonContact;
import net.diebuddies.jbox2d.dynamics.contacts.CircleContact;
import net.diebuddies.jbox2d.dynamics.contacts.Contact;
import net.diebuddies.jbox2d.dynamics.contacts.EdgeAndCircleContact;
import net.diebuddies.jbox2d.dynamics.contacts.EdgeAndPolygonContact;
import net.diebuddies.jbox2d.dynamics.contacts.PolygonAndCircleContact;
import net.diebuddies.jbox2d.dynamics.contacts.PolygonContact;
import net.diebuddies.jbox2d.pooling.IDynamicStack;
import net.diebuddies.jbox2d.pooling.IWorldPool;

public class DefaultWorldPool implements IWorldPool {
   private final OrderedStack<Vec2> vecs;
   private final OrderedStack<Vec3> vec3s;
   private final OrderedStack<Mat22> mats;
   private final OrderedStack<Mat33> mat33s;
   private final OrderedStack<AABB> aabbs;
   private final OrderedStack<Rot> rots;
   private final HashMap<Integer, float[]> afloats = new HashMap<>();
   private final HashMap<Integer, int[]> aints = new HashMap<>();
   private final HashMap<Integer, Vec2[]> avecs = new HashMap<>();
   private final IWorldPool world = this;
   private final MutableStack<Contact> pcstack = new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
      {
         Objects.requireNonNull(DefaultWorldPool.this);
      }

      protected Contact newInstance() {
         return new PolygonContact(DefaultWorldPool.this.world);
      }

      protected Contact[] newArray(int size) {
         return new PolygonContact[size];
      }
   };
   private final MutableStack<Contact> ccstack = new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
      {
         Objects.requireNonNull(DefaultWorldPool.this);
      }

      protected Contact newInstance() {
         return new CircleContact(DefaultWorldPool.this.world);
      }

      protected Contact[] newArray(int size) {
         return new CircleContact[size];
      }
   };
   private final MutableStack<Contact> cpstack = new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
      {
         Objects.requireNonNull(DefaultWorldPool.this);
      }

      protected Contact newInstance() {
         return new PolygonAndCircleContact(DefaultWorldPool.this.world);
      }

      protected Contact[] newArray(int size) {
         return new PolygonAndCircleContact[size];
      }
   };
   private final MutableStack<Contact> ecstack = new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
      {
         Objects.requireNonNull(DefaultWorldPool.this);
      }

      protected Contact newInstance() {
         return new EdgeAndCircleContact(DefaultWorldPool.this.world);
      }

      protected Contact[] newArray(int size) {
         return new EdgeAndCircleContact[size];
      }
   };
   private final MutableStack<Contact> epstack = new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
      {
         Objects.requireNonNull(DefaultWorldPool.this);
      }

      protected Contact newInstance() {
         return new EdgeAndPolygonContact(DefaultWorldPool.this.world);
      }

      protected Contact[] newArray(int size) {
         return new EdgeAndPolygonContact[size];
      }
   };
   private final MutableStack<Contact> chcstack = new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
      {
         Objects.requireNonNull(DefaultWorldPool.this);
      }

      protected Contact newInstance() {
         return new ChainAndCircleContact(DefaultWorldPool.this.world);
      }

      protected Contact[] newArray(int size) {
         return new ChainAndCircleContact[size];
      }
   };
   private final MutableStack<Contact> chpstack = new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
      {
         Objects.requireNonNull(DefaultWorldPool.this);
      }

      protected Contact newInstance() {
         return new ChainAndPolygonContact(DefaultWorldPool.this.world);
      }

      protected Contact[] newArray(int size) {
         return new ChainAndPolygonContact[size];
      }
   };
   private final Collision collision;
   private final TimeOfImpact toi;
   private final Distance dist;

   public DefaultWorldPool(int argSize, int argContainerSize) {
      this.vecs = new OrderedStack<Vec2>(argSize, argContainerSize) {
         {
            Objects.requireNonNull(DefaultWorldPool.this);
         }

         protected Vec2 newInstance() {
            return new Vec2();
         }
      };
      this.vec3s = new OrderedStack<Vec3>(argSize, argContainerSize) {
         {
            Objects.requireNonNull(DefaultWorldPool.this);
         }

         protected Vec3 newInstance() {
            return new Vec3();
         }
      };
      this.mats = new OrderedStack<Mat22>(argSize, argContainerSize) {
         {
            Objects.requireNonNull(DefaultWorldPool.this);
         }

         protected Mat22 newInstance() {
            return new Mat22();
         }
      };
      this.aabbs = new OrderedStack<AABB>(argSize, argContainerSize) {
         {
            Objects.requireNonNull(DefaultWorldPool.this);
         }

         protected AABB newInstance() {
            return new AABB();
         }
      };
      this.rots = new OrderedStack<Rot>(argSize, argContainerSize) {
         {
            Objects.requireNonNull(DefaultWorldPool.this);
         }

         protected Rot newInstance() {
            return new Rot();
         }
      };
      this.mat33s = new OrderedStack<Mat33>(argSize, argContainerSize) {
         {
            Objects.requireNonNull(DefaultWorldPool.this);
         }

         protected Mat33 newInstance() {
            return new Mat33();
         }
      };
      this.dist = new Distance();
      this.collision = new Collision(this);
      this.toi = new TimeOfImpact(this);
   }

   @Override
   public final IDynamicStack<Contact> getPolyContactStack() {
      return this.pcstack;
   }

   @Override
   public final IDynamicStack<Contact> getCircleContactStack() {
      return this.ccstack;
   }

   @Override
   public final IDynamicStack<Contact> getPolyCircleContactStack() {
      return this.cpstack;
   }

   @Override
   public IDynamicStack<Contact> getEdgeCircleContactStack() {
      return this.ecstack;
   }

   @Override
   public IDynamicStack<Contact> getEdgePolyContactStack() {
      return this.epstack;
   }

   @Override
   public IDynamicStack<Contact> getChainCircleContactStack() {
      return this.chcstack;
   }

   @Override
   public IDynamicStack<Contact> getChainPolyContactStack() {
      return this.chpstack;
   }

   @Override
   public final Vec2 popVec2() {
      return this.vecs.pop();
   }

   @Override
   public final Vec2[] popVec2(int argNum) {
      Vec2[] arr = new Vec2[argNum];

      for (int i = 0; i < arr.length; i++) {
         arr[i] = this.vecs.pop();
      }

      return arr;
   }

   @Override
   public final void pushVec2(int argNum) {
      this.vecs.push(argNum);
   }

   @Override
   public final Vec3 popVec3() {
      return this.vec3s.pop();
   }

   @Override
   public final Vec3[] popVec3(int argNum) {
      Vec3[] arr = new Vec3[argNum];

      for (int i = 0; i < arr.length; i++) {
         arr[i] = this.vec3s.pop();
      }

      return arr;
   }

   @Override
   public final void pushVec3(int argNum) {
      this.vec3s.push(argNum);
   }

   @Override
   public final Mat22 popMat22() {
      return this.mats.pop();
   }

   @Override
   public final Mat22[] popMat22(int argNum) {
      Mat22[] arr = new Mat22[argNum];

      for (int i = 0; i < arr.length; i++) {
         arr[i] = this.mats.pop();
      }

      return arr;
   }

   @Override
   public final void pushMat22(int argNum) {
      this.mats.push(argNum);
   }

   @Override
   public final Mat33 popMat33() {
      return this.mat33s.pop();
   }

   @Override
   public final void pushMat33(int argNum) {
      this.mat33s.push(argNum);
   }

   @Override
   public final AABB popAABB() {
      return this.aabbs.pop();
   }

   @Override
   public final AABB[] popAABB(int argNum) {
      AABB[] arr = new AABB[argNum];

      for (int i = 0; i < arr.length; i++) {
         arr[i] = this.aabbs.pop();
      }

      return arr;
   }

   @Override
   public final void pushAABB(int argNum) {
      this.aabbs.push(argNum);
   }

   @Override
   public final Rot popRot() {
      return this.rots.pop();
   }

   @Override
   public final void pushRot(int num) {
      this.rots.push(num);
   }

   @Override
   public final Collision getCollision() {
      return this.collision;
   }

   @Override
   public final TimeOfImpact getTimeOfImpact() {
      return this.toi;
   }

   @Override
   public final Distance getDistance() {
      return this.dist;
   }

   @Override
   public final float[] getFloatArray(int argLength) {
      if (!this.afloats.containsKey(argLength)) {
         this.afloats.put(argLength, new float[argLength]);
      }

      assert ((float[])this.afloats.get(argLength)).length == argLength : "Array not built with correct length";

      return this.afloats.get(argLength);
   }

   @Override
   public final int[] getIntArray(int argLength) {
      if (!this.aints.containsKey(argLength)) {
         this.aints.put(argLength, new int[argLength]);
      }

      assert ((int[])this.aints.get(argLength)).length == argLength : "Array not built with correct length";

      return this.aints.get(argLength);
   }

   @Override
   public final Vec2[] getVec2Array(int argLength) {
      if (!this.avecs.containsKey(argLength)) {
         Vec2[] ray = new Vec2[argLength];

         for (int i = 0; i < argLength; i++) {
            ray[i] = new Vec2();
         }

         this.avecs.put(argLength, ray);
      }

      assert ((Vec2[])this.avecs.get(argLength)).length == argLength : "Array not built with correct length";

      return this.avecs.get(argLength);
   }
}
