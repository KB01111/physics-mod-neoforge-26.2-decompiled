package net.diebuddies.physics.verlet;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.diebuddies.model.ClothMesh;
import net.diebuddies.model.ColladaMesh;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public class Cloth implements Comparable<Cloth> {
   public final String name;
   public final ColladaMesh mesh;
   private final Identifier texture;
   private final GpuSampler sampler;
   public final ClothRules rules;
   @Nullable
   public final ColladaMesh playerMesh;
   @Nullable
   private ClothMesh smoothMesh;
   @Nullable
   private ClothMesh flatMesh;
   @Nullable
   private ClothMesh playerRenderMesh;

   public Cloth(String name, ColladaMesh mesh, @Nullable ColladaMesh playerMesh, Identifier texture, ClothRules rules) {
      this.name = name;
      this.mesh = mesh;
      this.texture = texture;
      this.sampler = RenderSystem.getSamplerCache().getRepeat(FilterMode.NEAREST, false);
      this.rules = rules;
      this.playerMesh = playerMesh;
   }

   public Identifier getTexture(@Nullable Entity entity) {
      String specialTexture = this.rules.getSpecialTexture();
      if (specialTexture != null) {
         if (specialTexture.equals("minecraft:playerskin")) {
            return entity instanceof AbstractClientPlayer player ? player.getSkin().body().texturePath() : null;
         } else {
            return Identifier.parse(specialTexture);
         }
      } else {
         return this.texture;
      }
   }

   @Nullable
   public GpuTextureView getTextureView(@Nullable Entity entity) {
      Identifier identifier = this.getTexture(entity);
      return identifier == null ? null : Minecraft.getInstance().getTextureManager().getTexture(identifier).getTextureView();
   }

   public GpuSampler getSampler() {
      return this.sampler;
   }

   public ClothMesh getRenderMesh(boolean flatShading) {
      if (flatShading) {
         if (this.flatMesh == null) {
            this.flatMesh = this.mesh.createRenderMesh(true);
         }

         return this.flatMesh;
      } else {
         if (this.smoothMesh == null) {
            this.smoothMesh = this.mesh.createRenderMesh(false);
         }

         return this.smoothMesh;
      }
   }

   @Nullable
   public ClothMesh getPlayerRenderMesh(int brightness) {
      if (this.playerMesh == null) {
         return null;
      } else {
         if (this.playerRenderMesh == null) {
            this.playerRenderMesh = this.playerMesh.createRenderMesh(true);
         }

         return this.playerRenderMesh;
      }
   }

   public void destroy() {
      closeMesh(this.smoothMesh);
      this.smoothMesh = null;
      closeMesh(this.flatMesh);
      this.flatMesh = null;
      closeMesh(this.playerRenderMesh);
      this.playerRenderMesh = null;
      if (this.texture != null) {
         Minecraft.getInstance().getTextureManager().release(this.texture);
      }
   }

   private static void closeMesh(@Nullable ClothMesh mesh) {
      if (mesh != null) {
         mesh.close();
      }
   }

   public int compareTo(Cloth o) {
      return this.name.compareTo(o.name);
   }
}
