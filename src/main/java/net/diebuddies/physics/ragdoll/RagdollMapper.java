package net.diebuddies.physics.ragdoll;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.config.ConfigMobs;
import net.diebuddies.physics.PhysicsEntity;
import net.diebuddies.physics.settings.mobs.MobPhysicsType;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.animal.llama.LlamaModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;

public class RagdollMapper {
   private static final List<RagdollHook> hooks = new ObjectArrayList();
   private static final RagdollHook vanillaHook = new VanillaRagdollHook();

   public static void addHook(RagdollHook hook) {
      hooks.add(hook);
   }

   public static void removeHook(RagdollHook hook) {
      hooks.remove(hook);
   }

   public static Ragdoll map(MobPhysicsType type, Entity entity, EntityModel model, List<PhysicsEntity> parts, EntityRenderState renderState) {
      Ragdoll ragdoll;
      if (type == MobPhysicsType.RAGDOLL) {
         ragdoll = new BreakableRagdoll(-1.0F);
      } else {
         ragdoll = new BreakableRagdoll(ConfigClient.jointBreakForce);
      }

      ragdoll.bodies.addAll(parts);

      for (RagdollHook hook : hooks) {
         hook.map(ragdoll, entity, model, renderState);
      }

      if (ragdoll.joints.size() > 0) {
         return ragdoll;
      } else {
         vanillaHook.map(ragdoll, entity, model, renderState);
         return ragdoll.joints.size() == 0 ? null : ragdoll;
      }
   }

   public static void filterCuboidsFromEntities(Entity entity, EntityModel model, List<PhysicsEntity> parts) {
      for (RagdollHook hook : hooks) {
         hook.filterCuboidsFromEntities(parts, entity, model);
      }

      vanillaHook.filterCuboidsFromEntities(parts, entity, model);
   }

   public static void printModelParts(EntityModel model) {
      int index = 0;

      for (ModelPart part : (List<ModelPart>)(Object)model.allParts()) {
         index = printModelPart(part, index);
      }

      System.out.println(model.getClass());
      System.out.println("total: " + index);
   }

   public static int countModelParts(Entity entity, EntityModel model) {
      if (model instanceof LlamaModel) {
         int total = 9;
         if (!((AbstractChestedHorse)entity).isBaby() && ((AbstractChestedHorse)entity).hasChest()) {
            total = 11;
         }

         return total;
      } else {
         int total = 0;
         return printModelPart(model.root(), total, true);
      }
   }

   public static boolean areRagdollsEnabled(Entity entity) {
      MobPhysicsType type = ConfigMobs.getMobSetting(entity).getType();
      return type == MobPhysicsType.RAGDOLL || type == MobPhysicsType.RAGDOLL_BREAK || type == MobPhysicsType.RAGDOLL_BREAK_BLOOD;
   }

   public static boolean isMobFracturingEnabled(Entity entity) {
      MobPhysicsType type = ConfigMobs.getMobSetting(entity).getType();
      return type == MobPhysicsType.FRACTURED || type == MobPhysicsType.FRACTURED_BLOOD;
   }

   public static int printModelPart(ModelPart part, int index, boolean hidePrint) {
      if (part.visible) {
         for (int i = 0; i < part.cubes.size(); i++) {
            index++;
         }

         for (Entry<String, ModelPart> entry : part.children.entrySet()) {
            if (!hidePrint) {
               System.out.println(entry.getKey() + ": " + index);
            }

            ModelPart child = entry.getValue();
            index = printModelPart(child, index, hidePrint);
         }
      }

      return index;
   }

   public static int getModelPartIndices(ModelPart part, RagdollMapper.Counter counter, Map<String, RagdollMapper.ModelPartIndex> indices) {
      if (part.visible) {
         counter.count = counter.count + part.cubes.size();

         for (Entry<String, ModelPart> entry : part.children.entrySet()) {
            String name = entry.getKey();
            ModelPart child = entry.getValue();
            RagdollMapper.ModelPartIndex index = indices.get(name);
            if (index == null) {
               index = new RagdollMapper.ModelPartIndex(part, counter.count);
               indices.put(name, index);
            } else {
               index.overlays.add(new RagdollMapper.ModelPartIndex(part, counter.count));
            }

            counter.count = getModelPartIndices(child, counter, indices);
         }
      }

      return counter.count;
   }

   public static Map<String, RagdollMapper.ModelPartIndex> getModelPartIndices(EntityModel model) {
      Map<String, RagdollMapper.ModelPartIndex> indices = new Object2ObjectOpenHashMap();
      RagdollMapper.Counter counter = new RagdollMapper.Counter();

      for (ModelPart part : (List<ModelPart>)(Object)model.allParts()) {
         getModelPartIndices(part, counter, indices);
      }

      return indices;
   }

   public static int printModelPart(ModelPart part, int index) {
      return printModelPart(part, index, false);
   }

   public static int getCuboids(Ragdoll ragdoll, ModelPart part, RagdollMapper.Counter counter, boolean onlyVisual) {
      if (part.visible) {
         for (int c = 1; c < part.cubes.size(); c++) {
            if (counter.count < ragdoll.bodies.size()) {
               ragdoll.addConnection(counter.count + c, counter.count, true, onlyVisual);
            }
         }

         counter.count = counter.count + part.cubes.size();

         for (ModelPart p : part.children.values()) {
            counter.count = getCuboids(ragdoll, p, counter);
         }
      }

      return counter.count;
   }

   public static int getCuboids(Ragdoll ragdoll, ModelPart part, RagdollMapper.Counter counter) {
      return getCuboids(ragdoll, part, counter, false);
   }

   public static void addOverlayConnections(Ragdoll ragdoll, Map<String, RagdollMapper.ModelPartIndex> indices, boolean onlyVisual) {
      for (RagdollMapper.ModelPartIndex index : indices.values()) {
         int originalIndex = index.index;

         for (RagdollMapper.ModelPartIndex overlay : index.overlays) {
            int overlayIndex = overlay.index;
            ModelPart part = overlay.part;
            if (overlayIndex < ragdoll.bodies.size()) {
               ragdoll.addConnection(overlayIndex, originalIndex, true, onlyVisual);
            }

            for (int c = 1; c < part.cubes.size(); c++) {
               if (overlayIndex + c < ragdoll.bodies.size()) {
                  ragdoll.addConnection(overlayIndex + c, originalIndex, true, onlyVisual);
               }
            }
         }
      }
   }

   public static class Counter {
      public int count;
   }

   public static class ModelPartIndex {
      public ModelPart part;
      public List<RagdollMapper.ModelPartIndex> overlays;
      public int index;

      public ModelPartIndex(ModelPart part, int index) {
         this.part = part;
         this.overlays = new ObjectArrayList();
         this.index = index;
      }
   }
}
