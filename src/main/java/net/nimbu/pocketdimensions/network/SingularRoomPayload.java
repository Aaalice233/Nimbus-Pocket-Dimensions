package net.nimbu.pocketdimensions.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.nimbu.pocketdimensions.PocketDimensions;

public record SingularRoomPayload(BlockPos room) implements CustomPacketPayload {
	public static final Type<SingularRoomPayload> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "singlular_room_update"));

	public static final StreamCodec<RegistryFriendlyByteBuf, SingularRoomPayload> STREAM_CODEC =
			StreamCodec.of(
					(buf, payload) -> buf.writeBlockPos(payload.room),
					buf -> new SingularRoomPayload(buf.readBlockPos())
			);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(SingularRoomPayload payload, IPayloadContext context) {
		context.enqueueWork(() -> ClientPocketDimensionPersistentState.addRoom(payload.room()));
	}
}
