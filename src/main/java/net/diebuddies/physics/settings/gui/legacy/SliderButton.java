package net.diebuddies.physics.settings.gui.legacy;

import net.diebuddies.physics.settings.ButtonSettings;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractOptionSliderButton;
import net.minecraft.network.chat.Component;

public class SliderButton extends AbstractOptionSliderButton implements UXTooltipAccessor {
   private final ProgressOption option;
   private final Component tooltip;

   public SliderButton(Options options, int i, int j, int k, int l, ProgressOption progressOption, Component component) {
      super(options, i, j, k, l, (double)((float)progressOption.toPct(progressOption.get(options))));
      this.option = progressOption;
      this.tooltip = component;
      this.updateMessage();
      ButtonSettings.addCustomButtonStyle(this);
   }

   protected void applyValue() {
      this.option.set(this.options, this.option.toValue(this.value));
      this.options.save();
   }

   protected void updateMessage() {
      this.setMessage(this.option.getMessage(this.options));
   }

   public Component getTooltipComponent() {
      return this.tooltip;
   }

   public double getValue() {
      return this.value;
   }

   @Override
   public Component getTooltipLegacy() {
      return this.tooltip;
   }

   public boolean isMouseOver(double d, double e) {
      return this.visible
         && d >= (double)this.getX()
         && e >= (double)this.getY()
         && d < (double)(this.getX() + this.width)
         && e < (double)(this.getY() + this.height);
   }
}
