package net.nimbu.pocketdimensions.worldgen.dimension;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.nimbu.pocketdimensions.PocketDimensions;

public final class ModDimensions {
	public static final ResourceKey<LevelStem> POCKET_DIM_KEY = ResourceKey.create(Registries.LEVEL_STEM,
			ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "pocket_dim"));
	public static final ResourceKey<Level> POCKET_DIM_LEVEL_KEY = ResourceKey.create(Registries.DIMENSION,
			ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "pocket_dim"));
	public static final ResourceKey<DimensionType> POCKET_DIM_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE,
			ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "pocket_dim_type"));

	private ModDimensions() {}
}
