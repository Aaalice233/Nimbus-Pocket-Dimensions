package net.nimbu.pocketdimensions.component;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * NeoForge attachment replacement for the Fabric CCA player gateway component.
 * Call sites: {@code PlayerGatewayData.get(player)} or {@code player.getData(ModAttachments.PLAYER_GATEWAY.get())}.
 */
public class PlayerGatewayData implements INBTSerializable<CompoundTag> {
	public static final StreamCodec<RegistryFriendlyByteBuf, PlayerGatewayData> STREAM_CODEC =
			StreamCodec.composite(
					ByteBufCodecs.optional(BlockPos.STREAM_CODEC),
					d -> java.util.Optional.ofNullable(d.pos),
					ByteBufCodecs.optional(ResourceLocation.STREAM_CODEC),
					d -> d.dim == null ? java.util.Optional.empty() : java.util.Optional.of(d.dim.location()),
					ByteBufCodecs.VAR_INT,
					d -> d.material,
					(posOpt, dimOpt, material) -> {
						PlayerGatewayData data = new PlayerGatewayData();
						data.pos = posOpt.orElse(null);
						data.dim = dimOpt.map(id -> ResourceKey.create(Registries.DIMENSION, id)).orElse(null);
						data.material = material;
						return data;
					}
			);

	@Nullable
	private BlockPos pos;
	@Nullable
	private ResourceKey<Level> dim;
	private int material = 6;

	public static PlayerGatewayData get(Player player) {
		return player.getData(ModAttachments.PLAYER_GATEWAY.get());
	}

	@Nullable
	public BlockPos getGatewayPos() {
		return pos;
	}

	public void setGatewayPos(@Nullable BlockPos pos) {
		this.pos = pos;
	}

	@Nullable
	public ResourceKey<Level> getGatewayDim() {
		return dim;
	}

	public void setGatewayDim(@Nullable ResourceKey<Level> dim) {
		this.dim = dim;
	}

	public int getGatewayMaterial() {
		return material;
	}

	public void setGatewayMaterial(int material) {
		this.material = material;
	}

	/** Expose pure snapshot used by NBT (and unit-tested via {@link PlayerGatewaySnapshot}). */
	public PlayerGatewaySnapshot toSnapshot() {
		if (pos != null) {
			return new PlayerGatewaySnapshot(
					pos.getX(), pos.getY(), pos.getZ(),
					dim != null ? dim.location().toString() : null,
					material
			);
		}
		return new PlayerGatewaySnapshot(null, null, null,
				dim != null ? dim.location().toString() : null,
				material);
	}

	public void fromSnapshot(PlayerGatewaySnapshot snapshot) {
		if (snapshot.hasPos()) {
			pos = new BlockPos(snapshot.x(), snapshot.y(), snapshot.z());
		} else {
			pos = null;
		}
		if (snapshot.dimId() != null && !snapshot.dimId().isEmpty()) {
			try {
				dim = ResourceKey.create(Registries.DIMENSION, ResourceLocation.parse(snapshot.dimId()));
			} catch (Exception e) {
				dim = null;
			}
		} else {
			dim = null;
		}
		material = snapshot.material();
	}

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag nbt = new CompoundTag();
		PlayerGatewaySnapshot snapshot = toSnapshot();
		Map<String, Object> map = snapshot.toMap();
		if (map.containsKey(PlayerGatewaySnapshot.KEY_X)) {
			nbt.putInt(PlayerGatewaySnapshot.KEY_X, ((Number) map.get(PlayerGatewaySnapshot.KEY_X)).intValue());
			nbt.putInt(PlayerGatewaySnapshot.KEY_Y, ((Number) map.get(PlayerGatewaySnapshot.KEY_Y)).intValue());
			nbt.putInt(PlayerGatewaySnapshot.KEY_Z, ((Number) map.get(PlayerGatewaySnapshot.KEY_Z)).intValue());
		}
		if (map.containsKey(PlayerGatewaySnapshot.KEY_DIM)) {
			nbt.putString(PlayerGatewaySnapshot.KEY_DIM, (String) map.get(PlayerGatewaySnapshot.KEY_DIM));
		}
		nbt.putInt(PlayerGatewaySnapshot.KEY_MATERIAL, ((Number) map.get(PlayerGatewaySnapshot.KEY_MATERIAL)).intValue());
		return nbt;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
		Map<String, Object> map = new java.util.LinkedHashMap<>();
		if (nbt.contains(PlayerGatewaySnapshot.KEY_X)) {
			map.put(PlayerGatewaySnapshot.KEY_X, nbt.getInt(PlayerGatewaySnapshot.KEY_X));
			map.put(PlayerGatewaySnapshot.KEY_Y, nbt.getInt(PlayerGatewaySnapshot.KEY_Y));
			map.put(PlayerGatewaySnapshot.KEY_Z, nbt.getInt(PlayerGatewaySnapshot.KEY_Z));
		}
		if (nbt.contains(PlayerGatewaySnapshot.KEY_DIM)) {
			map.put(PlayerGatewaySnapshot.KEY_DIM, nbt.getString(PlayerGatewaySnapshot.KEY_DIM));
		}
		if (nbt.contains(PlayerGatewaySnapshot.KEY_MATERIAL)) {
			map.put(PlayerGatewaySnapshot.KEY_MATERIAL, nbt.getInt(PlayerGatewaySnapshot.KEY_MATERIAL));
		}
		fromSnapshot(PlayerGatewaySnapshot.fromMap(map));
	}
}
