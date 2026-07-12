package net.nimbu.pocketdimensions.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public final class DynamicSkyRenderer {
	private DynamicSkyRenderer() {}

	public static void render(PoseStack matrices, ResourceLocation skybox) {
		RenderSystem.enableBlend();
		RenderSystem.depthMask(false);
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, skybox);
		Tesselator tessellator = Tesselator.getInstance();

		for (int i = 0; i < 6; i++) {
			matrices.pushPose();
			if (i == 1) matrices.mulPose(Axis.XP.rotationDegrees(90.0F));
			if (i == 2) matrices.mulPose(Axis.XP.rotationDegrees(-90.0F));
			if (i == 3) matrices.mulPose(Axis.XP.rotationDegrees(180.0F));
			if (i == 4) matrices.mulPose(Axis.ZP.rotationDegrees(90.0F));
			if (i == 5) matrices.mulPose(Axis.ZP.rotationDegrees(-90.0F));

			Matrix4f matrix4f = matrices.last().pose();
			BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
			bufferBuilder.addVertex(matrix4f, -100.0F, -100.0F, -100.0F).setUv(0.0F, 0.0F).setColor(-14145496);
			bufferBuilder.addVertex(matrix4f, -100.0F, -100.0F, 100.0F).setUv(0.0F, 16.0F).setColor(-14145496);
			bufferBuilder.addVertex(matrix4f, 100.0F, -100.0F, 100.0F).setUv(16.0F, 16.0F).setColor(-14145496);
			bufferBuilder.addVertex(matrix4f, 100.0F, -100.0F, -100.0F).setUv(16.0F, 0.0F).setColor(-14145496);
			BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
			matrices.popPose();
		}

		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
	}
}
