package net.nimbu.pocketdimensions.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.nimbu.pocketdimensions.PocketDimensions;
import net.nimbu.pocketdimensions.entity.custom.GatewayProjectileEntity;

public class GatewayProjectileRenderer extends EntityRenderer<GatewayProjectileEntity> {
	private static final ResourceLocation TEXTURE =
			ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "textures/particle/gateway_projectile_particle.png");
	private static final RenderType LAYER = RenderType.entityCutoutNoCull(TEXTURE);

	public GatewayProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected int getBlockLightLevel(GatewayProjectileEntity entity, BlockPos blockPos) {
		return 15;
	}

	@Override
	public void render(GatewayProjectileEntity entity, float yaw, float partialTick, PoseStack poseStack,
	                   MultiBufferSource buffer, int packedLight) {
		poseStack.pushPose();
		poseStack.scale(0.5F, 0.5F, 0.5F);
		poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
		PoseStack.Pose entry = poseStack.last();
		VertexConsumer vertexConsumer = buffer.getBuffer(LAYER);
		produceVertex(vertexConsumer, entry, packedLight, 0.0F, 0, 0, 1);
		produceVertex(vertexConsumer, entry, packedLight, 1.0F, 0, 1, 1);
		produceVertex(vertexConsumer, entry, packedLight, 1.0F, 1, 1, 0);
		produceVertex(vertexConsumer, entry, packedLight, 0.0F, 1, 0, 0);
		poseStack.popPose();
		super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);
	}

	private static void produceVertex(VertexConsumer vertexConsumer, PoseStack.Pose matrix, int light,
	                                  float x, int z, int textureU, int textureV) {
		vertexConsumer.addVertex(matrix, x - 0.5F, z - 0.25F, 0.0F)
				.setColor(255, 255, 255, 255)
				.setUv(textureU, textureV)
				.setOverlay(OverlayTexture.NO_OVERLAY)
				.setLight(light)
				.setNormal(matrix, 0.0F, 1.0F, 0.0F);
	}

	@Override
	public ResourceLocation getTextureLocation(GatewayProjectileEntity entity) {
		return TEXTURE;
	}
}
