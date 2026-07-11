package net.diebuddies.physics.ragdoll;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.diebuddies.physics.IRigidBody;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.Model;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.util.DoublyLinkedList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class Ragdoll implements DoublyLinkedList.NodeStorage<Ragdoll> {
   private static int counter;
   private int hashCode;
   public List<PhysicsEntity> bodies = new ObjectArrayList();
   public List<RagdollJoint> joints = new ObjectArrayList();
   public List<Ragdoll.LinkedBody> btBodies = new ObjectArrayList();
   public Vector3d velocity = new Vector3d();
   public Vector3f hitboxScale = new Vector3f(1.0F);
   public boolean kinematic;
   public boolean frozen;
   private DoublyLinkedList.Node<Ragdoll> node;

   public Ragdoll() {
      this.hashCode = counter++;
   }

   public void updatePhysics(PhysicsWorld physics) {
   }

   public boolean blockUpdate(PhysicsWorld physics, BlockPos pos, BlockState state) {
      return false;
   }

   public RagdollJoint addConnection(int from, int connectedTo, boolean fixed, boolean onlyVisual) {
      Vector3f localPos1 = this.bodies.get(from).models.get(0).mesh.offset;
      Vector3f localPos2 = this.bodies.get(connectedTo).models.get(0).mesh.offset;
      Vector3f pivotPoint = this.bodies.get(from).pivot;
      Vector3d scaleFrom = this.bodies.get(from).getTransformation().getScale(new Vector3d());
      Vector3d scaleTo = this.bodies.get(connectedTo).getTransformation().getScale(new Vector3d());
      this.bodies.get(from).models.get(0).onlyVisual = onlyVisual;
      RagdollJoint ragdollJoint = new RagdollJoint(
         from,
         connectedTo,
         new Vector3d((double)(pivotPoint.x - localPos1.x), (double)(pivotPoint.y - localPos1.y), (double)(pivotPoint.z - localPos1.z)).mul(scaleTo),
         new Vector3d((double)(pivotPoint.x - localPos2.x), (double)(pivotPoint.y - localPos2.y), (double)(pivotPoint.z - localPos2.z)).mul(scaleFrom)
      );
      ragdollJoint.fixed = fixed;
      this.joints.add(ragdollJoint);
      if (fixed) {
         Matrix4d transTo = this.bodies.get(connectedTo).getTransformation();
         Matrix4d transFrom = this.bodies.get(from).getTransformation();
         if (!transTo.equals(transFrom)) {
            Matrix4d transformation = transFrom.mulLocal(transTo.invert(new Matrix4d()));
            Matrix3d normalTransformation = transformation.normal(new Matrix3d());
            Matrix4f tmp = new Matrix4f(transformation);

            for (Vector3f pos : this.bodies.get(from).models.get(0).mesh.positions) {
               tmp.transformPosition(pos);
            }

            for (Vector3f normal : this.bodies.get(from).models.get(0).mesh.normals) {
               normalTransformation.transform(normal);
            }
         }
      }

      return ragdollJoint;
   }

   public RagdollJoint addConnection(int from, int connectedTo, boolean fixed) {
      return this.addConnection(from, connectedTo, fixed, false);
   }

   public void addOverlayConnections(boolean onlyVisual, int bodiesSize, int offset, int multiple) {
      int size = bodiesSize / multiple;

      for (int j = 1; j < multiple; j++) {
         for (int i = 0; i < size; i++) {
            int count = i + size * j + offset;
            if (count < this.bodies.size()) {
               this.addConnection(count, i, true, onlyVisual);
            }
         }
      }
   }

   public void addOverlayConnections(boolean onlyVisual, int multiple) {
      this.addOverlayConnections(onlyVisual, this.bodies.size(), 0, multiple);
   }

   public void addOverlayConnections(boolean onlyVisual) {
      this.addOverlayConnections(onlyVisual, this.bodies.size(), 0, 2);
   }

   public void addOverlayConnections() {
      this.addOverlayConnections(false);
   }

   public void removeUnused() {
      IntSet used = new IntOpenHashSet();

      for (RagdollJoint j : this.joints) {
         used.add(j.index1);
         used.add(j.index2);
      }

      int n = this.bodies.size();
      Int2IntOpenHashMap remap = new Int2IntOpenHashMap(n);
      remap.defaultReturnValue(-1);
      ArrayList<PhysicsEntity> compact = new ArrayList<>(used.size());
      int oldIdx = 0;

      for (int newIdx = 0; oldIdx < n; oldIdx++) {
         if (used.contains(oldIdx)) {
            remap.put(oldIdx, newIdx);
            compact.add(this.bodies.get(oldIdx));
            newIdx++;
         }
      }

      this.bodies.clear();
      this.bodies.addAll(compact);

      for (RagdollJoint j : this.joints) {
         int a = remap.get(j.index1);
         int b = remap.get(j.index2);
         if (a < 0 || b < 0) {
            throw new IllegalStateException("Joint references removed body: " + j);
         }

         j.index1 = a;
         j.index2 = b;
      }
   }

   public RagdollJoint addConnection(int from, int connectedTo) {
      return this.addConnection(from, connectedTo, false);
   }

   public List<Ragdoll.Node> generateTree() {
      List<Ragdoll.Node> roots = new ObjectArrayList();
      List<PhysicsEntity> notUsedBodies = new ObjectArrayList(this.bodies);

      for (int j = 0; j < this.joints.size(); j++) {
         RagdollJoint joint = this.joints.get(j);
         if (notUsedBodies.contains(this.bodies.get(joint.index2))) {
            Ragdoll.Node parent = new Ragdoll.Node(joint.index2, -1);
            roots.add(parent);
            notUsedBodies.remove(this.bodies.get(joint.index2));
            this.searchChildren(parent, notUsedBodies);
         }
      }

      for (PhysicsEntity body : notUsedBodies) {
         Ragdoll.Node parent = new Ragdoll.Node(this.bodies.indexOf(body), -1);
         roots.add(parent);
      }

      return roots;
   }

   private void searchChildren(Ragdoll.Node parent, List<PhysicsEntity> notUsedBodies) {
      if (!notUsedBodies.isEmpty()) {
         for (int i = 0; i < this.joints.size(); i++) {
            RagdollJoint joint = this.joints.get(i);
            if (notUsedBodies.contains(this.bodies.get(joint.index2)) && parent.index == joint.index1) {
               Ragdoll.Node child = new Ragdoll.Node(joint.index2, i);
               parent.children.add(child);
               notUsedBodies.remove(this.bodies.get(joint.index2));
               this.searchChildren(child, notUsedBodies);
            } else if (notUsedBodies.contains(this.bodies.get(joint.index1)) && parent.index == joint.index2) {
               Ragdoll.Node child = new Ragdoll.Node(joint.index1, i);
               parent.children.add(child);
               notUsedBodies.remove(this.bodies.get(joint.index1));
               this.searchChildren(child, notUsedBodies);
            }
         }
      }
   }

   public void add(PhysicsWorld physics) {
   }

   protected void createChildLinkPrePass(Ragdoll.Node parent, Ragdoll.Node root) {
      PhysicsEntity particle = this.bodies.get(root.index);
      RagdollJoint rjoint = this.joints.get(root.jointIndex);
      if (rjoint.fixed) {
         PhysicsEntity parentParticle = this.bodies.get(parent.index);
         Model model = particle.models.get(0);
         Mesh mesh = model.mesh;
         Vector3f diff = mesh.offset.sub(parentParticle.models.get(0).mesh.offset);

         for (int i = 0; i < mesh.positions.size(); i++) {
            mesh.positions.get(i).add(diff);
         }

         mesh.getRadius(true);
         parentParticle.models.add(model);
      } else {
         for (int i = 0; i < root.children.size(); i++) {
            this.createChildLinkPrePass(root, root.children.get(i));
         }
      }
   }

   public void remove(PhysicsWorld physicsWorld) {
      for (Ragdoll.LinkedBody link : this.btBodies) {
         IRigidBody body = link.rigid();
         physicsWorld.removeBody(body);
         if (body.getLastChunk() != Long.MAX_VALUE && !body.isKinematicOrFrozen()) {
            physicsWorld.removeLoadedChunkEntity(body.getLastChunk());
         }
      }
   }

   public boolean isKinematic() {
      return this.kinematic;
   }

   public void setKinematic(boolean kinematic) {
      if (this.kinematic != kinematic) {
         for (Ragdoll.LinkedBody body : this.btBodies) {
            body.rigid().setKinematic(kinematic);
         }

         this.kinematic = kinematic;
      }
   }

   public void setFrozen(boolean frozen) {
      if (this.frozen != frozen) {
         for (Ragdoll.LinkedBody body : this.btBodies) {
            body.rigid().setFrozen(frozen);
         }

         this.frozen = frozen;
      }
   }

   public boolean isFrozen() {
      return this.frozen;
   }

   public void destroy() {
      for (Ragdoll.LinkedBody body : this.btBodies) {
         body.rigid().getEntity().destroy();
      }
   }

   @Override
   public void setNode(DoublyLinkedList.Node<Ragdoll> node) {
      this.node = node;
   }

   @Override
   public DoublyLinkedList.Node<Ragdoll> getNode() {
      return this.node;
   }

   @Override
   public int hashCode() {
      return this.hashCode;
   }

   public static record LinkedBody(IRigidBody rigid, PhysicsEntity entity) {
   }

   public class Node {
      public int index;
      public int jointIndex;
      public List<Ragdoll.Node> children;

      public Node(int index, int jointIndex) {
         Objects.requireNonNull(Ragdoll.this);
         super();
         this.children = new ObjectArrayList();
         this.index = index;
         this.jointIndex = jointIndex;
      }

      @Override
      public String toString() {
         return super.toString();
      }

      public String generateString(int indents) {
         String t = "";

         for (int i = 0; i < indents; i++) {
            t = t + ">";
         }

         t = t + this.index + "\n";

         for (int i = 0; i < this.children.size(); i++) {
            t = t + this.children.get(i).generateString(indents + 1);
         }

         return t;
      }
   }
}
