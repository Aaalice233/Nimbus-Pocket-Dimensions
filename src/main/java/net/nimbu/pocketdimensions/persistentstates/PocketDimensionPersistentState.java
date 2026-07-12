package net.nimbu.pocketdimensions.persistentstates;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.saveddata.SavedData;
import net.nimbu.pocketdimensions.worldgen.biome.DynamicBiomeEffects;

import java.util.HashSet;
import java.util.Set;

public class PocketDimensionPersistentState extends SavedData {
	private final HashSet<BlockPos> unlockedRooms = new HashSet<>();
	private DynamicBiomeEffects dynamicBiomeEffects =
			new DynamicBiomeEffects.Builder()
					.skyColor(0x78A6FF)
					.foliageColor(0x77AB2F)
					.grassColor(0x91BD59)
					.waterColor(0x3F76E4)
					.waterFogColor(0x050533)
					.music(Musics.createGameMusic(SoundEvents.MUSIC_CREATIVE))
					.fogColor(0xA0C8FF)
					.build();
	private ResourceLocation skybox = ResourceLocation.withDefaultNamespace("textures/environment/end_sky.png");

	public void addRoom(BlockPos pos) {
		unlockedRooms.add(pos.immutable());
		setDirty();
	}

	public boolean isRoomUnlocked(BlockPos pos) {
		return unlockedRooms.contains(pos);
	}

	public Set<BlockPos> getUnlockedRooms() {
		return Set.copyOf(unlockedRooms);
	}

	public DynamicBiomeEffects getDynamicBiomeEffects() {
		return dynamicBiomeEffects;
	}

	public void setDynamicBiomeEffects(DynamicBiomeEffects newBiomeEffects) {
		dynamicBiomeEffects = newBiomeEffects;
		setDirty();
	}

	public ResourceLocation getSkybox() {
		return skybox;
	}

	@Override
	public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registryLookup) {
		int i = 0;
		CompoundTag rooms = new CompoundTag();
		for (BlockPos pos : unlockedRooms) {
			rooms.putIntArray("r" + i++, new int[]{pos.getX(), pos.getY(), pos.getZ()});
		}
		nbt.put("Rooms", rooms);
		nbt.put("DimensionEffects", DynamicBiomeEffects.CODEC
				.encodeStart(NbtOps.INSTANCE, dynamicBiomeEffects)
				.getOrThrow());
		nbt.putString("Skybox", skybox.toString());
		return nbt;
	}

	public static PocketDimensionPersistentState load(CompoundTag nbt, HolderLookup.Provider registryLookup) {
		PocketDimensionPersistentState state = new PocketDimensionPersistentState();
		CompoundTag rooms = nbt.getCompound("Rooms");
		for (String key : rooms.getAllKeys()) {
			int[] arr = rooms.getIntArray(key);
			if (arr.length == 3) state.unlockedRooms.add(new BlockPos(arr[0], arr[1], arr[2]));
		}
		if (nbt.contains("DimensionEffects")) {
			state.dynamicBiomeEffects = DynamicBiomeEffects.CODEC
					.parse(NbtOps.INSTANCE, nbt.get("DimensionEffects"))
					.getOrThrow();
		}
		if (nbt.contains("Skybox")) {
			String raw = nbt.getString("Skybox");
			// tolerate legacy path-only values
			state.skybox = raw.contains(":")
					? ResourceLocation.parse(raw)
					: ResourceLocation.withDefaultNamespace(raw);
		}
		return state;
	}

	public static final Factory<PocketDimensionPersistentState> FACTORY =
			new Factory<>(
					PocketDimensionPersistentState::new,
					PocketDimensionPersistentState::load,
					null
			);

	public static PocketDimensionPersistentState get(ServerLevel world) {
		return world.getDataStorage().computeIfAbsent(FACTORY, "pocket_dimension_state");
	}
}
