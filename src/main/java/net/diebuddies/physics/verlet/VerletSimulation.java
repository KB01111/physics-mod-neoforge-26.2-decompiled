package net.diebuddies.physics.verlet;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.platform.Lighting.Entry;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.MatrixUtil;
import net.diebuddies.minecraft.LevelRendererAccessor;
import net.diebuddies.model.ColladaMesh;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.PhysicsWorld;
import net.diebuddies.physics.snow.math.AABB3D;
import net.diebuddies.physics.verlet.constraints.RenderConstraint;
import net.diebuddies.physics.verlet.constraints.VerletConstraint;
import net.diebuddies.physics.wind.WeatherDomain;
import net.diebuddies.util.PerformanceTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4i;

public class VerletSimulation implements Runnable {
   public static ExecutorService asynchronousWorker = createClothThreads();
   private Vector3d gravity;
   private Vector3d windDirection;
   private float windStrength;
   private double friction;
   private VerletSimulationData data;
   private List<VerletConstraint> constraints;
   public boolean active = true;
   public volatile boolean destroyed = false;
   public Identifier textureID;
   public int brightness;
   public Cloth cloth;
   public boolean fetchInstantly = false;
   public boolean alwaysFetchInstantly = false;
   public Future<?> task;
   public Vector3d viewDelta = new Vector3d();
   private long lastUpdate;
   private volatile double updateDelta;
   private volatile int iterations;
   public Matrix4d offsetTransform;
   public Matrix4d lastOffsetTransform;
   public AABB3D aabb;
   private static Matrix4f mojangTransform = new Matrix4f();
   private static Matrix4d tmpMove = new Matrix4d();
   private double lastRenderPercent = 0.0;

   public static ExecutorService createClothThreads() {
      return Executors.newFixedThreadPool(ConfigClient.clothThreads, r -> {
         Thread t = new Thread(r, "Physics Cloth Worker");
         t.setDaemon(true);
         return t;
      });
   }

   public VerletSimulation(Vector3d gravity, int iterations, double friction, Vector3d offset) {
      this.gravity = new Vector3d(gravity);
      this.windDirection = new Vector3d();
      this.data = new VerletSimulationData(offset);
      this.constraints = new ObjectArrayList();
      this.lastUpdate = Util.getNanos();
      this.friction = friction;
      this.iterations = iterations;
      this.constraints.add(new RenderConstraint());
      this.textureID = PhysicsMod.WHITE_TEXTURE;
      this.aabb = new AABB3D(new Vector3d(), new Vector3d());
   }

   public VerletSimulation(Vector3d gravity, int iterations, double friction) {
      this(gravity, iterations, friction, null);
   }

   public void update(PhysicsWorld world, double delta) {
      if (this.task != null) {
         this.finishTask();
      }

      this.downloadData();
      this.fetchInstantly = this.alwaysFetchInstantly;
      this.updateDelta = delta;
      this.iterations = this.iterations;
      this.lastUpdate = Util.getNanos();
      this.getWindForces(world);

      for (int i = 0; i < this.constraints.size(); i++) {
         this.fetchInstantly = this.fetchInstantly | this.constraints.get(i).initAsyncData(world, this);
      }

      this.updateOffsets();
      this.task = asynchronousWorker.submit(this);
      this.active = false;
   }

   private void getWindForces(PhysicsWorld world) {
      if (world != null) {
         if (ConfigClient.windPhysics) {
            WeatherDomain domain = world.getWeatherDomain();
            if (this.data.points.size() > 0 && this.getOffset() != null) {
               VerletPoint point = this.data.points.get(0);
               int ix = Mth.floor(point.position.x + this.getOffset().x);
               int iy = Mth.floor(point.position.y + this.getOffset().y);
               int iz = Mth.floor(point.position.z + this.getOffset().z);
               this.windDirection.set(domain.getWindDirection(ix, iy, iz));
               this.windStrength = domain.getWindStrength(ix, iy, iz);
            } else {
               this.windDirection.set(0.0, 0.0, 0.0);
               this.windStrength = 0.0F;
            }
         } else {
            this.windDirection.set(0.0, 0.0, 0.0);
            this.windStrength = 0.0F;
         }
      }
   }

