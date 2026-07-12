package net.nimbu.pocketdimensions.worldgen.biome;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.nimbu.pocketdimensions.PocketDimensions;

public final class ModBiomes {
	public static final ResourceKey<Biome> POCKET_DIM_BIOME = ResourceKey.create(Registries.BIOME,
			ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "pocket_dim_biome"));

	private ModBiomes() {}
}
