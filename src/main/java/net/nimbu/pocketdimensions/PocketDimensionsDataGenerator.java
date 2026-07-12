package net.nimbu.pocketdimensions;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Minimal datagen hook. JSON assets already ship under resources; providers stubbed for compile.
 */
@EventBusSubscriber(modid = PocketDimensions.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class PocketDimensionsDataGenerator {
	private PocketDimensionsDataGenerator() {}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		// Assets/data JSON already present under src/main/resources — no runtime providers required for compile.
		PocketDimensions.LOGGER.debug("GatherDataEvent received (no providers registered)");
	}
}
