package net.diebuddies.physics.ocean;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.diebuddies.physics.snow.math.AABB3D;
import net.minecraft.core.BlockPos;
import org.joml.Vector3d;

public class OceanSurface {
   public Long2ObjectMap<OceanMesh> meshes;
   public byte[] textureData;
   public int width;
   public int height;
   public int offsetX;
   public int offsetZ;
   public ProxyOceanLayer oceanLayer;
   public boolean removeAllMeshes;
   private AABB3D aabb = new AABB3D(new Vector3d());
   private boolean rebuildAABB = true;

   public OceanSurface(byte[] textureData, int width, int height, int offsetX, int offsetZ, ProxyOceanLayer proxy) {
      this.meshes = new Long2ObjectOpenHashMap();
      this.oceanLayer = proxy;
      this.textureData = textureData;
      this.width = width;
      this.height = height;
      this.offsetX = offsetX;
      this.offsetZ = offsetZ;
   }

   public OceanSurface(ProxyOceanLayer proxy, boolean removeAllMeshes) {
      this.meshes = new Long2ObjectOpenHashMap();
      this.oceanLayer = proxy;
      this.removeAllMeshes = removeAllMeshes;
   }

   public void set(OceanSurface surface) {
      this.textureData = surface.textureData;
      this.width = surface.width;
      this.height = surface.height;
      this.offsetX = surface.offsetX;
      this.offsetZ = surface.offsetZ;
   }

   public OceanSurface addOceanMesh(int chunkX, int layerY, int chunkZ, OceanMesh mesh) {
      return this.addOceanMesh(BlockPos.asLong(chunkX, layerY, chunkZ), mesh);
   }

   public OceanSurface addOceanMesh(long index, OceanMesh mesh) {
      this.rebuildAABB = true;
      this.meshes.put(index, mesh);
      return this;
   }

   public void removeOceanMesh(long index) {
      this.rebuildAABB = true;
      this.meshes.remove(index);
   }

   public Long2ObjectMap<OceanMesh> getMeshes() {
      return this.meshes;
   }

   public AABB3D getAABB() {
      if (this.rebuildAABB) {
         this.rebuildAABB = false;
         this.aabb.start.set(Double.MAX_VALUE);
         this.aabb.end.set(-Double.MAX_VALUE);
         ObjectIterator var1 = this.meshes.values().iterator();

         while (var1.hasNext()) {
            OceanMesh mesh = (OceanMesh)var1.next();
            this.aabb.include(mesh.aabb);
         }
      }

      return this.aabb;
   }
}
