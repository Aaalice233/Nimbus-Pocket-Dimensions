package net.nimbu.pocketdimensions.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.nimbu.pocketdimensions.PocketDimensions;

import java.util.HashSet;
import java.util.Set;

public record RoomSyncPayload(Set<BlockPos> rooms) implements CustomPacketPayload {
	public static final Type<RoomSyncPayload> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "room_sync"));

	public static final StreamCodec<RegistryFriendlyByteBuf, RoomSyncPayload> STREAM_CODEC =
			StreamCodec.of(RoomSyncPayload::write, RoomSyncPayload::read);

	private static void write(RegistryFriendlyByteBuf buf, RoomSyncPayload payload) {
		buf.writeVarInt(payload.rooms.size());
		for (BlockPos pos : payload.rooms) {
			buf.writeBlockPos(pos);
		}
	}

	private static RoomSyncPayload read(RegistryFriendlyByteBuf buf) {
		int count = buf.readVarInt();
		Set<BlockPos> rooms = new HashSet<>();
		for (int i = 0; i < count; i++) {
			rooms.add(buf.readBlockPos());
		}
		return new RoomSyncPayload(rooms);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(RoomSyncPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> ClientPocketDimensionPersistentState.setRooms(payload.rooms()));
	}
}
