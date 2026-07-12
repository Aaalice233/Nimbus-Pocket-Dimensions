package net.nimbu.pocketdimensions.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nimbu.pocketdimensions.PocketDimensions;
import net.nimbu.pocketdimensions.block.custom.GatewayBlock;
import net.nimbu.pocketdimensions.block.custom.PocketDimensionCustomizerBlock;
import net.nimbu.pocketdimensions.item.ModItems;

import java.util.function.Supplier;

public final class ModBlocks {
	public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(PocketDimensions.MOD_ID);

	public static final DeferredBlock<Block> OAK_GATEWAY = registerGateway("oak_gateway", BlockSetType.OAK);
	public static final DeferredBlock<Block> SPRUCE_GATEWAY = registerGateway("spruce_gateway", BlockSetType.SPRUCE);
	public static final DeferredBlock<Block> BIRCH_GATEWAY = registerGateway("birch_gateway", BlockSetType.BIRCH);
	public static final DeferredBlock<Block> JUNGLE_GATEWAY = registerGateway("jungle_gateway", BlockSetType.JUNGLE);
	public static final DeferredBlock<Block> ACACIA_GATEWAY = registerGateway("acacia_gateway", BlockSetType.ACACIA);
	public static final DeferredBlock<Block> DARK_OAK_GATEWAY = registerGateway("dark_oak_gateway", BlockSetType.DARK_OAK);
	public static final DeferredBlock<Block> MANGROVE_GATEWAY = registerGateway("mangrove_gateway", BlockSetType.MANGROVE);
	public static final DeferredBlock<Block> CHERRY_GATEWAY = registerGateway("cherry_gateway", BlockSetType.CHERRY);
	public static final DeferredBlock<Block> CRIMSON_GATEWAY = registerGateway("crimson_gateway", BlockSetType.CRIMSON);
	public static final DeferredBlock<Block> WARPED_GATEWAY = registerGateway("warped_gateway", BlockSetType.WARPED);
	public static final DeferredBlock<Block> BAMBOO_GATEWAY = registerGateway("bamboo_gateway", BlockSetType.BAMBOO);

	public static final DeferredBlock<Block> DIMENSION_CUSTOMIZER = registerBlock("dimension_customizer",
			() -> new PocketDimensionCustomizerBlock(BlockBehaviour.Properties.of()
					.noOcclusion()
					.lightLevel(state -> 10)
					.mapColor(MapColor.STONE)
					.instrument(NoteBlockInstrument.BASEDRUM)
					.requiresCorrectToolForDrops()
					.strength(1.5F, 6.0F)),
			new Item.Properties().rarity(Rarity.RARE));

	public static final DeferredBlock<Block> GUI_OAK_LEAVES = BLOCKS.register("gui_oak_leaves",
			() -> new Block(BlockBehaviour.Properties.of().noOcclusion()));
	public static final DeferredBlock<Block> GUI_WATER = BLOCKS.register("gui_water",
			() -> new Block(BlockBehaviour.Properties.of().noOcclusion()));
	public static final DeferredBlock<Block> GUI_GRASS = BLOCKS.register("gui_grass",
			() -> new Block(BlockBehaviour.Properties.of().noOcclusion()));

	private static DeferredBlock<Block> registerGateway(String name, BlockSetType type) {
		return registerBlock(name,
				() -> new GatewayBlock(BlockBehaviour.Properties.of().noOcclusion(), type),
				new Item.Properties());
	}

	private static DeferredBlock<Block> registerBlock(String name, Supplier<Block> block, Item.Properties itemProps) {
		DeferredBlock<Block> deferred = BLOCKS.register(name, block);
		ModItems.ITEMS.register(name, () -> new BlockItem(deferred.get(), itemProps));
		return deferred;
	}

	private ModBlocks() {}
}
