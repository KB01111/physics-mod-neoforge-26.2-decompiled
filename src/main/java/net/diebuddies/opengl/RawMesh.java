package net.diebuddies.opengl;

import java.nio.ByteBuffer;
import net.diebuddies.physics.snow.math.AABB3D;
import org.lwjgl.system.MemoryUtil;

public class RawMesh {
   public ByteBuffer data;
   public ByteBuffer indexData;
   public AABB3D boundingBox;

   public void destroy() {
      if (this.data != null) {
         MemoryUtil.memFree(this.data);
      }

      this.data = null;
      if (this.indexData != null) {
         MemoryUtil.memFree(this.indexData);
      }

      this.indexData = null;
   }
}
