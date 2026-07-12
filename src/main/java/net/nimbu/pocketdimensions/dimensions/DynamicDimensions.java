package net.nimbu.pocketdimensions.dimensions;

import com.google.common.collect.ImmutableList;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.nimbu.pocketdimensions.PocketDimensions;
import net.nimbu.pocketdimensions.worldgen.biome.ModBiomes;
import net.nimbu.pocketdimensions.worldgen.dimension.ModDimensions;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * DimLib-free dynamic dimension create/get for NeoForge 1.21.1.
 * Mirrors {@code MinecraftServer#createLevels} for non-overworld stems via reflection on private fields.
 */
public final class DynamicDimensions {
	private static final ChunkProgressListener NOOP_LISTENER = new ChunkProgressListener() {
		@Override public void updateSpawnPos(ChunkPos pos) {}
		@Override public void onStatusChange(ChunkPos pos, @Nullable ChunkStatus status) {}
		@Override public void start() {}
		@Override public void stop() {}
	};

	private DynamicDimensions() {}

	public static ResourceKey<Level> levelKey(ResourceLocation id) {
		return ResourceKey.create(Registries.DIMENSION, id);
	}

	public static ResourceKey<LevelStem> stemKey(ResourceLocation id) {
		return ResourceKey.create(Registries.LEVEL_STEM, id);
	}

	@Nullable
	public static ServerLevel getOrCreate(MinecraftServer server, ResourceLocation instanceId) {
		ResourceKey<Level> key = levelKey(instanceId);
		ServerLevel existing = server.getLevel(key);
		if (existing != null) {
			return existing;
		}

		try {
			Holder<DimensionType> typeEntry = server.registryAccess()
					.registryOrThrow(Registries.DIMENSION_TYPE)
					.getHolderOrThrow(ModDimensions.POCKET_DIM_TYPE);

			Holder<Biome> biome = server.registryAccess()
					.registryOrThrow(Registries.BIOME)
					.getHolderOrThrow(ModBiomes.POCKET_DIM_BIOME);

			LevelStem stem = new LevelStem(typeEntry, SolidGenerator.create(biome));
			return createAndRegisterLevel(server, key, stem);
		} catch (Exception e) {
			PocketDimensions.LOGGER.error("Failed to create dynamic dimension {}", instanceId, e);
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private static ServerLevel createAndRegisterLevel(MinecraftServer server, ResourceKey<Level> levelKey, LevelStem stem) throws Exception {
		// Register LevelStem into the server's LEVEL_STEM registry when missing
		Registry<LevelStem> stems = server.registryAccess().registryOrThrow(Registries.LEVEL_STEM);
		ResourceKey<LevelStem> stemKey = ResourceKey.create(Registries.LEVEL_STEM, levelKey.location());
		if (!stems.containsKey(stemKey)) {
			unfreezeRegistry(stems);
			if (stems instanceof MappedRegistry<LevelStem> mapped) {
				mapped.register(stemKey, stem, RegistrationInfo.BUILT_IN);
			} else {
				Method register = stems.getClass().getMethod("register", ResourceKey.class, Object.class, RegistrationInfo.class);
				register.invoke(stems, stemKey, stem, RegistrationInfo.BUILT_IN);
			}
			freezeRegistry(stems);
		} else {
			LevelStem registered = stems.get(stemKey);
			if (registered != null) {
				stem = registered;
			}
		}

		WorldData worldData = server.getWorldData();
		ServerLevelData overworldData = worldData.overworldData();
		DerivedLevelData derived = new DerivedLevelData(worldData, overworldData);
		boolean debug = worldData.isDebugWorld();
		long seed = worldData.worldGenOptions().seed();
		long biomeZoomSeed = BiomeManager.obfuscateSeed(seed);

		Executor executor = requireField(server, "executor", Executor.class);
		LevelStorageSource.LevelStorageAccess storageSource =
				requireField(server, "storageSource", LevelStorageSource.LevelStorageAccess.class);
		Map<ResourceKey<Level>, ServerLevel> levels = requireField(server, "levels", Map.class);

		RandomSequences sequences = server.overworld().getRandomSequences();

		// Vanilla non-overworld ctor (see MinecraftServer#createLevels)
		ServerLevel level = new ServerLevel(
				server,
				executor,
				storageSource,
				derived,
				levelKey,
				stem,
				NOOP_LISTENER,
				debug,
				biomeZoomSeed,
				ImmutableList.of(),
				false,
				sequences
		);

		levels.put(levelKey, level);

		try {
			server.overworld().getWorldBorder().addListener(
					new BorderChangeListener.DelegateBorderChangeListener(level.getWorldBorder()));
		} catch (Exception ignored) {
		}

		NeoForge.EVENT_BUS.post(new LevelEvent.Load(level));
		PocketDimensions.LOGGER.info("Created dynamic dimension {}", levelKey.location());
		return level;
	}

	private static void unfreezeRegistry(Registry<?> registry) throws Exception {
		Field frozen = findField(registry.getClass(), "frozen");
		if (frozen != null) {
			frozen.setAccessible(true);
			frozen.setBoolean(registry, false);
		}
	}

	private static void freezeRegistry(Registry<?> registry) throws Exception {
		try {
			Method freeze = registry.getClass().getMethod("freeze");
			freeze.invoke(registry);
		} catch (NoSuchMethodException e) {
			Field frozen = findField(registry.getClass(), "frozen");
			if (frozen != null) {
				frozen.setAccessible(true);
				frozen.setBoolean(registry, true);
			}
		}
	}

	@Nullable
	private static Field findField(Class<?> type, String name) {
		Class<?> c = type;
		while (c != null) {
			try {
				return c.getDeclaredField(name);
			} catch (NoSuchFieldException ignored) {
				c = c.getSuperclass();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static <T> T requireField(Object instance, String name, Class<T> type) throws Exception {
		Field f = findField(instance.getClass(), name);
		if (f == null) {
			throw new IllegalStateException("Missing field " + name + " on " + instance.getClass().getName());
		}
		f.setAccessible(true);
		Object val = f.get(instance);
		if (val == null || !type.isInstance(val)) {
			// Map is raw-typed; allow any Map when type is Map
			if (type == Map.class && val instanceof Map) {
				return (T) val;
			}
			throw new IllegalStateException("Field " + name + " not of type " + type.getName());
		}
		return type.cast(val);
	}
}
