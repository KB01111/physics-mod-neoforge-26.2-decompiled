package net.diebuddies.physics.settings.vines;

import net.diebuddies.physics.settings.ButtonSettings;
import net.diebuddies.physics.settings.gui.legacy.LegacyOption;
import net.diebuddies.physics.vines.BlockFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class BlockOption extends LegacyOption {
   private String text;
   private String block;
   private boolean canBeNull;
   private ValueChanged changed;
   private Screen parent;
   private Button button;
   private BlockFilter filter;

   public BlockOption(String text, String block, boolean canBeNull, Screen parent, ValueChanged changed) {
      super(text);
      this.text = text;
      this.block = block;
      this.canBeNull = canBeNull;
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
         Component.literal(this.text + ": " + this.block),
         button -> Minecraft.getInstance().setScreenAndShow(new BlockSearchScreen(this.parent, this, this.filter, this.canBeNull))
      );
   }

   public void setFilter(BlockFilter filter) {
      this.filter = filter;
   }

   public void setBlock(String block) {
      this.block = block;
      if (this.button != null) {
         this.button.setMessage(Component.literal(this.text + ": " + (block == null ? "null" : block)));
      }

      this.changed.changed(block);
   }
}
