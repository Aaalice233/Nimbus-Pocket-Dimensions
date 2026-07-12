package net.nimbu.pocketdimensions.item.custom;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.nimbu.pocketdimensions.dimensions.DimensionalInstancer;
import net.nimbu.pocketdimensions.entity.ModEntities;
import net.nimbu.pocketdimensions.entity.custom.GatewayProjectileEntity;

public class GatewayWandItem extends Item {
	public GatewayWandItem(Properties settings) {
		super(settings);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		ItemStack itemStack = user.getItemInHand(hand);

		world.playSound(null, user.getX(), user.getY(), user.getZ(),
				SoundEvents.SHULKER_SHOOT,
				SoundSource.NEUTRAL,
				0.7f,
				0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));

		if (!world.isClientSide && world.getServer() != null) {
			ServerLevel targetDimension = DimensionalInstancer.createInstance(world.getServer(), user.getUUID());
			if (targetDimension != null) {
				GatewayProjectileEntity spellPortal = new GatewayProjectileEntity(ModEntities.SPELL_PORTAL.get(), world);
				spellPortal.setPos(new Vec3(user.getX(), user.getY() + 1.5, user.getZ()));
				spellPortal.shootFromRotation(user, user.getXRot() - 30, user.getYRot(), 0.0f, 0.45f, 0f);
				spellPortal.setExitDimension(targetDimension.dimension());
				spellPortal.setOwner(user);
				world.addFreshEntity(spellPortal);
			}
		}

		return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide);
	}
}
