package net.nimbu.pocketdimensions.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nimbu.pocketdimensions.block.entity.ModBlockEntityTypes;
import net.nimbu.pocketdimensions.block.entity.custom.PocketDimensionCustomizerBlockEntity;
import net.nimbu.pocketdimensions.screen.custom.DimensionCustomizerScreenHandler;
import net.nimbu.pocketdimensions.worldgen.dimension.ModDimensions;
import org.jetbrains.annotations.Nullable;

public class PocketDimensionCustomizerBlock extends BaseEntityBlock {
	public static final VoxelShape BASE = box(0, 0, 0, 16, 9, 16);
	public static final VoxelShape PILLAR_NW = box(0, 9, 0, 4, 16, 4);
	public static final VoxelShape PILLAR_NE = box(12, 9, 0, 16, 16, 4);
	public static final VoxelShape PILLAR_SW = box(0, 9, 12, 4, 16, 16);
	public static final VoxelShape PILLAR_SE = box(12, 9, 12, 16, 16, 16);
	public static final VoxelShape SHAPE = Shapes.or(BASE, PILLAR_NW, PILLAR_NE, PILLAR_SW, PILLAR_SE);

	public static final MapCodec<PocketDimensionCustomizerBlock> CODEC = simpleCodec(PocketDimensionCustomizerBlock::new);

	public PocketDimensionCustomizerBlock(BlockBehaviour.Properties settings) {
		super(settings);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new PocketDimensionCustomizerBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return world.isClientSide
				? createTickerHelper(type, ModBlockEntityTypes.POCKET_DIMENSION_CUSTOMIZER_BLOCK_ENTITY.get(), PocketDimensionCustomizerBlockEntity::tick)
				: null;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
		if (!world.isClientSide && world.dimensionTypeRegistration().is(ModDimensions.POCKET_DIM_TYPE)) {
			player.openMenu(new SimpleMenuProvider(
					(syncId, inv, p) -> new DimensionCustomizerScreenHandler(syncId, inv),
					Component.literal("Pocket dimension customiser")
			));
			return InteractionResult.CONSUME;
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}
}
