package net.diebuddies.mixins.ocean;

import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Screen.class})
public class MixinScreen {
   @Inject(
      at = {@At("HEAD")},
      method = {"extractRenderStateWithTooltipAndSubtitles"}
   )
   public void physicsmod$renderHead(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo info) {
      PhysicsMod.stopOceanDisplacement = true;
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"extractRenderStateWithTooltipAndSubtitles"}
   )
   public void physicsmod$renderTail(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo info) {
      PhysicsMod.stopOceanDisplacement = false;
   }
}
