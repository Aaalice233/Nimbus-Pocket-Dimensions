package net.nimbu.pocketdimensions.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nimbu.pocketdimensions.PocketDimensions;

public final class ModParticleTypes {
	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
			DeferredRegister.create(Registries.PARTICLE_TYPE, PocketDimensions.MOD_ID);

	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GATEWAY_PROJECTILE_PARTICLE =
			PARTICLE_TYPES.register("gateway_projectile_particle", () -> new SimpleParticleType(true));

	private ModParticleTypes() {}
}
