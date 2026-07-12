package net.nimbu.pocketdimensions.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.AmbientParticleSettings;
import net.nimbu.pocketdimensions.PocketDimensions;
import net.nimbu.pocketdimensions.persistentstates.PocketDimensionPersistentState;
import net.nimbu.pocketdimensions.worldgen.biome.DynamicBiomeEffects;

import java.util.HashSet;
import java.util.Set;

public final class ClientPocketDimensionPersistentState {
	private static final Set<BlockPos> ROOMS = new HashSet<>();
	private static boolean isClientInPocketDimension = false;
	private static DynamicBiomeEffects dynamicBiomeBiomeEffects =
			new DynamicBiomeEffects.Builder()
					.skyColor(0xFF00FF)
					.foliageColor(0xFF00FF)
					.additionsSound(new AmbientAdditionsSettings(SoundEvents.AMBIENT_WARPED_FOREST_ADDITIONS, 0.05))
					.particleConfig(new AmbientParticleSettings(ParticleTypes.END_ROD, 0.001f))
					.grassColor(0xFF00FF)
					.waterColor(0xFF00FF)
					.waterFogColor(0xFF00FF)
					.loopSound(SoundEvents.AMBIENT_WARPED_FOREST_LOOP)
					.moodSound(new AmbientMoodSettings(SoundEvents.AMBIENT_NETHER_WASTES_MOOD, 6000, 8, 2.0))
					.music(Musics.createGameMusic(SoundEvents.MUSIC_BIOME_NETHER_WASTES))
					.fogColor(0xFF00FF)
					.build();
	private static ResourceLocation skybox = ResourceLocation.withDefaultNamespace("textures/environment/end_sky.png");

	private ClientPocketDimensionPersistentState() {}

	public static void setRooms(Set<BlockPos> rooms) {
		ROOMS.clear();
		ROOMS.addAll(rooms);
		PocketDimensions.LOGGER.info("Client received {} rooms", rooms.size());
	}

	public static void updateDimensionBiome(DynamicBiomeEffects newEffects, ServerLevel world) {
		PocketDimensionPersistentState state = PocketDimensionPersistentState.get(world);
		state.setDynamicBiomeEffects(newEffects);
		for (ServerPlayer player : world.players()) {
			PocketDimensionSync.syncDynamicBiome(world, player);
		}
	}

	public static void addRoom(BlockPos room) {
		ROOMS.add(room);
		PocketDimensions.LOGGER.info("Client received room {}", room);
	}

	public static boolean hasRoom(BlockPos pos) {
		return ROOMS.contains(pos);
	}

	public static Set<BlockPos> getRooms() {
		return ROOMS;
	}

	public static void addRoom(ServerLevel world, BlockPos pos) {
		PocketDimensionPersistentState state = PocketDimensionPersistentState.get(world);
		if (state.isRoomUnlocked(pos)) return;
		state.addRoom(pos);
		for (ServerPlayer player : world.players()) {
			PocketDimensionSync.updateSingularRoom(player, pos);
		}
	}

	public static final int[][] neighbourPositions = new int[][]{
			{1, 0, 0},
			{-1, 0, 0},
			{0, 1, 0},
			{0, -1, 0},
			{0, 0, 1},
			{0, 0, -1},
	};

	public static boolean[] getAdjacents(BlockPos pos) {
		Set<BlockPos> state = getRooms();
		boolean[] dirs = new boolean[6];
		if (!state.contains(pos)) return dirs;
		for (int i = 0; i < 6; i++) {
			dirs[i] = !state.contains(pos.offset(neighbourPositions[i][0], neighbourPositions[i][1], neighbourPositions[i][2]));
		}
		dirs[2] &= pos.getY() <= 20;
		dirs[3] &= pos.getY() >= 0;
		return dirs;
	}

	public static boolean hasAdjacents(BlockPos pos) {
		Set<BlockPos> state = getRooms();
		for (int i = 0; i < 6; i++) {
			if (state.contains(pos.offset(neighbourPositions[i][0], neighbourPositions[i][1], neighbourPositions[i][2]))) {
				return true;
			}
		}
		return false;
	}

	public static ResourceLocation getSkybox() {
		return skybox;
	}

	public static void setSkybox(ResourceLocation skybox) {
		ClientPocketDimensionPersistentState.skybox = skybox;
	}

	public static DynamicBiomeEffects getDynamicBiomeEffects() {
		return dynamicBiomeBiomeEffects;
	}

	public static void setDynamicBiomeBiomeEffects(DynamicBiomeEffects dynamicBiomeBiomeEffects) {
		ClientPocketDimensionPersistentState.dynamicBiomeBiomeEffects = dynamicBiomeBiomeEffects;
	}

	public static boolean isClientInPocketDimension() {
		return isClientInPocketDimension;
	}

	public static void setIsClientInPocketDimension(boolean isClientInPocketDimension) {
		ClientPocketDimensionPersistentState.isClientInPocketDimension = isClientInPocketDimension;
	}
}
