package net.nimbu.pocketdimensions.component;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * @deprecated Use {@link PlayerGatewayData} via {@link ModAttachments#PLAYER_GATEWAY}.
 * Kept as a thin alias surface for any lingering call patterns.
 */
@Deprecated
public interface PlayerGatewayComponent {
	@Nullable
	BlockPos getGatewayPos();

	void setGatewayPos(@Nullable BlockPos pos);

	@Nullable
	ResourceKey<Level> getGatewayDim();

	void setGatewayDim(@Nullable ResourceKey<Level> dim);

	int getGatewayMaterial();

	void setGatewayMaterial(int material);
}
