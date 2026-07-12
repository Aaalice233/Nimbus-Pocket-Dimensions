package net.nimbu.pocketdimensions.component;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Drives shipped {@link PlayerGatewaySnapshot} map encoding used by {@link PlayerGatewayData} NBT.
 */
class PlayerGatewaySnapshotTest {

	@Test
	void roundTripPreservesPosDimAndMaterial() {
		PlayerGatewaySnapshot original = new PlayerGatewaySnapshot(
				6, 148, 1,
				"pocketdimensions:pocket_dimension_test",
				8
		);

		PlayerGatewaySnapshot restored = original.roundTrip();

		assertEquals(original, restored);
		assertEquals(6, restored.x());
		assertEquals(148, restored.y());
		assertEquals(1, restored.z());
		assertEquals("pocketdimensions:pocket_dimension_test", restored.dimId());
		assertEquals(8, restored.material());
		assertTrue(restored.hasPos());
	}

	@Test
	void emptyPosRoundTripsWithDefaultMaterial() {
		PlayerGatewaySnapshot original = new PlayerGatewaySnapshot(null, null, null, null, 6);
		PlayerGatewaySnapshot restored = original.roundTrip();

		assertEquals(original, restored);
		assertFalse(restored.hasPos());
		assertNull(restored.dimId());
		assertEquals(6, restored.material());
	}

	@Test
	void toMapUsesStableNbtKeys() {
		PlayerGatewaySnapshot original = new PlayerGatewaySnapshot(1, 2, 3, "minecraft:overworld", 11);
		Map<String, Object> map = original.toMap();

		assertEquals(1, map.get(PlayerGatewaySnapshot.KEY_X));
		assertEquals(2, map.get(PlayerGatewaySnapshot.KEY_Y));
		assertEquals(3, map.get(PlayerGatewaySnapshot.KEY_Z));
		assertEquals("minecraft:overworld", map.get(PlayerGatewaySnapshot.KEY_DIM));
		assertEquals(11, map.get(PlayerGatewaySnapshot.KEY_MATERIAL));
	}
}
