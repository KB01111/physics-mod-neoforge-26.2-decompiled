package net.diebuddies.mixins.sodium;

import net.caffeinemc.mods.sodium.api.vertex.format.common.EntityVertex;
import net.diebuddies.opengl.TextureHelper;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.Model;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.render.PhysicsFeatureRenderDispatcher;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin({EntityVertex.class})
public class MixinQuad {
   @Inject(
      at = {@At("HEAD")},
      method = {"write"}
   )
   private static void physicsmod$catchRenderingForBlockEntities(
      long ptr, float x, float y, float z, int color, float u, float v, int light, int overlay, int normal, CallbackInfo info
   ) {
      if (PhysicsMod.sodiumCatchBoundingBox) {
         Vector3d start = PhysicsMod.sodiumBoundingBox.start;
         Vector3d end = PhysicsMod.sodiumBoundingBox.end;
         if ((double)x < start.x) {
            start.x = (double)x;
         }

         if ((double)y < start.y) {
            start.y = (double)y;
         }

         if ((double)z < start.z) {
            start.z = (double)z;
         }

         if ((double)x > end.x) {
            end.x = (double)x;
         }

         if ((double)y > end.y) {
            end.y = (double)y;
         }

         if ((double)z > end.z) {
            end.z = (double)z;
         }
      }

      if (PhysicsMod.sodiumCatch) {
         Model model = PhysicsFeatureRenderDispatcher.BlockEntityGroup.currentConsumer.getModel();
         if (model == null) {
            return;
         }

         model.textureID = TextureHelper.getLoadedTextures();
         Mesh mesh = model.mesh;
         mesh.positions.add(new Vector3f(x, y, z));
         float normRange = 0.007874016F;
         float normX = (float)((byte)(normal & 0xFF)) * normRange;
         float normY = (float)((byte)(normal >> 8 & 0xFF)) * normRange;
         float normZ = (float)((byte)(normal >> 16 & 0xFF)) * normRange;
         mesh.colors.add(color);
         mesh.normals.add(new Vector3f(normX, normY, normZ));
         mesh.uvs.add(new Vector2f(u, v));
         if ((mesh.positions.size() & 3) == 0) {
            int index = mesh.positions.size() - 4;
            mesh.indices.add(index);
            mesh.indices.add(index + 1);
            mesh.indices.add(index + 2);
            mesh.indices.add(index);
            mesh.indices.add(index + 2);
            mesh.indices.add(index + 3);
         }
      }
   }
}
