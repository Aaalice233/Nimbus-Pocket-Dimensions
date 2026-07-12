package net.nimbu.pocketdimensions.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.nimbu.pocketdimensions.PocketDimensions;
import net.nimbu.pocketdimensions.block.custom.GatewayBlock;
import net.nimbu.pocketdimensions.block.entity.ModBlockEntityTypes;
import net.nimbu.pocketdimensions.network.ClientPocketDimensionPersistentState;
import net.nimbu.pocketdimensions.renderer.PocketDimensionBorderRenderer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class GatewayBlockEntity extends BlockEntity {
	private long age;
	@Nullable
	private BlockPos exitPortalPos;
	@Nullable
	private ResourceKey<Level> exitDimension;
	private boolean exactTeleport;
	private int teleportCooldown;

	public GatewayBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntityTypes.GATEWAY_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
		super.saveAdditional(nbt, registryLookup);
		nbt.putLong("Age", this.age);
		if (this.exitPortalPos != null) {
			nbt.put("exit_portal", NbtUtils.writeBlockPos(this.exitPortalPos));
		}
		nbt.putString("exit_dimension", exitDimension != null ? this.exitDimension.location().toString() : "");
		if (this.exactTeleport) {
			nbt.putBoolean("ExactTeleport", true);
		}
	}

	@Override
	protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
		super.loadAdditional(nbt, registryLookup);
		this.age = nbt.getLong("Age");
		NbtUtils.readBlockPos(nbt, "exit_portal").filter(Level::isInSpawnableBounds).ifPresent(p -> this.exitPortalPos = p);
		String dim = nbt.getString("exit_dimension");
		if (!dim.isEmpty()) {
			this.exitDimension = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(dim));
		}
		this.exactTeleport = nbt.getBoolean("ExactTeleport");
	}

	public static void clientTick(Level world, BlockPos pos, BlockState state, GatewayBlockEntity blockEntity) {
		blockEntity.age++;
		if (blockEntity.needsCooldownBeforeTeleporting()) {
			blockEntity.teleportCooldown--;
		}
	}

	public static void serverTick(Level world, BlockPos pos, BlockState state, GatewayBlockEntity blockEntity) {
		boolean bl = blockEntity.isRecentlyGenerated();
		boolean bl2 = blockEntity.needsCooldownBeforeTeleporting();
		blockEntity.age++;
		if (bl2) {
			blockEntity.teleportCooldown--;
		} else if (blockEntity.age % 2400L == 0L) {
			startTeleportCooldown(world, pos, state, blockEntity);
		}
		if (bl != blockEntity.isRecentlyGenerated() || bl2 != blockEntity.needsCooldownBeforeTeleporting()) {
			setChanged(world, pos, state);
		}
	}

	public void TriggerInitialIDUpdate(Level world, BlockPos entryPortalPosition, ResourceKey<Level> exitID, Block doorType) {
		if (world.getBlockEntity(entryPortalPosition) instanceof GatewayBlockEntity gatewayBlockEntity && !world.isClientSide) {
			PocketDimensions.LOGGER.info("Created with ID \n{}", exitID);
			gatewayBlockEntity.setPortalID(exitID);
			BlockPos exitPosition = new BlockPos(6, 148, 1);
			ServerLevel targetWorld = world.getServer().getLevel(exitID);
			if (targetWorld == null) return;

			if (targetWorld.getBlockEntity(exitPosition) instanceof GatewayBlockEntity exitPortal) {
				exitPortal.setExitPosition(entryPortalPosition, world.dimension());
			} else {
				targetWorld.setBlock(exitPosition, doorType.defaultBlockState()
						.setValue(GatewayBlock.FACING, Direction.SOUTH)
						.setValue(GatewayBlock.OPEN, true)
						.setValue(GatewayBlock.EXIT, true), 3);
				targetWorld.setBlock(exitPosition.offset(0, 1, 0), doorType.defaultBlockState()
						.setValue(GatewayBlock.FACING, Direction.SOUTH)
						.setValue(GatewayBlock.HALF, DoubleBlockHalf.UPPER)
						.setValue(GatewayBlock.OPEN, true)
						.setValue(GatewayBlock.EXIT, true), 3);
				targetWorld.setBlock(exitPosition.offset(-1, -1, -1), Blocks.STONE_BRICKS.defaultBlockState(), 3);
				targetWorld.setBlock(exitPosition.offset(-1, -1, 0), Blocks.STONE_BRICKS.defaultBlockState(), 3);
				targetWorld.setBlock(exitPosition.offset(-1, -1, 1), Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3);
				targetWorld.setBlock(exitPosition.offset(0, -1, -1), Blocks.STONE_BRICKS.defaultBlockState(), 3);
				targetWorld.setBlock(exitPosition.offset(0, -1, 0), Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 3);
				targetWorld.setBlock(exitPosition.offset(0, -1, 1), Blocks.STONE_BRICK_STAIRS.defaultBlockState(), 3);
				targetWorld.setBlock(exitPosition.offset(1, -1, -1), Blocks.STONE_BRICKS.defaultBlockState(), 3);
				targetWorld.setBlock(exitPosition.offset(1, -1, 0), Blocks.STONE_BRICKS.defaultBlockState(), 3);
				targetWorld.setBlock(exitPosition.offset(1, -1, 1), Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3);
				if (targetWorld.getBlockEntity(exitPosition) instanceof GatewayBlockEntity newExit) {
					newExit.setExitPosition(entryPortalPosition, world.dimension());
				}
			}

			BlockPos roomPos = new BlockPos(
					Math.floorDiv(exitPosition.getX(), PocketDimensionBorderRenderer.BorderLength),
					Math.floorDiv(exitPosition.getY() - 2, PocketDimensionBorderRenderer.BorderHeight),
					Math.floorDiv(exitPosition.getZ(), PocketDimensionBorderRenderer.BorderLength)
			);
			ClientPocketDimensionPersistentState.addRoom(targetWorld, roomPos);
		}
	}

	public void setPortalID(ResourceKey<Level> exitWorld) {
		exitDimension = exitWorld;
		exitPortalPos = new BlockPos(6, 148, 1);
		setChanged();
	}

	public void setExitPosition(BlockPos exitPosition, ResourceKey<Level> exitWorld) {
		exitDimension = exitWorld;
		exitPortalPos = exitPosition;
		setChanged();
	}

	public boolean isRecentlyGenerated() {
		return this.age < 200L;
	}

	public boolean needsCooldownBeforeTeleporting() {
		return this.teleportCooldown > 0;
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
		return this.saveWithoutMetadata(registryLookup);
	}

	public static void startTeleportCooldown(Level world, BlockPos pos, BlockState state, GatewayBlockEntity blockEntity) {
		if (!world.isClientSide) {
			blockEntity.teleportCooldown = 40;
			world.blockEvent(pos, state.getBlock(), 1, 0);
			setChanged(world, pos, state);
		}
	}

	@Override
	public boolean triggerEvent(int type, int data) {
		if (type == 1) {
			this.teleportCooldown = 40;
			return true;
		}
		return super.triggerEvent(type, data);
	}

	@Nullable
	public ResourceKey<Level> getExitDimension() {
		return exitDimension;
	}

	@Nullable
	public BlockPos getExitBlockPos() {
		return exitPortalPos;
	}

	public Vec3 getExitPosition(Direction facingDir) {
		assert exitPortalPos != null;
		Vector3f vec = facingDir.step().mul(0.5f);
		return exitPortalPos.getBottomCenter().add(vec.x, vec.y, vec.z);
	}
}