   private void fetchData() {
      if (this.task != null && this.fetchInstantly) {
         this.finishTask();
         this.downloadData();
         if (this.offsetTransform == null) {
            this.offsetTransform = new Matrix4d();
         } else {
            this.offsetTransform.identity();
         }

         if (this.lastOffsetTransform == null) {
            this.lastOffsetTransform = new Matrix4d();
         } else {
            this.lastOffsetTransform.identity();
         }
      }
   }

   private void finishTask() {
      try {
         this.task.get();
         this.task = null;
      } catch (ExecutionException | InterruptedException var2) {
         var2.printStackTrace();
      }
   }

   public void downloadData() {
      for (int i = 0; i < this.data.points.size(); i++) {
         VerletPoint point = this.data.points.get(i);
         point.bufferPosition.set(point.position);
         point.bufferPrevPosition.set(point.prevPosition);
         point.bufferNormal.set(point.normal);
      }

      for (int i = 0; i < this.data.quads.size(); i++) {
         VerletQuad quad = this.data.quads.get(i);
         quad.bufferNormal.set(quad.normal);
      }

      for (int i = 0; i < this.data.triangles.size(); i++) {
         VerletTriangle triangle = this.data.triangles.get(i);
         triangle.bufferNormal.set(triangle.normal);
      }

      if (this.data.offset == null) {
         this.data.bufferOffset = null;
      } else {
         this.data.bufferOffset.set(this.data.offset);
      }

      this.data.bufferTransformation.set(this.data.transformation);
   }

   public void updateOffsets() {
      if (this.lastOffsetTransform != null) {
         this.lastOffsetTransform.set(this.offsetTransform);
      }

      this.offsetTransform = new Matrix4d(this.data.transformation);
      this.offsetTransform.mul(this.data.bufferTransformation.invert(new Matrix4d()));
      if (this.lastOffsetTransform == null) {
         this.lastOffsetTransform = new Matrix4d();
         this.lastOffsetTransform.set(this.offsetTransform);
      }
   }

