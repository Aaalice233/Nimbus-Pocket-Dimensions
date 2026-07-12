package net.nimbu.pocketdimensions.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.Nullable;

public class GatewayProjectileParticle extends TextureSheetParticle {
	public GatewayProjectileParticle(ClientLevel clientWorld, double x, double y, double z,
	                                 SpriteSet spriteProvider, double xSpeed, double ySpeed, double zSpeed) {
		super(clientWorld, x, y, z, xSpeed, ySpeed, zSpeed);
		this.friction = 0.8f;
		this.lifetime = 8;
		this.setSpriteFromAge(spriteProvider);
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@Override
	protected int getLightColor(float tint) {
		return 255;
	}

	public static class Factory implements ParticleProvider<SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public Factory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Nullable
		@Override
		public Particle createParticle(SimpleParticleType parameters, ClientLevel world,
		                               double x, double y, double z,
		                               double velocityX, double velocityY, double velocityZ) {
			return new GatewayProjectileParticle(world, x, y, z, this.spriteProvider, velocityX, velocityY, velocityZ);
		}
	}
}
