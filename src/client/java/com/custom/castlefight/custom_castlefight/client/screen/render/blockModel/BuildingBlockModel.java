package com.custom.castlefight.custom_castlefight.client.screen.render.blockModel;


import com.custom.castlefight.custom_castlefight.blocks.BuildingBlock;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBlockStateModel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class BuildingBlockModel implements BlockStateModel, FabricBlockStateModel{
    @Override
    public void emitQuads(QuadEmitter emitter, BlockRenderView blockView, BlockPos pos, BlockState state, Random random, Predicate<@Nullable Direction> cullTest) {
        var attachment = blockView.getBlockEntityRenderData(pos);

        if (attachment instanceof BlockState blockState){
            if (blockState.isOf(BuildingBlock.BUILDING_BLOCK)) return;
            FabricBlockStateModel visualState = (FabricBlockStateModel) MinecraftClient.getInstance().getBlockRenderManager().getModel(blockState);
            visualState.emitQuads(emitter,blockView,pos,blockState,random,cullTest);
        }

    }

    @Override
    public void addParts(Random random, List<BlockModelPart> parts) {

    }

    @Override
    public Sprite particleSprite() {
        return MinecraftClient.getInstance().getBlockRenderManager().getModel(Blocks.STONE.getDefaultState()).particleSprite();
    }
}
