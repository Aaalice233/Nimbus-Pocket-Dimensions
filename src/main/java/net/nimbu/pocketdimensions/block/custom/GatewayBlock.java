package net.nimbu.pocketdimensions.block.custom;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.nimbu.pocketdimensions.PocketDimensions;
import net.nimbu.pocketdimensions.block.entity.ModBlockEntityTypes;
import net.nimbu.pocketdimensions.block.entity.custom.GatewayBlockEntity;
import org.jetbrains.annotations.Nullable;

public class GatewayBlock extends Block implements EntityBlock, Portal {
	public static final MapCodec<GatewayBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
			instance.group(
					propertiesCodec(),
					BlockSetType.CODEC.fieldOf("block_set_type").forGetter(block -> block.blockSetType)
			).apply(instance, GatewayBlock::new)
	);
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	public static final BooleanProperty EXIT = BooleanProperty.create("exit");
	private static final VoxelShape X_SHAPE = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
	private static final VoxelShape Z_SHAPE = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);
	private final BlockSetType blockSetType;

	public GatewayBlock(BlockBehaviour.Properties settings, BlockSetType type) {
		super(settings.strength(-1.0F, 3600000.0F).noLootTable());
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(OPEN, false)
				.setValue(HALF, DoubleBlockHalf.LOWER)
				.setValue(EXIT, false));
		this.blockSetType = type;
	}

	@Override
	protected MapCodec<? extends GatewayBlock> codec() {
		return CODEC;
	}

	@Override
	protected BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
		super.tick(state, world, pos, random);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
		if (!state.getValue(EXIT)) {
			state = state.cycle(OPEN);
			world.setBlock(pos, state, Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE);
			this.playOpenCloseSound(player, world, pos, state.getValue(OPEN));
			return InteractionResult.sidedSuccess(world.isClientSide);
		}
		return InteractionResult.PASS;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(FACING)) {
			case NORTH, SOUTH -> X_SHAPE;
			default -> Z_SHAPE;
		};
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		BlockPos blockPos = ctx.getClickedPos();
		Level world = ctx.getLevel();
		if (blockPos.getY() < world.getMaxBuildHeight() - 1 && world.getBlockState(blockPos.above()).canBeReplaced(ctx)) {
			return this.defaultBlockState()
					.setValue(FACING, ctx.getHorizontalDirection().getOpposite())
					.setValue(OPEN, false)
					.setValue(HALF, DoubleBlockHalf.LOWER)
					.setValue(EXIT, false);
		}
		return null;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
		world.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
	                                 LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
		DoubleBlockHalf half = state.getValue(HALF);
		if (direction.getAxis() != Direction.Axis.Y || half == DoubleBlockHalf.LOWER != (direction == Direction.UP)) {
			return half == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !state.canSurvive(world, pos)
					? Blocks.AIR.defaultBlockState()
					: super.updateShape(state, direction, neighborState, world, pos, neighborPos);
		}
		return neighborState.getBlock() instanceof GatewayBlock && neighborState.getValue(HALF) != half
				? neighborState.setValue(HALF, half)
				: Blocks.AIR.defaultBlockState();
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, OPEN, HALF, EXIT);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return state.getValue(HALF) == DoubleBlockHalf.LOWER
				? new GatewayBlockEntity(pos, state)
				: null;
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
			return null;
		}
		return createTickerHelper(type, ModBlockEntityTypes.GATEWAY_BLOCK_ENTITY.get(),
				world.isClientSide ? GatewayBlockEntity::clientTick : GatewayBlockEntity::serverTick);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
			BlockEntityType<A> actual, BlockEntityType<E> expected, BlockEntityTicker<? super E> ticker) {
		return expected == actual ? (BlockEntityTicker<A>) ticker : null;
	}

	@Override
	public ItemStack getCloneItemStack(LevelReader world, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	protected boolean canBeReplaced(BlockState state, Fluid fluid) {
		return false;
	}

	@Override
	protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
		if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
			world.getBlockState(pos.below()).entityInside(world, pos.below(), entity);
			return;
		}

		if (getTeleportBox(pos, state.getValue(FACING)).intersects(entity.getBoundingBox())
				&& entity.canUsePortal(false)
				&& !world.isClientSide
				&& world.getBlockEntity(pos) instanceof GatewayBlockEntity doorway
				&& !doorway.needsCooldownBeforeTeleporting()) {
			entity.setAsInsidePortal(this, pos);
			GatewayBlockEntity.startTeleportCooldown(world, pos, state, doorway);
		}
	}

	@Nullable
	@Override
	public DimensionTransition getPortalDestination(ServerLevel world, Entity entity, BlockPos pos) {
		if (!(world.getBlockEntity(pos) instanceof GatewayBlockEntity gatewayBlockEntity)) {
			return null;
		}

		PocketDimensions.LOGGER.info("FoundBlockEntity");
		ResourceKey<Level> registryKey = gatewayBlockEntity.getExitDimension();
		if (registryKey == null) return null;

		ServerLevel serverWorld = world.getServer().getLevel(registryKey);
		if (serverWorld == null) return null;

		BlockPos exitBlock = gatewayBlockEntity.getExitBlockPos();
		if (exitBlock == null) return null;

		BlockState blockState = serverWorld.getBlockState(exitBlock);
		Direction exitDirection = Direction.NORTH;
		if (blockState.getBlock() instanceof GatewayBlock) {
			exitDirection = blockState.getValue(GatewayBlock.FACING);
		}

		PocketDimensions.LOGGER.info("exitDimension of {}", gatewayBlockEntity.getExitDimension());
		Vec3 exitPos = gatewayBlockEntity.getExitPosition(exitDirection);
		PocketDimensions.LOGGER.info("exitPos of {}", exitPos);
		if (exitPos == null) return null;

		float yRot = entity.getYRot() + exitDirection.toYRot()
				- world.getBlockState(pos).getValue(GatewayBlock.FACING).toYRot() + 180;
		return new DimensionTransition(
				serverWorld,
				exitPos,
				getTeleportVelocity(entity),
				yRot,
				entity.getXRot(),
				DimensionTransition.PLAY_PORTAL_SOUND.then(DimensionTransition.PLACE_PORTAL_TICKET)
		);
	}

	private static Vec3 getTeleportVelocity(Entity entity) {
		return entity instanceof ThrownEnderpearl ? new Vec3(0.0, -1.0, 0.0) : entity.getDeltaMovement();
	}

	private static AABB getTeleportBox(BlockPos pos, Direction facing) {
		double depth = 0.05;
		if (facing == Direction.NORTH || facing == Direction.SOUTH) {
			return new AABB(pos.getX(), pos.getY(), pos.getZ() + 0.5 - depth,
					pos.getX() + 1, pos.getY() + 2, pos.getZ() + 0.5 + depth);
		}
		return new AABB(pos.getX() + 0.5 - depth, pos.getY(), pos.getZ(),
				pos.getX() + 0.5 + depth, pos.getY() + 2, pos.getZ() + 1);
	}

	@Override
	protected RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
			if (state.getValue(EXIT)) {
				VoxelShape shape1 = rotateShape(Direction.NORTH, state.getValue(FACING), 15.99, 0, 3, 19.99, 16, 13);
				VoxelShape shape2 = Shapes.join(
						rotateShape(Direction.NORTH, state.getValue(FACING), -3.99, 0, 3, 0.01, 16, 13),
						rotateShape(Direction.NORTH, state.getValue(FACING), 0, 0, 9, 16, 16, 11),
						BooleanOp.OR);
				VoxelShape shape3 = Shapes.join(
						rotateShape(Direction.NORTH, state.getValue(FACING), -2, 20, 2, 18, 24, 14),
						rotateShape(Direction.NORTH, state.getValue(FACING), 0, 0, 9, 16, 16, 11),
						BooleanOp.OR);
				return Shapes.join(shape1, Shapes.join(shape2, shape3, BooleanOp.OR), BooleanOp.OR);
			} else if (state.getValue(OPEN)) {
				VoxelShape shape1 = Shapes.join(
						rotateShape(Direction.NORTH, state.getValue(FACING), 13, 0, -7, 16, 16, 9),
						rotateShape(Direction.NORTH, state.getValue(FACING), 15.99, 0, 3, 19.99, 16, 13),
						BooleanOp.OR);
				VoxelShape shape2 = Shapes.join(
						rotateShape(Direction.NORTH, state.getValue(FACING), -3.99, 0, 3, 0.01, 16, 13),
						rotateShape(Direction.NORTH, state.getValue(FACING), -4.8, 16, 2, 20.8, 20, 14),
						BooleanOp.OR);
				VoxelShape shape3 = Shapes.join(
						rotateShape(Direction.NORTH, state.getValue(FACING), -2, 20, 2, 18, 24, 14),
						rotateShape(Direction.NORTH, state.getValue(FACING), 0, 0, 9, 16, 16, 11),
						BooleanOp.OR);
				return Shapes.join(shape1, Shapes.join(shape2, shape3, BooleanOp.OR), BooleanOp.OR);
			} else {
				VoxelShape shape1 = Shapes.join(
						rotateShape(Direction.NORTH, state.getValue(FACING), 0, 0, 6, 16, 16, 9),
						rotateShape(Direction.NORTH, state.getValue(FACING), 15.99, 0, 3, 19.99, 16, 13),
						BooleanOp.OR);
				VoxelShape shape2 = Shapes.join(
						rotateShape(Direction.NORTH, state.getValue(FACING), -3.99, 0, 3, 0.01, 16, 13),
						rotateShape(Direction.NORTH, state.getValue(FACING), -4.8, 16, 2, 20.8, 20, 14),
						BooleanOp.OR);
				VoxelShape shape3 = Shapes.join(
						rotateShape(Direction.NORTH, state.getValue(FACING), -2, 20, 2, 18, 24, 14),
						rotateShape(Direction.NORTH, state.getValue(FACING), 0, 0, 9, 16, 16, 11),
						BooleanOp.OR);
				return Shapes.join(shape1, Shapes.join(shape2, shape3, BooleanOp.OR), BooleanOp.OR);
			}
		} else {
			if (state.getValue(EXIT)) {
				VoxelShape shape1 = rotateShape(Direction.NORTH, state.getValue(FACING), 15.99, 0, 3, 19.99, 16, 13);
				VoxelShape shape2 = Shapes.join(
						rotateShape(Direction.NORTH, state.getValue(FACING), -3.99, 0, 3, 0.01, 16, 13),
						rotateShape(Direction.NORTH, state.getValue(FACING), 0, 0, 9, 16, 16, 11),
						BooleanOp.OR);
				return Shapes.join(shape1, shape2, BooleanOp.OR);
			} else if (state.getValue(OPEN)) {
				VoxelShape shape1 = Shapes.join(
						rotateShape(Direction.NORTH, state.getValue(FACING), 13, 0, -7, 16, 16, 9),
						rotateShape(Direction.NORTH, state.getValue(FACING), 15.99, 0, 3, 19.99, 16, 13),
						BooleanOp.OR);
				VoxelShape shape2 = Shapes.join(
						rotateShape(Direction.NORTH, state.getValue(FACING), -3.99, 0, 3, 0.01, 16, 13),
						rotateShape(Direction.NORTH, state.getValue(FACING), 0, 0, 9, 16, 16, 11),
						BooleanOp.OR);
				return Shapes.join(shape1, shape2, BooleanOp.OR);
			} else {
				VoxelShape shape1 = Shapes.join(
						rotateShape(Direction.NORTH, state.getValue(FACING), 0, 0, 6, 16, 16, 9),
						rotateShape(Direction.NORTH, state.getValue(FACING), 15.99, 0, 3, 19.99, 16, 13),
						BooleanOp.OR);
				VoxelShape shape2 = Shapes.join(
						rotateShape(Direction.NORTH, state.getValue(FACING), -3.99, 0, 3, 0.01, 16, 13),
						rotateShape(Direction.NORTH, state.getValue(FACING), 0, 0, 9, 16, 16, 11),
						BooleanOp.OR);
				return Shapes.join(shape1, shape2, BooleanOp.OR);
			}
		}
	}

	public static VoxelShape rotateShape(Direction from, Direction to,
	                                     double minX, double minY, double minZ,
	                                     double maxX, double maxY, double maxZ) {
		int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
		double sin = Math.sin((times / 4.0) * Math.TAU);
		double cos = Math.cos((times / 4.0) * Math.TAU);

		double x1 = (minX - 8) * cos - (minZ - 8) * sin + 8;
		double x2 = (maxX - 8) * cos - (maxZ - 8) * sin + 8;
		double z1 = (minX - 8) * sin + (minZ - 8) * cos + 8;
		double z2 = (maxX - 8) * sin + (maxZ - 8) * cos + 8;
		return Block.box(Math.min(x1, x2), minY, Math.min(z1, z2), Math.max(x1, x2), maxY, Math.max(z1, z2));
	}

	private void playOpenCloseSound(@Nullable Entity entity, Level world, BlockPos pos, boolean open) {
		world.playSound(entity, pos,
				open ? this.blockSetType.doorOpen() : this.blockSetType.doorClose(),
				SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
	}
}
