package net.diebuddies.bridge;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.physics.settings.PhysicsSettingsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping.Category;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.util.Lazy;

public class KeyBindingsRegistry {
   public static final Category MAIN_CATEGORY = new Category(Identifier.fromNamespaceAndPath("physicsmod", "general"));
   public static final Lazy<KeyMapping> GRAB_PHYSICS = Lazy.of(
      () -> new KeyMapping("physicsmod.keybinding.grabobject", KeyConflictContext.IN_GAME, Type.KEYSYM, InputConstants.UNKNOWN.getValue(), MAIN_CATEGORY)
   );
   public static final Lazy<KeyMapping> GUI_PHYSICS = Lazy.of(
      () -> new KeyMapping("physicsmod.keybinding.guiphysics", KeyConflictContext.GUI, Type.KEYSYM, 295, MAIN_CATEGORY)
   );
   public static final Lazy<KeyMapping> PHYISCS_MENU = Lazy.of(
      () -> new KeyMapping("physicsmod.keybinding.physicsmenu", KeyConflictContext.UNIVERSAL, Type.KEYSYM, 296, MAIN_CATEGORY)
   );
   public static final Lazy<KeyMapping> TOGGLE_PHYSICS = Lazy.of(
      () -> new KeyMapping("physicsmod.keybinding.togglephysics", KeyConflictContext.IN_GAME, Type.KEYSYM, InputConstants.UNKNOWN.getValue(), MAIN_CATEGORY)
   );
   public static final Lazy<KeyMapping> PHYSICS_DEBUG_OVERLAY = Lazy.of(
      () -> new KeyMapping("physicsmod.keybinding.debug", KeyConflictContext.IN_GAME, Type.KEYSYM, InputConstants.UNKNOWN.getValue(), MAIN_CATEGORY)
   );

   public static void register(IEventBus modEventBus) {
      NeoForge.EVENT_BUS.addListener(KeyBindingsRegistry::onClientTick);
      modEventBus.addListener(KeyBindingsRegistry::registerBindings);
   }

   @SubscribeEvent
   public static void registerBindings(RegisterKeyMappingsEvent event) {
      event.registerCategory(MAIN_CATEGORY);
      event.register((KeyMapping)GRAB_PHYSICS.get());
      event.register((KeyMapping)GUI_PHYSICS.get());
      event.register((KeyMapping)PHYISCS_MENU.get());
      event.register((KeyMapping)TOGGLE_PHYSICS.get());
      event.register((KeyMapping)PHYSICS_DEBUG_OVERLAY.get());
   }

   @SubscribeEvent
   public static void onClientTick(Post event) {
      while (((KeyMapping)GRAB_PHYSICS.get()).consumeClick()) {
      }

      while (((KeyMapping)GUI_PHYSICS.get()).consumeClick()) {
      }

      while (((KeyMapping)PHYISCS_MENU.get()).consumeClick()) {
         Minecraft.getInstance().setScreenAndShow(new PhysicsSettingsScreen(null));
      }

      while (((KeyMapping)TOGGLE_PHYSICS.get()).consumeClick()) {
         ConfigClient.toggleSettings();
      }

      while (((KeyMapping)PHYSICS_DEBUG_OVERLAY.get()).consumeClick()) {
         ConfigClient.renderPhysicsDebugOverlay = !ConfigClient.renderPhysicsDebugOverlay;
      }
   }
}
