package net.nimbu.pocketdimensions.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nimbu.pocketdimensions.PocketDimensions;
import net.nimbu.pocketdimensions.block.ModBlocks;

public final class ModItemGroups {
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
			DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PocketDimensions.MOD_ID);

	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> POCKET_DIMENSIONS_ITEM_GROUP =
			CREATIVE_MODE_TABS.register("pocketdimensions_items", () -> CreativeModeTab.builder()
					.icon(() -> new ItemStack(ModItems.GATEWAY_WAND.get()))
					.title(Component.translatable("itemgroup.pocketdimensions.pocketdimensions_items"))
					.displayItems((params, output) -> {
						output.accept(ModItems.GATEWAY_WAND.get());
						output.accept(ModItems.EXPANSION_GEM.get());
						output.accept(ModBlocks.DIMENSION_CUSTOMIZER.get());
					})
					.build());

	private ModItemGroups() {}
}
