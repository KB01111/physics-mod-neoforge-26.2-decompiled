package net.diebuddies.physics.settings.gui;

import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.animation.SoundSearchScreen;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.diebuddies.physics.settings.vines.ValueChanged;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class SoundOption extends LegacyOption {
   private String text;
   private String sound;
   private ValueChanged changed;
   private Screen parent;
   private Button button;

   public SoundOption(String text, String sound, Screen parent, ValueChanged changed) {
      super(text);
      this.text = text;
      this.sound = sound;
      this.changed = changed;
      this.parent = parent;
   }

   @Override
   public AbstractWidget createButton(Options options, int i, int j, int k) {
      return this.button = ButtonSettings.builder(
         i,
         j,
         k,
         20,
         Component.literal(this.text + ": " + this.sound),
         button -> Minecraft.getInstance().setScreenAndShow(new SoundSearchScreen(this.parent, this))
      );
   }

   public void setSound(String sound) {
      this.sound = sound;
      if (this.button != null) {
         this.button.setMessage(Component.literal(this.text + ": " + (sound == null ? "null" : sound)));
      }

      this.changed.changed(sound);
   }
}
