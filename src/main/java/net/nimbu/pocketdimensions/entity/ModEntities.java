package net.nimbu.pocketdimensions.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nimbu.pocketdimensions.PocketDimensions;
import net.nimbu.pocketdimensions.entity.custom.GatewayProjectileEntity;

public final class ModEntities {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
			DeferredRegister.create(Registries.ENTITY_TYPE, PocketDimensions.MOD_ID);

	public static final DeferredHolder<EntityType<?>, EntityType<GatewayProjectileEntity>> SPELL_PORTAL =
			ENTITY_TYPES.register("spell_portal", () ->
					EntityType.Builder.<GatewayProjectileEntity>of(GatewayProjectileEntity::new, MobCategory.MISC)
							.sized(0.25f, 0.25f)
							.clientTrackingRange(4)
							.updateInterval(10)
							.build("spell_portal"));

	private ModEntities() {}
}
