package net.diebuddies.physics.settings.gui;

import net.diebuddies.physics.settings.ux.GUIResources;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.network.chat.Component;

public class EditButton extends FunctionButton {
   public EditButton(int i, int j, int k, int l, Component component, OnPress onPress) {
      super(i, j, k, l, component, onPress, GUIResources.EDIT_TEXTURE);
   }
}
