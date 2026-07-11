package net.diebuddies.mixins.settings;

import com.llamalad7.mixinextras.sugar.Local;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.minecraft.PhysicsDebugOverlay;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Gui.class})
public class MixinGui {
   @Shadow
   @Final
   private Minecraft minecraft;
   @Unique
   private PhysicsDebugOverlay physicsmod$debugOverlay;

   @Inject(
      at = {@At("RETURN")},
      method = {"extractRenderState"}
   )
   public void physicsmod$renderDebugInfo(
      DeltaTracker deltaTracker, boolean shouldRenderLevel, boolean resourcesLoaded, CallbackInfo info, @Local GuiGraphicsExtractor graphics
   ) {
      if (this.physicsmod$debugOverlay == null) {
         this.physicsmod$debugOverlay = new PhysicsDebugOverlay(this.minecraft);
      }

      if (ConfigClient.renderPhysicsDebugOverlay) {
         this.physicsmod$debugOverlay.render(graphics);
      }
   }
}
