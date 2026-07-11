package net.diebuddies.model;

import com.mojang.blaze3d.IndexType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice.MappedView;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.compat.Iris;
import net.diebuddies.opengl.Pack;
import net.diebuddies.physics.StarterClient;
import net.diebuddies.render.shader.PhysicsShaders;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import org.joml.GeometryUtils;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4i;
import org.lwjgl.system.MemoryUtil;

public class ColladaMesh {
   private static final int OFF_X = 0;
   private static final int OFF_Y = 4;
   private static final int OFF_Z = 8;
   private static final int OFF_COLOR = 12;
   private static final int OFF_U = 16;
   private static final int OFF_V = 20;
   private static final int OFF_NORMAL = 24;
   private static final int OFF_MID_U = 28;
   private static final int OFF_MID_V = 32;
   private static final int OFF_TANGENT = 36;
   public List<Vector3f> positions;
   public List<Vector3f> normals;
   public List<Vector2f> texCoords;
   public List<Vector3f> colors;
   public List<Vector4i> indices;
   public List<Integer> lineIndices;
   public byte[] polyCount;
   private static volatile int bufferCount = 0;
   private static final Vector2f ZERO_UV = new Vector2f();

   public static ColladaMesh parseMesh(XmlNode geometryNode) {
      XmlNode meshNode = geometryNode.getChild("mesh");
      ColladaMesh mesh = new ColladaMesh();
      mesh.parsePositions(meshNode);
      mesh.parseNormals(meshNode);
      mesh.parseTextureCoordinates(meshNode);
      mesh.parseColors(meshNode);
      mesh.parseIndices(meshNode);
      return mesh;
   }

   private void parsePositions(XmlNode mesh) {
      String id = mesh.getChild("vertices").getChild("input").getAttribute("source").substring(1);
      XmlNode source = mesh.getChildWithAttribute("source", "id", id);
      XmlNode floatArray = source.getChild("float_array");
      this.positions = new ObjectArrayList();
      String[] data = floatArray.getData().split(" ");

      for (int i = 0; i < data.length / 3; i++) {
         this.positions.add(new Vector3f(Float.parseFloat(data[i * 3]), Float.parseFloat(data[i * 3 + 1]), Float.parseFloat(data[i * 3 + 2])));
      }
   }

   private void parseIndices(XmlNode mesh) {
      boolean onlyTriangles = false;
      XmlNode triangles = mesh.getChild("polylist");
      if (triangles == null) {
         triangles = mesh.getChild("triangles");
         onlyTriangles = true;
      }

      XmlNode indicesArray = triangles.getChild("p");
      int vertexOffset = Integer.parseInt(triangles.getChildWithAttribute("input", "semantic", "VERTEX").getAttribute("offset"));
      int normalOffset = Integer.parseInt(triangles.getChildWithAttribute("input", "semantic", "NORMAL").getAttribute("offset"));
      int texCoordOffset = Integer.parseInt(triangles.getChildWithAttribute("input", "semantic", "TEXCOORD").getAttribute("offset"));
      int colorOffset = Integer.parseInt(triangles.getChildWithAttribute("input", "semantic", "COLOR").getAttribute("offset"));
      this.indices = new ObjectArrayList();
      this.lineIndices = new ObjectArrayList();
      XmlNode lines = mesh.getChild("lines");
      if (lines != null) {
         XmlNode lineIndicesArray = lines.getChild("p");
         String[] lineData = lineIndicesArray.getData().split(" ");

         for (int i = 0; i < lineData.length; i++) {
            this.lineIndices.add(Integer.parseInt(lineData[i]));
         }
      }

      String[] data = indicesArray.getData().split(" ");
      if (onlyTriangles) {
         this.polyCount = new byte[data.length / 12];

         for (int i = 0; i < this.polyCount.length; i++) {
            this.polyCount[i] = 3;
         }
      } else {
         String[] vcountData = triangles.getChild("vcount").getData().split(" ");
         this.polyCount = new byte[vcountData.length];

         for (int i = 0; i < vcountData.length; i++) {
            this.polyCount[i] = Byte.parseByte(vcountData[i]);
         }
      }

      for (int i = 0; i < data.length / 4; i++) {
         int[] resolved = new int[]{
            Integer.parseInt(data[i * 4]), Integer.parseInt(data[i * 4 + 1]), Integer.parseInt(data[i * 4 + 2]), Integer.parseInt(data[i * 4 + 3])
         };
         this.indices.add(new Vector4i(resolved[vertexOffset], resolved[normalOffset], resolved[texCoordOffset], resolved[colorOffset]));
      }
   }

