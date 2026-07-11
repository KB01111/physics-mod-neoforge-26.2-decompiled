package net.diebuddies.mixins.settings;

import com.mojang.blaze3d.platform.InputConstants.Key;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({KeyMapping.class})
public interface MixinKeyMappingAccessor {
   @Accessor("key")
   Key getKey();
}
