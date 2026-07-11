package net.diebuddies.physics;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.diebuddies.opengl.Pack;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Mesh {
   private static final Vector3f[] faceNormals = new Vector3f[]{
      new Vector3f(0.0F, 0.0F, 1.0F),
      new Vector3f(1.0F, 0.0F, 0.0F),
      new Vector3f(0.0F, 0.0F, -1.0F),
      new Vector3f(-1.0F, 0.0F, 0.0F),
      new Vector3f(0.0F, 1.0F, 0.0F),
      new Vector3f(0.0F, -1.0F, 0.0F)
   };
   public List<Vector3f> positions;
   public List<Vector2f> uvs;
   public List<Vector3f> normals;
   public IntList colors;
   public ByteList sides;
   public List<Vector4f> tangents;
   public Vector2f midcoord;
   public List<Vector2f> midcoords;
   public IntList indices;
   public IntList indicesQuads;
   public Vector3f offset;
   public float radius = -1.0F;
   public int sodiumUVOffset = 0;
   public boolean canDiscard = true;

   public Mesh(boolean canDiscard) {
      this.canDiscard = canDiscard;
      this.positions = new ObjectArrayList();
      this.uvs = new ObjectArrayList();
      this.normals = new ObjectArrayList();
      this.indices = new IntArrayList();
      this.indicesQuads = new IntArrayList();
      this.colors = new IntArrayList();
      this.sides = new ByteArrayList();
   }

   public Mesh() {
      this(true);
   }

   public void calculatePBRData(boolean defaultMidCoord) {
      int vertexCount = this.positions.size();
      this.midcoords = new ObjectArrayList(vertexCount);
      this.midcoord = new Vector2f();
      this.tangents = new ObjectArrayList(vertexCount);
      Vector3f[] tan1 = new Vector3f[vertexCount];
      Vector3f[] tan2 = new Vector3f[vertexCount];
      int[] midcoordCounts = new int[vertexCount];

      for (int i = 0; i < vertexCount; i++) {
         this.midcoords.add(new Vector2f(defaultMidCoord ? 0.5F : 0.0F));
         this.tangents.add(new Vector4f());
         tan1[i] = new Vector3f();
         tan2[i] = new Vector3f();
      }

      if (this.uvs.size() != 0 && this.indices.size() >= 3) {
         Vector3f tmp1 = new Vector3f();
         Vector3f tmp2 = new Vector3f();
         Vector2f uvmin = new Vector2f();
         Vector2f uvmax = new Vector2f();
         Vector2f faceMid = new Vector2f();

         for (int i = 0; i < this.indices.size(); i += 3) {
            int i0 = this.indices.getInt(i);
            int i1 = this.indices.getInt(i + 1);
            int i2 = this.indices.getInt(i + 2);
            Vector3f v0 = this.positions.get(i0);
            Vector3f v1 = this.positions.get(i1);
            Vector3f v2 = this.positions.get(i2);
            Vector2f uv0 = this.uvs.get(i0);
            Vector2f uv1 = this.uvs.get(i1);
            Vector2f uv2 = this.uvs.get(i2);
            Vector3f edge1 = v1.sub(v0, tmp1);
            Vector3f edge2 = v2.sub(v0, tmp2);
            float deltaU1 = uv1.x - uv0.x;
            float deltaV1 = uv1.y - uv0.y;
            float deltaU2 = uv2.x - uv0.x;
            float deltaV2 = uv2.y - uv0.y;
            float denom = deltaU1 * deltaV2 - deltaU2 * deltaV1;
            float f = denom == 0.0F ? 0.0F : 1.0F / denom;
            float tangentx = f * (deltaV2 * edge1.x - deltaV1 * edge2.x);
            float tangenty = f * (deltaV2 * edge1.y - deltaV1 * edge2.y);
            float tangentz = f * (deltaV2 * edge1.z - deltaV1 * edge2.z);
            float bitangentx = f * (-deltaU2 * edge1.x + deltaU1 * edge2.x);
            float bitangenty = f * (-deltaU2 * edge1.y + deltaU1 * edge2.y);
            float bitangentz = f * (-deltaU2 * edge1.z + deltaU1 * edge2.z);
            tan1[i0].add(tangentx, tangenty, tangentz);
            tan1[i1].add(tangentx, tangenty, tangentz);
            tan1[i2].add(tangentx, tangenty, tangentz);
            tan2[i0].add(bitangentx, bitangenty, bitangentz);
            tan2[i1].add(bitangentx, bitangenty, bitangentz);
            tan2[i2].add(bitangentx, bitangenty, bitangentz);
            if (!defaultMidCoord) {
               uvmin.set(uv0);
               uvmin.min(uv1);
               uvmin.min(uv2);
               uvmax.set(uv0);
               uvmax.max(uv1);
               uvmax.max(uv2);
               faceMid.set(uvmin).add(uvmax).mul(0.5F);
               this.midcoords.get(i0).add(faceMid);
               this.midcoords.get(i1).add(faceMid);
               this.midcoords.get(i2).add(faceMid);
               midcoordCounts[i0]++;
               midcoordCounts[i1]++;
               midcoordCounts[i2]++;
            }
         }

         Vector3f tmp = new Vector3f();
         Vector3f tangent = new Vector3f();
         Vector3f cross = new Vector3f();

         for (int ix = 0; ix < vertexCount; ix++) {
            if (!defaultMidCoord) {
               if (midcoordCounts[ix] > 0) {
                  this.midcoords.get(ix).mul(1.0F / (float)midcoordCounts[ix]);
               } else {
                  this.midcoords.get(ix).set(0.5F);
               }
            }

            if (this.normals != null && ix < this.normals.size()) {
               Vector3f normal = this.normals.get(ix);
               Vector3f t = tan1[ix];
               if (normal.lengthSquared() != 0.0F && t.lengthSquared() != 0.0F) {
                  tangent.set(t).sub(tmp.set(normal).mul(normal.dot(t)));
                  if (tangent.lengthSquared() == 0.0F) {
                     this.tangents.get(ix).set(1.0F, 0.0F, 0.0F, 1.0F);
                  } else {
                     tangent.normalize();
                     float tangentw = cross.set(tangent).cross(normal).dot(tan2[ix]) < 0.0F ? -1.0F : 1.0F;
                     this.tangents.get(ix).set(tangent.x, tangent.y, tangent.z, tangentw);
                  }
               } else {
                  this.tangents.get(ix).set(1.0F, 0.0F, 0.0F, 1.0F);
               }
            } else {
               this.tangents.get(ix).set(1.0F, 0.0F, 0.0F, 1.0F);
            }
         }
      }
   }

   public void addColor(float r, float g, float b) {
      this.colors.add(Pack.color(r, g, b));
   }

   public void addColor(float r, float g, float b, float a) {
      this.colors.add(Pack.color(r, g, b, a));
   }

   public void addColor(int r, int g, int b, int a) {
      this.colors.add(Pack.color(r, g, b, a));
   }

   public float getRadius(boolean reculalcate) {
      if (reculalcate) {
         this.radius = -1.0F;
      }

      if (this.radius < 0.0F) {
         for (int i = 0; i < this.positions.size(); i++) {
            Vector3f position = this.positions.get(i);
            float radiusSquared = position.lengthSquared();
            if (radiusSquared > this.radius) {
               this.radius = radiusSquared;
            }
         }

         this.radius = (float)Math.sqrt((double)this.radius);
      }

      return this.radius;
   }

   public float getRadius() {
      return this.getRadius(false);
   }

   public void move(Vector3f position) {
      for (int i = 0; i < this.positions.size(); i++) {
         this.positions.get(i).add(position);
      }
   }

   public void calculateOffset() {
      this.calculateOffset(false);
   }

   public void calculateOffset(boolean needsSides) {
      calculateMeshOffsets(this, needsSides);
   }

   public static void calculateMeshOffsets(Mesh mesh, boolean needsSides) {
      Vector3f offset = mesh.offset = new Vector3f();

      for (Vector3f position : mesh.positions) {
         offset.add(position);
      }

      if (mesh.positions.size() > 0) {
         offset.div((float)mesh.positions.size());
      }

      calculateMetaData(mesh, needsSides);
   }

   public static void calculateMeshOffsets(List<Mesh> meshes, boolean needsSides) {
      int meshSize = meshes.size();
      int count = 0;
      Vector3f offset = new Vector3f();

      for (int i = 0; i < meshSize; i++) {
         Mesh mesh = meshes.get(i);

         for (Vector3f position : mesh.positions) {
            offset.add(position);
         }

         count += mesh.positions.size();
      }

      if (count > 0) {
         offset.div((float)count);
      }

      for (int i = 0; i < meshSize; i++) {
         Mesh mesh = meshes.get(i);
         mesh.offset = new Vector3f(offset);
         calculateMetaData(mesh, needsSides);
      }
   }

   private static void calculateMetaData(Mesh mesh, boolean needsSides) {
      Vector3f offset = mesh.offset;

      for (Vector3f position : mesh.positions) {
         position.sub(offset);
         float radiusSquared = position.lengthSquared();
         if (radiusSquared > mesh.radius) {
            mesh.radius = radiusSquared;
         }
      }

      mesh.radius = (float)Math.sqrt((double)mesh.radius);
      if (needsSides) {
         for (int j = 0; j < mesh.normals.size(); j++) {
            boolean hasSide = false;
            Vector3f normal = mesh.normals.get(j);

            for (int i = 0; i < faceNormals.length; i++) {
               if ((double)Math.abs(org.joml.Math.acos(normal.dot(faceNormals[i]))) < 0.01) {
                  mesh.sides.add((byte)i);
                  hasSide = true;
                  break;
               }
            }

            if (!hasSide) {
               mesh.sides.add((byte)-1);
            }
         }
      }
   }

   public List<Integer> calculateFaceDirections() {
      List<Integer> sides = new ObjectArrayList();

      for (int j = 0; j < this.normals.size(); j++) {
         Vector3f normal = this.normals.get(j);
         int maxComp = normal.maxComponent();
         double max = (double)normal.get(maxComp);
         if (max >= 0.0) {
            if (maxComp == 0) {
               sides.add(1);
            } else if (maxComp == 1) {
               sides.add(4);
            } else {
               sides.add(0);
            }
         } else if (maxComp == 0) {
            sides.add(3);
         } else if (maxComp == 1) {
            sides.add(5);
         } else {
            sides.add(2);
         }
      }

      return sides;
   }

   public boolean isEmpty() {
      return this.indices.size() == 0;
   }

   public void clearMemory() {
      if (this.canDiscard) {
         this.positions = null;
         this.uvs = null;
         this.normals = null;
         this.colors = null;
         this.sides = null;
         this.tangents = null;
         this.midcoord = null;
         this.midcoords = null;
         this.indices = null;
         this.indicesQuads = null;
      }
   }
}
