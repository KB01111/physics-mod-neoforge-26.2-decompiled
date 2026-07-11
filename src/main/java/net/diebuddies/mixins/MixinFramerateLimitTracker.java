package net.diebuddies.mixins;

import com.mojang.blaze3d.platform.FramerateLimitTracker;
import net.diebuddies.physics.settings.PhysicsSettingsScreen;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({FramerateLimitTracker.class})
public class MixinFramerateLimitTracker {
   @Shadow
   @Final
   private Minecraft minecraft;

   @Inject(
      at = {@At("HEAD")},
      method = {"getFramerateLimit"},
      cancellable = true
   )
   private void physicsmod$adjustPhysicsSettingsFPS(CallbackInfoReturnable<Integer> info) {
      if (this.minecraft.level == null
         && (this.minecraft.gui.screen() != null || this.minecraft.gui.overlay() != null)
         && this.minecraft.gui.screen() instanceof PhysicsSettingsScreen) {
         info.setReturnValue(120);
      }
   }
}
