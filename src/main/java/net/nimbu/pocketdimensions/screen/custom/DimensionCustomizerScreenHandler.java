package net.nimbu.pocketdimensions.screen.custom;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.nimbu.pocketdimensions.network.ClientPocketDimensionPersistentState;
import net.nimbu.pocketdimensions.screen.ModScreenHandlers;
import net.nimbu.pocketdimensions.worldgen.biome.DynamicBiomeEffects;

public class DimensionCustomizerScreenHandler extends AbstractContainerMenu {
	public DimensionCustomizerScreenHandler(int syncId, Inventory inventory) {
		super(ModScreenHandlers.POCKET_DIM_BIOME_SCREEN_HANDLER.get(), syncId);
	}

	public int[] getSkyColour() {
		DynamicBiomeEffects fx = ClientPocketDimensionPersistentState.getDynamicBiomeEffects();
		return new int[]{(fx.getSkyColor() >> 16) & 0xFF, (fx.getSkyColor() >> 8) & 0xFF, fx.getSkyColor() & 0xFF};
	}

	public int[] getFoliageColour() {
		DynamicBiomeEffects fx = ClientPocketDimensionPersistentState.getDynamicBiomeEffects();
		int c = fx.getFoliageColor().orElse(0x77AB2F);
		return new int[]{(c >> 16) & 0xFF, (c >> 8) & 0xFF, c & 0xFF};
	}

	public int[] getWaterFogColour() {
		DynamicBiomeEffects fx = ClientPocketDimensionPersistentState.getDynamicBiomeEffects();
		return new int[]{(fx.getWaterFogColor() >> 16) & 0xFF, (fx.getWaterFogColor() >> 8) & 0xFF, fx.getWaterFogColor() & 0xFF};
	}

	public int[] getWaterColour() {
		DynamicBiomeEffects fx = ClientPocketDimensionPersistentState.getDynamicBiomeEffects();
		return new int[]{(fx.getWaterColor() >> 16) & 0xFF, (fx.getWaterColor() >> 8) & 0xFF, fx.getWaterColor() & 0xFF};
	}

	public int[] getGrassColour() {
		DynamicBiomeEffects fx = ClientPocketDimensionPersistentState.getDynamicBiomeEffects();
		int c = fx.getGrassColor().orElse(0x91BD59);
		return new int[]{(c >> 16) & 0xFF, (c >> 8) & 0xFF, c & 0xFF};
	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player player, int slot) {
		return ItemStack.EMPTY;
	}
}
