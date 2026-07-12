package net.nimbu.pocketdimensions.mixin;

import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.nimbu.pocketdimensions.network.ClientPocketDimensionPersistentState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(BiomeSpecialEffects.class)
public class BiomeEffectsMixin {

	@Inject(method = "getFogColor", at = @At("HEAD"), cancellable = true)
	public void getDynamicBiomeFogColour(CallbackInfoReturnable<Integer> cir) {
		if (ClientPocketDimensionPersistentState.isClientInPocketDimension()) {
			cir.setReturnValue(ClientPocketDimensionPersistentState.getDynamicBiomeEffects().getFogColor());
		}
	}

	@Inject(method = "getWaterColor", at = @At("HEAD"), cancellable = true)
	public void getDynamicBiomeWaterColor(CallbackInfoReturnable<Integer> cir) {
		if (ClientPocketDimensionPersistentState.isClientInPocketDimension()) {
			cir.setReturnValue(ClientPocketDimensionPersistentState.getDynamicBiomeEffects().getWaterColor());
		}
	}

	@Inject(method = "getWaterFogColor", at = @At("HEAD"), cancellable = true)
	public void getDynamicBiomeWaterFogColor(CallbackInfoReturnable<Integer> cir) {
		if (ClientPocketDimensionPersistentState.isClientInPocketDimension()) {
			cir.setReturnValue(ClientPocketDimensionPersistentState.getDynamicBiomeEffects().getWaterFogColor());
		}
	}

	@Inject(method = "getSkyColor", at = @At("HEAD"), cancellable = true)
	public void getDynamicBiomeSkyColor(CallbackInfoReturnable<Integer> cir) {
		if (ClientPocketDimensionPersistentState.isClientInPocketDimension()) {
			cir.setReturnValue(ClientPocketDimensionPersistentState.getDynamicBiomeEffects().getSkyColor());
		}
	}

	@Inject(method = "getAmbientParticleSettings", at = @At("HEAD"), cancellable = true)
	public void getDynamicBiomeParticleConfig(CallbackInfoReturnable<Optional<AmbientParticleSettings>> cir) {
		if (ClientPocketDimensionPersistentState.isClientInPocketDimension()) {
			cir.setReturnValue(ClientPocketDimensionPersistentState.getDynamicBiomeEffects().getParticleConfig());
		}
	}

	@Inject(method = "getAmbientLoopSoundEvent", at = @At("HEAD"), cancellable = true)
	public void getDynamicBiomeLoopSound(CallbackInfoReturnable<Optional<Holder<SoundEvent>>> cir) {
		if (ClientPocketDimensionPersistentState.isClientInPocketDimension()) {
			cir.setReturnValue(ClientPocketDimensionPersistentState.getDynamicBiomeEffects().getLoopSound());
		}
	}

	@Inject(method = "getAmbientMoodSettings", at = @At("HEAD"), cancellable = true)
	public void getDynamicBiomeMoodSound(CallbackInfoReturnable<Optional<AmbientMoodSettings>> cir) {
		if (ClientPocketDimensionPersistentState.isClientInPocketDimension()) {
			cir.setReturnValue(ClientPocketDimensionPersistentState.getDynamicBiomeEffects().getMoodSound());
		}
	}

	@Inject(method = "getAmbientAdditionsSettings", at = @At("HEAD"), cancellable = true)
	public void getDynamicBiomeAdditionsSound(CallbackInfoReturnable<Optional<AmbientAdditionsSettings>> cir) {
		if (ClientPocketDimensionPersistentState.isClientInPocketDimension()) {
			cir.setReturnValue(ClientPocketDimensionPersistentState.getDynamicBiomeEffects().getAdditionsSound());
		}
	}

	@Inject(method = "getBackgroundMusic", at = @At("HEAD"), cancellable = true)
	public void getDynamicBiomeMusic(CallbackInfoReturnable<Optional<Music>> cir) {
		if (ClientPocketDimensionPersistentState.isClientInPocketDimension()) {
			cir.setReturnValue(ClientPocketDimensionPersistentState.getDynamicBiomeEffects().getMusic());
		}
	}
}
