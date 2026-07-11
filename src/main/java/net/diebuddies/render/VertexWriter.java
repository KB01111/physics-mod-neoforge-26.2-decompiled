package net.diebuddies.render;

import java.nio.ByteBuffer;

public interface VertexWriter {
   void write(ByteBuffer var1);

   int count();
}
