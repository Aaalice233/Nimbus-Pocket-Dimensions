package net.nimbu.pocketdimensions;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import net.nimbu.pocketdimensions.block.ModBlocks;
import net.nimbu.pocketdimensions.block.entity.ModBlockEntityTypes;
import net.nimbu.pocketdimensions.block.entity.renderer.PocketDimensionCustomizerBlockEntityRenderer;
import net.nimbu.pocketdimensions.entity.ModEntities;
import net.nimbu.pocketdimensions.entity.client.GatewayProjectileRenderer;
import net.nimbu.pocketdimensions.entity.client.PocketDimensionOrbModel;
import net.nimbu.pocketdimensions.particle.GatewayProjectileParticle;
import net.nimbu.pocketdimensions.particle.ModParticleTypes;
import net.nimbu.pocketdimensions.renderer.PocketDimensionBorderRenderer;
import net.nimbu.pocketdimensions.screen.ModScreenHandlers;
import net.nimbu.pocketdimensions.screen.custom.DimensionCustomizerScreen;

@EventBusSubscriber(modid = PocketDimensions.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class PocketDimensionsClient {
	private PocketDimensionsClient() {}

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.DIMENSION_CUSTOMIZER.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.OAK_GATEWAY.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.JUNGLE_GATEWAY.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.ACACIA_GATEWAY.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.CHERRY_GATEWAY.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.BAMBOO_GATEWAY.get(), RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(ModBlocks.GUI_WATER.get(), RenderType.translucent());
		});
	}

	@SubscribeEvent
	public static void registerScreens(RegisterMenuScreensEvent event) {
		event.register(ModScreenHandlers.POCKET_DIM_BIOME_SCREEN_HANDLER.get(), DimensionCustomizerScreen::new);
	}

	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntities.SPELL_PORTAL.get(), GatewayProjectileRenderer::new);
		event.registerBlockEntityRenderer(ModBlockEntityTypes.POCKET_DIMENSION_CUSTOMIZER_BLOCK_ENTITY.get(),
				PocketDimensionCustomizerBlockEntityRenderer::new);
	}

	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(PocketDimensionOrbModel.ORB, PocketDimensionOrbModel::createBodyLayer);
	}

	@SubscribeEvent
	public static void registerParticles(RegisterParticleProvidersEvent event) {
		event.registerSpriteSet(ModParticleTypes.GATEWAY_PROJECTILE_PARTICLE.get(), GatewayProjectileParticle.Factory::new);
	}

	@SubscribeEvent
	public static void registerShaders(RegisterShadersEvent event) {
		try {
			event.registerShader(
					new ShaderInstance(event.getResourceProvider(),
							ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "border"),
							com.mojang.blaze3d.vertex.DefaultVertexFormat.NEW_ENTITY),
					shader -> PocketDimensionBorderRenderer.BORDER_SHADER = shader);
			event.registerShader(
					new ShaderInstance(event.getResourceProvider(),
							ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "far_view_border"),
							com.mojang.blaze3d.vertex.DefaultVertexFormat.NEW_ENTITY),
					shader -> PocketDimensionBorderRenderer.FAR_BORDER_SHADER = shader);
		} catch (Exception e) {
			PocketDimensions.LOGGER.error("Failed to register pocket dimension shaders", e);
		}
	}
}
