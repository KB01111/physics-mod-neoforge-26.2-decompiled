package net.diebuddies.mixins;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import net.diebuddies.physics.settings.PipPoolHack;
import net.diebuddies.physics.settings.gui.GuiPhysicsCustomRenderState;
import net.diebuddies.physics.settings.gui.GuiPhysicsCustomRenderer;
import net.diebuddies.physics.settings.gui.GuiPhysicsEntityRenderState;
import net.diebuddies.physics.settings.gui.GuiPhysicsEntityRenderer;
import net.diebuddies.physics.settings.gui.RendererReset;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.neoforged.neoforge.client.gui.PictureInPictureRendererPool;
import net.neoforged.neoforge.client.gui.PictureInPictureRendererRegistration;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiRenderer.class})
public class MixinGuiRenderer {
   @Shadow
   @Final
   private Map<Class<? extends PictureInPictureRenderState>, PictureInPictureRendererPool<?>> pictureInPictureRendererPools;

   @ModifyVariable(
      at = @At("HEAD"),
      method = {"<init>"},
      ordinal = 0
   )
   private static List<PictureInPictureRendererRegistration<?>> physicsmod$addPhysicsRenderer(
      List<PictureInPictureRendererRegistration<?>> pipRendererFactories
   ) {
      List<PictureInPictureRendererRegistration<?>> pips = new ObjectArrayList(pipRendererFactories);
      pips.add(
         new PictureInPictureRendererRegistration(
            GuiPhysicsEntityRenderState.class, () -> new GuiPhysicsEntityRenderer(Minecraft.getInstance().getEntityRenderDispatcher())
         )
      );
      pips.add(new PictureInPictureRendererRegistration(GuiPhysicsCustomRenderState.class, () -> new GuiPhysicsCustomRenderer()));
      return List.copyOf(pips);
   }

   @Inject(
      at = {@At("HEAD")},
      method = {"preparePictureInPicture"}
   )
   private void physicsmod$resetTexture(CallbackInfo info) {
      this.pictureInPictureRendererPools.forEach((clzz, pool) -> {
         Map<?, ?> renderers = PipPoolHack.getRenderersLastFrame(pool);
         renderers.forEach((state, renderer) -> {
            if (renderer instanceof RendererReset rendererExtension) {
               rendererExtension.reset();
            }
         });
      });
   }
}
