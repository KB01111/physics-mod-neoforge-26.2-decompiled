package net.diebuddies.physics.settings.animation;

import java.util.List;
import net.diebuddies.config.ConfigAnimations;
import net.diebuddies.physics.animation.Animation;
import net.diebuddies.physics.animation.CurveType;
import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.ButtonOption;
import net.diebuddies.physics.settings.gui.LabelOption;
import net.diebuddies.physics.settings.gui.ParticleOption;
import net.diebuddies.physics.settings.gui.SoundOption;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.diebuddies.physics.vines.AdjustableUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class AnimationEditScreen extends ParticleDisplayScreen {
   private CustomAnimationList list;
   private Animation setting;
   private long identifier;

   public AnimationEditScreen(Screen parent, Animation setting, long identifier) {
      super(
         parent,
         setting != null ? Component.translatable("physicsmod.menu.animation.edit.title") : Component.translatable("physicsmod.menu.animation.add.title")
      );
      if (setting == null) {
         this.setting = new Animation("new", CurveType.Linear, 0.5F);
         this.identifier = 0L;

         while (ConfigAnimations.animations.containsKey(this.identifier)) {
            this.identifier++;
         }
      } else {
         this.setting = setting.copy();
         this.identifier = identifier;
      }
   }

   @Override
   protected void init() {
      super.init();
      this.addRenderableWidget(ButtonSettings.builder(this.width / 2 - 80, this.height - 27, 75, 20, CommonComponents.GUI_CANCEL, button -> this.onClose()));
      this.addRenderableWidget(ButtonSettings.builder(this.width / 2 + 5, this.height - 27, 75, 20, CommonComponents.GUI_DONE, button -> {
         Animation animation = (Animation)ConfigAnimations.animations.get(this.identifier);
         if (animation != null) {
            animation.set(this.setting);
         } else {
            ConfigAnimations.animations.put(this.identifier, this.setting);
         }

         ConfigAnimations.save();
         this.onClose();
      }));
      this.generateOptions();
      this.animation = this.setting;
      this.startAnimation();
   }

   private void generateOptions() {
      if (this.list != null) {
         this.children.remove(this.list);
      }

      this.list = new CustomAnimationList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.children.add(this.list);
      List<LegacyOption> options = AdjustableUtil.generateOptions(this, this.setting, () -> this.generateOptions(), () -> this.startAnimation());
      int startIndex = 0;

      for (int i = 0; i < options.size(); i++) {
         LegacyOption option = options.get(i);
         if (option instanceof LabelOption || option instanceof ButtonOption || option instanceof ParticleOption || option instanceof SoundOption || i <= 3) {
            if (startIndex != i) {
               List<LegacyOption> subList = options.subList(startIndex, i);
               this.list.addSmall(subList.toArray(new LegacyOption[subList.size()]));
            }

            this.list.addBig(option);
            startIndex = i + 1;
         }
      }

      if (startIndex != options.size()) {
         List<LegacyOption> subList = options.subList(startIndex, options.size());
         this.list.addSmall(subList.toArray(new LegacyOption[subList.size()]));
      }
   }

   @Override
   public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
      this.list.extractRenderState(guiGraphics, mouseX, mouseY, delta);
      super.extractRenderState(guiGraphics, mouseX, mouseY, delta);
   }

   @Override
   public void extractBackground(GuiGraphicsExtractor guiGraphics, int i, int j, float f) {
   }
}
