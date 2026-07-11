package net.diebuddies.mixins;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public class MixinMinecraft {
   @Inject(
      at = {@At("HEAD")},
      method = {"clearClientLevel(Lnet/minecraft/client/gui/screens/Screen;)V"}
   )
   public void clearLevel(Screen screen, CallbackInfo info) {
      ObjectIterator var3 = PhysicsMod.getInstances().values().iterator();

      while (var3.hasNext()) {
         PhysicsMod mod = (PhysicsMod)var3.next();
         mod.getPhysicsWorld().destroy();
      }

      PhysicsMod.getInstances().clear();
   }
}
