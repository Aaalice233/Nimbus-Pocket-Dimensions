package net.nimbu.pocketdimensions.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.ChatFormatting;
import net.nimbu.pocketdimensions.PocketDimensions;
import net.nimbu.pocketdimensions.network.ClientPocketDimensionPersistentState;
import net.nimbu.pocketdimensions.renderer.PocketDimensionBorderRenderer;

import java.util.List;

import static net.nimbu.pocketdimensions.renderer.PocketDimensionBorderRenderer.BorderHeight;
import static net.nimbu.pocketdimensions.renderer.PocketDimensionBorderRenderer.BorderLength;

public class DimensionExpanderItem extends Item {
	private int raycastCooldown = 0;

	public DimensionExpanderItem(Properties settings) {
		super(settings);
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(stack, world, entity, slot, selected);

		if (world.isClientSide) {
			if (!(entity instanceof Player player)) return;

			if (player.getMainHandItem() == stack || player.getOffhandItem() == stack) {
				PocketDimensionBorderRenderer.expansionModeActive = true;
				renderReticle(player);
			} else {
				cancelRenderReticle();
			}
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
		ItemStack item = user.getItemInHand(hand);

		if (!world.isClientSide) {
			BlockPos pos = raycastRoomFace(user);
			PocketDimensions.LOGGER.info("Tried to expand pocket dimension at position {}", pos);
			if (pos != null) {
				if (ClientPocketDimensionPersistentState.hasAdjacents(pos)) {
					if (pos.getY() > -1 && pos.getY() < 30) {
						ClientPocketDimensionPersistentState.addRoom((ServerLevel) world, pos);
						BlockState state = Blocks.AIR.defaultBlockState();
						for (int x = 0; x < BorderLength; x++) {
							for (int y = 0; y < BorderHeight; y++) {
								for (int z = 0; z < BorderLength; z++) {
									world.setBlock(new BlockPos(
													x + BorderLength * pos.getX(),
													y + BorderHeight * pos.getY() + 7,
													z + BorderLength * pos.getZ()),
											state, 3);
								}
							}
						}
					}
				}
			}
			if (!user.getAbilities().instabuild) {
				item.shrink(1);
			}
			if (item.isEmpty()) {
				PocketDimensionBorderRenderer.expansionModeActive = false;
			}
			world.playSound(null, user.getX(), user.getY(), user.getZ(),
					SoundEvents.BEACON_ACTIVATE,
					SoundSource.NEUTRAL,
					1f,
					1.5f);
		}

		return InteractionResultHolder.sidedSuccess(item, world.isClientSide);
	}

	public void renderReticle(Player user) {
		if (raycastCooldown-- <= 0) {
			BlockPos hit = raycastRoomFace(user);
			if (hit != null) {
				PocketDimensionBorderRenderer.expansionValid =
						!(hit.getY() < 0 || hit.getY() > 29 || !ClientPocketDimensionPersistentState.hasAdjacents(hit));
				PocketDimensionBorderRenderer.expansionModePosition = hit;
				raycastCooldown = 4;
			}
		}
		PocketDimensionBorderRenderer.expansionModeActive = true;
	}

	public void cancelRenderReticle() {
		PocketDimensionBorderRenderer.expansionValid = false;
		PocketDimensionBorderRenderer.expansionModeActive = false;
	}

	private BlockPos raycastRoomFace(Player user) {
		Vec3 camWorld = user.getEyePosition(1.0f);
		Vec3 dirWorld = user.getViewVector(1.0f).normalize();

		Vec3 cam = new Vec3(
				camWorld.x / BorderLength,
				(camWorld.y - 7) / BorderHeight,
				camWorld.z / BorderLength
		);

		Vec3 dir = new Vec3(
				dirWorld.x / BorderLength,
				dirWorld.y / BorderHeight,
				dirWorld.z / BorderLength
		).normalize();

		int stepX = dir.x > 0 ? 1 : -1;
		int stepY = dir.y > 0 ? 1 : -1;
		int stepZ = dir.z > 0 ? 1 : -1;

		double maxDistance = 4;
		double currentDistance = 0;
		Vec3 currentRayPos = cam.add(0, 0, 0);

		BlockPos outPos = null;
		do {
			double xRemaining = Math.abs(stepX * (1 - fract(cam.x)));
			double yRemaining = Math.abs(stepY * (1 - fract(cam.y)));
			double zRemaining = Math.abs(stepZ * (1 - fract(cam.z)));

			double tDeltaX = dir.x == 0 ? Double.POSITIVE_INFINITY : Math.abs(xRemaining / dir.x);
			double tDeltaY = dir.y == 0 ? Double.POSITIVE_INFINITY : Math.abs(yRemaining / dir.y);
			double tDeltaZ = dir.z == 0 ? Double.POSITIVE_INFINITY : Math.abs(zRemaining / dir.z);

			double dist = Math.min(Math.min(tDeltaX, tDeltaY), tDeltaZ);
			currentRayPos = currentRayPos.add(dir.scale(dist));
			currentDistance += dist;
			outPos = new BlockPos(
					(int) Math.round(currentRayPos.x - 0.5),
					(int) Math.round(currentRayPos.y - 0.5),
					(int) Math.round(currentRayPos.z - 0.5));
		} while (currentDistance < maxDistance && ClientPocketDimensionPersistentState.hasRoom(outPos));
		return !ClientPocketDimensionPersistentState.hasRoom(outPos) ? outPos : null;
	}

	private double fract(double k) {
		return k - Math.floor(k);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
		tooltip.add(Component.literal("Expands the size of a pocket dimension").withStyle(ChatFormatting.GRAY));
	}
}
