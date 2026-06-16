package com.custom.castlefight.custom_castlefight.blocks.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public class BuildingBlockEntity extends BlockEntity {
    private BlockState VisualState = Blocks.STONE.getDefaultState();
    private boolean center = false;
    public static Identifier ID = Identifier.of(MOD_ID,"building_block_entity");
    public void setVisualState(BlockState visualState) {
        VisualState = visualState;
        markDirty();
    }

    public void setCenter(boolean center) {
        this.center = center;
        markDirty();
    }

    public boolean isCenter() {
        return center;
    }

    public BlockState getVisualState() {
        return VisualState;
    }
    public BuildingBlockEntity( BlockPos pos, BlockState state) {
        super(CastlefightBlockEntities.BUILDING_BLOCK_TYPE, pos, state);
    }


}
