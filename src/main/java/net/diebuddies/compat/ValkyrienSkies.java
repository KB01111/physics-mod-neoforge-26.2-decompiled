package net.diebuddies.compat;

import net.diebuddies.physics.ocean.EntityOcean;
import net.diebuddies.physics.ocean.OceanRenderState;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import org.joml.Vector3d;

public class ValkyrienSkies {
   public static EntityOcean hasShipMount(Entity entity) {
      return null;
   }

   public static void doEntityOnShipTransformation(Matrix4f transformation, Entity entity, float renderPercent) {
   }

   public static void extractEntityOnShipTransformation(OceanRenderState renderState, Entity entity, float renderPercent) {
   }

   public static float getEntityOffset(Entity entity, float renderPercent) {
      return 0.0F;
   }

   public static Vector3d getEntityOffset3D(Entity entity, float renderPercent) {
      return null;
   }
}
