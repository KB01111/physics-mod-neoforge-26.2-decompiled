package net.diebuddies.minecraft;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Random;
import net.diebuddies.physics.Mesh;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class RenderHelper {
   private static Random random = new Random();

   public static void renderMesh(
      SubmitNodeCollector submitNodeCollector,
      int seed,
      float rotation,
      Identifier texture,
      Mesh mesh,
      PoseStack poseStack,
      int light,
      int overlay,
      boolean shade
   ) {
      Matrix4f transformation = new Matrix4f(poseStack.last().pose());
      random.setSeed((long)seed);
      transformation.rotateX(random.nextFloat() * (float) Math.PI);
      transformation.rotateY(random.nextFloat() * (float) Math.PI);
      transformation.rotateZ(random.nextFloat() * (float) Math.PI + rotation * 0.5F);
      Matrix3f normalMatrix = transformation.normal(new Matrix3f());
      if (!shade) {
         normalMatrix.set(poseStack.last().normal());
      }

      submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.entitySolid(texture), (pose, consumer) -> {
         Vector3f tmpPos = new Vector3f();
         Vector3f tmpNormal = new Vector3f();

         for (int i = 0; i < mesh.indicesQuads.size(); i++) {
            int index = mesh.indicesQuads.getInt(i);
            Vector3f position = mesh.positions.get(index);
            Vector2f uv = mesh.uvs.get(index);
            Vector3f normal = mesh.normals.get(index);
            position = transformation.transformPosition(position, tmpPos);
            if (shade) {
               tmpNormal.set(normal.x, normal.y, normal.z);
            } else {
               tmpNormal.set(0.0, 1.0, 0.0);
            }

            normalMatrix.transform(tmpNormal);
            float r = 1.0F;
            float g = 1.0F;
            float b = 1.0F;
            if (mesh.colors.size() > 0) {
               int color = mesh.colors.getInt(index);
               r = (float)(color & 0xFF) / 255.0F;
               g = (float)(color >> 8 & 0xFF) / 255.0F;
               b = (float)(color >> 16 & 0xFF) / 255.0F;
            }

            int color = ARGB.colorFromFloat(1.0F, r, g, b);
            consumer.addVertex(position.x, position.y, position.z, color, uv.x, uv.y, overlay, light, tmpNormal.x, tmpNormal.y, tmpNormal.z);
         }
      });
   }
}
