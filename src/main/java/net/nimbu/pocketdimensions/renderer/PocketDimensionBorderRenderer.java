package net.nimbu.pocketdimensions.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.nimbu.pocketdimensions.PocketDimensions;
import net.nimbu.pocketdimensions.network.ClientPocketDimensionPersistentState;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class PocketDimensionBorderRenderer {
	public static final int BorderLength = 13;
	public static final int BorderHeight = 10;

	public static boolean expansionModeActive = false;
	public static BlockPos expansionModePosition = null;
	public static boolean expansionValid = true;

	public static final ResourceLocation BORDER_TEXTURE =
			ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "textures/shader/near_border.png");
	public static ShaderInstance BORDER_SHADER;
	public static ShaderInstance FAR_BORDER_SHADER;

	private static final RenderType BORDER_RENDER_LAYER = RenderType.create(
			"pocketdimensions_border",
			DefaultVertexFormat.NEW_ENTITY,
			VertexFormat.Mode.QUADS,
			256,
			false,
			true,
			RenderType.CompositeState.builder()
					.setShaderState(new RenderStateShard.ShaderStateShard(() -> BORDER_SHADER))
					.setTextureState(new RenderStateShard.TextureStateShard(BORDER_TEXTURE, false, false))
					.setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
					.setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
					.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
					.setCullState(RenderStateShard.NO_CULL)
					.setLightmapState(RenderStateShard.NO_LIGHTMAP)
					.createCompositeState(false)
	);

	private static final RenderType CONSTANT_BORDER_RENDER_LAYER = RenderType.create(
			"pocketdimensions_border_far",
			DefaultVertexFormat.NEW_ENTITY,
			VertexFormat.Mode.QUADS,
			256,
			false,
			true,
			RenderType.CompositeState.builder()
					.setShaderState(new RenderStateShard.ShaderStateShard(() -> FAR_BORDER_SHADER))
					.setTextureState(new RenderStateShard.TextureStateShard(BORDER_TEXTURE, false, false))
					.setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
					.setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
					.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
					.setCullState(RenderStateShard.NO_CULL)
					.setLightmapState(RenderStateShard.NO_LIGHTMAP)
					.createCompositeState(false)
	);

	private static final RenderType EXPANSION_RETICLE_LAYER = RenderType.create(
			"pocketdimensions_border_reticle",
			DefaultVertexFormat.NEW_ENTITY,
			VertexFormat.Mode.QUADS,
			256,
			false,
			true,
			RenderType.CompositeState.builder()
					.setShaderState(new RenderStateShard.ShaderStateShard(() -> FAR_BORDER_SHADER))
					.setTextureState(new RenderStateShard.TextureStateShard(BORDER_TEXTURE, false, false))
					.setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
					.setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
					.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
					.setCullState(RenderStateShard.NO_CULL)
					.setLightmapState(RenderStateShard.NO_LIGHTMAP)
					.createCompositeState(false)
	);

	private static final float[][] FACE_NORMALS = {{1, 0, 0}, {-1, 0, 0}, {0, 1, 0}, {0, -1, 0}, {0, 0, 1}, {0, 0, -1}};
	private static final float[][][] FACE_VERTICES = {
			{{BorderLength, BorderHeight, BorderLength, 0, BorderHeight}, {BorderLength, BorderHeight, 0, BorderLength, BorderHeight}, {BorderLength, 0, 0, BorderLength, 0}, {BorderLength, 0, BorderLength, 0, 0}},
			{{0, BorderHeight, 0, 0, BorderHeight}, {0, BorderHeight, BorderLength, BorderLength, BorderHeight}, {0, 0, BorderLength, BorderLength, 0}, {0, 0, 0, 0, 0}},
			{{0, BorderHeight, BorderLength, 0, BorderLength}, {BorderLength, BorderHeight, BorderLength, BorderLength, BorderLength}, {BorderLength, BorderHeight, 0, BorderLength, 0}, {0, BorderHeight, 0, 0, 0}},
			{{0, 0, 0, 0, 0}, {BorderLength, 0, 0, BorderLength, 0}, {BorderLength, 0, BorderLength, BorderLength, BorderLength}, {0, 0, BorderLength, 0, BorderLength}},
			{{0, 0, BorderLength, 0, 0}, {BorderLength, 0, BorderLength, BorderLength, 0}, {BorderLength, BorderHeight, BorderLength, BorderLength, BorderHeight}, {0, BorderHeight, BorderLength, 0, BorderHeight}},
			{{BorderLength, 0, 0, 0, 0}, {0, 0, 0, BorderLength, 0}, {0, BorderHeight, 0, BorderLength, BorderHeight}, {BorderLength, BorderHeight, 0, 0, BorderHeight}}
	};

	private PocketDimensionBorderRenderer() {}

	public static void render(RenderLevelStageEvent event) {
		if (!ClientPocketDimensionPersistentState.isClientInPocketDimension()) return;
		PoseStack matrices = event.getPoseStack();
		Vector3f cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().toVector3f();
		matrices.pushPose();
		matrices.translate(-cam.x, -cam.y, -cam.z);

		MultiBufferSource.BufferSource consumers = Minecraft.getInstance().renderBuffers().bufferSource();
		VertexConsumer vc = consumers.getBuffer(expansionModeActive ? CONSTANT_BORDER_RENDER_LAYER : BORDER_RENDER_LAYER);
		AbstractClientPlayer player = Minecraft.getInstance().player;

		if (BORDER_SHADER != null && player != null) {
			var camUniform = BORDER_SHADER.getUniform("cameraPosition");
			if (camUniform != null) camUniform.set(cam.x, cam.y, cam.z);
			var world = Minecraft.getInstance().level;
			if (world != null) {
				var players = world.players();
				int count = Math.min(players.size(), 8);
				var countUniform = BORDER_SHADER.getUniform("playerCount");
				if (countUniform != null) countUniform.set(count);
				float[] data = new float[8 * 3];
				if (count > 0) {
					Vector3f playerPos = player.position().toVector3f();
					data[0] = playerPos.x;
					data[1] = playerPos.y;
					data[2] = playerPos.z;
				}
				if (count > 1) {
					for (int i = 1; i < count; i++) {
						Vec3 p = players.get(i).position();
						int base = i * 3;
						data[base] = (float) p.x;
						data[base + 1] = (float) p.y;
						data[base + 2] = (float) p.z;
					}
				}
				var posUniform = BORDER_SHADER.getUniform("playerPositions");
				if (posUniform != null) posUniform.set(data);
			}
			if (Minecraft.getInstance().level != null) {
				var timeUniform = BORDER_SHADER.getUniform("time");
				if (timeUniform != null) timeUniform.set((float) Minecraft.getInstance().level.getGameTime());
			}
		}

		BlockPos relativePosition = new BlockPos(
				(int) Math.floor(cam.x / BorderLength),
				(int) Math.floor(cam.y / BorderHeight),
				(int) Math.floor(cam.z / BorderLength));
		final int renderRadius = 2;
		for (int x = -renderRadius; x <= renderRadius; x++) {
			for (int y = -renderRadius; y <= renderRadius; y++) {
				for (int z = -renderRadius; z <= renderRadius; z++) {
					BlockPos currentPos = new BlockPos(
							relativePosition.getX() + x,
							relativePosition.getY() + y,
							relativePosition.getZ() + z);
					if (expansionModeActive && expansionModePosition != null && currentPos.equals(expansionModePosition)) {
						continue;
					}
					boolean[] adj = ClientPocketDimensionPersistentState.getAdjacents(currentPos);
					if (expansionModeActive && expansionModePosition != null) {
						for (int i = 0; i < 6; i++) {
							BlockPos neighbour = currentPos.offset((int) FACE_NORMALS[i][0], (int) FACE_NORMALS[i][1], (int) FACE_NORMALS[i][2]);
							if (neighbour.equals(expansionModePosition)) adj[i] = false;
						}
					}
					for (int i = 0; i < 6; i++) {
						if (adj[i]) {
							renderFace(vc, matrices, i, currentPos.getX(), currentPos.getY(), currentPos.getZ());
						}
					}
				}
			}
		}
		matrices.popPose();

		if (expansionModeActive && expansionModePosition != null && FAR_BORDER_SHADER != null && player != null) {
			matrices.pushPose();
			var camUniform = FAR_BORDER_SHADER.getUniform("cameraPosition");
			if (camUniform != null) camUniform.set(cam.x, cam.y, cam.z);
			matrices.translate(
					expansionModePosition.getX() * BorderLength,
					expansionModePosition.getY() * BorderHeight + 7,
					expansionModePosition.getZ() * BorderLength
			);
			VertexConsumer expVc = consumers.getBuffer(EXPANSION_RETICLE_LAYER);
			Matrix4f mat = matrices.last().pose();
			if (expansionValid) {
				for (int i = 0; i < 6; i++) {
					for (int v = 0; v < 4; v++) {
						expVc.addVertex(mat, FACE_VERTICES[i][v][0], FACE_VERTICES[i][v][1], FACE_VERTICES[i][v][2])
								.setColor(0, 255, 0, 255)
								.setUv(FACE_VERTICES[i][v][3], FACE_VERTICES[i][v][4])
								.setLight(LightTexture.FULL_BRIGHT)
								.setNormal(FACE_NORMALS[i][0], FACE_NORMALS[i][1], FACE_NORMALS[i][2]);
					}
				}
			}
			matrices.popPose();
		}
		consumers.endBatch();
	}

	private static void renderFace(VertexConsumer vc, PoseStack matrix, int index, int x, int y, int z) {
		Matrix4f mat = matrix.last().pose();
		x *= BorderLength;
		y *= BorderHeight;
		z *= BorderLength;
		y += 7;
		for (int i = 0; i < 4; i++) {
			vc.addVertex(mat, FACE_VERTICES[index][i][0] + x, FACE_VERTICES[index][i][1] + y, FACE_VERTICES[index][i][2] + z)
					.setColor(255, 255, 255, 255)
					.setNormal(matrix.last(), FACE_NORMALS[index][0], FACE_NORMALS[index][1], FACE_NORMALS[index][2])
					.setLight(LightTexture.FULL_BRIGHT)
					.setUv(FACE_VERTICES[index][i][3], FACE_VERTICES[index][i][4]);
		}
	}
}
