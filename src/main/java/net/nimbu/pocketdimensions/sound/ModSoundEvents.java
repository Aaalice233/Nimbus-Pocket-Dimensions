package net.nimbu.pocketdimensions.sound;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.nimbu.pocketdimensions.PocketDimensions;

public final class ModSoundEvents {
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
			DeferredRegister.create(Registries.SOUND_EVENT, PocketDimensions.MOD_ID);

	public static final DeferredHolder<SoundEvent, SoundEvent> MAGIC_CHARGE =
			SOUND_EVENTS.register("magic_charge", () -> SoundEvent.createVariableRangeEvent(
					ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "magic_charge")));

	public static final DeferredHolder<SoundEvent, SoundEvent> MAGIC_CHIME =
			SOUND_EVENTS.register("magic_chime", () -> SoundEvent.createVariableRangeEvent(
					ResourceLocation.fromNamespaceAndPath(PocketDimensions.MOD_ID, "magic_chime")));

	private ModSoundEvents() {}
}
