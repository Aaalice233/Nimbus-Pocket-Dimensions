package net.nimbu.pocketdimensions;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.nimbu.pocketdimensions.block.ModBlocks;
import net.nimbu.pocketdimensions.block.entity.ModBlockEntityTypes;
import net.nimbu.pocketdimensions.component.ModAttachments;
import net.nimbu.pocketdimensions.entity.ModEntities;
import net.nimbu.pocketdimensions.item.ModItemGroups;
import net.nimbu.pocketdimensions.item.ModItems;
import net.nimbu.pocketdimensions.network.ModNetworking;
import net.nimbu.pocketdimensions.particle.ModParticleTypes;
import net.nimbu.pocketdimensions.screen.ModScreenHandlers;
import net.nimbu.pocketdimensions.sound.ModSoundEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(PocketDimensions.MOD_ID)
public class PocketDimensions {
	public static final String MOD_ID = "pocketdimensions";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public PocketDimensions(IEventBus modBus) {
		ModBlocks.BLOCKS.register(modBus);
		ModItems.ITEMS.register(modBus);
		ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modBus);
		ModEntities.ENTITY_TYPES.register(modBus);
		ModSoundEvents.SOUND_EVENTS.register(modBus);
		ModParticleTypes.PARTICLE_TYPES.register(modBus);
		ModScreenHandlers.MENU_TYPES.register(modBus);
		ModItemGroups.CREATIVE_MODE_TABS.register(modBus);
		ModAttachments.ATTACHMENT_TYPES.register(modBus);

		modBus.addListener(this::commonSetup);
		modBus.addListener(ModNetworking::register);

		NeoForge.EVENT_BUS.register(ModGameEvents.class);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		LOGGER.info("Pocket Dimensions common setup");
	}
}
