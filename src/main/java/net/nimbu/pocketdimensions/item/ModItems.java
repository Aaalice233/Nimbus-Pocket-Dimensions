package net.nimbu.pocketdimensions.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nimbu.pocketdimensions.PocketDimensions;
import net.nimbu.pocketdimensions.item.custom.DimensionExpanderItem;
import net.nimbu.pocketdimensions.item.custom.GatewayWandItem;

public final class ModItems {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PocketDimensions.MOD_ID);

	public static final DeferredItem<Item> GATEWAY_WAND = ITEMS.register("gateway_wand",
			() -> new GatewayWandItem(new Item.Properties()
					.stacksTo(1)
					.durability(131)
					.rarity(Rarity.EPIC)
					.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));

	public static final DeferredItem<Item> EXPANSION_GEM = ITEMS.register("expansion_gem",
			() -> new DimensionExpanderItem(new Item.Properties()));

	private ModItems() {}
}
