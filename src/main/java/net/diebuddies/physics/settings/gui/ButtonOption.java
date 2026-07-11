package net.diebuddies.physics.settings.gui;

import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;

public class ButtonOption extends LegacyOption {
   private String value;
   private OnPress consumer;

   public ButtonOption(String value, OnPress consumer) {
      super(value);
      this.value = value;
      this.consumer = consumer;
   }

   @Override
   public AbstractWidget createButton(Options options, int x, int y, int width) {
      return ButtonSettings.builder(x, y, width, 20, Component.literal(this.value), this.consumer);
   }
}
