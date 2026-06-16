package com.custom.castlefight.custom_castlefight.blocks.blockentity;

import com.custom.castlefight.custom_castlefight.blocks.BuildingBlock;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;


public class CastlefightBlockEntities {
    public static BlockEntityType<BuildingBlockEntity> BUILDING_BLOCK_TYPE = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            BuildingBlockEntity.ID,
            FabricBlockEntityTypeBuilder.create(
                    BuildingBlockEntity::new,
                    BuildingBlock.BUILDING_BLOCK
            ).build()
    );
    public static void register(){

    }
}
