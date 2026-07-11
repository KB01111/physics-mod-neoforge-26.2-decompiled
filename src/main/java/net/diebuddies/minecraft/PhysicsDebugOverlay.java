package net.diebuddies.minecraft;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vulkan.VulkanDevice;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.List;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.liquid.Liquid;
import net.diebuddies.physics.ragdoll.DynamicRagdoll;
import net.diebuddies.physics.ragdoll.Ragdoll;
import net.diebuddies.util.PerformanceTracker;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL32C;

public class PhysicsDebugOverlay {
   public static final String OCEAN_RENDERING = "ocean_rendering";
   public static final String LIQUID_RENDERING = "liquid_rendering";
   public static final String SMOKE_RENDERING = "smoke_rendering";
   public static final String SNOW_RENDERING = "snow_rendering";
   public static final String MAIN_RENDERING = "blocks_mobs_particles_rendering";
   public static final String TRANSPARENT_RENDERING = "transparent_blocks_mobs_particles_rendering";
   public static final String CLOTH_RENDERING = "cloth_rendering";
   public static final String PHYSICS_TICK = "physics_tick";
   public static final String PHYSICS_TICK_SMOKE = "physics_tick_smoke";
   public static final String PHYSICS_TICK_PHYSX = "physics_tick_physx";
   public static final String PHYSICS_TICK_ENTITIES = "physics_tick_entities";
   public static final String PHYSICS_TICK_CLOTH = "physics_tick_cloth";
   private static final String TITLE_PREFIX = "" + ChatFormatting.BOLD + ChatFormatting.GOLD;
   private static final String SUB_PREFIX = "" + ChatFormatting.BOLD + ChatFormatting.AQUA;
   private final Minecraft minecraft;
   private final Font font;
   private List<String> debugInformation;

   public PhysicsDebugOverlay(Minecraft minecraft) {
      this.minecraft = minecraft;
      this.font = minecraft.font;
      this.debugInformation = new ObjectArrayList();
   }

   public void render(GuiGraphicsExtractor guiGraphics) {
      this.processDebugInformation();
      this.drawPhysicsDebugInformation(guiGraphics);
      PerformanceTracker.flush("ocean_rendering");
      PerformanceTracker.flush("liquid_rendering");
      PerformanceTracker.flush("smoke_rendering");
      PerformanceTracker.flush("snow_rendering");
      PerformanceTracker.flush("blocks_mobs_particles_rendering");
      PerformanceTracker.flush("transparent_blocks_mobs_particles_rendering");
      PerformanceTracker.flush("cloth_rendering");
   }

