package net.nimbu.pocketdimensions.network;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.nimbu.pocketdimensions.PocketDimensions;
import net.nimbu.pocketdimensions.persistentstates.PocketDimensionPersistentState;

import java.util.Set;

public final class PocketDimensionSync {
	private PocketDimensionSync() {}

	public static void sync(ServerLevel world, ServerPlayer player) {
		PocketDimensionPersistentState state = PocketDimensionPersistentState.get(world);
		Set<BlockPos> rooms = state.getUnlockedRooms();
		PocketDimensions.LOGGER.info("Sending {} rooms to {}", rooms.size(), player.getName().getString());
		PacketDistributor.sendToPlayer(player, new RoomSyncPayload(rooms));
	}

	public static void updateSingularRoom(ServerPlayer player, BlockPos posToSync) {
		PacketDistributor.sendToPlayer(player, new SingularRoomPayload(posToSync));
	}

	public static void syncDynamicBiome(ServerLevel world, ServerPlayer player) {
		PocketDimensionPersistentState state = PocketDimensionPersistentState.get(world);
		boolean inPocket = world.dimension().location().toString().contains("pocket_dimension");
		PocketDimensions.LOGGER.info("doingDynamicBiome with skybox id of {}", state.getSkybox().getPath());
		PacketDistributor.sendToPlayer(player,
				new DynamicBiomePayload(inPocket, state.getDynamicBiomeEffects(), state.getSkybox()));
	}
}
