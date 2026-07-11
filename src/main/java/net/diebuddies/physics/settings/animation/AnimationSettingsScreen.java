package net.diebuddies.physics.settings.animation;

import net.diebuddies.config.ConfigAnimations;
import net.diebuddies.config.ConfigBlocks;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.PopupWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class AnimationSettingsScreen extends ParticleDisplayScreen {
   private AnimationEditList animationList;

   public AnimationSettingsScreen(Screen parent) {
      super(parent, Component.translatable("physicsmod.menu.animation.settings.title"));
   }

   @Override
   protected void init() {
      super.init();
      this.animationList = new AnimationEditList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.children.add(this.animationList);
      this.addRenderableWidget(
         ButtonSettings.builder(
            this.width / 2 - 130,
            this.height - 27,
            80,
            20,
            Component.translatable("physicsmod.gui.reset"),
            button -> PopupWidget.create(
                  Language.getInstance().getOrDefault("physicsmod.menu.animation.settings.reset"),
                  this,
                  widget -> this.addRenderableWidget(widget),
                  widget -> this.removeWidget(widget),
                  response -> {
                     if (response == PopupWidget.PopupResponse.YES) {
                        ConfigAnimations.loadDefaultConfigSettings();
                        this.children.remove(this.animationList);
                        this.animationList = new AnimationEditList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
                        this.children.add(this.animationList);
                        ConfigAnimations.save();
                     }
                  }
               )
         )
      );
      this.addRenderableWidget(
         ButtonSettings.builder(
            this.width / 2 - 40,
            this.height - 27,
            80,
            20,
            Component.translatable("physicsmod.menu.animation.add"),
            button -> this.minecraft.setScreenAndShow(new AnimationEditScreen(this, null, 0L))
         )
      );
      this.addRenderableWidget(ButtonSettings.builder(this.width / 2 + 50, this.height - 27, 80, 20, CommonComponents.GUI_DONE, button -> {
         ConfigAnimations.save();
         this.onClose();
      }));
   }

   @Override
   public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
      if (this.animationList.getSelected() != null && this.animation != ConfigAnimations.animations.get((Long)this.animationList.getSelected().getUserData())) {
         this.animation = (Animation)ConfigAnimations.animations.get((Long)this.animationList.getSelected().getUserData());
         this.startAnimation();
      }

      this.animationList.extractRenderState(guiGraphics, mouseX, mouseY, delta);
      super.extractRenderState(guiGraphics, mouseX, mouseY, delta);
   }

   @Override
   public void onClose() {
      super.onClose();
      this.reloadAllAnimations();
   }

   @Override
   public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
   }

   private void reloadAllAnimations() {
      ConfigClient.reload();
      ConfigBlocks.reload();
   }
}
