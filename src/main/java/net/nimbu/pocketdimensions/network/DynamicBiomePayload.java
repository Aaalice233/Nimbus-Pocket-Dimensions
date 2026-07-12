package net.nimbu.pocketdimensions.network;

import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.nimbu.pocketdimensions.PocketDimensions;
import net.nimbu.pocketdimensions.worldgen.biome.DynamicBiomeEffects;

public record DynamicBiomePayload(
		boolean inPocketDimension,
		DynamicBiomeEffects dynamicBiomeEffects,
		ResourceLocation skybox
) implements CustomPacketPayload {
	public static final Type<DynamicBiomePayload> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "dynamic_biome_sync"));

	public static final StreamCodec<RegistryFriendlyByteBuf, DynamicBiomePayload> STREAM_CODEC =
			StreamCodec.of(DynamicBiomePayload::write, DynamicBiomePayload::read);

	private static void write(RegistryFriendlyByteBuf buf, DynamicBiomePayload payload) {
		buf.writeBoolean(payload.inPocketDimension);
		buf.writeNbt(DynamicBiomeEffects.CODEC.encodeStart(NbtOps.INSTANCE, payload.dynamicBiomeEffects).getOrThrow());
		buf.writeResourceLocation(payload.skybox);
	}

	private static DynamicBiomePayload read(RegistryFriendlyByteBuf buf) {
		return new DynamicBiomePayload(
				buf.readBoolean(),
				DynamicBiomeEffects.CODEC.parse(NbtOps.INSTANCE, buf.readNbt()).getOrThrow(),
				buf.readResourceLocation()
		);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(DynamicBiomePayload payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			PocketDimensions.LOGGER.info("done biome sync with inPocketDim {}", payload.inPocketDimension());
			ClientPocketDimensionPersistentState.setIsClientInPocketDimension(payload.inPocketDimension());
			ClientPocketDimensionPersistentState.setDynamicBiomeBiomeEffects(payload.dynamicBiomeEffects());
			ClientPocketDimensionPersistentState.setSkybox(payload.skybox());
		});
	}
}
