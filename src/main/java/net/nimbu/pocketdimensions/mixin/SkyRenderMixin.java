package net.nimbu.pocketdimensions.mixin;

import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Optional skybox control hook (disabled). Method names kept as empty mixin target holder.
 */
@Mixin(LevelRenderer.class)
public abstract class SkyRenderMixin {
}
