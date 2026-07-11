package net.diebuddies.mixins.vines;

import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin({RenderSectionManager.class})
public interface RenderSectionManagerAccessor {
   @Accessor("sectionByPosition")
   Long2ReferenceMap<RenderSection> getSectionByPosition();
}
