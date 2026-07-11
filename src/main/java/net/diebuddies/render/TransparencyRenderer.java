package net.diebuddies.render;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.util.PerformanceTracker;
import net.diebuddies.util.Pool;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4fStack;

public class TransparencyRenderer {
   private MainRenderer mainRenderer;
   public List<MainRenderer.PhysicsDrawCall> drawCalls;
   public Pool<MainRenderer.PhysicsDrawCall> pool = new Pool<>(100, TransparencyRenderer.TranslucentPhysicsDrawCall::new, drawCall -> drawCall.reset());

   public TransparencyRenderer(MainRenderer mainRenderer) {
      this.mainRenderer = mainRenderer;
      this.drawCalls = new ObjectArrayList();
   }

   public void render(PhysicsWorld physics, ClientLevel level, Matrix4fStack viewMatrixStack, Vec3 view) {
      if (this.drawCalls.size() > 0) {
         PerformanceTracker.startNoFlush("transparent_blocks_mobs_particles_rendering");
         this.drawCalls
            .sort(
               (a, b) -> -Double.compare(
                     ((TransparencyRenderer.TranslucentPhysicsDrawCall)a).distanceToCamera,
                     ((TransparencyRenderer.TranslucentPhysicsDrawCall)b).distanceToCamera
                  )
            );
         this.mainRenderer.uploadDrawCallUniforms(this.drawCalls);
         this.mainRenderer.executeDrawCalls(this.drawCalls, true);
         this.pool.freeAll(this.drawCalls);
         this.drawCalls.clear();
         PerformanceTracker.end("transparent_blocks_mobs_particles_rendering");
      }

      this.drawCalls.clear();
   }

   public void destroy() {
   }

   public static class TranslucentPhysicsDrawCall extends MainRenderer.PhysicsDrawCall {
      public double distanceToCamera;
   }
}
