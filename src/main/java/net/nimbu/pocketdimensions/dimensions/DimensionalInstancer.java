package net.nimbu.pocketdimensions.dimensions;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.nimbu.pocketdimensions.PocketDimensions;
import net.nimbu.pocketdimensions.renderer.PocketDimensionBorderRenderer;

import java.util.UUID;

public final class DimensionalInstancer {
	private DimensionalInstancer() {}

	/**
	 * Stable per-owner pocket dimension id (namespace + path). Used by createInstance and unit-tested.
	 */
	public static ResourceLocation dimensionIdFor(UUID ownerID) {
		return ResourceLocation.fromNamespaceAndPath(
				PocketDimensions.MOD_ID,
				pocketDimensionPath(ownerID)
		);
	}

	/** Path segment only: {@code pocket_dimension_<uuid>}. */
	public static String pocketDimensionPath(UUID ownerID) {
		return "pocket_dimension_" + ownerID;
	}

	public static ServerLevel createInstance(MinecraftServer server, UUID ownerID) {
		ResourceLocation instanceID = dimensionIdFor(ownerID);

		ServerLevel existing = server.getLevel(DynamicDimensions.levelKey(instanceID));
		if (existing != null) {
			return existing;
		}

		ServerLevel world = DynamicDimensions.getOrCreate(server, instanceID);
		if (world != null) {
			// Fresh ServerLevel registration: clear the initial room volume out of the barrier fill
			doWorldPreset(world);
		}
		return world;
	}

	private static void doWorldPreset(ServerLevel world) {
		BlockState state = Blocks.AIR.defaultBlockState();
		for (int x = 0; x < PocketDimensionBorderRenderer.BorderLength; x++) {
			for (int y = 0; y < PocketDimensionBorderRenderer.BorderHeight; y++) {
				for (int z = 0; z < PocketDimensionBorderRenderer.BorderLength; z++) {
					world.setBlock(BlockPos.ZERO.offset(x, 147 + y, z), state, 2);
				}
			}
		}
	}
}
