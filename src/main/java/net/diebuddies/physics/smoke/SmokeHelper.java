package net.diebuddies.physics.smoke;

import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.physics.PhysicsMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class SmokeHelper {
   public static boolean addParticle(Level level, double x, double y, double z, float spawnChance) {
      return addParticle(level, x, y, z, spawnChance, 1.0F, SmokeDomain.SmokeParticleStyle.FIRE);
   }

   public static boolean addSteamParticle(Level level, double x, double y, double z, float spawnChance) {
      return addParticle(level, x, y, z, spawnChance, 0.2F, SmokeDomain.SmokeParticleStyle.STEAM);
   }

   public static boolean addParticle(Level level, double x, double y, double z, float scale, float spawnChance, SmokeDomain.SmokeParticleStyle style) {
      if (!(level instanceof ClientLevel clientLevel)) {
         return false;
      } else {
         boolean spawnSmoke = false;
         boolean inRange = false;
         GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
         if (spawnChance <= 0.0F) {
            return false;
         } else {
            if (gameRenderer != null && spawnSmoke) {
               Vec3 view = gameRenderer.mainCamera().position();
               double distSquared = Vector3d.distanceSquared(view.x, view.y, view.z, x, y, z);
               if (distSquared < ConfigClient.smokePhysicsRange * ConfigClient.smokePhysicsRange) {
                  inRange = true;
               }
            }

            if (spawnSmoke && inRange) {
               PhysicsMod mod = PhysicsMod.getInstance(clientLevel);

               for (SmokeDomain smokeDomain = mod.getPhysicsWorld().getSmokeDomain(); spawnChance > 0.0F; spawnChance--) {
                  float fraction = spawnChance - (float)((int)spawnChance);
                  if (spawnChance >= 1.0F || Math.random() < fraction) {
                     mod.getPhysicsWorld().queue(() -> smokeDomain.spawnParticle(x, y, z, 1.0F, true, style));
                  }
               }

               return true;
            } else {
               return false;
            }
         }
      }
   }
}
