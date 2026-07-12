package net.nimbu.pocketdimensions.dimensions;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;

import java.util.List;
import java.util.Optional;

public final class SolidGenerator {
	private SolidGenerator() {}

	public static FlatLevelSource create(Holder<Biome> biome) {
		FlatLevelGeneratorSettings config = new FlatLevelGeneratorSettings(
				Optional.empty(),
				biome,
				List.of()
		);

		// ENTIRE WORLD = BARRIER
		config.getLayersInfo().add(
				new FlatLayerInfo(
						DimensionType.Y_SIZE,
						Blocks.BARRIER
				)
		);

		config.updateLayers();
		return new FlatLevelSource(config);
	}
}
