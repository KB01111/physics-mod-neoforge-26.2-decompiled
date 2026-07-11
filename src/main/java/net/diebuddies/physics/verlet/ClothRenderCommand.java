package net.diebuddies.physics.verlet;

import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.world.entity.LivingEntity;

public class ClothRenderCommand {
   public Cloth cloth;
   public GpuTextureView textureID;
   public LivingEntity entity;
   public ModelPart modelPart;
   public PartPose modelPose;
   public int brightness;
   public boolean onlyRenderPlayer;

   public ClothRenderCommand(Cloth cloth, GpuTextureView textureID, LivingEntity entity, ModelPart part, int brightness) {
      this.cloth = cloth;
      this.textureID = textureID;
      this.entity = entity;
      this.modelPart = part;
      this.modelPose = part.storePose();
      this.brightness = brightness;
   }

   public ClothRenderCommand(Cloth cloth, LivingEntity entity, ModelPart part, int brightness) {
      this(cloth, cloth.getTextureView(entity), entity, part, brightness);
   }

   public ClothRenderCommand setOnlyRenderPlayer(boolean onlyRenderPlayer) {
      this.onlyRenderPlayer = onlyRenderPlayer;
      return this;
   }
}
