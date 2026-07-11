package net.diebuddies.jbox2d.dynamics.joints;

import net.diebuddies.jbox2d.common.Vec2;
import net.diebuddies.jbox2d.dynamics.Body;
import net.diebuddies.jbox2d.dynamics.SolverData;
import net.diebuddies.jbox2d.dynamics.World;
import net.diebuddies.jbox2d.pooling.IWorldPool;

public abstract class Joint {
   private final JointType m_type;
   public Joint m_prev;
   public Joint m_next;
   public JointEdge m_edgeA;
   public JointEdge m_edgeB;
   protected Body m_bodyA;
   protected Body m_bodyB;
   public boolean m_islandFlag;
   private boolean m_collideConnected;
   public Object m_userData;
   protected IWorldPool pool;

   public static Joint create(World world, JointDef def) {
      switch (def.type) {
         case MOUSE:
            return new MouseJoint(world.getPool(), (MouseJointDef)def);
         case DISTANCE:
            return new DistanceJoint(world.getPool(), (DistanceJointDef)def);
         case PRISMATIC:
            return new PrismaticJoint(world.getPool(), (PrismaticJointDef)def);
         case REVOLUTE:
            return new RevoluteJoint(world.getPool(), (RevoluteJointDef)def);
         case WELD:
            return new WeldJoint(world.getPool(), (WeldJointDef)def);
         case FRICTION:
            return new FrictionJoint(world.getPool(), (FrictionJointDef)def);
         case WHEEL:
            return new WheelJoint(world.getPool(), (WheelJointDef)def);
         case GEAR:
            return new GearJoint(world.getPool(), (GearJointDef)def);
         case PULLEY:
            return new PulleyJoint(world.getPool(), (PulleyJointDef)def);
         case CONSTANT_VOLUME:
            return new ConstantVolumeJoint(world, (ConstantVolumeJointDef)def);
         case ROPE:
            return new RopeJoint(world.getPool(), (RopeJointDef)def);
         case MOTOR:
            return new MotorJoint(world.getPool(), (MotorJointDef)def);
         case UNKNOWN:
         default:
            return null;
      }
   }

   public static void destroy(Joint joint) {
      joint.destructor();
   }

   protected Joint(IWorldPool worldPool, JointDef def) {
      assert def.bodyA != def.bodyB;

      this.pool = worldPool;
      this.m_type = def.type;
      this.m_prev = null;
      this.m_next = null;
      this.m_bodyA = def.bodyA;
      this.m_bodyB = def.bodyB;
      this.m_collideConnected = def.collideConnected;
      this.m_islandFlag = false;
      this.m_userData = def.userData;
      this.m_edgeA = new JointEdge();
      this.m_edgeA.joint = null;
      this.m_edgeA.other = null;
      this.m_edgeA.prev = null;
      this.m_edgeA.next = null;
      this.m_edgeB = new JointEdge();
      this.m_edgeB.joint = null;
      this.m_edgeB.other = null;
      this.m_edgeB.prev = null;
      this.m_edgeB.next = null;
   }

   public JointType getType() {
      return this.m_type;
   }

   public final Body getBodyA() {
      return this.m_bodyA;
   }

   public final Body getBodyB() {
      return this.m_bodyB;
   }

   public abstract void getAnchorA(Vec2 var1);

   public abstract void getAnchorB(Vec2 var1);

   public abstract void getReactionForce(float var1, Vec2 var2);

   public abstract float getReactionTorque(float var1);

   public Joint getNext() {
      return this.m_next;
   }

   public Object getUserData() {
      return this.m_userData;
   }

   public void setUserData(Object data) {
      this.m_userData = data;
   }

   public final boolean getCollideConnected() {
      return this.m_collideConnected;
   }

   public boolean isActive() {
      return this.m_bodyA.isActive() && this.m_bodyB.isActive();
   }

   public abstract void initVelocityConstraints(SolverData var1);

   public abstract void solveVelocityConstraints(SolverData var1);

   public abstract boolean solvePositionConstraints(SolverData var1);

   public void destructor() {
   }
}
