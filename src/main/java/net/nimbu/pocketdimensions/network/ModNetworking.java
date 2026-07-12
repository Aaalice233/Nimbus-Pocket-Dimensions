package net.nimbu.pocketdimensions.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.nimbu.pocketdimensions.ModGameEvents;
import net.nimbu.pocketdimensions.PocketDimensions;

public final class ModNetworking {
	private ModNetworking() {}

	public static void register(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar(PocketDimensions.MOD_ID).versioned("1");

		// S2C
		registrar.playToClient(RoomSyncPayload.TYPE, RoomSyncPayload.STREAM_CODEC, RoomSyncPayload::handle);
		registrar.playToClient(SingularRoomPayload.TYPE, SingularRoomPayload.STREAM_CODEC, SingularRoomPayload::handle);
		registrar.playToClient(DynamicBiomePayload.TYPE, DynamicBiomePayload.STREAM_CODEC, DynamicBiomePayload::handle);

		// C2S
		registrar.playToServer(UpdateBiomePacket.TYPE, UpdateBiomePacket.STREAM_CODEC, UpdateBiomePacket::handle);
		registrar.playToServer(GatewayMaterialPayload.TYPE, GatewayMaterialPayload.STREAM_CODEC, ModGameEvents::handleGatewayMaterial);
	}
}
