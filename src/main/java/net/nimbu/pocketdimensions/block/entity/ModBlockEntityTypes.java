package net.nimbu.pocketdimensions.block.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nimbu.pocketdimensions.PocketDimensions;
import net.nimbu.pocketdimensions.block.ModBlocks;
import net.nimbu.pocketdimensions.block.entity.custom.GatewayBlockEntity;
import net.nimbu.pocketdimensions.block.entity.custom.PocketDimensionCustomizerBlockEntity;

public final class ModBlockEntityTypes {
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
			DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, PocketDimensions.MOD_ID);

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GatewayBlockEntity>> GATEWAY_BLOCK_ENTITY =
			BLOCK_ENTITY_TYPES.register("gateway_block", () ->
					BlockEntityType.Builder.of(
							GatewayBlockEntity::new,
							ModBlocks.OAK_GATEWAY.get(),
							ModBlocks.SPRUCE_GATEWAY.get(),
							ModBlocks.BIRCH_GATEWAY.get(),
							ModBlocks.JUNGLE_GATEWAY.get(),
							ModBlocks.ACACIA_GATEWAY.get(),
							ModBlocks.DARK_OAK_GATEWAY.get(),
							ModBlocks.MANGROVE_GATEWAY.get(),
							ModBlocks.CHERRY_GATEWAY.get(),
							ModBlocks.CRIMSON_GATEWAY.get(),
							ModBlocks.WARPED_GATEWAY.get(),
							ModBlocks.BAMBOO_GATEWAY.get()
					).build(null));

	public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PocketDimensionCustomizerBlockEntity>> POCKET_DIMENSION_CUSTOMIZER_BLOCK_ENTITY =
			BLOCK_ENTITY_TYPES.register("dimension_customizer_block_entity", () ->
					BlockEntityType.Builder.of(
							PocketDimensionCustomizerBlockEntity::new,
							ModBlocks.DIMENSION_CUSTOMIZER.get()
					).build(null));

	private ModBlockEntityTypes() {}
}
