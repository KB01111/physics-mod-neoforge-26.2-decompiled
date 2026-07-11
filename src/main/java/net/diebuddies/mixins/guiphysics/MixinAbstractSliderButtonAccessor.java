package net.diebuddies.mixins.guiphysics;

import net.minecraft.client.gui.components.AbstractSliderButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({AbstractSliderButton.class})
public interface MixinAbstractSliderButtonAccessor {
   @Accessor("value")
   double getValue();
}
