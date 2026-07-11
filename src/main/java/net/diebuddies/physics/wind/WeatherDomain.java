package net.diebuddies.physics.wind;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap.Entry;
import java.util.Iterator;
import java.util.Random;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.Math;
import net.diebuddies.math.PerlinNoise;
import net.diebuddies.physics.PhysicsWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import org.joml.Vector2d;
import org.joml.Vector3f;

public class WeatherDomain {
   private static final int DOMAIN_MODIFIER = 0;
   private static final int RESET_FORCES_EVERY_X_TICKS = 8;
   private static final int REMOVE_CACHE_DISTANCE = 16900;
   private final Long2IntMap cachedHeights = new Long2IntOpenHashMap();
   private Long2ObjectMap<Vector3f> windForces = new Long2ObjectOpenHashMap();
   private PhysicsWorld physics;
   private double delta;
   private int updateCount;
   private PerlinNoise perlin;
   private MutableBlockPos tmp = new MutableBlockPos();

   public WeatherDomain(PhysicsWorld physics) {
      this.physics = physics;
      this.perlin = new PerlinNoise(new Random());
   }

   public Vector3f getWindDirection(int x, int y, int z) {
      long blockPos = BlockPos.asLong(x >> 0, y >> 0, z >> 0);
      return (Vector3f)this.windForces.computeIfAbsent(blockPos, key -> this.computeWindDirection(x >> 0, y >> 0, z >> 0));
   }

   public float getWindStrength(int x, int y, int z) {
      int height = this.cachedHeights.computeIfAbsent(BlockPos.asLong(x, 0, z), key -> this.computeWindHeight(x, z));
      return y >= height ? this.getWindStrengthFast() : 0.0F;
   }

   private int computeWindHeight(int x, int z) {
      Level level = this.physics.getLevel();
      int motionBlocking = level.getHeight(Types.MOTION_BLOCKING, x, z);
      this.tmp.setX(x);
      this.tmp.setZ(z);

      for (int y = motionBlocking; y >= level.getMinY(); y--) {
         this.tmp.setY(y);
         BlockState state = level.getBlockState(this.tmp);
         if (this.blocksWind(level, state, this.tmp)) {
            return y;
         }
      }

      return level.getMinY();
   }

   private boolean blocksWind(Level level, BlockState state, BlockPos pos) {
      if (state.isAir()) {
         return false;
      } else {
         return state.getBlock() instanceof LeavesBlock
            ? false
            : state.isCollisionShapeFullBlock(level, pos) || state.isFaceSturdy(level, pos, Direction.DOWN) || state.isFaceSturdy(level, pos, Direction.UP);
      }
   }

   public void blockUpdate(BlockPos pos) {
      long blockPos = BlockPos.asLong(pos.getX(), 0, pos.getZ());
      if (this.cachedHeights.containsKey(blockPos)) {
         int currentHeight = this.cachedHeights.get(blockPos);
         if (pos.getY() >= currentHeight) {
            this.cachedHeights.remove(blockPos);
         }
      }
   }

   public float getWindStrengthFast() {
      float rainLevel = this.physics.getLevel().getRainLevel(1.0F);
      float thunderLevel = this.physics.getLevel().getThunderLevel(1.0F);
      return (rainLevel * ConfigClient.weatherRainStrength + thunderLevel * ConfigClient.weatherThunderStrength) * 0.3333F + ConfigClient.weatherClearStrength;
   }

   private Vector3f computeWindDirection(int x, int y, int z) {
      x *= 5;
      y *= 5;
      z *= 5;
      double wind = this.perlin
         .noise(
            this.perlin.noise((double)x / 45.0, (double)y / 45.0, this.delta / 4000.0) * 1.0 + (double)x / 50.0,
            this.perlin.noise((double)x / 35.0, (double)y / 35.0, this.delta / 4000.0) * 1.0 + (double)y / 50.0
         );
      double scale = 0.1;
      double bigWind = this.perlin
         .noise(
            this.perlin.noise((double)x / 45.0 * scale, (double)y / 45.0 * scale, this.delta / 4000.0) * 4.0 + (double)x / 50.0 * scale,
            this.perlin.noise((double)x / 35.0 * scale, (double)y / 35.0 * scale, this.delta / 4000.0) * 4.0 + (double)y / 50.0 * scale
         );
      double jitter = this.perlin
         .noise(
            this.perlin.noise((double)x / 25.0, (double)y / 25.0, this.delta / 100.0) * 1.0 + (double)x / 20.0,
            this.perlin.noise((double)x / 15.0, (double)y / 15.0, this.delta / 100.0) * 1.0 + (double)y / 20.0
         );
      double windForce = Math.clamp((bigWind * 0.5 + 0.5) * 0.7 + (wind * 0.5 + 0.0) * 0.25 + (jitter * 0.5 + 0.5) * 0.05, 0.0, 1.0);
      double upScale = 0.1;
      double upWind = this.perlin
         .noise(
            this.perlin.noise((double)x / 45.0 * upScale, (double)y / 45.0 * upScale, this.delta / 4000.0) * 1.0 + (double)x / 50.0 * upScale,
            this.perlin.noise((double)x / 35.0 * upScale, (double)y / 35.0 * upScale, this.delta / 4000.0) * 1.0 + (double)y / 50.0 * upScale
         );
      double upForce = Math.clamp(upWind, 0.0, 1.0);
      double angle = org.joml.Math.toRadians(
         (this.perlin.noise(0.2412, this.delta / 200000.0) + this.perlin.noise(0.74128, this.delta / 50.0) * 0.005) * 1080.0
      );
      double windX = org.joml.Math.sin(angle) * windForce;
      double windZ = org.joml.Math.cos(angle) * windForce;
      return new Vector3f((float)windX, (float)upForce, (float)windZ);
   }

   public void update(double diff) {
      this.delta += diff * 1000.0;
      this.updateCount++;
      if (this.updateCount >= 8) {
         BlockPos cameraPos = Minecraft.getInstance().gameRenderer.mainCamera().blockPosition();
         this.windForces.clear();
         Iterator<Entry> it = this.cachedHeights.long2IntEntrySet().iterator();

         while (it.hasNext()) {
            Entry entry = it.next();
            long blockPos = entry.getLongKey();
            this.tmp.set(blockPos);
            double lengthSquared = Vector2d.distanceSquared(
               (double)this.tmp.getX(), (double)this.tmp.getZ(), (double)cameraPos.getX(), (double)cameraPos.getZ()
            );
            if (lengthSquared > 16900.0) {
               it.remove();
            }
         }

         this.updateCount = 0;
      }
   }
}