   @Override
   public void run() {
      for (int i = 0; i < this.data.points.size(); i++) {
         VerletPoint p = this.data.points.get(i);
         p.force.set(0.0);
         if (p.locked) {
            p.prevPosition.set(p.position);
         }
      }

      for (int ix = 0; ix < this.constraints.size(); ix++) {
         this.constraints.get(ix).updateBefore(this.updateDelta, this);
      }

      double timeSquared = this.updateDelta * this.updateDelta;
      double gx = (this.gravity.x + this.windDirection.x * (double)this.windStrength * 42.0) * timeSquared;
      double gy = this.gravity.y * timeSquared;
      double gz = (this.gravity.z + this.windDirection.z * (double)this.windStrength * 42.0) * timeSquared;
      double fraction = 1.0 / (double)this.iterations;

      for (int ix = 0; ix < this.data.points.size(); ix++) {
         VerletPoint p = this.data.points.get(ix);
         if (!p.locked) {
            double vx = p.position.x - p.prevPosition.x;
            double vy = p.position.y - p.prevPosition.y;
            double vz = p.position.z - p.prevPosition.z;
            p.prevPosition.set(p.position);
            p.force.x = p.force.x + gx + vx * this.friction;
            p.force.y = p.force.y + gy + vy * this.friction;
            p.force.z = p.force.z + gz + vz * this.friction;
            if (p.softRestriction != null) {
               double softRestrictionX = p.softRestriction.x - (p.position.x + p.force.x);
               double softRestrictionY = p.softRestriction.y - (p.position.y + p.force.y);
               double softRestrictionZ = p.softRestriction.z - (p.position.z + p.force.z);
               double restriction = p.restriction;
               p.force.x += softRestrictionX * restriction;
               p.force.y += softRestrictionY * restriction;
               p.force.z += softRestrictionZ * restriction;
            }

            p.force.x *= fraction;
            p.force.y *= fraction;
            p.force.z *= fraction;
         }
      }

      List<VerletPoint> points = this.data.points;
      List<VerletStick> sticks = this.data.sticks;
      Vector3d start = this.aabb.start;
      Vector3d end = this.aabb.end;
      if (points.size() > 0) {
         Vector3d pos = points.get(0).position;
         start.set(pos);
         end.set(pos);
      }

      for (int ixx = 0; ixx < this.iterations; ixx++) {
         double percent = (double)ixx / (double)this.iterations + 1.0 / (double)this.iterations;

         for (int j = 0; j < this.constraints.size(); j++) {
            this.constraints.get(j).preSubStep(percent, this);
         }

         for (int j = 0; j < points.size(); j++) {
            VerletPoint p = points.get(j);
            if (!p.locked) {
               p.position.x = p.position.x + p.force.x;
               p.position.y = p.position.y + p.force.y;
               p.position.z = p.position.z + p.force.z;
            }

            start.min(p.position);
            end.max(p.position);
         }

         for (int j = 0; j < sticks.size(); j++) {
            VerletStick stick = sticks.get(j);
            VerletPoint pointA = stick.pointA;
            VerletPoint pointB = stick.pointB;
            if (!pointA.locked || !pointB.locked) {
               Vector3d positionA = pointA.position;
               Vector3d positionB = pointB.position;
               double stickCenterX = (positionA.x + positionB.x) * 0.5;
               double stickCenterY = (positionA.y + positionB.y) * 0.5;
               double stickCenterZ = (positionA.z + positionB.z) * 0.5;
               double stickDirX = positionA.x - positionB.x;
               double stickDirY = positionA.y - positionB.y;
               double stickDirZ = positionA.z - positionB.z;
               double lengthSquared = Vector3d.lengthSquared(stickDirX, stickDirY, stickDirZ);
               if (lengthSquared != 0.0) {
                  double invLength = stick.halfLength / Math.sqrt(lengthSquared);
                  stickDirX *= invLength;
                  stickDirY *= invLength;
                  stickDirZ *= invLength;
               } else {
                  stickDirZ = 1.0 * stick.halfLength;
               }

               if (!pointA.locked) {
                  positionA.x = stickCenterX + stickDirX;
                  positionA.y = stickCenterY + stickDirY;
                  positionA.z = stickCenterZ + stickDirZ;
               }

               if (!pointB.locked) {
                  positionB.x = stickCenterX - stickDirX;
                  positionB.y = stickCenterY - stickDirY;
                  positionB.z = stickCenterZ - stickDirZ;
               }
            }
         }

         for (int jx = 0; jx < this.constraints.size(); jx++) {
            this.constraints.get(jx).subStep(percent, this);
         }
      }

      for (int ixx = 0; ixx < this.constraints.size(); ixx++) {
         this.constraints.get(ixx).updateAfter(this.updateDelta, this);
      }

      this.calculateNormals();
   }

   public void setTransformation(Matrix4d transformation) {
      this.data.transformation.set(transformation);
   }

   public void setBufferTransformation(Matrix4d transformation) {
      this.data.bufferTransformation.set(transformation);
   }

