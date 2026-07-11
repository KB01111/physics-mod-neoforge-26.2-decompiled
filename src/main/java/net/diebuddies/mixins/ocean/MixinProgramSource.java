package net.diebuddies.mixins.ocean;

import net.irisshaders.iris.shaderpack.programs.ProgramSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ProgramSource.class})
public interface MixinProgramSource {
   @Accessor
   @Mutable
   void setVertexSource(String var1);

   @Accessor
   @Mutable
   void setFragmentSource(String var1);
}
