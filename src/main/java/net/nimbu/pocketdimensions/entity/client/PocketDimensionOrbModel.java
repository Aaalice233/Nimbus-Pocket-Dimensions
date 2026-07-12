package net.nimbu.pocketdimensions.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.nimbu.pocketdimensions.PocketDimensions;

public class PocketDimensionOrbModel extends Model {
	public static final ModelLayerLocation ORB = new ModelLayerLocation(
			ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "orb"), "main");

	private final ModelPart root;
	public final ModelPart ring;

	public PocketDimensionOrbModel(ModelPart root) {
		super(RenderType::entitySolid);
		this.root = root;
		this.ring = root.getChild("ring");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition modelPartData = modelData.getRoot();
		PartDefinition orb = modelPartData.addOrReplaceChild("orb", CubeListBuilder.create(), PartPose.offset(0.0F, 16.5F, 0.0F));
		orb.addOrReplaceChild("cube_r1", CubeListBuilder.create()
						.texOffs(0, 40)
						.addBox(-4.5F, -4.5F, -4.5F, 9.0F, 9.0F, 9.0F, new CubeDeformation(0.0F)),
				PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.7854F, 0.0F, 0.6154F));

		modelPartData.addOrReplaceChild("ring",
				CubeListBuilder.create()
						.texOffs(0, 0).addBox(-9.0F, -2.0F, -9.0F, 0.0F, 2.0F, 18.0F)
						.texOffs(0, 20).addBox(9.0F, -2.0F, -9.0F, 0.0F, 2.0F, 18.0F)
						.texOffs(36, 0).addBox(-9.0F, -2.0F, -9.0F, 18.0F, 2.0F, 0.0F)
						.texOffs(36, 20).addBox(-9.0F, -2.0F, 9.0F, 18.0F, 2.0F, 0.0F),
				PartPose.offset(0.0F, 16.5F, 0.0F));
		return LayerDefinition.create(modelData, 64, 64);
	}

	@Override
	public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int light, int overlay, int color) {
		renderOrb(matrices, vertexConsumer, light, overlay, color);
	}

	public void renderOrb(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
		this.root.render(matrices, vertices, light, overlay, color);
	}
}
