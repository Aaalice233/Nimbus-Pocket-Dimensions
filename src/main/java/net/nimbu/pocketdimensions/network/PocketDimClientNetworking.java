package net.nimbu.pocketdimensions.network;

/** Client receivers registered via RegisterPayloadHandlersEvent payload handlers. */
public final class PocketDimClientNetworking {
	private PocketDimClientNetworking() {}

	public static void register() {
		// no-op on NeoForge — S2C handlers are on payload records
	}
}
