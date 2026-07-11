package net.diebuddies.mixins.sodium;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.diebuddies.compat.Sodium;
import net.diebuddies.physics.Mesh;
import net.diebuddies.physics.Model;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.render.PhysicsFeatureRenderDispatcher;
import net.minecraft.client.renderer.SpriteCoordinateExpander;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(
   value = {SpriteCoordinateExpander.class},
   priority = 2010
)
public class MixinVertexTransform {
   @Inject(
      at = {@At("HEAD")},
      method = {"transform"}
   )
   private static void physicsmod$transformSprite(long ptr, int count, VertexFormat format, float minU, float minV, float maxU, float maxV, CallbackInfo info) {
      if (PhysicsMod.sodiumCatch && StarterClient.sodium) {
         Model model = PhysicsFeatureRenderDispatcher.BlockEntityGroup.currentConsumer.getModel();
         if (model == null) {
            return;
         }

         float textureWidth = maxU - minU;
         float textureHeight = maxV - minV;
         Mesh mesh = model.mesh;
         int counter = 0;
         long offsetUV = Sodium.getTextureElementOffset(format);
         long stride = (long)format.getVertexSize();

         for (int i = mesh.sodiumUVOffset; i < mesh.uvs.size() && counter < count; counter++) {
            Vector2f uv = mesh.uvs.get(i);
            float u = MemoryUtil.memGetFloat(ptr + offsetUV + 0L);
            float v = MemoryUtil.memGetFloat(ptr + offsetUV + 4L);
            uv.x = minU + textureWidth * u;
            uv.y = minV + textureHeight * v;
            ptr += stride;
            i++;
         }

         mesh.sodiumUVOffset = mesh.uvs.size();
      }
   }
}
