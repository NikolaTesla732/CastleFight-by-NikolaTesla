package com.custom.castlefight.custom_castlefight.blocks;

import com.custom.castlefight.custom_castlefight.Custom_castlefight;
import com.custom.castlefight.custom_castlefight.blocks.blockentity.BuildingBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public class BuildingBlock extends Block implements BlockEntityProvider {
    public BuildingBlock(Settings settings) {
        super(settings);
    }
    private static final Identifier ID = Identifier.of(MOD_ID,"building_block");
    public static final RegistryKey<Block> KEY = RegistryKey.of(RegistryKeys.BLOCK,ID);
    public static final Block BUILDING_BLOCK = Blocks.register(KEY,
            BuildingBlock::new,
            Settings.create().nonOpaque()
                    .blockVision((state, world, pos) -> false)
                    .solidBlock((state, world, pos) ->true)
                    .suffocates((state, world, pos) -> false)
    );

    @Override
    protected boolean isTransparent(BlockState state) {
        return true;
    }

    @Override
    protected VoxelShape getCullingShape(BlockState state) {
        return VoxelShapes.empty();
    }

    @Override
    protected int getOpacity(BlockState state) {
        return 0;
    }

    @Override
    protected VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BuildingBlockEntity buildingEntity){
            BlockState buildingEntityVisualState = buildingEntity.getVisualState();
            if (!buildingEntityVisualState.isOf(BuildingBlock.BUILDING_BLOCK)) {
                return buildingEntityVisualState.getCameraCollisionShape(world, pos, context);
            }
        }
        return VoxelShapes.fullCube();
    }

    @Override
    protected float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof BuildingBlockEntity buildingEntity){
            BlockState buildingEntityVisualState = buildingEntity.getVisualState();
            if (!buildingEntityVisualState.isOf(BuildingBlock.BUILDING_BLOCK)) {
                return buildingEntityVisualState.getOutlineShape(world, pos, context);
            }
        }
        return VoxelShapes.fullCube();
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return super.onUse(state, world, pos, player, hit);
    }

    public static void register(){

    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BuildingBlockEntity(pos,state);
    }

}
