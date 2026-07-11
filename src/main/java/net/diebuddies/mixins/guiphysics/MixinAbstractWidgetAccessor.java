package net.diebuddies.mixins.guiphysics;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.WidgetTooltipHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({AbstractWidget.class})
public interface MixinAbstractWidgetAccessor {
   @Accessor("isHovered")
   boolean getIsHovered();

   @Accessor("isHovered")
   void setIsHovered(boolean var1);

   @Accessor("focused")
   void setFocused(boolean var1);

   @Accessor("tooltip")
   WidgetTooltipHolder getTooltipHolder();
}
