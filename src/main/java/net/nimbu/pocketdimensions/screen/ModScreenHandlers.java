package net.nimbu.pocketdimensions.screen;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nimbu.pocketdimensions.PocketDimensions;
import net.nimbu.pocketdimensions.screen.custom.DimensionCustomizerScreenHandler;

public final class ModScreenHandlers {
	public static final DeferredRegister<MenuType<?>> MENU_TYPES =
			DeferredRegister.create(Registries.MENU, PocketDimensions.MOD_ID);

	public static final DeferredHolder<MenuType<?>, MenuType<DimensionCustomizerScreenHandler>> POCKET_DIM_BIOME_SCREEN_HANDLER =
			MENU_TYPES.register("pocket_dimension_screen_handler",
					() -> new MenuType<>(DimensionCustomizerScreenHandler::new, FeatureFlags.VANILLA_SET));

	private ModScreenHandlers() {}
}
