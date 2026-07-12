package net.nimbu.pocketdimensions.screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import net.nimbu.pocketdimensions.PocketDimensions;
import net.nimbu.pocketdimensions.block.ModBlocks;
import net.nimbu.pocketdimensions.component.PlayerGatewayData;
import net.nimbu.pocketdimensions.network.ClientPocketDimensionPersistentState;
import net.nimbu.pocketdimensions.network.GatewayMaterialPayload;
import net.nimbu.pocketdimensions.network.UpdateBiomePacket;
import net.nimbu.pocketdimensions.screen.widgets.InvisibleButton;
import net.nimbu.pocketdimensions.screen.widgets.RGBSliderGroup;
import net.nimbu.pocketdimensions.screen.widgets.Slider;

import java.util.Optional;

public class DimensionCustomizerScreen extends AbstractContainerScreen<DimensionCustomizerScreenHandler> {
	public static final ResourceLocation SLIDER_KNOB = ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "textures/gui/widgets/slider_knob.png");
	public static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "textures/gui/pocket_dimension_customizer/pocket_dimension_customizer.png");
	public static final ResourceLocation BACKGROUND_0 = ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "textures/gui/pocket_dimension_customizer/pocket_dimension_customizer_0.png");
	public static final ResourceLocation BACKGROUND_1 = ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "textures/gui/pocket_dimension_customizer/pocket_dimension_customizer_1.png");
	public static final ResourceLocation BACKGROUND_2 = ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "textures/gui/pocket_dimension_customizer/pocket_dimension_customizer_2.png");
	public static final ResourceLocation BACKGROUND_3 = ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "textures/gui/pocket_dimension_customizer/pocket_dimension_customizer_3.png");
	public static final ResourceLocation BACKGROUND_4 = ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "textures/gui/pocket_dimension_customizer/pocket_dimension_customizer_4.png");

	private RGBSliderGroup grassSliders;
	private RGBSliderGroup leavesSliders;
	private RGBSliderGroup waterSliders;
	private RGBSliderGroup skySliders;
	private Slider doorSlider;

	public DimensionCustomizerScreen(DimensionCustomizerScreenHandler handler, Inventory inventory, Component title) {
		super(handler, inventory, Component.empty());
	}

	@Override
	protected void init() {
		super.init();

		Minecraft client = Minecraft.getInstance();
		Player player = client.player;

		grassSliders = new RGBSliderGroup(leftPos + imageWidth - 101, topPos + 114, 89, 46, 5, 3, menu.getGrassColour());
		grassSliders.visitWidgets(this::addRenderableWidget);
		leavesSliders = new RGBSliderGroup(leftPos + imageWidth - 101, topPos + 114, 89, 46, 5, 3, menu.getFoliageColour());
		leavesSliders.visitWidgets(this::addRenderableWidget);
		waterSliders = new RGBSliderGroup(leftPos + imageWidth - 101, topPos + 114, 89, 46, 5, 3, menu.getWaterColour());
		waterSliders.visitWidgets(this::addRenderableWidget);
		skySliders = new RGBSliderGroup(leftPos + imageWidth - 101, topPos + 114, 89, 46, 5, 3, menu.getSkyColour());
		skySliders.visitWidgets(this::addRenderableWidget);
		int material = player != null ? PlayerGatewayData.get(player).getGatewayMaterial() : 6;
		doorSlider = new Slider(leftPos + imageWidth - 113, topPos + 130, 68, 46, Component.literal("Door Material"), material, 11);
		doorSlider.visitWidgets(this::addRenderableWidget);

		addRenderableWidget(InvisibleButton.builder(Component.literal("Grass colours"), button -> {
			grassSliders.setVisibility(true);
			leavesSliders.setVisibility(false);
			waterSliders.setVisibility(false);
			skySliders.setVisibility(false);
			doorSlider.setVisibility(false);
		}).dimensions(leftPos + 1, topPos + 7, 46, 17).build());

		addRenderableWidget(InvisibleButton.builder(Component.literal("Foliage colours"), button -> {
			grassSliders.setVisibility(false);
			leavesSliders.setVisibility(true);
			waterSliders.setVisibility(false);
			skySliders.setVisibility(false);
			doorSlider.setVisibility(false);
		}).dimensions(leftPos + 1, topPos + 24, 46, 17).build());

		addRenderableWidget(InvisibleButton.builder(Component.literal("Water colours"), button -> {
			grassSliders.setVisibility(false);
			leavesSliders.setVisibility(false);
			waterSliders.setVisibility(true);
			skySliders.setVisibility(false);
			doorSlider.setVisibility(false);
		}).dimensions(leftPos + 1, topPos + 41, 46, 17).build());

		addRenderableWidget(InvisibleButton.builder(Component.literal("Sky colour"), button -> {
			grassSliders.setVisibility(false);
			leavesSliders.setVisibility(false);
			waterSliders.setVisibility(false);
			skySliders.setVisibility(true);
			doorSlider.setVisibility(false);
		}).dimensions(leftPos + 1, topPos + 58, 46, 17).build());

		addRenderableWidget(InvisibleButton.builder(Component.literal("Door material"), button -> {
			grassSliders.setVisibility(false);
			leavesSliders.setVisibility(false);
			waterSliders.setVisibility(false);
			skySliders.setVisibility(false);
			doorSlider.setVisibility(true);
		}).dimensions(leftPos + 1, topPos + 75, 46, 17).build());

		grassSliders.setVisibility(true);
		leavesSliders.setVisibility(false);
		waterSliders.setVisibility(false);
		skySliders.setVisibility(false);
		doorSlider.setVisibility(false);
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);

		int[] skyColour = skySliders.getColour();
		int[] waterColour = waterSliders.getColour();
		int[] leavesColour = leavesSliders.getColour();
		int[] grassColour = grassSliders.getColour();

		if (grassSliders.getVisibility()) {
			renderBlock(context, Blocks.DIRT.defaultBlockState(), 51, 255, 255, 255);
			renderBlock(context, ModBlocks.GUI_GRASS.get().defaultBlockState(), 51, grassColour[0], grassColour[1], grassColour[2]);
		} else if (skySliders.getVisibility()) {
			renderBlock(context, Blocks.WHITE_STAINED_GLASS.defaultBlockState(), 51, skyColour[0], skyColour[1], skyColour[2]);
		} else if (waterSliders.getVisibility()) {
			renderBlock(context, ModBlocks.GUI_WATER.get().defaultBlockState(), 51, waterColour[0], waterColour[1], waterColour[2]);
		} else if (leavesSliders.getVisibility()) {
			renderBlock(context, ModBlocks.GUI_OAK_LEAVES.get().defaultBlockState(), 51, leavesColour[0], leavesColour[1], leavesColour[2]);
		} else if (doorSlider.getVisibility()) {
			Block blockType = switch (doorSlider.getValue()) {
				case 1 -> Blocks.OAK_PLANKS;
				case 2 -> Blocks.SPRUCE_PLANKS;
				case 3 -> Blocks.BIRCH_PLANKS;
				case 4 -> Blocks.JUNGLE_PLANKS;
				case 5 -> Blocks.ACACIA_PLANKS;
				case 7 -> Blocks.MANGROVE_PLANKS;
				case 8 -> Blocks.CHERRY_PLANKS;
				case 9 -> Blocks.CRIMSON_PLANKS;
				case 10 -> Blocks.WARPED_PLANKS;
				case 11 -> Blocks.STRIPPED_BAMBOO_BLOCK;
				default -> Blocks.DARK_OAK_PLANKS;
			};
			renderBlock(context, blockType.defaultBlockState(), 51, 255, 255, 255);
		}
	}

	@Override
	protected void renderLabels(GuiGraphics context, int mouseX, int mouseY) {
	}

	private void renderBlock(GuiGraphics context, BlockState state, float scale, int r, int g, int b) {
		int x = this.leftPos + 72;
		int y = this.topPos + 80;
		Minecraft client = Minecraft.getInstance();

		var matrices = context.pose();
		matrices.pushPose();
		matrices.translate(x, y, 100);
		matrices.scale(scale, -scale, scale);
		matrices.mulPose(Axis.XP.rotationDegrees(30));
		matrices.mulPose(Axis.YP.rotationDegrees(45));

		int light = LightTexture.FULL_BRIGHT;
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(r / 255f, g / 255f, b / 255f, 1f);

		client.getBlockRenderer().renderSingleBlock(
				state,
				matrices,
				context.bufferSource(),
				light,
				OverlayTexture.NO_OVERLAY
		);

		context.flush();
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.disableBlend();
		matrices.popPose();
	}

	@Override
	public void setFocused(boolean focused) {
		super.setFocused(true);
	}

	@Override
	protected void renderBg(GuiGraphics context, float delta, int mouseX, int mouseY) {
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		context.blit(BACKGROUND, x, y, 0, 0, imageWidth, imageHeight);
		if (grassSliders.getVisibility()) context.blit(BACKGROUND_0, x, y, 0, 0, 256, 256);
		if (leavesSliders.getVisibility()) context.blit(BACKGROUND_1, x, y, 0, 0, 256, 256);
		if (waterSliders.getVisibility()) context.blit(BACKGROUND_2, x, y, 0, 0, 256, 256);
		if (skySliders.getVisibility()) context.blit(BACKGROUND_3, x, y, 0, 0, 256, 256);
		if (doorSlider.getVisibility()) context.blit(BACKGROUND_4, x, y, 0, 0, 256, 256);
	}

	@Override
	public void removed() {
		applyChanges();
		super.removed();
	}

	private void applyChanges() {
		int[] fogColour = skySliders.getColour();
		int[] waterColour = waterSliders.getColour();
		int[] foliageColour = leavesSliders.getColour();
		int[] grassColour = grassSliders.getColour();

		int lighten = 40;
		int fogHex = (Math.min(255, fogColour[0] + lighten) << 16)
				| (Math.min(255, fogColour[1] + lighten) << 8)
				| (Math.min(255, fogColour[2] + lighten));
		int skyHex = (fogColour[0] << 16) | (fogColour[1] << 8) | fogColour[2];
		int waterHex = (waterColour[0] << 16) | (waterColour[1] << 8) | waterColour[2];
		int waterFogHex = (waterColour[0] / 10 << 16) | (waterColour[1] / 10 << 8) | waterColour[2] / 10;
		int foliageHex = (foliageColour[0] << 16) | (foliageColour[1] << 8) | foliageColour[2];
		int grassHex = (grassColour[0] << 16) | (grassColour[1] << 8) | grassColour[2];

		ClientPocketDimensionPersistentState.getDynamicBiomeEffects().setFogColor(fogHex);
		ClientPocketDimensionPersistentState.getDynamicBiomeEffects().setSkyColor(skyHex);
		ClientPocketDimensionPersistentState.getDynamicBiomeEffects().setWaterColor(waterHex);
		ClientPocketDimensionPersistentState.getDynamicBiomeEffects().setWaterFogColor(waterFogHex);
		ClientPocketDimensionPersistentState.getDynamicBiomeEffects().setFoliageColor(Optional.of(foliageHex));
		ClientPocketDimensionPersistentState.getDynamicBiomeEffects().setGrassColor(Optional.of(grassHex));
		PacketDistributor.sendToServer(new UpdateBiomePacket(ClientPocketDimensionPersistentState.getDynamicBiomeEffects()));
		PacketDistributor.sendToServer(new GatewayMaterialPayload(doorSlider.getValue()));

		Minecraft client = Minecraft.getInstance();
		if (client.level != null) {
			client.levelRenderer.allChanged();
		}
	}
}
