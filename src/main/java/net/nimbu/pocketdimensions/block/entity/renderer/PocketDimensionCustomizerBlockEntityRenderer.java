package net.nimbu.pocketdimensions.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.nimbu.pocketdimensions.PocketDimensions;
import net.nimbu.pocketdimensions.block.entity.custom.PocketDimensionCustomizerBlockEntity;
import net.nimbu.pocketdimensions.entity.client.PocketDimensionOrbModel;
import net.nimbu.pocketdimensions.worldgen.dimension.ModDimensions;

public class PocketDimensionCustomizerBlockEntityRenderer implements BlockEntityRenderer<PocketDimensionCustomizerBlockEntity> {
	public static final ResourceLocation ORB_TEXTURE =
			ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "textures/entity/dimension_customizer_orb.png");
	private final PocketDimensionOrbModel orb;

	public PocketDimensionCustomizerBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
		this.orb = new PocketDimensionOrbModel(context.bakeLayer(PocketDimensionOrbModel.ORB));
	}

	@Override
	public void render(PocketDimensionCustomizerBlockEntity entity, float tickDelta, PoseStack matrices,
	                   MultiBufferSource vertexConsumers, int light, int overlay) {
		Level world = entity.getLevel();
		if (world == null) return;
		if (world.dimensionTypeRegistration().is(ModDimensions.POCKET_DIM_TYPE)) {
			float g = entity.ticks + tickDelta;
			matrices.pushPose();
			matrices.translate(0.5f, 1.0f, 0.5f);
			matrices.translate(0, Mth.sin(g * 0.03F) * 0.1F, 0);
			matrices.scale(1.2f, 1.2f, 1.2f);
			matrices.mulPose(Axis.YP.rotationDegrees(entity.rotation));

			float time = world.getGameTime() + tickDelta;
			this.orb.ring.yRot = -time * 0.03F;

			VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutout(ORB_TEXTURE));
			this.orb.renderOrb(matrices, vertexConsumer, 255, overlay, -1);
			matrices.popPose();
		}
	}
}
