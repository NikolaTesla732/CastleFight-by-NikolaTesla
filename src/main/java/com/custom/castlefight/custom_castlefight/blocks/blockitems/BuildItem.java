package com.custom.castlefight.custom_castlefight.blocks.blockitems;

import net.fabricmc.fabric.api.resource.v1.reloader.ResourceReloaderKeys;
import net.minecraft.block.Block;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;
public class BuildItem extends BlockItem {

    public BuildItem(Block block, Settings settings) {
        super(block, settings);
    }

}






//    int Timer = 20;
//    @Override
//    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
//        super.inventoryTick(stack, world, entity, slot);
//        HitResult CanUseBlock = entity.raycast(4.8,1.0f,false);
//         if (entity.isPlayer()){
//            if (slot == EquipmentSlot.MAINHAND &&  CanUseBlock.getType() == HitResult.Type.BLOCK){
//                if (Timer<=0){
//                    LOGGER.info("Ты смотришь на блок");
//                    Timer = 10;
//                }
//            }
//         }
//        if (Timer > 0)Timer--;
//    }
