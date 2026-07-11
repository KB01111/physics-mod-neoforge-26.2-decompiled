package net.diebuddies.model;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4i;

public class ColladaParser {
   public static ColladaMesh loadStaticModel(BufferedReader reader) {
      return loadMultipleStaticModel(reader).values().iterator().next();
   }

   public static Map<String, ColladaMesh> loadMultipleStaticModel(BufferedReader reader) {
      try {
         XmlNode colladaFile = XmlParser.loadFile(reader);
         XmlNode geometries = colladaFile.getChild("library_geometries");
         Map<String, ColladaMesh> meshes = new Object2ObjectOpenHashMap();

         for (XmlNode geometry : geometries.getChildren("geometry")) {
            meshes.put(geometry.getAttribute("name"), ColladaMesh.parseMesh(geometry));
         }

         return meshes;
      } catch (Exception var6) {
         var6.printStackTrace();
         return null;
      }
   }

   public static void convertFolder(File src) {
      for (File file : src.listFiles()) {
         if (file.getName().endsWith("dae")) {
            String name = file.getName();
            String nameNoExtension = name;
            if (name.contains(".")) {
               nameNoExtension = name.substring(0, name.lastIndexOf(46));
            }

            try (
               FileReader fileReader = new FileReader(file);
               BufferedReader reader = new BufferedReader(fileReader);
            ) {
               saveModelsBinary(new File(file.getParentFile().getAbsolutePath() + "/" + nameNoExtension + ".bf"), loadMultipleStaticModel(reader));
            } catch (IOException var15) {
               var15.printStackTrace();
            }
         }
      }
   }

   public static void saveModelsBinary(File file, Map<String, ColladaMesh> models) {
      if (file.exists()) {
         file.delete();
      }

      try {
         file.createNewFile();
      } catch (IOException var9) {
         var9.printStackTrace();
      }

      try (
         OutputStream outputStream = new FileOutputStream(file);
         DataOutputStream output = new DataOutputStream(outputStream);
      ) {
         output.writeByte(models.size());

         for (Entry<String, ColladaMesh> entry : models.entrySet()) {
            output.writeUTF(entry.getKey());
            ColladaMesh mesh = entry.getValue();
            write3f(output, mesh.positions);
            write3f(output, mesh.normals);
            write2f(output, mesh.texCoords);
            write3b(output, mesh.colors);
            write4s(output, mesh.indices);
            write1s(output, mesh.lineIndices);
            write1b(output, mesh.polyCount);
         }
      } catch (IOException var12) {
         var12.printStackTrace();
      }
   }

   public static Map<String, ColladaMesh> readModelsBinary(File file) {
      try {
         Object var13;
         try (
            InputStream inputStream = new FileInputStream(file);
            DataInputStream input = new DataInputStream(inputStream);
         ) {
            int size = input.readInt();
            Map<String, ColladaMesh> models = new Object2ObjectOpenHashMap();

            for (int i = 0; i < size; i++) {
               ColladaMesh mesh = new ColladaMesh();
               String name = input.readUTF();
               mesh.positions = read3f(input);
               mesh.normals = read3f(input);
               mesh.texCoords = read2f(input);
               mesh.colors = read3f(input);
               mesh.indices = read4s(input);
               mesh.lineIndices = read1s(input);
               mesh.polyCount = read1b(input);
               models.put(name, mesh);
            }

            var13 = models;
         }

         return (Map<String, ColladaMesh>)var13;
      } catch (IOException var12) {
         var12.printStackTrace();
         return null;
      }
   }

   private static void write1b(DataOutputStream output, byte[] list) throws IOException {
      output.writeInt(list.length);

      for (byte v : list) {
         output.writeByte(v);
      }
   }

   private static void write1s(DataOutputStream output, List<Integer> list) throws IOException {
      output.writeInt(list.size());

      for (Integer v : list) {
         output.writeShort(v);
      }
   }

   private static void write4s(DataOutputStream output, List<Vector4i> list) throws IOException {
      output.writeInt(list.size());

      for (Vector4i v : list) {
         output.writeShort(v.x);
         output.writeShort(v.y);
         output.writeShort(v.z);
         output.writeShort(v.w);
      }
   }

   private static void write2f(DataOutputStream output, List<Vector2f> list) throws IOException {
      output.writeInt(list.size());

      for (Vector2f v : list) {
         output.writeFloat(v.x);
         output.writeFloat(v.y);
      }
   }

   private static void write3f(DataOutputStream output, List<Vector3f> list) throws IOException {
      output.writeInt(list.size());

      for (Vector3f v : list) {
         output.writeFloat(v.x);
         output.writeFloat(v.y);
         output.writeFloat(v.z);
      }
   }

   private static void write3b(DataOutputStream output, List<Vector3f> list) throws IOException {
      output.writeInt(list.size());

      for (Vector3f v : list) {
         output.writeByte((byte)((int)((double)(v.x * 255.0F) + 0.5) & 0xFF));
         output.writeByte((byte)((int)((double)(v.y * 255.0F) + 0.5) & 0xFF));
         output.writeByte((byte)((int)((double)(v.z * 255.0F) + 0.5) & 0xFF));
      }
   }

   private static byte[] read1b(DataInputStream input) throws IOException {
      byte[] data = new byte[input.readInt()];

      for (int i = 0; i < data.length; i++) {
         data[i] = input.readByte();
      }

      return data;
   }

   private static List<Integer> read1s(DataInputStream input) throws IOException {
      int size = input.readInt();
      List<Integer> data = new ObjectArrayList(size);

      for (int i = 0; i < size; i++) {
         data.add(Integer.valueOf(input.readShort()));
      }

      return data;
   }

   private static List<Vector4i> read4s(DataInputStream input) throws IOException {
      int size = input.readInt();
      List<Vector4i> data = new ObjectArrayList(size);

      for (int i = 0; i < size; i++) {
         data.add(new Vector4i(input.readShort(), input.readShort(), input.readShort(), input.readShort()));
      }

      return data;
   }

   private static List<Vector2f> read2f(DataInputStream input) throws IOException {
      int size = input.readInt();
      List<Vector2f> data = new ObjectArrayList(size);

      for (int i = 0; i < size; i++) {
         data.add(new Vector2f(input.readFloat(), input.readFloat()));
      }

      return data;
   }

   private static List<Vector3f> read3f(DataInputStream input) throws IOException {
      int size = input.readInt();
      List<Vector3f> data = new ObjectArrayList(size);

      for (int i = 0; i < size; i++) {
         data.add(new Vector3f(input.readFloat(), input.readFloat(), input.readFloat()));
      }

      return data;
   }
}
