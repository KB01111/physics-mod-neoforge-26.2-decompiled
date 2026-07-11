package net.diebuddies.physics.settings;

import net.diebuddies.physics.settings.gui.FunctionButton;
import net.diebuddies.physics.settings.ux.Animatable;
import net.diebuddies.physics.settings.ux.BarRenderer;
import net.diebuddies.physics.settings.ux.ButtonRenderer;
import net.diebuddies.physics.settings.ux.SliderRenderer;
import net.diebuddies.physics.settings.ux.TextAlignment;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;

public class ButtonSettings {
   public static Button builder(int x, int y, int width, int height, Component component, OnPress onPress) {
      Button button = Button.builder(component, onPress).bounds(x, y, width, height).build();
      addCustomButtonStyle(button);
      return button;
   }

   public static Button builder(int x, int y, int width, int height, Component component, OnPress onPress, Tooltip onTooltip) {
      Button button = Button.builder(component, onPress).bounds(x, y, width, height).tooltip(onTooltip).build();
      addCustomButtonStyle(button);
      return button;
   }

   public static Button builderNoStyle(int x, int y, int width, int height, Component component, OnPress onPress) {
      return Button.builder(component, onPress).bounds(x, y, width, height).build();
   }

   public static Button builderNoStyle(int x, int y, int width, int height, Component component, OnPress onPress, Tooltip onTooltip) {
      return Button.builder(component, onPress).bounds(x, y, width, height).tooltip(onTooltip).build();
   }

   public static Animatable addCustomButtonStyle(FunctionButton button) {
      return ((Animatable)button)
         .addAnimator(new ButtonRenderer(TextAlignment.CENTER).setImage(button.getTexture()), new BarRenderer(BarRenderer.BarAlignment.BOTTOM, true, 1.0F));
   }

   public static Animatable addCustomButtonStyle(AbstractButton button) {
      return ((Animatable)button).addAnimator(new ButtonRenderer(TextAlignment.CENTER), new BarRenderer(BarRenderer.BarAlignment.BOTTOM, true, 1.0F));
   }

   public static Animatable addCustomButtonStyle(AbstractSliderButton button) {
      return ((Animatable)button).addAnimator(new SliderRenderer(TextAlignment.CENTER), new BarRenderer(BarRenderer.BarAlignment.BOTTOM, true, 1.0F));
   }
}
