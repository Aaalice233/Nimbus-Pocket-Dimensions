package net.nimbu.pocketdimensions.dimensions;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Drives shipped {@link DimensionalInstancer#pocketDimensionPath(UUID)} (pure path segment used by dimensionIdFor).
 */
class DimensionalInstancerIdTest {

	private static final UUID FIXED_OWNER = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

	@Test
	void pocketDimensionPathUsesOwnerUuid() {
		assertEquals(
				"pocket_dimension_123e4567-e89b-12d3-a456-426614174000",
				DimensionalInstancer.pocketDimensionPath(FIXED_OWNER)
		);
	}

	@Test
	void pocketDimensionPathIsStableForSameUuid() {
		String a = DimensionalInstancer.pocketDimensionPath(FIXED_OWNER);
		String b = DimensionalInstancer.pocketDimensionPath(FIXED_OWNER);
		assertEquals(a, b);
		assertEquals("pocket_dimension_" + FIXED_OWNER, a);
	}
}