   public void processDebugInformation() {
      double totalPhysics = PerformanceTracker.getInMillis("physics_tick_physx") + PerformanceTracker.getInMillis("physics_tick_cloth") * 0.5;
      this.debugInformation.clear();
      this.debugInformation.add(TITLE_PREFIX + "Physics Times (ms/physics_tick, 40 times/second)");
      this.debugInformation
         .add(SUB_PREFIX + "Dynamic Entities Hitbox: " + ChatFormatting.WHITE + PerformanceTracker.getInMillisFormatted("physics_tick_entities"));
      this.debugInformation.add(SUB_PREFIX + "PhysX Simulation: " + ChatFormatting.WHITE + PerformanceTracker.getInMillisFormatted("physics_tick_physx"));
      this.debugInformation.add(SUB_PREFIX + "Physics Mod Simulation: " + ChatFormatting.WHITE + PerformanceTracker.getInMillisFormatted("physics_tick"));
      this.debugInformation
         .add(SUB_PREFIX + "Physics Smoke Simulation: " + ChatFormatting.WHITE + PerformanceTracker.getInMillisFormatted("physics_tick_smoke"));
      this.debugInformation
         .add(SUB_PREFIX + "Cloth Simulation: " + ChatFormatting.WHITE + String.format("%.2f", PerformanceTracker.getInMillis("physics_tick_cloth") * 0.5));
      this.debugInformation.add(SUB_PREFIX + "Total: " + ChatFormatting.WHITE + String.format("%.2f", totalPhysics));
      double totalRender = PerformanceTracker.getInMillis("blocks_mobs_particles_rendering")
         + PerformanceTracker.getInMillis("transparent_blocks_mobs_particles_rendering")
         + PerformanceTracker.getInMillis("ocean_rendering")
         + PerformanceTracker.getInMillis("snow_rendering")
         + PerformanceTracker.getInMillis("liquid_rendering")
         + PerformanceTracker.getInMillis("smoke_rendering")
         + PerformanceTracker.getInMillis("cloth_rendering");
      this.debugInformation.add("");
      this.debugInformation.add(TITLE_PREFIX + "Render Times (ms/frame, CPU overhead)");
      this.debugInformation
         .add(SUB_PREFIX + "Blocks/Mobs/Particles: " + ChatFormatting.WHITE + PerformanceTracker.getInMillisFormatted("blocks_mobs_particles_rendering"));
      this.debugInformation
         .add(
            SUB_PREFIX
               + "Transparent Blocks/Mobs/Particles: "
               + ChatFormatting.WHITE
               + PerformanceTracker.getInMillisFormatted("transparent_blocks_mobs_particles_rendering")
         );
      this.debugInformation.add(SUB_PREFIX + "Ocean: " + ChatFormatting.WHITE + PerformanceTracker.getInMillisFormatted("ocean_rendering"));
      this.debugInformation.add(SUB_PREFIX + "Snow: " + ChatFormatting.WHITE + PerformanceTracker.getInMillisFormatted("snow_rendering"));
      this.debugInformation.add(SUB_PREFIX + "Liquids: " + ChatFormatting.WHITE + PerformanceTracker.getInMillisFormatted("liquid_rendering"));
      this.debugInformation.add(SUB_PREFIX + "Smoke: " + ChatFormatting.WHITE + PerformanceTracker.getInMillisFormatted("smoke_rendering"));
      this.debugInformation.add(SUB_PREFIX + "Cloth: " + ChatFormatting.WHITE + PerformanceTracker.getInMillisFormatted("cloth_rendering"));
      this.debugInformation.add(SUB_PREFIX + "Total: " + ChatFormatting.WHITE + String.format("%.2f", totalRender));
      if (!(RenderSystem.getDevice().backend instanceof VulkanDevice) && GL.getCapabilities().GL_NVX_gpu_memory_info) {
         this.debugInformation.add("");
         this.debugInformation.add(TITLE_PREFIX + "GPU Memory Usage (MiB)");
         int totalVRAM = GL32C.glGetInteger(36935);
         int freeVRAM = GL32C.glGetInteger(36937);
         this.debugInformation.add(SUB_PREFIX + "Free: " + ChatFormatting.WHITE + Integer.toString(freeVRAM / 1024));
         this.debugInformation.add(SUB_PREFIX + "Total: " + ChatFormatting.WHITE + Integer.toString(totalVRAM / 1024));
      }

      int totalObjects = 0;
      int ragdolls = 0;
      int dynamicBlockRagdolls = 0;
      int smoke = 0;
      int liquids = 0;
      int loadedChunks = 0;
      ObjectIterator var11 = PhysicsMod.getInstances().values().iterator();

      while (var11.hasNext()) {
         PhysicsMod mod = (PhysicsMod)var11.next();
         PhysicsWorld physicsWorld = mod.getPhysicsWorld();
         totalObjects += physicsWorld.getBodies().size();

         for (Ragdoll ragdoll : physicsWorld.getRagdolls()) {
            if (ragdoll instanceof DynamicRagdoll) {
               dynamicBlockRagdolls++;
            } else {
               ragdolls++;
            }
         }

         smoke += physicsWorld.getSmokeDomain().particleCount();
         loadedChunks += physicsWorld.loadedChunksAmount();

         for (Liquid liquid : physicsWorld.getLiquids()) {
            liquids += liquid.particleCount();
         }
      }

      this.debugInformation.add("");
      this.debugInformation.add(TITLE_PREFIX + "Physics Objects");
      this.debugInformation.add(SUB_PREFIX + "Total Objects: " + ChatFormatting.WHITE + Integer.toString(totalObjects));
      this.debugInformation.add(SUB_PREFIX + "Loaded Regions: " + ChatFormatting.WHITE + Integer.toString(loadedChunks));
      this.debugInformation.add(SUB_PREFIX + "Mob Ragdolls: " + ChatFormatting.WHITE + Integer.toString(ragdolls));
      this.debugInformation.add(SUB_PREFIX + "Dynamic Block Links: " + ChatFormatting.WHITE + Integer.toString(dynamicBlockRagdolls));
      this.debugInformation.add(SUB_PREFIX + "Smoke: " + ChatFormatting.WHITE + Integer.toString(smoke));
      this.debugInformation.add(SUB_PREFIX + "Liquids: " + ChatFormatting.WHITE + Integer.toString(liquids));
   }

   private void drawPhysicsDebugInformation(GuiGraphicsExtractor guiGraphics) {
      for (int i = 0; i < this.debugInformation.size(); i++) {
         String text = this.debugInformation.get(i);
         if (!text.isBlank()) {
            int lineHeight = 9;
            int padding = 2;
            int y = padding + lineHeight * i;
            int textWidth = this.font.width(text);
            guiGraphics.fill(1, y - 1, 2 + textWidth + 1, y + lineHeight - 1, -1873784752);
            guiGraphics.text(this.font, text, padding, y, -2039584);
         }
      }
   }
}
