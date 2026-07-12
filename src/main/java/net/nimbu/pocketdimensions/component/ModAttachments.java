package net.nimbu.pocketdimensions.component;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.nimbu.pocketdimensions.PocketDimensions;

import java.util.function.Supplier;

public final class ModAttachments {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
			DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, PocketDimensions.MOD_ID);

	/** Player gateway location/material — serializable NBT, copyOnDeath, synced to clients. */
	public static final Supplier<AttachmentType<PlayerGatewayData>> PLAYER_GATEWAY =
			ATTACHMENT_TYPES.register("player_gateway", () ->
					AttachmentType.serializable(PlayerGatewayData::new)
							.copyOnDeath()
							.sync(PlayerGatewayData.STREAM_CODEC)
							.build());

	private ModAttachments() {}
}
