package com.custom.castlefight.custom_castlefight.blocks.blockentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public class BuildingBlockEntity extends BlockEntity {
    private BlockState visualState = Blocks.STONE.getDefaultState();
    private boolean center = false;
    public static Identifier ID = Identifier.of(MOD_ID,"building_block_entity");
    public void setVisualState(BlockState visualState) {
        this.visualState = visualState;
        markDirty();
        if (this.getWorld() != null && !this.getWorld().isClient()){
            this.getWorld().updateListeners(this.getPos(),this.getCachedState(),this.getVisualState(),Block.NOTIFY_ALL);
        }
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        int stateRawId = Block.getRawIdFromState(this.getVisualState());
        view.putInt("visualState",stateRawId);
    }

    @Override
    public @Nullable Object getRenderData() {
        return this.getVisualState();
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return this.createNbt(registries);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        int stateRawId = view.getInt("visualState",Block.getRawIdFromState(Blocks.AIR.getDefaultState()));
        this.setVisualState(Block.getStateFromRawId(stateRawId));
    }

    public void setCenter(boolean center) {
        this.center = center;
        markDirty();
    }

    public boolean isCenter() {
        return center;
    }

    public BlockState getVisualState() {
        return visualState;
    }
    public BuildingBlockEntity( BlockPos pos, BlockState state) {
        super(CastlefightBlockEntities.BUILDING_BLOCK_TYPE, pos, state);
    }


}
