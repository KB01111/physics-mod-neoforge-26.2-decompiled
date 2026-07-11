package net.diebuddies.physics.smoke;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.joml.Vector3i;

public class SmokeSimulation {
   public static final Vector3i[] OFFSETS = new Vector3i[]{
      new Vector3i(0, 0, 1),
      new Vector3i(0, 1, 0),
      new Vector3i(0, 1, 1),
      new Vector3i(1, 0, 0),
      new Vector3i(1, 0, 1),
      new Vector3i(1, 1, 0),
      new Vector3i(1, 1, 1),
      new Vector3i(-1, 0, 1),
      new Vector3i(-1, 1, 0),
      new Vector3i(-1, 1, 1),
      new Vector3i(0, -1, 1),
      new Vector3i(1, -1, 1),
      new Vector3i(1, 1, -1),
      new Vector3i(-1, -1, 1),
      new Vector3i(-1, 1, -1),
      new Vector3i(0, 1, -1),
      new Vector3i(1, -1, -1),
      new Vector3i(1, -1, 0),
      new Vector3i(1, 0, -1),
      new Vector3i(-1, -1, -1),
      new Vector3i(-1, -1, 0),
      new Vector3i(-1, 0, -1),
      new Vector3i(-1, 0, 0),
      new Vector3i(0, -1, -1),
      new Vector3i(0, -1, 0),
      new Vector3i(0, 0, -1)
   };
   private static double EFFECT_DISTANCE = 0.25;
   private static double EFFECT_DISTANCE_INV = 1.0 / EFFECT_DISTANCE;
   private static double EFFECT_DISTANCE_SQUARED = EFFECT_DISTANCE * EFFECT_DISTANCE;
   private static double EFFECT_STRENGTH = 0.25;
   private static double POWER = 0.5;
   private static final int CHUNK_SIZE = (int)Math.round(Math.ceil(EFFECT_DISTANCE));
   private final Map<Vector3i, List<SmokePanel.Particle>> chunks;
   private final Object2BooleanMap<Vector3i> masks;
   private final List<SmokePanel.Particle> allParticles;
   private final List<SmokePanel.Particle> changedParticles;
   private final Vector3i tmp = new Vector3i();

   public SmokeSimulation() {
      this.chunks = new Object2ObjectOpenHashMap();
      this.changedParticles = new ObjectArrayList();
      this.allParticles = new ObjectArrayList();
      this.masks = new Object2BooleanOpenHashMap();
      this.masks.defaultReturnValue(false);
   }

   public void update(double delta) {
      long time = System.nanoTime();
      Iterator<Entry<Vector3i, List<SmokePanel.Particle>>> it = this.chunks.entrySet().iterator();
      this.changedParticles.clear();

      while (it.hasNext()) {
         Entry<Vector3i, List<SmokePanel.Particle>> entry = it.next();
         Vector3i chunk = entry.getKey();
         List<SmokePanel.Particle> particles = entry.getValue();

         for (int i = 0; i < particles.size(); i++) {
            SmokePanel.Particle particle = particles.get(i);
            particle.update(delta);
            int cx = net.diebuddies.math.Math.fastRound(particle.x) / CHUNK_SIZE;
            int cy = net.diebuddies.math.Math.fastRound(particle.y) / CHUNK_SIZE;
            if (cx != chunk.x || cy != chunk.y) {
               particles.remove(i--);
               this.changedParticles.add(particle);
            }
         }

         if (particles.size() == 0) {
            it.remove();
         }
      }

      for (int ix = 0; ix < this.changedParticles.size(); ix++) {
         SmokePanel.Particle particle = this.changedParticles.get(ix);
         int cx = net.diebuddies.math.Math.fastRound(particle.x) / CHUNK_SIZE;
         int cy = net.diebuddies.math.Math.fastRound(particle.y) / CHUNK_SIZE;
         this.tmp.set(cx, cy, 0);
         List<SmokePanel.Particle> particles = this.chunks.get(this.tmp);
         if (particles == null) {
            particles = new ObjectArrayList();
            this.chunks.put(new Vector3i(this.tmp), particles);
         }

         particles.add(particle);
      }

      for (Entry<Vector3i, List<SmokePanel.Particle>> entry : this.chunks.entrySet()) {
         Vector3i chunk = entry.getKey();
         List<SmokePanel.Particle> particles = entry.getValue();
         this.repellParticles(particles, particles);

         for (int ix = 0; ix < OFFSETS.length; ix++) {
            Vector3i offset = OFFSETS[ix];
            this.tmp.set(chunk.x + offset.x, chunk.y + offset.y, chunk.z + offset.z);
            List<SmokePanel.Particle> otherParticles = this.chunks.get(this.tmp);
            if (otherParticles != null && !this.masks.getBoolean(this.tmp)) {
               this.repellParticles(particles, otherParticles);
            }
         }

         this.masks.put(chunk, true);
      }

      this.masks.clear();
      System.out.println("took (" + this.allParticles.size() + "): " + (double)(System.nanoTime() - time) / 1000000.0);
   }

   private void repellParticles(List<SmokePanel.Particle> particles, List<SmokePanel.Particle> otherParticles) {
      double EFFECT_STRENGTH = 4.25;
      double POWER = 1.0;
      double MAX_SPEED = 2.9;
      boolean same = particles == otherParticles;

      for (int i = 0; i < particles.size(); i++) {
         SmokePanel.Particle particle1 = particles.get(i);

         for (int j = 0; j < otherParticles.size(); j++) {
            SmokePanel.Particle particle2 = otherParticles.get(j);
            if (particle1 != particle2) {
               double dx = particle1.x - particle2.x;
               double dy = particle1.y - particle2.y;
               double distanceSquared = dx * dx + dy * dy;
               if (distanceSquared < EFFECT_DISTANCE_SQUARED) {
                  double length = Math.sqrt(distanceSquared);
                  double effect = 1.0 - length * EFFECT_DISTANCE_INV;
                  double invLength = 1.0 / length;
                  boolean small = false;
                  if (length <= 0.001) {
                     dy = 1.0;
                     effect = 1.0;
                     invLength = 1.0;
                     small = true;
                  }

                  dx *= invLength;
                  dy *= invLength;
                  double totalStrength = EFFECT_STRENGTH * effect;
                  particle1.vx += dx * totalStrength;
                  particle1.vy += dy * totalStrength;
                  if (!same) {
                     particle2.vx -= dx * totalStrength;
                     particle2.vy -= dy * totalStrength;
                  }
               }
            }
         }
      }
   }

   public void addParticle(SmokePanel.Particle particle) {
      int cx = net.diebuddies.math.Math.fastRound(particle.x) / CHUNK_SIZE;
      int cy = net.diebuddies.math.Math.fastRound(particle.y) / CHUNK_SIZE;
      this.tmp.set(cx, cy, 0);
      List<SmokePanel.Particle> particles = this.chunks.get(this.tmp);
      if (particles == null) {
         particles = new ObjectArrayList();
         this.chunks.put(new Vector3i(this.tmp), particles);
      }

      particles.add(particle);
      this.allParticles.add(particle);
   }

   public List<SmokePanel.Particle> getAllParticles() {
      return this.allParticles;
   }

   public void clear() {
      this.allParticles.clear();
      this.chunks.clear();
      this.changedParticles.clear();
   }
}
