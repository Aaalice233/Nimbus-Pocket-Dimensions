package net.nimbu.pocketdimensions.network;

import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.nimbu.pocketdimensions.PocketDimensions;
import net.nimbu.pocketdimensions.persistentstates.PocketDimensionPersistentState;
import net.nimbu.pocketdimensions.worldgen.biome.DynamicBiomeEffects;

public record UpdateBiomePacket(DynamicBiomeEffects biome) implements CustomPacketPayload {
	public static final Type<UpdateBiomePacket> TYPE =
			new Type<>(ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "update_dynamic_biome"));

	public static final StreamCodec<RegistryFriendlyByteBuf, UpdateBiomePacket> STREAM_CODEC =
			StreamCodec.of(UpdateBiomePacket::write, UpdateBiomePacket::read);

	private static void write(RegistryFriendlyByteBuf buf, UpdateBiomePacket payload) {
		buf.writeNbt(DynamicBiomeEffects.CODEC.encodeStart(NbtOps.INSTANCE, payload.biome).getOrThrow());
	}

	private static UpdateBiomePacket read(RegistryFriendlyByteBuf buf) {
		return new UpdateBiomePacket(
				DynamicBiomeEffects.CODEC.parse(NbtOps.INSTANCE, buf.readNbt()).getOrThrow()
		);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void handle(UpdateBiomePacket payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			if (!(context.player() instanceof ServerPlayer player)) return;
			ServerLevel world = player.serverLevel();
			PocketDimensionPersistentState state = PocketDimensionPersistentState.get(world);
			state.setDynamicBiomeEffects(payload.biome());
			state.setDirty();
			for (ServerPlayer p : world.players()) {
				PocketDimensionSync.syncDynamicBiome(world, p);
			}
		});
	}
}
