package net.diebuddies.render.util;

import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.booleans.BooleanList;

public interface DummyVertexFormat {
   boolean physicsmod$isDummy(int var1);

   void physicsmod$setDummyIndices(BooleanList var1);

   static boolean isDummy(VertexFormat format, int index) {
      return ((DummyVertexFormat)format).physicsmod$isDummy(index);
   }
}
