package net.nimbu.pocketdimensions.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.nimbu.pocketdimensions.block.ModBlocks;
import net.nimbu.pocketdimensions.block.entity.custom.GatewayBlockEntity;
import net.nimbu.pocketdimensions.component.ModAttachments;
import net.nimbu.pocketdimensions.component.PlayerGatewayData;
import net.nimbu.pocketdimensions.particle.ModParticleTypes;
import net.nimbu.pocketdimensions.worldgen.dimension.ModDimensions;

import static net.nimbu.pocketdimensions.block.custom.GatewayBlock.HALF;

public class GatewayProjectileEntity extends Projectile {
	private ResourceKey<Level> exitDimensionID;

	public GatewayProjectileEntity(EntityType<? extends Projectile> entityType, Level world) {
		super(entityType, world);
	}

	public void setExitDimension(ResourceKey<Level> exitDimension) {
		exitDimensionID = exitDimension;
	}

	private void createPortal(Level world, BlockPos bottomHalf, PlayerGatewayData data) {
		if (!world.dimensionTypeRegistration().is(ModDimensions.POCKET_DIM_TYPE)) {
			BlockPos topHalf = bottomHalf.above();
			if (!(world.getBlockEntity(bottomHalf.below()) instanceof GatewayBlockEntity)
					&& isReplaceable(world, bottomHalf)
					&& isReplaceable(world, topHalf)) {

				float yaw = this.getYRot();
				Direction direction;
				if (45 < yaw && yaw < 135) {
					direction = Direction.WEST;
				} else if (-45 < yaw && yaw < 45) {
					direction = Direction.NORTH;
				} else if (-135 < yaw && yaw < -45) {
					direction = Direction.EAST;
				} else {
					direction = Direction.SOUTH;
				}

				Block doortype = switch (data.getGatewayMaterial()) {
					case 1 -> ModBlocks.OAK_GATEWAY.get();
					case 2 -> ModBlocks.SPRUCE_GATEWAY.get();
					case 3 -> ModBlocks.BIRCH_GATEWAY.get();
					case 4 -> ModBlocks.JUNGLE_GATEWAY.get();
					case 5 -> ModBlocks.ACACIA_GATEWAY.get();
					case 7 -> ModBlocks.MANGROVE_GATEWAY.get();
					case 8 -> ModBlocks.CHERRY_GATEWAY.get();
					case 9 -> ModBlocks.CRIMSON_GATEWAY.get();
					case 10 -> ModBlocks.WARPED_GATEWAY.get();
					case 11 -> ModBlocks.BAMBOO_GATEWAY.get();
					default -> ModBlocks.DARK_OAK_GATEWAY.get();
				};

				world.setBlock(bottomHalf, doortype.defaultBlockState()
						.setValue(BlockStateProperties.HORIZONTAL_FACING, direction), 3);
				if (world.getBlockEntity(bottomHalf) instanceof GatewayBlockEntity portalData) {
					portalData.TriggerInitialIDUpdate(world, bottomHalf, exitDimensionID, doortype);
				}
				world.setBlock(topHalf, doortype.defaultBlockState()
						.setValue(HALF, DoubleBlockHalf.UPPER)
						.setValue(BlockStateProperties.HORIZONTAL_FACING, direction), 3);

				BlockPos previousPos = data.getGatewayPos();
				ResourceKey<Level> previousDimension = data.getGatewayDim();
				if (previousPos != null && previousDimension != null && world.getServer() != null) {
					ServerLevel targetWorld = world.getServer().getLevel(previousDimension);
					if (targetWorld != null) {
						targetWorld.setBlock(previousPos, Blocks.AIR.defaultBlockState(), 3);
					}
				}

				data.setGatewayPos(bottomHalf);
				data.setGatewayDim(world.dimension());

				world.playSound(null, this.getX(), this.getY(), this.getZ(),
						SoundEvents.BEACON_ACTIVATE,
						SoundSource.NEUTRAL,
						1f, 1.5f);

				Position pos = this.position();
				((ServerLevel) world).sendParticles(ModParticleTypes.GATEWAY_PROJECTILE_PARTICLE.get(),
						pos.x(), pos.y(), pos.z(), 50, 0.5, 1, 0.5, 0.5);
			} else {
				world.playSound(null, this.getX(), this.getY(), this.getZ(),
						SoundEvents.TRIAL_SPAWNER_PLACE,
						SoundSource.NEUTRAL,
						1f, 1.4f);
			}
		} else {
			world.playSound(null, this.getX(), this.getY(), this.getZ(),
					SoundEvents.TRIAL_SPAWNER_PLACE,
					SoundSource.NEUTRAL,
					1f, 1.4f);
		}
	}