   private void parseNormals(XmlNode mesh) {
      XmlNode triangles = mesh.getChild("polylist");
      if (triangles == null) {
         triangles = mesh.getChild("triangles");
      }

      String id = triangles.getChildWithAttribute("input", "semantic", "NORMAL").getAttribute("source").substring(1);
      XmlNode source = mesh.getChildWithAttribute("source", "id", id);
      XmlNode floatArray = source.getChild("float_array");
      this.normals = new ObjectArrayList();
      String[] data = floatArray.getData().split(" ");

      for (int i = 0; i < data.length / 3; i++) {
         this.normals.add(new Vector3f(Float.parseFloat(data[i * 3]), Float.parseFloat(data[i * 3 + 1]), Float.parseFloat(data[i * 3 + 2])));
      }
   }

   private void parseTextureCoordinates(XmlNode mesh) {
      XmlNode triangles = mesh.getChild("polylist");
      if (triangles == null) {
         triangles = mesh.getChild("triangles");
      }

      XmlNode texCoordNode = triangles.getChildWithAttribute("input", "semantic", "TEXCOORD");
      if (texCoordNode != null) {
         String id = texCoordNode.getAttribute("source").substring(1);
         XmlNode source = mesh.getChildWithAttribute("source", "id", id);
         XmlNode floatArray = source.getChild("float_array");
         this.texCoords = new ObjectArrayList();
         String[] data = floatArray.getData().split(" ");

         for (int i = 0; i < data.length / 2; i++) {
            this.texCoords.add(new Vector2f(Float.parseFloat(data[i * 2]), Float.parseFloat(data[i * 2 + 1])));
         }
      }
   }

   private void parseColors(XmlNode mesh) {
      XmlNode triangles = mesh.getChild("polylist");
      if (triangles == null) {
         triangles = mesh.getChild("triangles");
      }

      XmlNode color = triangles.getChildWithAttribute("input", "semantic", "COLOR");
      if (color != null) {
         String id = color.getAttribute("source").substring(1);
         XmlNode source = mesh.getChildWithAttribute("source", "id", id);
         XmlNode floatArray = source.getChild("float_array");
         int stride = Integer.parseInt(source.getChild("technique_common").getChild("accessor").getAttribute("stride"));
         this.colors = new ObjectArrayList();
         String[] data = floatArray.getData().split(" ");

         for (int i = 0; i < data.length / stride; i++) {
            this.colors.add(new Vector3f(Float.parseFloat(data[i * stride]), Float.parseFloat(data[i * stride + 1]), Float.parseFloat(data[i * stride + 2])));
         }
      }
   }

   public ClothMesh createRenderMesh(boolean flatShading) {
      int stride = this.getModelVertexFormat().getVertexSize();
      int vertexCount = this.indices.size();
      int indexCount = this.computeTriangulatedIndexCount();
      GpuDevice device = RenderSystem.getDevice();
      GpuBuffer vertexBuffer = device.createBuffer(() -> "Physics Cloth Vertex Buffer " + bufferCount, 34, (long)stride * (long)vertexCount);
      GpuBuffer indexBuffer = device.createBuffer(() -> "Physics Cloth Index Buffer " + bufferCount, 66, 4L * (long)indexCount);
      bufferCount++;
      boolean shaderMods = StarterClient.iris() || StarterClient.optifabric;
      List<Vector3f> tangents = null;
      if (shaderMods) {
         tangents = this.calculateTangents();
      }

      Vector3f flatNormal = new Vector3f();

      try {
         MappedView vertexView = vertexBuffer.map(false, true);

         try {
            MappedView indexView = indexBuffer.map(false, true);

            try {
               long vAddr = MemoryUtil.memAddress(vertexView.data());
               long iAddr = MemoryUtil.memAddress(indexView.data());
               int baseVertex = 0;
               int outIndex = 0;

               for (int p = 0; p < this.polyCount.length; p++) {
                  int pc = this.polyCount[p] & 255;
                  if (pc < 3) {
                     baseVertex += pc;
                  } else {
                     if (flatShading) {
                        int a = this.indices.get(baseVertex).x;
                        int b = this.indices.get(baseVertex + 1).x;
                        int c = this.indices.get(baseVertex + 2).x;
                        GeometryUtils.normal((Vector3fc)this.positions.get(a), (Vector3fc)this.positions.get(b), (Vector3fc)this.positions.get(c), flatNormal);
                     }

                     for (int t = 0; t < pc - 2; t++) {
                        int i1 = baseVertex + t + 1;
                        int i2 = baseVertex + t + 2;
                        MemoryUtil.memPutInt(iAddr + (long)(outIndex + 0) * 4L, i2);
                        MemoryUtil.memPutInt(iAddr + (long)(outIndex + 1) * 4L, i1);
                        MemoryUtil.memPutInt(iAddr + (long)(outIndex + 2) * 4L, baseVertex);
                        outIndex += 3;
                     }

                     for (int j = 0; j < pc; j++) {
                        Vector4i idx = this.indices.get(baseVertex + j);
                        Vector3f pos = this.positions.get(idx.x);
                        Vector2f uv = this.texCoords != null ? this.texCoords.get(idx.z) : ZERO_UV;
                        Vector3f normal = flatShading ? flatNormal : this.normals.get(idx.y);
                        long addr = vAddr + (long)(baseVertex + j) * (long)stride;
                        MemoryUtil.memPutFloat(addr + 0L, pos.x);
                        MemoryUtil.memPutFloat(addr + 4L, pos.y);
                        MemoryUtil.memPutFloat(addr + 8L, pos.z);
                        MemoryUtil.memPutInt(addr + 12L, -1);
                        MemoryUtil.memPutFloat(addr + 16L, uv.x);
                        MemoryUtil.memPutFloat(addr + 20L, uv.y);
                        MemoryUtil.memPutInt(addr + 24L, Pack.normal(normal.x, normal.y, normal.z));
                        if (shaderMods) {
                           Vector3f tan = tangents.get(idx.y);
                           MemoryUtil.memPutFloat(addr + 28L, uv.x);
                           MemoryUtil.memPutFloat(addr + 32L, uv.y);
                           MemoryUtil.memPutInt(addr + 36L, Pack.normal(tan.x, tan.y, tan.z, 1.0F));
                        }
                     }

                     baseVertex += pc;
                  }
               }
            } catch (Throwable var31) {
               if (indexView != null) {
                  try {
                     indexView.close();
                  } catch (Throwable var30) {
                     var31.addSuppressed(var30);
                  }
               }

               throw var31;
            }

            if (indexView != null) {
               indexView.close();
            }
         } catch (Throwable var32) {
            if (vertexView != null) {
               try {
                  vertexView.close();
               } catch (Throwable var29) {
                  var32.addSuppressed(var29);
               }
            }

            throw var32;
         }

         if (vertexView != null) {
            vertexView.close();
         }
      } catch (RuntimeException var33) {
         vertexBuffer.close();
         indexBuffer.close();
         throw var33;
      }

      return new ClothMesh(PhysicsShaders.PHYSICS_ENTITY_FORMAT, vertexBuffer, indexBuffer, indexCount, IndexType.INT);
   }

