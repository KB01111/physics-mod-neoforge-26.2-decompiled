package net.diebuddies.physics.verlet.constraints;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.compat.Iris;
import net.diebuddies.compat.Optifine;
import net.diebuddies.config.ConfigClient;
import net.diebuddies.math.SimplePoolVector3d;
import net.diebuddies.physics.PhysicsMod;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.physics.verlet.VerletLine;
import net.diebuddies.physics.verlet.VerletPoint;
import net.diebuddies.physics.verlet.VerletQuad;
import net.diebuddies.physics.verlet.VerletSimulation;
import net.diebuddies.physics.verlet.VerletTriangle;
import net.diebuddies.render.shader.PhysicsShaders;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

public class RenderConstraint implements VerletConstraint {
   private static final Vector3d UP_VECTOR = new Vector3d(0.0, 1.0, 0.0);
   private static Vector3d tmp1 = new Vector3d();
   private static Vector3d tmp2 = new Vector3d();
   private static Vector3d tmp3 = new Vector3d();
   private static SimplePoolVector3d vectorPool;
   private static ObjectArrayList<Vector3d> vertices = new ObjectArrayList();
   private static IntArrayList lineData = new IntArrayList();
   private static IntArrayList indices = new IntArrayList();
   public double lineThickness = 0.02;
   private Vector3f scratchPosition = new Vector3f();

