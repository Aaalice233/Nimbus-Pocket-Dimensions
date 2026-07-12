package net.nimbu.pocketdimensions.worldgen.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Carbon copy of BiomeSpecialEffects fields for dynamic overrides without recursive mixin calls.
 */
public class DynamicBiomeEffects {
	public static final Codec<DynamicBiomeEffects> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Codec.INT.fieldOf("fog_color").forGetter(effects -> effects.fogColor),
					Codec.INT.fieldOf("water_color").forGetter(effects -> effects.waterColor),
					Codec.INT.fieldOf("water_fog_color").forGetter(effects -> effects.waterFogColor),
					Codec.INT.fieldOf("sky_color").forGetter(effects -> effects.skyColor),
					Codec.INT.optionalFieldOf("foliage_color").forGetter(effects -> effects.foliageColor),
					Codec.INT.optionalFieldOf("grass_color").forGetter(effects -> effects.grassColor),
					AmbientParticleSettings.CODEC.optionalFieldOf("particle").forGetter(effects -> effects.particleConfig),
					SoundEvent.CODEC.optionalFieldOf("ambient_sound").forGetter(effects -> effects.loopSound),
					AmbientMoodSettings.CODEC.optionalFieldOf("mood_sound").forGetter(effects -> effects.moodSound),
					AmbientAdditionsSettings.CODEC.optionalFieldOf("additions_sound").forGetter(effects -> effects.additionsSound),
					Music.CODEC.optionalFieldOf("music").forGetter(effects -> effects.music)
			).apply(instance, DynamicBiomeEffects::new)
	);

	private int fogColor;
	private int waterColor;
	private int waterFogColor;
	private int skyColor;
	private Optional<Integer> foliageColor;
	private Optional<Integer> grassColor;
	private Optional<AmbientParticleSettings> particleConfig;
	private Optional<Holder<SoundEvent>> loopSound;
	private Optional<AmbientMoodSettings> moodSound;
	private Optional<AmbientAdditionsSettings> additionsSound;
	private Optional<Music> music;

	DynamicBiomeEffects(
			int fogColor,
			int waterColor,
			int waterFogColor,
			int skyColor,
			Optional<Integer> foliageColor,
			Optional<Integer> grassColor,
			Optional<AmbientParticleSettings> particleConfig,
			Optional<Holder<SoundEvent>> loopSound,
			Optional<AmbientMoodSettings> moodSound,
			Optional<AmbientAdditionsSettings> additionsSound,
			Optional<Music> music
	) {
		this.fogColor = fogColor;
		this.waterColor = waterColor;
		this.waterFogColor = waterFogColor;
		this.skyColor = skyColor;
		this.foliageColor = foliageColor;
		this.grassColor = grassColor;
		this.particleConfig = particleConfig;
		this.loopSound = loopSound;
		this.moodSound = moodSound;
		this.additionsSound = additionsSound;
		this.music = music;
	}

	public int getFogColor() { return fogColor; }
	public void setFogColor(int fogColor) { this.fogColor = fogColor; }
	public int getWaterColor() { return waterColor; }
	public void setWaterColor(int waterColor) { this.waterColor = waterColor; }
	public int getWaterFogColor() { return waterFogColor; }
	public void setWaterFogColor(int waterFogColor) { this.waterFogColor = waterFogColor; }
	public int getSkyColor() { return skyColor; }
	public void setSkyColor(int skyColor) { this.skyColor = skyColor; }
	public Optional<Integer> getFoliageColor() { return foliageColor; }
	public void setFoliageColor(Optional<Integer> foliageColor) { this.foliageColor = foliageColor; }
	public Optional<Integer> getGrassColor() { return grassColor; }
	public void setGrassColor(Optional<Integer> grassColor) { this.grassColor = grassColor; }
	public Optional<AmbientParticleSettings> getParticleConfig() { return particleConfig; }
	public void setParticleConfig(Optional<AmbientParticleSettings> particleConfig) { this.particleConfig = particleConfig; }
	public Optional<Holder<SoundEvent>> getLoopSound() { return loopSound; }
	public void setLoopSound(Optional<Holder<SoundEvent>> loopSound) { this.loopSound = loopSound; }
	public Optional<AmbientMoodSettings> getMoodSound() { return moodSound; }
	public void setMoodSound(Optional<AmbientMoodSettings> moodSound) { this.moodSound = moodSound; }
	public Optional<AmbientAdditionsSettings> getAdditionsSound() { return additionsSound; }
	public void setAdditionsSound(Optional<AmbientAdditionsSettings> additionsSound) { this.additionsSound = additionsSound; }
	public Optional<Music> getMusic() { return music; }
	public void setMusic(Optional<Music> music) { this.music = music; }

	public static class Builder {
		private OptionalInt fogColor = OptionalInt.empty();
		private OptionalInt waterColor = OptionalInt.empty();
		private OptionalInt waterFogColor = OptionalInt.empty();
		private OptionalInt skyColor = OptionalInt.empty();
		private Optional<Integer> foliageColor = Optional.empty();
		private Optional<Integer> grassColor = Optional.empty();
		private Optional<AmbientParticleSettings> particleConfig = Optional.empty();
		private Optional<Holder<SoundEvent>> loopSound = Optional.empty();
		private Optional<AmbientMoodSettings> moodSound = Optional.empty();
		private Optional<AmbientAdditionsSettings> additionsSound = Optional.empty();
		private Optional<Music> musicSound = Optional.empty();

		public Builder fogColor(int fogColor) { this.fogColor = OptionalInt.of(fogColor); return this; }
		public Builder waterColor(int waterColor) { this.waterColor = OptionalInt.of(waterColor); return this; }
		public Builder waterFogColor(int waterFogColor) { this.waterFogColor = OptionalInt.of(waterFogColor); return this; }
		public Builder skyColor(int skyColor) { this.skyColor = OptionalInt.of(skyColor); return this; }
		public Builder foliageColor(int foliageColor) { this.foliageColor = Optional.of(foliageColor); return this; }
		public Builder grassColor(int grassColor) { this.grassColor = Optional.of(grassColor); return this; }
		public Builder particleConfig(AmbientParticleSettings particleConfig) { this.particleConfig = Optional.of(particleConfig); return this; }
		public Builder loopSound(Holder<SoundEvent> loopSound) { this.loopSound = Optional.of(loopSound); return this; }
		public Builder moodSound(AmbientMoodSettings moodSound) { this.moodSound = Optional.of(moodSound); return this; }
		public Builder additionsSound(AmbientAdditionsSettings additionsSound) { this.additionsSound = Optional.of(additionsSound); return this; }
		public Builder music(@Nullable Music music) { this.musicSound = Optional.ofNullable(music); return this; }

		public DynamicBiomeEffects build() {
			return new DynamicBiomeEffects(
					this.fogColor.orElseThrow(() -> new IllegalStateException("Missing 'fog' color.")),
					this.waterColor.orElseThrow(() -> new IllegalStateException("Missing 'water' color.")),
					this.waterFogColor.orElseThrow(() -> new IllegalStateException("Missing 'water fog' color.")),
					this.skyColor.orElseThrow(() -> new IllegalStateException("Missing 'sky' color.")),
					this.foliageColor,
					this.grassColor,
					this.particleConfig,
					this.loopSound,
					this.moodSound,
					this.additionsSound,
					this.musicSound
			);
		}
	}
}
