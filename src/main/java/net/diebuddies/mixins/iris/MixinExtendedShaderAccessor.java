package net.diebuddies.mixins.iris;

import net.irisshaders.iris.pipeline.programs.ExtendedShader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin({ExtendedShader.class})
public interface MixinExtendedShaderAccessor {
   @Accessor("modelViewInverse")
   int getModelViewInverse();

   @Accessor("normalMat")
   int getNormalMat();
}
