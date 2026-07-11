package net.diebuddies.mixins;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.settings.ux.Animatable;
import net.diebuddies.physics.settings.ux.GUIResources;
import net.diebuddies.physics.settings.ux.HighlightButtonRenderer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({TitleScreen.class})
public class MixinTitleScreen {
   @Inject(
      at = {@At("RETURN")},
      method = {"registerTextures"},
      cancellable = true
   )
   private static void physicsmod$registerTextures(TextureManager textureManager, CallbackInfo info) {
      textureManager.registerForNextReload(GUIResources.PARALLAX_BLOCKS_BACKGROUND);
      textureManager.registerForNextReload(GUIResources.PARALLAX_BLOCKS_CLOUDS);
      textureManager.registerForNextReload(GUIResources.PARALLAX_BLOCKS_RUBBLE);
      textureManager.registerForNextReload(GUIResources.ARROW);
      textureManager.registerForNextReload(GUIResources.EDIT_TEXTURE);
      textureManager.registerForNextReload(GUIResources.REMOVE_TEXTURE);
      textureManager.registerForNextReload(GUIResources.BACKGROUND_TEXTURE);
   }

   @Inject(
      at = {@At("TAIL")},
      method = {"init"}
   )
   private void init(CallbackInfo info) {
      TitleScreen screen = (TitleScreen)(Object)this;
      if (screen.children != null && ConfigClient.firstStartup) {
         for (GuiEventListener widget : screen.children) {
            if (widget instanceof Button) {
               Button button = (Button)widget;
               Component component = button.getMessage();
               if (component != null) {
                  ComponentContents content = component.getContents();
                  if (content != null && content instanceof TranslatableContents) {
                     TranslatableContents translatable = (TranslatableContents)content;
                     String key = translatable.getKey();
                     if (key != null && key.equalsIgnoreCase("menu.options")) {
                        ((Animatable)button).addAnimator(new HighlightButtonRenderer());
                     }
                  }
               }
            }
         }
      }
   }
}