   private List<Vector3f> calculateTangents() {
      List<Vector3f> tangents = new ObjectArrayList(this.normals.size());
      int indexCount = 0;
      Vector3f tmpTangent = new Vector3f();

      for (int i = 0; i < this.normals.size(); i++) {
         tangents.add(new Vector3f());
      }

      for (int i = 0; i < this.polyCount.length; i++) {
         byte polyCount = this.polyCount[i];
         if (polyCount > 2) {
            int pindex1 = this.indices.get(indexCount).x;
            int tindex1 = this.indices.get(indexCount).z;
            int pindex2 = this.indices.get(indexCount + 1).x;
            int tindex2 = this.indices.get(indexCount + 1).z;
            int pindex3 = this.indices.get(indexCount + 2).x;
            int tindex3 = this.indices.get(indexCount + 2).z;
            GeometryUtils.tangent(
               (Vector3fc)this.positions.get(pindex1),
               (Vector2fc)this.texCoords.get(tindex1),
               (Vector3fc)this.positions.get(pindex2),
               (Vector2fc)this.texCoords.get(tindex2),
               (Vector3fc)this.positions.get(pindex3),
               (Vector2fc)this.texCoords.get(tindex3),
               tmpTangent
            );

            for (int j = 0; j < polyCount; j++) {
               int nindex = this.indices.get(indexCount + j).y;
               tangents.get(nindex).add(tmpTangent);
            }
         } else {
            for (int j = 0; j < polyCount; j++) {
               int nindex = this.indices.get(indexCount + j).y;
               Vector3f normal = this.normals.get(nindex);
               tangents.get(nindex).add(normal.z, -normal.x, normal.y);
            }
         }

         indexCount += polyCount;
      }

      for (int i = 0; i < this.normals.size(); i++) {
         Vector3f tangent = tangents.get(i);
         float lengthSquared = tangent.lengthSquared();
         if (lengthSquared > 0.0F) {
            tangent.div((float)Math.sqrt((double)lengthSquared));
         } else {
            tangent.set(0.0F, 1.0F, 0.0F);
         }
      }

      return tangents;
   }

   public VertexFormat getModelVertexFormat() {
      return StarterClient.iris() ? Iris.remapFormat(PhysicsShaders.PHYSICS_ENTITY_FORMAT) : PhysicsShaders.PHYSICS_ENTITY_FORMAT;
   }

   private int computeTriangulatedIndexCount() {
      int indexCount = 0;

      for (int i = 0; i < this.polyCount.length; i++) {
         int pc = this.polyCount[i] & 255;
         if (pc >= 3) {
            indexCount += (pc - 2) * 3;
         }
      }

      return indexCount;
   }

