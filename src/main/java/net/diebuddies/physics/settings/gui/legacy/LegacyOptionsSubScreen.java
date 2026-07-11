package net.diebuddies.physics.settings.gui.legacy;

import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class LegacyOptionsSubScreen extends Screen {
   protected final Screen lastScreen;
   protected final Options options;

   public LegacyOptionsSubScreen(Screen p_96284_, Options p_96285_, Component p_96286_) {
      super(p_96286_);
      this.lastScreen = p_96284_;
      this.options = p_96285_;
   }

   public void removed() {
      this.minecraft.options.save();
   }

   public void onClose() {
      this.minecraft.setScreenAndShow(this.lastScreen);
   }
}