	private static boolean isReplaceable(Level world, BlockPos pos) {
		var state = world.getBlockState(pos);
		return state.isAir()
				|| state.is(Blocks.CAVE_AIR)
				|| state.is(Blocks.SNOW)
				|| state.is(Blocks.TALL_GRASS)
				|| state.is(Blocks.SHORT_GRASS);
	}

	@Override
	public void tick() {
		super.tick();
		Vec3 vec3d = this.getDeltaMovement();
		HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
		this.hitTargetOrDeflectSelf(hitResult);
		double d = this.getX() + vec3d.x;
		double e = this.getY() + vec3d.y;
		double f = this.getZ() + vec3d.z;
		this.updateRotation();
		this.setDeltaMovement(vec3d.scale(0.95F));
		this.applyGravity();
		this.setPos(d, e, f);

		Level world = this.level();
		if (!world.isClientSide()) {
			Position pos = this.position();
			((ServerLevel) world).sendParticles(ModParticleTypes.GATEWAY_PROJECTILE_PARTICLE.get(),
					pos.x(), pos.y() + 0.25, pos.z(), 5, 0, 0, 0, 0);
		}
	}

	@Override
	protected double getDefaultGravity() {
		return 0.03;
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
	}

	@Override
	public boolean canUsePortal(boolean allowVehicles) {
		return false;
	}

	@Override
	public boolean shouldRenderAtSqrDistance(double distance) {
		double d = this.getBoundingBox().getSize() * 4.0;
		if (Double.isNaN(d)) {
			d = 4.0;
		}
		d *= 64.0;
		return distance < d * d;
	}

	@Override
	protected void onHitBlock(BlockHitResult blockHitResult) {
		Level world = this.level();
		if (world.isClientSide()) return;

		BlockPos blockPos = blockHitResult.getBlockPos();
		Direction direction = blockHitResult.getDirection();

		Entity owner = this.getOwner();
		if (!(owner instanceof Player player)) return;
		PlayerGatewayData data = PlayerGatewayData.get(player);

		BlockEntity blockEntity = world.getBlockEntity(blockPos);
		if (!(blockEntity instanceof GatewayBlockEntity)) {
			blockEntity = world.getBlockEntity(blockPos.below());
		}
		if ((blockEntity instanceof GatewayBlockEntity gatewayBlockEntity)
				&& (exitDimensionID != null && exitDimensionID.equals(gatewayBlockEntity.getExitDimension()))) {
			world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
			data.setGatewayPos(null);
			world.playSound(null, this.getX(), this.getY(), this.getZ(),
					SoundEvents.TRIAL_SPAWNER_PLACE,
					SoundSource.NEUTRAL,
					1f, 1.4f);
		} else {
			blockPos = switch (direction) {
				case UP -> blockPos.above();
				case DOWN -> blockPos.below();
				case NORTH -> blockPos.north();
				case SOUTH -> blockPos.south();
				case EAST -> blockPos.east();
				case WEST -> blockPos.west();
			};
			createPortal(this.level(), blockPos, data);
			// write back in case attachment copy semantics need explicit set
			player.setData(ModAttachments.PLAYER_GATEWAY.get(), data);
		}

		super.onHitBlock(blockHitResult);
		this.discard();
	}
}