   public void addCloth(Cloth cloth, Identifier textureID, Matrix4d transformation, boolean flipUV) {
      this.cloth = cloth;
      this.textureID = textureID;
      ColladaMesh mesh = cloth.mesh;

      for (Vector3f position : mesh.positions) {
         Vector3d pointPosition = new Vector3d((double)position.x, (double)position.y, (double)position.z);
         if (transformation != null) {
            transformation.transformPosition(pointPosition);
         }

         VerletPoint point = new VerletPoint(pointPosition);
         this.addPoint(point);
      }

      List<VerletPoint> points = this.getPoints();

      for (int i = 0; i < mesh.indices.size(); i++) {
         int pindex = mesh.indices.get(i).x;
         int cindex = mesh.indices.get(i).w;
         int tindex = mesh.indices.get(i).z;
         VerletPoint point = points.get(pindex);
         Vector3f color = mesh.colors.get(cindex);
         point.locked = color.x < 0.99F;
         point.uv.set((Vector2fc)mesh.texCoords.get(tindex));
         if (color.y < 0.99F) {
            point.setRestriction((double)color.y);
         }

         if (flipUV) {
            point.uv.y = 1.0F - point.uv.y;
         }
      }

      int offset = 0;

      for (int i = 0; i < mesh.lineIndices.size() / 2; i++) {
         int index1 = mesh.lineIndices.get(i * 2);
         int index2 = mesh.lineIndices.get(i * 2 + 1);
         this.addStick(new VerletStick(points.get(index1), points.get(index2)));
      }

      for (int i = 0; i < mesh.polyCount.length; i++) {
         byte polyCount = mesh.polyCount[i];
         if (polyCount == 4) {
            Vector4i index1 = mesh.indices.get(offset);
            Vector4i index2 = mesh.indices.get(offset + 1);
            Vector4i index3 = mesh.indices.get(offset + 2);
            Vector4i index4 = mesh.indices.get(offset + 3);
            VerletPoint point1 = points.get(index1.x);
            VerletPoint point2 = points.get(index2.x);
            VerletPoint point3 = points.get(index3.x);
            VerletPoint point4 = points.get(index4.x);
            this.addStick(new VerletStick(point1, point2));
            this.addStick(new VerletStick(point2, point3));
            this.addStick(new VerletStick(point3, point4));
            this.addStick(new VerletStick(point4, point1));
            this.addStick(new VerletStick(point1, point3));
            this.addStick(new VerletStick(point2, point4));
            this.addQuad(
               new VerletQuad(
                  point1,
                  point2,
                  point3,
                  point4,
                  new Vector2f((Vector2fc)mesh.texCoords.get(index1.z)),
                  new Vector2f((Vector2fc)mesh.texCoords.get(index2.z)),
                  new Vector2f((Vector2fc)mesh.texCoords.get(index3.z)),
                  new Vector2f((Vector2fc)mesh.texCoords.get(index4.z)),
                  flipUV
               )
            );
         } else if (polyCount == 3) {
            Vector4i index1 = mesh.indices.get(offset);
            Vector4i index2 = mesh.indices.get(offset + 1);
            Vector4i index3 = mesh.indices.get(offset + 2);
            VerletPoint point1 = points.get(index1.x);
            VerletPoint point2 = points.get(index2.x);
            VerletPoint point3 = points.get(index3.x);
            this.addStick(new VerletStick(point1, point2));
            this.addStick(new VerletStick(point2, point3));
            this.addStick(new VerletStick(point3, point1));
            this.addTriangle(
               new VerletTriangle(
                  point1,
                  point2,
                  point3,
                  new Vector2f((Vector2fc)mesh.texCoords.get(index1.z)),
                  new Vector2f((Vector2fc)mesh.texCoords.get(index2.z)),
                  new Vector2f((Vector2fc)mesh.texCoords.get(index3.z)),
                  flipUV
               )
            );
         }

         offset += polyCount;
      }

      this.calculateNormals();
   }

   private void improveSimulationQualityBySorting() {
      List<VerletStick> ordered = new ObjectArrayList();
      List<VerletStick> available = new ObjectArrayList(this.data.sticks);
      ArrayDeque<VerletPoint> toCheck = new ArrayDeque<>();
      Iterator<VerletStick> it = available.iterator();

      while (it.hasNext()) {
         VerletStick stick = it.next();
         if (stick.pointA.locked && !stick.pointB.locked) {
            toCheck.add(stick.pointB);
            ordered.add(stick);
            it.remove();
         } else if (!stick.pointA.locked && stick.pointB.locked) {
            toCheck.add(stick.pointA);
            ordered.add(stick);
            it.remove();
         } else if (stick.pointA.locked && stick.pointB.locked) {
            ordered.add(stick);
            it.remove();
         }
      }

      VerletPoint check;
      while ((check = toCheck.poll()) != null) {
         Iterator<VerletStick> itA = available.iterator();

         while (itA.hasNext()) {
            VerletStick stick = itA.next();
            if (stick.pointA == check) {
               toCheck.add(stick.pointB);
               ordered.add(stick);
               itA.remove();
            } else if (stick.pointB == check) {
               toCheck.add(stick.pointA);
               ordered.add(stick);
               itA.remove();
            }
         }
      }

      ordered.addAll(available);
      this.data.sticks = ordered;
   }

