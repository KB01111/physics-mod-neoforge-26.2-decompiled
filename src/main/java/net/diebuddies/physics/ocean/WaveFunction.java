package net.diebuddies.physics.ocean;

import net.diebuddies.config.ConfigClient;
import org.joml.Vector2d;
import org.joml.Vector3d;

public class WaveFunction {
   public static final int ITERATIONS_OFFSET = 13;
   public static final int ITERATIONS_NORMAL = 15;
   public static final double DRAG_MULT = 0.048;
   public static final double XZ_SCALE = 0.035;
   public static final double TIME_MULTIPLICATOR = 0.45;
   public static final double W_DETAIL = 0.75;
   public static final double FREQUENCY = 6.0;
   public static final double PHYSICS_WEIGHT = 0.8;
   public static final double SPEED = 2.0;
   public static final double FREQUENCY_MULT = 1.18;
   public static final double SPEED_MULT = 1.07;
   public static final double ITER_INC = 12.0;
   private static Vector2d tmp = new Vector2d();

   public static double waveHeight(Vector2d position, int iterations, double factor, double oceanHeight, double time) {
      position.mul(0.035 * (double)ConfigClient.oceanHorizontalWaveScale);
      double iter = 0.0;
      double frequency = 6.0;
      double speed = 2.0;
      double weight = 1.0;
      double height = 0.0;
      double waveSum = 0.0;
      double modifiedTime = time * 0.45;
      double dragFactor = dragFactor(factor);

      for (int i = 0; i < iterations; i++) {
         double dirX = Math.sin(iter);
         double dirY = Math.cos(iter);
         double x = (dirX * position.x + dirY * position.y) * frequency + modifiedTime * speed;
         double wave = Math.exp(Math.sin(x) - 1.0);
         double result = wave * Math.cos(x);
         double force = result * weight * 0.048 * dragFactor;
         position.sub(dirX * force, dirY * force);
         height += wave * weight;
         iter += 12.0;
         waveSum += weight;
         weight *= 0.8;
         frequency *= 1.18;
         speed *= 1.07;
      }

      return height / waveSum * oceanHeight * factor - oceanHeight * factor * 0.5;
   }

   private static Vector2d waveDirection(Vector2d position, int iterations, double factor, double time) {
      position.mul(0.035 * (double)ConfigClient.oceanHorizontalWaveScale);
      double iter = 0.0;
      double frequency = 6.0;
      double speed = 2.0;
      double weight = 1.0;
      double waveSum = 0.0;
      double modifiedTime = time * 0.45;
      double dragFactor = dragFactor(factor);
      Vector2d dx = tmp.set(0.0);

      for (int i = 0; i < iterations; i++) {
         double dirX = Math.sin(iter);
         double dirY = Math.cos(iter);
         double x = (dirX * position.x + dirY * position.y) * frequency + modifiedTime * speed;
         double wave = Math.exp(Math.sin(x) - 1.0);
         double result = wave * Math.cos(x);
         double force = result * weight;
         double forceX = force * dirX;
         double forceY = force * dirY;
         double detail = Math.pow(weight, 0.75);
         dx.add(forceX * detail, forceY * detail);
         position.sub(forceX * 0.048 * dragFactor, forceY * 0.048 * dragFactor);
         iter += 12.0;
         waveSum += weight;
         weight *= 0.8;
         frequency *= 1.18;
         speed *= 1.07;
      }

      double length = 0.0;
      return (length = dx.lengthSquared()) > 0.0 ? dx.div(Math.pow(waveSum, 0.25)).div(length) : dx;
   }

   public static Vector3d waveNormal(Vector2d position, double factor, double oceanHeight, double time, Vector3d dst) {
      Vector2d wave = waveDirection(position, 15, factor, time).negate();
      double oceanHeightFactor = oceanHeight / 13.0;
      double totalFactor = oceanHeightFactor * Math.max(0.1, factor);
      return dst.set(wave.x * totalFactor, 0.6, wave.y * totalFactor).normalize();
   }

   private static double dragFactor(double factor) {
      return Math.clamp(factor * 2.0, 0.1, 1.0);
   }
}