   public void flipUVs() {
      for (Vector2f uv : this.texCoords) {
         uv.y = 1.0F - uv.y;
      }
   }

   public void renderSlow(SubmitNodeCollector submitNodeStorage, Matrix4f modelMatrix, Identifier texture, int brightness, boolean smoothShading) {
      Vector3f normal = new Vector3f();
      RenderType renderType = PhysicsShaders.PHYSICS_CLOTH_RENDER.apply(texture);
      PoseStack currentPose = new PoseStack();
      currentPose.mulPose(modelMatrix);
      submitNodeStorage.submitCustomGeometry(
         currentPose,
         renderType,
         (pose, vertexConsumer) -> {
            int indexCount = 0;

            for (int i = 0; i < this.polyCount.length; i++) {
               byte polyCount = this.polyCount[i];
               if (smoothShading) {
                  if (polyCount == 4) {
                     this.bufferVertex(pose.pose(), vertexConsumer, indexCount + 2, brightness);
                     this.bufferVertex(pose.pose(), vertexConsumer, indexCount + 1, brightness);
                     this.bufferVertex(pose.pose(), vertexConsumer, indexCount, brightness);
                     this.bufferVertex(pose.pose(), vertexConsumer, indexCount, brightness);
                     this.bufferVertex(pose.pose(), vertexConsumer, indexCount + 3, brightness);
                     this.bufferVertex(pose.pose(), vertexConsumer, indexCount + 2, brightness);
                  } else if (polyCount == 3) {
                     this.bufferVertex(pose.pose(), vertexConsumer, indexCount, brightness);
                     this.bufferVertex(pose.pose(), vertexConsumer, indexCount + 1, brightness);
                     this.bufferVertex(pose.pose(), vertexConsumer, indexCount + 2, brightness);
                  }
               } else if (polyCount == 4) {
                  GeometryUtils.normal(
                     (Vector3fc)this.positions.get(this.indices.get(indexCount).x),
                     (Vector3fc)this.positions.get(this.indices.get(indexCount + 1).x),
                     (Vector3fc)this.positions.get(this.indices.get(indexCount + 2).x),
                     normal
                  );
                  this.bufferVertex(pose.pose(), vertexConsumer, indexCount + 2, normal, brightness);
                  this.bufferVertex(pose.pose(), vertexConsumer, indexCount + 1, normal, brightness);
                  this.bufferVertex(pose.pose(), vertexConsumer, indexCount, normal, brightness);
                  this.bufferVertex(pose.pose(), vertexConsumer, indexCount, normal, brightness);
                  this.bufferVertex(pose.pose(), vertexConsumer, indexCount + 3, normal, brightness);
                  this.bufferVertex(pose.pose(), vertexConsumer, indexCount + 2, normal, brightness);
               } else if (polyCount == 3) {
                  GeometryUtils.normal(
                     (Vector3fc)this.positions.get(this.indices.get(indexCount).x),
                     (Vector3fc)this.positions.get(this.indices.get(indexCount + 1).x),
                     (Vector3fc)this.positions.get(this.indices.get(indexCount + 2).x),
                     normal
                  );
                  this.bufferVertex(pose.pose(), vertexConsumer, indexCount, normal, brightness);
                  this.bufferVertex(pose.pose(), vertexConsumer, indexCount + 1, normal, brightness);
                  this.bufferVertex(pose.pose(), vertexConsumer, indexCount + 2, normal, brightness);
               }

               indexCount += polyCount;
            }
         }
      );
   }

   private void bufferVertex(Matrix4f transform, VertexConsumer bufferbuilder, int index, int brightness) {
      int pindex = this.indices.get(index).x;
      int nindex = this.indices.get(index).y;
      int tindex = this.indices.get(index).z;
      Vector3f position = this.positions.get(pindex);
      Vector3f normal = this.normals.get(nindex);
      Vector2f uv = this.texCoords.get(tindex);
      bufferbuilder.addVertex(transform, position.x, position.y, position.z)
         .setColor(1.0F, 1.0F, 1.0F, 1.0F)
         .setUv(uv.x, uv.y)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(brightness)
         .setNormal(normal.x, normal.y, normal.z);
   }

   private void bufferVertex(Matrix4f transform, VertexConsumer bufferbuilder, int index, Vector3f normal, int brightness) {
      int pindex = this.indices.get(index).x;
      int tindex = this.indices.get(index).z;
      Vector3f position = this.positions.get(pindex);
      Vector2f uv = this.texCoords.get(tindex);
      bufferbuilder.addVertex(transform, position.x, position.y, position.z)
         .setColor(1.0F, 1.0F, 1.0F, 1.0F)
         .setUv(uv.x, uv.y)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(brightness)
         .setNormal(normal.x, normal.y, normal.z);
   }
}
