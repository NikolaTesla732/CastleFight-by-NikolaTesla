package com.custom.castlefight.custom_castlefight.blocks.blockitems;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class ConstructionItem extends BlockItem {

    public ConstructionItem(Block block, Settings settings) {
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
