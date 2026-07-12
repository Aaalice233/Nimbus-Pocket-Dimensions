package net.nimbu.pocketdimensions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.nimbu.pocketdimensions.block.ModBlocks;
import net.nimbu.pocketdimensions.block.custom.GatewayBlock;
import net.nimbu.pocketdimensions.block.entity.custom.GatewayBlockEntity;
import net.nimbu.pocketdimensions.component.ModAttachments;
import net.nimbu.pocketdimensions.component.PlayerGatewayData;
import net.nimbu.pocketdimensions.network.GatewayMaterialPayload;
import net.nimbu.pocketdimensions.network.PocketDimensionSync;

public final class ModGameEvents {
	private ModGameEvents() {}

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) return;
		ServerLevel level = player.serverLevel();
		PocketDimensionSync.sync(level, player);
		PocketDimensionSync.syncDynamicBiome(level, player);
	}

	@SubscribeEvent
	public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) return;
		ServerLevel level = player.serverLevel();
		PocketDimensionSync.sync(level, player);
		PocketDimensionSync.syncDynamicBiome(level, player);
	}

	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) return;
		ServerLevel level = player.serverLevel();
		PocketDimensionSync.sync(level, player);
		PocketDimensionSync.syncDynamicBiome(level, player);
	}

	/** Server handler for client-chosen gateway material. */
	public static void handleGatewayMaterial(GatewayMaterialPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (!(context.player() instanceof ServerPlayer player)) return;

			PlayerGatewayData data = PlayerGatewayData.get(player);
			data.setGatewayMaterial(payload.material());
			player.setData(ModAttachments.PLAYER_GATEWAY.get(), data);

			ServerLevel world = player.serverLevel();
			BlockPos pocketDimensionGatewayPos = new BlockPos(6, 148, 1);

			BlockEntity blockEntity = world.getBlockEntity(pocketDimensionGatewayPos);
			if (!(blockEntity instanceof GatewayBlockEntity previousPocketDimensionGatewayEntity)) return;

			Block gatewayType = switch (data.getGatewayMaterial()) {
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

			placeGateway(world, pocketDimensionGatewayPos, gatewayType.defaultBlockState()
					.setValue(GatewayBlock.FACING, Direction.SOUTH)
					.setValue(GatewayBlock.EXIT, true));

			BlockEntity be = world.getBlockEntity(pocketDimensionGatewayPos);
			if (!(be instanceof GatewayBlockEntity exitGatewayEntity)) return;

			BlockPos entranceGatewayBlockPos = previousPocketDimensionGatewayEntity.getExitBlockPos();
			exitGatewayEntity.setExitPosition(entranceGatewayBlockPos, previousPocketDimensionGatewayEntity.getExitDimension());

			ServerLevel targetWorld = world.getServer().getLevel(exitGatewayEntity.getExitDimension());
			if (targetWorld == null || entranceGatewayBlockPos == null) return;

			BlockEntity entranceBlockEntity = targetWorld.getBlockEntity(entranceGatewayBlockPos);
			BlockState entranceBlock = targetWorld.getBlockState(entranceGatewayBlockPos);
			if (!(entranceBlock.getBlock() instanceof GatewayBlock)) return;
			if (!(entranceBlockEntity instanceof GatewayBlockEntity previousEntranceGatewayBlockEntity)) return;

			placeGateway(targetWorld, entranceGatewayBlockPos, gatewayType.defaultBlockState()
					.setValue(GatewayBlock.FACING, entranceBlock.getValue(GatewayBlock.FACING))
					.setValue(GatewayBlock.OPEN, entranceBlock.getValue(GatewayBlock.OPEN)));

			BlockEntity newEntranceBe = targetWorld.getBlockEntity(entranceGatewayBlockPos);
			if (!(newEntranceBe instanceof GatewayBlockEntity entranceGatewayEntity)) return;

			entranceGatewayEntity.setExitPosition(
					previousEntranceGatewayBlockEntity.getExitBlockPos(),
					previousEntranceGatewayBlockEntity.getExitDimension());
		});
	}

	private static void placeGateway(ServerLevel world, BlockPos pos, BlockState baseState) {
		world.setBlock(pos, baseState.setValue(GatewayBlock.HALF, DoubleBlockHalf.LOWER), Block.UPDATE_ALL);
		world.setBlock(pos.above(), baseState.setValue(GatewayBlock.HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
	}
}
