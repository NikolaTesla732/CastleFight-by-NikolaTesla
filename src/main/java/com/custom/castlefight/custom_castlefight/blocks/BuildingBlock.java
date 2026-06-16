package com.custom.castlefight.custom_castlefight.blocks;

import com.custom.castlefight.custom_castlefight.Custom_castlefight;
import com.custom.castlefight.custom_castlefight.blocks.blockentity.BuildingBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
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
            Settings.create()
    );

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
