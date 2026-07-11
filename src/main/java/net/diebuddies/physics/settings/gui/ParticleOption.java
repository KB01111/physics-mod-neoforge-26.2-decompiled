package net.diebuddies.physics.settings.gui;

import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.animation.ParticleSearchScreen;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.diebuddies.physics.settings.vines.ValueChanged;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ParticleOption extends LegacyOption {
   private String text;
   private String particle;
   private ValueChanged changed;
   private Screen parent;
   private Button button;

   public ParticleOption(String text, String block, Screen parent, ValueChanged changed) {
      super(text);
      this.text = text;
      this.particle = block;
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
         Component.literal(this.text + ": " + this.particle),
         button -> Minecraft.getInstance().setScreenAndShow(new ParticleSearchScreen(this.parent, this))
      );
   }

   public void setParticle(String particle) {
      this.particle = particle;
      if (this.button != null) {
         this.button.setMessage(Component.literal(this.text + ": " + (particle == null ? "null" : particle)));
      }

      this.changed.changed(particle);
   }
}
