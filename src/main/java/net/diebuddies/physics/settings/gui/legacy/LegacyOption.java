package net.diebuddies.physics.settings.gui.legacy;

import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public abstract class LegacyOption {
   private final MutableComponent caption;

   public LegacyOption(String string) {
      this.caption = Component.translatable(string);
   }

   public abstract AbstractWidget createButton(Options var1, int var2, int var3, int var4);

   protected Component getCaption() {
      return this.caption;
   }

   public Component customFormat(String translatable, String literal) {
      return Component.translatable("physicsmod.menu.options.format", new Object[]{Component.translatable(translatable), Component.literal(literal)});
   }

   public Component pixelValueLabel(int i) {
      return Component.translatable("options.pixel_value", new Object[]{this.getCaption(), i});
   }

   public Component percentValueLabel(double d) {
      return Component.translatable("options.percent_value", new Object[]{this.getCaption(), (int)(d * 100.0)});
   }

   public Component percentAddValueLabel(int i) {
      return Component.translatable("options.percent_add_value", new Object[]{this.getCaption(), i});
   }

   public Component genericValueLabel(Component component) {
      return Component.translatable("options.generic_value", new Object[]{this.getCaption(), component});
   }

   public Component genericValueLabel(int i) {
      return this.genericValueLabel(Component.literal(Integer.toString(i)));
   }
}