   public void calculateNormals() {
      if (ConfigClient.clothSmoothShading) {
         for (VerletPoint point : this.data.points) {
            point.normal.zero();
         }
      }

      for (VerletQuad quad : this.data.quads) {
         double ax = quad.point2.position.x - quad.point1.position.x;
         double ay = quad.point2.position.y - quad.point1.position.y;
         double az = quad.point2.position.z - quad.point1.position.z;
         double bx = quad.point3.position.x - quad.point1.position.x;
         double by = quad.point3.position.y - quad.point1.position.y;
         double bz = quad.point3.position.z - quad.point1.position.z;
         double nx = ay * bz - az * by;
         double ny = az * bx - ax * bz;
         double nz = ax * by - ay * bx;
         double invLength = org.joml.Math.invsqrt(nx * nx + ny * ny + nz * nz);
         nx *= invLength;
         ny *= invLength;
         nz *= invLength;
         if (ConfigClient.clothSmoothShading) {
            quad.point1.normal.add(nx, ny, nz);
            quad.point2.normal.add(nx, ny, nz);
            quad.point3.normal.add(nx, ny, nz);
            quad.point4.normal.add(nx, ny, nz);
         } else {
            quad.normal.set(nx, ny, nz);
         }
      }

      for (VerletTriangle triangle : this.data.triangles) {
         double ax = triangle.point2.position.x - triangle.point1.position.x;
         double ay = triangle.point2.position.y - triangle.point1.position.y;
         double az = triangle.point2.position.z - triangle.point1.position.z;
         double bx = triangle.point3.position.x - triangle.point1.position.x;
         double by = triangle.point3.position.y - triangle.point1.position.y;
         double bz = triangle.point3.position.z - triangle.point1.position.z;
         double nx = ay * bz - az * by;
         double ny = az * bx - ax * bz;
         double nz = ax * by - ay * bx;
         double invLength = org.joml.Math.invsqrt(nx * nx + ny * ny + nz * nz);
         nx *= invLength;
         ny *= invLength;
         nz *= invLength;
         if (ConfigClient.clothSmoothShading) {
            triangle.point1.normal.set(nx, ny, nz);
            triangle.point2.normal.set(nx, ny, nz);
            triangle.point3.normal.set(nx, ny, nz);
         } else {
            triangle.normal.set(nx, ny, nz);
         }
      }

      if (ConfigClient.clothSmoothShading) {
         for (VerletPoint point : this.data.points) {
            double lengthSquared = point.normal.lengthSquared();
            if (lengthSquared != 0.0) {
               point.normal.div(Math.sqrt(lengthSquared));
            } else {
               point.normal.set(0.0, 1.0, 0.0);
            }
         }
      }
   }

   public void addPoint(VerletPoint point) {
      if (this.data.offset == null) {
         this.data.offset = new Vector3d(point.position);
         this.data.bufferOffset = new Vector3d(point.position);
      }

      point.position.sub(this.data.offset);
      point.prevPosition.set(point.position);
      point.bufferPosition.set(point.position);
      point.bufferPrevPosition.set(point.position);
      this.data.points.add(point);
   }

   public void addStick(VerletStick stick) {
      this.data.sticks.add(stick);
   }

   public void addQuad(VerletQuad quad) {
      this.data.quads.add(quad);
   }

   public void addTriangle(VerletTriangle triangle) {
      this.data.triangles.add(triangle);
   }

   public void addLine(VerletLine line) {
      this.data.lines.add(line);
   }

   public void addConstraint(VerletConstraint constraint) {
      this.constraints.add(constraint);
   }

   public void removePoint(VerletPoint point) {
      this.data.points.remove(point);
   }

   public void removeStick(VerletStick stick) {
      this.data.sticks.remove(stick);
   }

   public void removeQuad(VerletQuad quad) {
      this.data.quads.remove(quad);
   }

   public void removeTriangle(VerletTriangle triangle) {
      this.data.triangles.remove(triangle);
   }

   public void removeLine(VerletLine line) {
      this.data.lines.remove(line);
   }

   public void removeConstraint(VerletConstraint constraint) {
      this.constraints.remove(constraint);
   }

   public List<VerletStick> getSticks() {
      return this.data.sticks;
   }

   public List<VerletPoint> getPoints() {
      return this.data.points;
   }

   public List<VerletQuad> getQuads() {
      return this.data.quads;
   }

   public List<VerletTriangle> getTriangles() {
      return this.data.triangles;
   }

   public List<VerletLine> getLines() {
      return this.data.lines;
   }

   public List<VerletConstraint> getConstraints() {
      return this.constraints;
   }

   public Vector3d getGravity() {
      return this.gravity;
   }

   public void setGravity(Vector3d gravity) {
      this.gravity.set(gravity);
   }

