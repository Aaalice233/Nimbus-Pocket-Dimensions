package net.nimbu.pocketdimensions.mixin;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.nimbu.pocketdimensions.network.ClientPocketDimensionPersistentState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeColors.class)
public class BiomeColorsMixin {

	@Inject(method = "getAverageGrassColor", at = @At("HEAD"), cancellable = true)
	private static void getDynamicGrassColor(
			BlockAndTintGetter world,
			BlockPos pos,
			CallbackInfoReturnable<Integer> cir
	) {
		if (ClientPocketDimensionPersistentState.isClientInPocketDimension()) {
			cir.setReturnValue(ClientPocketDimensionPersistentState.getDynamicBiomeEffects().getGrassColor().orElse(0x00FF00));
		}
	}

	@Inject(method = "getAverageFoliageColor", at = @At("HEAD"), cancellable = true)
	private static void getDynamicFoliageColor(
			BlockAndTintGetter world,
			BlockPos pos,
			CallbackInfoReturnable<Integer> cir
	) {
		if (ClientPocketDimensionPersistentState.isClientInPocketDimension()) {
			cir.setReturnValue(ClientPocketDimensionPersistentState.getDynamicBiomeEffects().getFoliageColor().orElse(0x00FF00));
		}
	}
}
