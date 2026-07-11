package net.diebuddies.physics.settings.gui.legacy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.diebuddies.physics.settings.ux.BaseRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public class LegacyOptionsList extends LegacyContainerObjectSelectionList<LegacyOptionsList.LegacyEntry> {
   public Runnable renderBeforeTooltips;

   public LegacyOptionsList(Minecraft minecraft, int i, int j, int k, int l, int m) {
      super(minecraft, i, j, k, l, m);
      this.centerListVertically = false;
   }

   public int addBig(LegacyOption option) {
      return this.addEntry(LegacyOptionsList.LegacyEntry.big(this.minecraft.options, this.width, option));
   }

   public void addSmall(LegacyOption option, @Nullable LegacyOption option2) {
      this.addEntry(LegacyOptionsList.LegacyEntry.small(this.minecraft.options, this.width, option, option2));
   }

   public void addSmall(LegacyOption[] options) {
      for (int i = 0; i < options.length; i += 2) {
         this.addSmall(options[i], i < options.length - 1 ? options[i + 1] : null);
      }
   }

   @Override
   public int getRowWidth() {
      return 400;
   }

   @Override
   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 32;
   }

   @Nullable
   public AbstractWidget findOption(LegacyOption option) {
      for (LegacyOptionsList.LegacyEntry entry : this.children()) {
         AbstractWidget abstractWidget = entry.options.get(option);
         if (abstractWidget != null) {
            return abstractWidget;
         }
      }

      return null;
   }

   public Optional<AbstractWidget> getMouseOver(double d, double e) {
      for (LegacyOptionsList.LegacyEntry entry : this.children()) {
         for (AbstractWidget abstractWidget : entry.children) {
            if (abstractWidget.isMouseOver(d, e)) {
               return Optional.of(abstractWidget);
            }
         }
      }

      return Optional.empty();
   }

   public static Component tooltipAt(LegacyOptionsList optionsList, int i, int j) {
      Optional<AbstractWidget> optional = optionsList.getMouseOver((double)i, (double)j);
      return optional.isPresent() && optional.get() instanceof UXTooltipAccessor ? ((UXTooltipAccessor)optional.get()).getTooltipLegacy() : null;
   }

   public static AbstractWidget widgetAt(LegacyOptionsList optionsList, int i, int j) {
      Optional<AbstractWidget> optional = optionsList.getMouseOver((double)i, (double)j);
      return optional.isPresent() && optional.get() instanceof UXTooltipAccessor ? optional.get() : null;
   }

   @Override
   protected void renderDecorations(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
      super.renderDecorations(guiGraphics, mouseX, mouseY);
      BaseRenderer.renderSettingsTooltip(this, guiGraphics, mouseX, mouseY, this.width, this.height);
   }

   protected static class LegacyEntry extends LegacyContainerObjectSelectionList.LegacyEntry<LegacyOptionsList.LegacyEntry> {
      final Map<LegacyOption, AbstractWidget> options;
      final List<AbstractWidget> children;

      private LegacyEntry(Map<LegacyOption, AbstractWidget> map) {
         this.options = map;
         this.children = ImmutableList.copyOf(map.values());
      }

      public static LegacyOptionsList.LegacyEntry big(Options options, int i, LegacyOption option) {
         return new LegacyOptionsList.LegacyEntry(ImmutableMap.of(option, option.createButton(options, i / 2 - 155, 0, 310)));
      }

      public static LegacyOptionsList.LegacyEntry small(Options options, int i, LegacyOption option, @Nullable LegacyOption option2) {
         AbstractWidget abstractWidget = option.createButton(options, i / 2 - 155, 0, 150);
         return option2 == null
            ? new LegacyOptionsList.LegacyEntry(ImmutableMap.of(option, abstractWidget))
            : new LegacyOptionsList.LegacyEntry(ImmutableMap.of(option, abstractWidget, option2, option2.createButton(options, i / 2 - 155 + 160, 0, 150)));
      }

      @Override
      public void extractRenderState(GuiGraphicsExtractor guiGraphics, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
         this.children.forEach(abstractWidget -> {
            abstractWidget.setY(j);
            abstractWidget.extractRenderState(guiGraphics, n, o, f);
         });
      }

      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      @Override
      public List<? extends NarratableEntry> narratables() {
         return this.children;
      }
   }
}