   public Vector3d getOffset() {
      return this.data.offset;
   }

   public void setOffset(Vector3d offset, boolean changePoints) {
      if (changePoints) {
         double dx = -offset.x + this.data.offset.x;
         double dy = -offset.y + this.data.offset.y;
         double dz = -offset.z + this.data.offset.z;

         for (VerletPoint point : this.data.points) {
            point.position.x += dx;
            point.position.y += dy;
            point.position.z += dz;
            point.prevPosition.x += dx;
            point.prevPosition.y += dy;
            point.prevPosition.z += dz;
         }
      }

      this.data.offset.set(offset);
   }

   public void setOffset(Vector3d offset) {
      this.setOffset(offset, true);
   }

   public void render(Matrix4fStack matrixStack, SubmitNodeCollector submitNodeStorage) {
      long time = Util.getNanos();
      double diff = (double)(time - this.lastUpdate) / 1.0E9;
      double renderPercent = net.diebuddies.math.Math.clamp(diff / this.updateDelta, 0.0, 1.0);
      this.viewDelta.set(0.0);
      this.render(matrixStack, renderPercent, submitNodeStorage);
   }

   public void render(Matrix4fStack matrixStack, double renderPercent, SubmitNodeCollector submitNodeStorage) {
      PhysicsMod.clothSmootShadingIrisFix = true;
      matrixStack.pushMatrix();
      this.fetchData();

      for (VerletConstraint constraint : this.constraints) {
         constraint.renderBefore(matrixStack, renderPercent, this);
      }

      if (this.offsetTransform != null) {
         Matrix4d moveTransform = this.offsetTransform;
         if (this.lastOffsetTransform != null) {
            moveTransform = MatrixUtil.lerp(this.lastOffsetTransform, this.offsetTransform, renderPercent, tmpMove);
         }

         mojangTransform.set(moveTransform);
         matrixStack.mul(mojangTransform);
      }

      for (VerletConstraint constraint : this.constraints) {
         constraint.render(matrixStack, renderPercent, submitNodeStorage, this);
      }

      matrixStack.popMatrix();

      for (VerletConstraint constraint : this.constraints) {
         constraint.renderAfter(matrixStack, renderPercent, this);
      }

      PhysicsMod.clothSmootShadingIrisFix = false;
   }

   public <T extends VerletConstraint> T getConstraint(Class<T> clzz) {
      for (VerletConstraint constraint : this.constraints) {
         if (clzz == constraint.getClass()) {
            return (T)constraint;
         }
      }

      return null;
   }

   public VerletSimulationData getData() {
      return this.data;
   }

   public void setFriction(double friction) {
      this.friction = friction;
   }

   public double getFriction() {
      return this.friction;
   }

   public void renderSlow(Level level, Matrix4f modelMatrix, Matrix4f projectionMatrix, SubmitNodeStorage submitNodeStorage) {
      if (!this.destroyed) {
         PerformanceTracker.startNoFlush("cloth_rendering");
         RenderSystem.backupProjectionMatrix();
         RenderSystem.setProjectionMatrix(
            ((LevelRendererAccessor)Minecraft.getInstance().levelRenderer).physicsmod$getMainRenderer().levelProjectionMatrixBuffer.getBuffer(projectionMatrix),
            ProjectionType.PERSPECTIVE
         );
         Matrix4fStack modelView = new Matrix4fStack(5);
         modelView.pushMatrix();
         modelView.set(modelMatrix);
         PhysicsMod.storeShaderLightDirections();
         Minecraft.getInstance().gameRenderer.lighting().setupFor(Entry.LEVEL);
         Vec3 view = Minecraft.getInstance().gameRenderer.mainCamera().position();
         this.viewDelta.set(this.getOffset()).sub(view.x, view.y, view.z);
         double renderPercent = (double)Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
         if (Minecraft.getInstance().isPaused()) {
            renderPercent = this.lastRenderPercent;
         } else {
            this.lastRenderPercent = renderPercent;
         }

         this.render(modelView, renderPercent, submitNodeStorage);
         modelView.popMatrix();
         PhysicsMod.restoreShaderLightDirections();
         RenderSystem.restoreProjectionMatrix();
         PerformanceTracker.end("cloth_rendering");
      }
   }
}
