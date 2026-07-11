package net.diebuddies.physics.settings.ux;

import net.diebuddies.physics.settings.gui.legacy.LegacyOptionsList;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

public class BaseRenderer {
   public static void renderSettingsTooltip(LegacyOptionsList list, GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, int width, int height) {
      Component tooltip = LegacyOptionsList.tooltipAt(list, mouseX, mouseY);
      if (tooltip != null) {
         float border = 28.0F;
         AbstractWidget widget = LegacyOptionsList.widgetAt(list, mouseX, mouseY);
         if (!widget.isHovered()) {
            return;
         }

         if ((float)mouseY > (float)height * 0.6F) {
            MainToolTipRenderer.renderToolTip(
               MainToolTipRenderer.TooltipAlignment.TOP,
               (Animatable)widget,
               tooltip,
               guiGraphics,
               border,
               (float)width - border * 2.0F,
               border,
               1.0F,
               ARGB.color(255, 0, 0, 0)
            );
         } else {
            MainToolTipRenderer.renderToolTip(
               (Animatable)widget, tooltip, guiGraphics, border, (float)width - border * 2.0F, (float)height - border, 1.0F, ARGB.color(255, 0, 0, 0)
            );
         }
      }
   }
}
