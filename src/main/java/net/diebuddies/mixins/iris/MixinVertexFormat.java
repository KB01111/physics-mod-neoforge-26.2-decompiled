package net.diebuddies.mixins.iris;

import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import net.diebuddies.render.util.DummyVertexFormat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({VertexFormat.class})
public abstract class MixinVertexFormat implements DummyVertexFormat {
   @Unique
   private BooleanList physicsmod$dummyIndices;

   @Override
   public boolean physicsmod$isDummy(int index) {
      return this.physicsmod$dummyIndices == null ? false : this.physicsmod$dummyIndices.getBoolean(index);
   }

   @Override
   public void physicsmod$setDummyIndices(BooleanList dummyIndices) {
      this.physicsmod$dummyIndices = dummyIndices;
   }
}
