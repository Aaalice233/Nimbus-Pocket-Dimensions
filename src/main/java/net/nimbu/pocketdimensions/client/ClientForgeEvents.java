package net.nimbu.pocketdimensions.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.nimbu.pocketdimensions.PocketDimensions;
import net.nimbu.pocketdimensions.renderer.PocketDimensionBorderRenderer;

@EventBusSubscriber(modid = PocketDimensions.MOD_ID, value = Dist.CLIENT)
public final class ClientForgeEvents {
	private ClientForgeEvents() {}

	@SubscribeEvent
	public static void onRenderLevel(RenderLevelStageEvent event) {
		if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
			PocketDimensionBorderRenderer.render(event);
		}
	}

	@SubscribeEvent
	public static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
		PocketDimensionBorderRenderer.expansionModeActive = false;
		PocketDimensionBorderRenderer.expansionModePosition = null;
		PocketDimensionBorderRenderer.expansionValid = true;
	}
}
