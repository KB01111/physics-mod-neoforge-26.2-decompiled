package net.diebuddies.render.util;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import java.util.ArrayList;
import java.util.List;
import net.diebuddies.mixins.iris.VertexFormatInvoker;
import net.minecraft.util.Mth;

public class PhysicsVertexFormatBuilder {
   private final List<VertexFormatElement> elements = new ArrayList<>(16);
   private final BooleanList dummies = new BooleanArrayList();
   private int offset = 0;
   private final int stepRate;

   private PhysicsVertexFormatBuilder(int stepRate) {
      this.stepRate = stepRate;
   }

   public static PhysicsVertexFormatBuilder builder(int stepRate) {
      return new PhysicsVertexFormatBuilder(stepRate);
   }

   private void createAttribute(String name, int offset, GpuFormat elementFormat, boolean dummy) {
      if (this.elements.size() >= 16) {
         throw new IllegalArgumentException("Having more than 16 attributes are not supported");
      } else if (!Mth.isMultipleOf(offset, elementFormat.byteAlignment())) {
         throw new IllegalArgumentException(name + " is not aligned to " + elementFormat.byteAlignment() + " as required by " + elementFormat);
      } else {
         VertexFormatElement element = new VertexFormatElement(name, offset, elementFormat);
         this.elements.add(element);
         this.dummies.add(dummy);
      }
   }

   private void validateUniqueName(String name) {
      for (VertexFormatElement element : this.elements) {
         if (element.name().equals(name)) {
            throw new IllegalArgumentException("Another vertex attribute exists with the name " + name);
         }
      }
   }

   public PhysicsVertexFormatBuilder addAttribute(String name, GpuFormat elementFormat) {
      this.validateUniqueName(name);
      this.createAttribute(name, this.offset, elementFormat, false);
      this.offset = this.offset + elementFormat.blockSize();
      return this;
   }

   public PhysicsVertexFormatBuilder addDummy(String name, GpuFormat elementFormat) {
      this.validateUniqueName(name);
      this.createAttribute(name, this.offset, elementFormat, true);
      return this;
   }

   public VertexFormat build() {
      int vertexSize = this.offset;
      if (!Mth.isMultipleOf(vertexSize, 4)) {
         throw new IllegalStateException("Vertex size must be a multiple of 4, was " + vertexSize);
      } else {
         VertexFormat format = VertexFormatInvoker.physicsmod$invokeInit(this.elements, vertexSize, this.stepRate);
         ((DummyVertexFormat)format).physicsmod$setDummyIndices(this.dummies);
         return format;
      }
   }
}
