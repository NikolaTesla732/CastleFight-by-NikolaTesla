package com.custom.castlefight.custom_castlefight.screenhandler;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildFunc;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;
import static com.custom.castlefight.custom_castlefight.Custom_castlefight.SCANSCREEN_TYPE;

public class ScanScreen extends ScreenHandler {


    private final BlockPos startPos;
    private final World world;
    public ScanScreen(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, buf.readBlockPos());
    }

    public ScanScreen(int syncId,PlayerInventory playerInventory, BlockPos blockPos) {
        super( SCANSCREEN_TYPE,syncId);
        this.startPos = blockPos.add(1,0,1);
        this.world = playerInventory.player.getEntityWorld();
    }
    public void OnscanClicked(){
        LOGGER.info("Entry point detected");
        if (world instanceof ServerWorld serverWorld){

            LOGGER.info(serverWorld.toString());
        }
    }
    public BlockPos getBlockPos() {
        return startPos;
    }
    @Override
    public boolean onButtonClick(PlayerEntity player,int id){
        if (world instanceof ServerWorld serverWorld){
            if (id == 1){
                var BlockList = BuildFunc.scanSection(startPos, serverWorld);
                BuildFunc.buildSection(serverWorld,startPos, BlockList, 2);
                return true;
            }
        }
        return super.onButtonClick(player,id);
    }



    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
