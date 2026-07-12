package net.nimbu.pocketdimensions.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.nimbu.pocketdimensions.PocketDimensions;

public record GatewayMaterialPayload(int material) implements CustomPacketPayload {
	public static final Type<GatewayMaterialPayload> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "set_gateway_material"));

	public static final StreamCodec<RegistryFriendlyByteBuf, GatewayMaterialPayload> STREAM_CODEC =
			StreamCodec.composite(
					ByteBufCodecs.VAR_INT,
					GatewayMaterialPayload::material,
					GatewayMaterialPayload::new
			);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