   @Override
   public void render(Matrix4fStack matrixStack, double renderPercent, SubmitNodeCollector submitNodeStorage, VerletSimulation simulation) {
      int brightness = simulation.brightness;
      List<VerletQuad> quads = simulation.getQuads();
      List<VerletTriangle> triangles = simulation.getTriangles();
      List<VerletLine> lines = simulation.getLines();
      List<VerletPoint> points = simulation.getPoints();

      for (int i = 0; i < points.size(); i++) {
         VerletPoint point = points.get(i);
         point.updateRenderPosition(renderPercent);
         point.renderPosition.add(simulation.viewDelta);
      }

      PoseStack currentPose = new PoseStack();
      currentPose.mulPose(matrixStack);
      Matrix4f modelMatrix = currentPose.last().pose();
      if (simulation.getQuads().size() > 0 || simulation.getTriangles().size() > 0) {
         if ((!StarterClient.iris() || !Iris.isExtending()) && (!StarterClient.optifabric || !Optifine.isUsingShadersNoInternal())) {
            RenderType renderType = PhysicsShaders.PHYSICS_CLOTH_RENDER.apply(simulation.textureID);
            submitNodeStorage.submitCustomGeometry(
               currentPose,
               renderType,
               (pose, vertexConsumer) -> {
                  for (int i = 0; i < quads.size(); i++) {
                     VerletQuad quad = quads.get(i);
                     if (ConfigClient.clothSmoothShading) {
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           quad.point4.renderPosition,
                           quad.point4.uv,
                           quad.point4.bufferNormal,
                           brightness,
                           quad.point4.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           quad.point3.renderPosition,
                           quad.point3.uv,
                           quad.point3.bufferNormal,
                           brightness,
                           quad.point3.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           quad.point2.renderPosition,
                           quad.point2.uv,
                           quad.point2.bufferNormal,
                           brightness,
                           quad.point2.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           quad.point1.renderPosition,
                           quad.point1.uv,
                           quad.point1.bufferNormal,
                           brightness,
                           quad.point1.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           quad.point4.renderPosition,
                           quad.point4.uv,
                           quad.point4.bufferNormal,
                           brightness,
                           quad.point4.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           quad.point2.renderPosition,
                           quad.point2.uv,
                           quad.point2.bufferNormal,
                           brightness,
                           quad.point2.rgba
                        );
                     } else {
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           quad.point4.renderPosition,
                           quad.point4.uv,
                           quad.bufferNormal,
                           brightness,
                           quad.point4.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           quad.point3.renderPosition,
                           quad.point3.uv,
                           quad.bufferNormal,
                           brightness,
                           quad.point3.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           quad.point2.renderPosition,
                           quad.point2.uv,
                           quad.bufferNormal,
                           brightness,
                           quad.point2.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           quad.point1.renderPosition,
                           quad.point1.uv,
                           quad.bufferNormal,
                           brightness,
                           quad.point1.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           quad.point4.renderPosition,
                           quad.point4.uv,
                           quad.bufferNormal,
                           brightness,
                           quad.point4.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           quad.point2.renderPosition,
                           quad.point2.uv,
                           quad.bufferNormal,
                           brightness,
                           quad.point2.rgba
                        );
                     }
                  }

                  for (int ix = 0; ix < triangles.size(); ix++) {
                     VerletTriangle triangle = triangles.get(ix);
                     if (ConfigClient.clothSmoothShading) {
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           triangle.point3.renderPosition,
                           triangle.point3.uv,
                           triangle.point3.bufferNormal,
                           brightness,
                           triangle.point3.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           triangle.point2.renderPosition,
                           triangle.point2.uv,
                           triangle.point2.bufferNormal,
                           brightness,
                           triangle.point2.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           triangle.point1.renderPosition,
                           triangle.point1.uv,
                           triangle.point1.bufferNormal,
                           brightness,
                           triangle.point1.rgba
                        );
                     } else {
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           triangle.point3.renderPosition,
                           triangle.point3.uv,
                           triangle.bufferNormal,
                           brightness,
                           triangle.point3.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           triangle.point2.renderPosition,
                           triangle.point2.uv,
                           triangle.bufferNormal,
                           brightness,
                           triangle.point2.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           triangle.point1.renderPosition,
                           triangle.point1.uv,
                           triangle.bufferNormal,
                           brightness,
                           triangle.point1.rgba
                        );
                     }
                  }
               }
            );
         } else {
            RenderType renderType = PhysicsShaders.PHYSICS_CLOTH_RENDER_IRIS.apply(simulation.textureID);
            submitNodeStorage.submitCustomGeometry(
               currentPose,
               renderType,
               (pose, vertexConsumer) -> {
                  for (int i = 0; i < quads.size(); i++) {
                     VerletQuad quad = quads.get(i);
                     if (ConfigClient.clothSmoothShading) {
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           quad.point1.renderPosition,
                           quad.point1.uv,
                           quad.point1.bufferNormal,
                           brightness,
                           quad.point1.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           quad.point2.renderPosition,
                           quad.point2.uv,
                           quad.point2.bufferNormal,
                           brightness,
                           quad.point2.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           quad.point3.renderPosition,
                           quad.point3.uv,
                           quad.point3.bufferNormal,
                           brightness,
                           quad.point3.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           quad.point4.renderPosition,
                           quad.point4.uv,
                           quad.point4.bufferNormal,
                           brightness,
                           quad.point4.rgba
                        );
                     } else {
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           quad.point1.renderPosition,
                           quad.point1.uv,
                           quad.bufferNormal,
                           brightness,
                           quad.point1.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           quad.point2.renderPosition,
                           quad.point2.uv,
                           quad.bufferNormal,
                           brightness,
                           quad.point2.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           quad.point3.renderPosition,
                           quad.point3.uv,
                           quad.bufferNormal,
                           brightness,
                           quad.point3.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           quad.point4.renderPosition,
                           quad.point4.uv,
                           quad.bufferNormal,
                           brightness,
                           quad.point4.rgba
                        );
                     }
                  }

                  for (int ix = 0; ix < triangles.size(); ix++) {
                     VerletTriangle triangle = triangles.get(ix);
                     if (ConfigClient.clothSmoothShading) {
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           triangle.point1.renderPosition,
                           triangle.point1.uv,
                           triangle.point1.bufferNormal,
                           brightness,
                           triangle.point1.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           triangle.point2.renderPosition,
                           triangle.point2.uv,
                           triangle.point2.bufferNormal,
                           brightness,
                           triangle.point2.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           triangle.point3.renderPosition,
                           triangle.point3.uv,
                           triangle.point3.bufferNormal,
                           brightness,
                           triangle.point3.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           triangle.point3.renderPosition,
                           triangle.point3.uv,
                           triangle.point3.bufferNormal,
                           brightness,
                           triangle.point3.rgba
                        );
                     } else {
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           triangle.point1.renderPosition,
                           triangle.point1.uv,
                           triangle.bufferNormal,
                           brightness,
                           triangle.point1.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           triangle.point2.renderPosition,
                           triangle.point2.uv,
                           triangle.bufferNormal,
                           brightness,
                           triangle.point2.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           triangle.point3.renderPosition,
                           triangle.point3.uv,
                           triangle.bufferNormal,
                           brightness,
                           triangle.point3.rgba
                        );
                        this.bufferVertex(
                           modelMatrix,
                           vertexConsumer,
                           renderPercent,
                           triangle.point3.renderPosition,
                           triangle.point3.uv,
                           triangle.bufferNormal,
                           brightness,
                           triangle.point3.rgba
                        );
                     }
                  }
               }
            );
         }
      }

      if (lines.size() > 0) {
         if (vectorPool == null) {
            vectorPool = new SimplePoolVector3d(128);
         }

         this.renderFromFrenetFrame(submitNodeStorage, currentPose, lines, this.circleShape(5), brightness, this.lineThickness, renderPercent);
         vectorPool.reset();
      }
   }

   private void renderFromFrenetFrame(
      SubmitNodeCollector submitNodeStorage,
      PoseStack currentPose,
      List<VerletLine> spline,
      List<Vector2d> shape,
      int brightness,
      double radius,
      double renderPercent
   ) {
      vertices.clear();
      lineData.clear();
      indices.clear();
      int vertexSegments = shape.size();
      int size = spline.size();
      Vector3d tangent = vectorPool.get();
      Vector3d binormal = vectorPool.get();
      Vector3d normal = vectorPool.get();

      for (int i = 0; i <= size; i++) {
         VerletLine line = spline.get(Math.min(i, size - 1));
         Vector3d point;
         Vector3d nextPoint;
         if (size == i) {
            point = tmp1.set(line.point1.renderPosition);
            nextPoint = tmp2.set(line.point2.renderPosition);
            Vector3d dir = nextPoint.sub(point, tmp3);
            point.set(nextPoint);
            nextPoint.add(dir);
         } else {
            point = tmp1.set(line.point1.renderPosition);
            nextPoint = tmp2.set(line.point2.renderPosition);
         }

         this.normalize(nextPoint.sub(point, tangent));
         this.normalize(tangent.cross(UP_VECTOR, binormal));
         this.normalize(binormal.cross(tangent, normal));

         for (int j = vertexSegments - 1; j >= 0; j--) {
            Vector2d shapePos = shape.get(j);
            vertices.add(
               vectorPool.get(
                  point.x + (normal.x * shapePos.x + binormal.x * shapePos.y) * radius,
                  point.y + (normal.y * shapePos.x + binormal.y * shapePos.y) * radius,
                  point.z + (normal.z * shapePos.x + binormal.z * shapePos.y) * radius
               )
            );
            lineData.add(i);
         }
      }

      int indexOffset = 0;
      int iterations = vertices.size() / vertexSegments - 1;
      boolean closeEnds = true;

      for (int i = 0; i < iterations; i++) {
         for (int j = 0; j < vertexSegments; j++) {
            int index1 = i * vertexSegments + j + indexOffset;
            int index2 = i * vertexSegments + (j + 1) % vertexSegments + indexOffset;
            indices.add(index1);
            indices.add(index1 + vertexSegments);
            indices.add(index2);
            indices.add(index2);
            indices.add(index1 + vertexSegments);
            indices.add(index2 + vertexSegments);
         }
      }

      if (closeEnds) {
         VerletLine startLine = spline.get(0);
         VerletLine endLine = spline.get(spline.size() - 1);
         Vector3d start = startLine.point1.renderPosition;
         Vector3d end = endLine.point2.renderPosition;
         if (vertices.size() % shape.size() == 0) {
            vertices.add(vectorPool.get(start.x, start.y, start.z));
            vertices.add(vectorPool.get(end.x, end.y, end.z));
            lineData.add(0);
            lineData.add(spline.size() - 1);
         } else {
            ((Vector3d)vertices.get(vertices.size() - 2)).set(start.x, start.y, start.z);
            ((Vector3d)vertices.get(vertices.size() - 1)).set(end.x, end.y, end.z);
            lineData.add(0);
            lineData.add(spline.size() - 1);
         }

         int indexStart = vertices.size() - 2;
         int indexEnd = vertices.size() - 1;
         int startEnd = vertices.size() - 2 - vertexSegments;

         for (int j = 0; j < vertexSegments; j++) {
            indices.add(j);
            indices.add((j + 1) % vertexSegments);
            indices.add(indexStart);
            indices.add(startEnd + j);
            indices.add(indexEnd);
            indices.add(startEnd + (j + 1) % vertexSegments);
         }
      }

      RenderType renderType = null;
      if ((!StarterClient.iris() || !Iris.isExtending()) && (!StarterClient.optifabric || !Optifine.isUsingShadersNoInternal())) {
         renderType = PhysicsShaders.PHYSICS_CLOTH_RENDER.apply(PhysicsMod.WHITE_TEXTURE);
      } else {
         renderType = RenderTypes.armorCutoutNoCull(PhysicsMod.WHITE_TEXTURE);
      }

      submitNodeStorage.submitCustomGeometry(currentPose, renderType, (pose, vertexConsumer) -> {
         Matrix4f modelMatrix = pose.pose();

         for (int i = 0; i < indices.size(); i++) {
            int index = indices.getInt(i);
            Vector3d position = (Vector3d)vertices.get(index);
            index = indices.getInt(i / 3 * 3);
            int lineIndex = lineData.getInt(index);
            VerletLine line = spline.get(Math.min(lineIndex, size - 1));
            VerletPoint point = lineIndex == size ? line.point2 : line.point1;
            this.bufferVertex(modelMatrix, vertexConsumer, renderPercent, position, point.uv, point.bufferNormal, brightness, point.rgba);
            if (StarterClient.iris() && Iris.isExtending() && i % 3 == 0) {
               this.bufferVertex(modelMatrix, vertexConsumer, renderPercent, position, point.uv, point.bufferNormal, brightness, point.rgba);
            }
         }
      });
   }

   private void bufferVertex(
      Matrix4f modelMatrix, VertexConsumer bufferbuilder, double renderPercent, Vector3d position, Vector2f uv, Vector3d normal, int brightness, int color
   ) {
      modelMatrix.transformPosition((float)position.x, (float)position.y, (float)position.z, this.scratchPosition);
      bufferbuilder.addVertex(
         this.scratchPosition.x,
         this.scratchPosition.y,
         this.scratchPosition.z,
         color,
         uv.x,
         uv.y,
         OverlayTexture.NO_OVERLAY,
         brightness,
         (float)normal.x,
         (float)normal.y,
         (float)normal.z
      );
   }

   private void normalize(Vector3d v) {
      double lengthSquared = v.x * v.x + v.y * v.y + v.z * v.z;
      if (lengthSquared != 0.0) {
         double invLength = Math.invsqrt(lengthSquared);
         v.x *= invLength;
         v.y *= invLength;
         v.z *= invLength;
      }
   }

   public List<Vector2d> circleShape(int segments) {
      List<Vector2d> circle = new ObjectArrayList();

      for (int i = 0; i < segments; i++) {
         double angle = (java.lang.Math.PI * 2) * (double)i / (double)segments;
         Vector2d circlePoint = new Vector2d(Math.cos(angle), Math.sin(angle));
         circle.add(circlePoint);
      }

      return circle;
   }

   public List<Vector2d> starShape(double spikeRadius, int segments) {
      List<Vector2d> circle = new ObjectArrayList();
      double radius = 1.0 - spikeRadius;

      for (int i = 0; i < segments; i++) {
         if (i % 2 == 0) {
            radius = 1.0 + spikeRadius;
         } else {
            radius = 1.0 - spikeRadius;
         }

         double angle = (java.lang.Math.PI * 2) * (double)i / (double)segments;
         Vector2d circlePoint = new Vector2d(Math.cos(angle) * radius, Math.sin(angle) * radius);
         circle.add(circlePoint);
      }

      return circle;
   }
}
