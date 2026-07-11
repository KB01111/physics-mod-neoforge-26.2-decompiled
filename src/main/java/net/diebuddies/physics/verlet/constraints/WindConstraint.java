package net.diebuddies.physics.verlet.constraints;

import java.util.List;
import java.util.Random;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.PerlinNoise;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.VerletSimulationData;
import net.diebuddies.physics.wind.WeatherDomain;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import org.joml.Vector3d;

public class WindConstraint implements VerletConstraint {
   private static final PerlinNoise noise = new PerlinNoise(new Random());
   private Vector3d windForce = new Vector3d();

   @Override
   public boolean initAsyncData(PhysicsWorld world, VerletSimulation simulation) {
      if (ConfigClient.windPhysics) {
         WeatherDomain domain = world.getWeatherDomain();
         VerletSimulationData data = simulation.getData();
         Vector3d offset = simulation.getOffset();
         if (data.points.size() > 0 && offset != null) {
            VerletPoint point = data.points.get(0);
            double x = point.position.x + simulation.getOffset().x * 0.5;
            double z = point.position.z + simulation.getOffset().z * 0.5;
            double y = point.position.y + simulation.getOffset().y / 1024.0;
            long time = 0L;
            if (Minecraft.getInstance().level != null) {
               time = Minecraft.getInstance().level.getGameTime();
            }

            double xForce = noise.noise(x, z, 0.5 + y + (double)time / 100.0);
            double zForce = noise.noise(x, z, 1641.5 + y + (double)time / 100.0);
            double yForce = noise.noise(x, z, 89641.5 + y + (double)time / 100.0);
            double strength = noise.noise(x, z, -11.5 + y + (double)time / 7.0) * 16.0;
            int ix = Mth.floor(point.position.x + offset.x);
            int iy = Mth.floor(point.position.y + offset.y);
            int iz = Mth.floor(point.position.z + offset.z);
            double addStrength = (double)domain.getWindStrength(ix, iy, iz);
            if (addStrength <= 1.0E-4) {
               strength = 0.0;
            }

            this.windForce.set(xForce, yForce, zForce).mul(strength);
         }
      }

      return false;
   }

   @Override
   public void updateBefore(double delta, VerletSimulation simulation) {
      double fx = this.windForce.x * delta * delta;
      double fy = this.windForce.y * delta * delta;
      double fz = this.windForce.z * delta * delta;
      List<VerletPoint> points = simulation.getPoints();

      for (int i = 0; i < points.size(); i++) {
         VerletPoint p = points.get(i);
         if (!p.locked) {
            p.force.x += fx;
            p.force.y += fy;
            p.force.z += fz;
         }
      }
   }
}
