package net.nimbu.pocketdimensions.block.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.nimbu.pocketdimensions.block.entity.ModBlockEntityTypes;
import net.nimbu.pocketdimensions.worldgen.dimension.ModDimensions;
import org.jetbrains.annotations.Nullable;

public class PocketDimensionCustomizerBlockEntity extends BlockEntity {
	private final NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
	public float rotation = 0;
	public int ticks = 0;

	public PocketDimensionCustomizerBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntityTypes.POCKET_DIMENSION_CUSTOMIZER_BLOCK_ENTITY.get(), pos, state);
	}

	public static void tick(Level world, BlockPos blockPos, BlockState blockState, PocketDimensionCustomizerBlockEntity blockEntity) {
		if (world.dimensionTypeRegistration().is(ModDimensions.POCKET_DIM_TYPE)) {
			blockEntity.ticks++;
			blockEntity.rotation += 0.3f;
			if (blockEntity.rotation >= 360) {
				blockEntity.rotation = 0;
			}
		}
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
		super.saveAdditional(nbt, registryLookup);
		ContainerHelper.saveAllItems(nbt, inventory, registryLookup);
	}

	@Override
	protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
		super.loadAdditional(nbt, registryLookup);
		ContainerHelper.loadAllItems(nbt, inventory, registryLookup);
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
		return saveWithoutMetadata(registryLookup);
	}
}
