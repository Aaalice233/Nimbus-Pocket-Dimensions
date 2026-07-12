package net.nimbu.pocketdimensions.component;

import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Pure persistence snapshot for player gateway state (no Minecraft types).
 * {@link PlayerGatewayData} converts to/from this for NBT; unit tests drive this real model.
 */
public final class PlayerGatewaySnapshot {
	public static final String KEY_X = "GatewayX";
	public static final String KEY_Y = "GatewayY";
	public static final String KEY_Z = "GatewayZ";
	public static final String KEY_DIM = "GatewayDim";
	public static final String KEY_MATERIAL = "GatewayMaterial";

	@Nullable
	private Integer x;
	@Nullable
	private Integer y;
	@Nullable
	private Integer z;
	@Nullable
	private String dimId;
	private int material = 6;

	public PlayerGatewaySnapshot() {}

	public PlayerGatewaySnapshot(@Nullable Integer x, @Nullable Integer y, @Nullable Integer z,
			@Nullable String dimId, int material) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.dimId = dimId;
		this.material = material;
	}

	@Nullable public Integer x() { return x; }
	@Nullable public Integer y() { return y; }
	@Nullable public Integer z() { return z; }
	@Nullable public String dimId() { return dimId; }
	public int material() { return material; }

	public void setPos(@Nullable Integer x, @Nullable Integer y, @Nullable Integer z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setDimId(@Nullable String dimId) {
		this.dimId = dimId;
	}

	public void setMaterial(int material) {
		this.material = material;
	}

	public boolean hasPos() {
		return x != null && y != null && z != null;
	}

	/** Encode to a plain map (same keys as NBT). */
	public Map<String, Object> toMap() {
		Map<String, Object> map = new LinkedHashMap<>();
		if (hasPos()) {
			map.put(KEY_X, x);
			map.put(KEY_Y, y);
			map.put(KEY_Z, z);
		}
		if (dimId != null) {
			map.put(KEY_DIM, dimId);
		}
		map.put(KEY_MATERIAL, material);
		return map;
	}

	/** Decode from a plain map (same keys as NBT). */
	public static PlayerGatewaySnapshot fromMap(Map<String, ?> map) {
		PlayerGatewaySnapshot s = new PlayerGatewaySnapshot();
		if (map.containsKey(KEY_X) && map.containsKey(KEY_Y) && map.containsKey(KEY_Z)) {
			s.x = ((Number) map.get(KEY_X)).intValue();
			s.y = ((Number) map.get(KEY_Y)).intValue();
			s.z = ((Number) map.get(KEY_Z)).intValue();
		}
		Object dim = map.get(KEY_DIM);
		if (dim instanceof String str && !str.isEmpty()) {
			s.dimId = str;
		}
		Object mat = map.get(KEY_MATERIAL);
		if (mat instanceof Number n) {
			s.material = n.intValue();
		}
		return s;
	}

	/** Round-trip through the same map encoding used for NBT key layout. */
	public PlayerGatewaySnapshot roundTrip() {
		return fromMap(toMap());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PlayerGatewaySnapshot that)) return false;
		return material == that.material
				&& Objects.equals(x, that.x)
				&& Objects.equals(y, that.y)
				&& Objects.equals(z, that.z)
				&& Objects.equals(dimId, that.dimId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, z, dimId, material);
	}
}
