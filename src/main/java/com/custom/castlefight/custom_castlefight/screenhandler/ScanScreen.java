package com.custom.castlefight.custom_castlefight.screenhandler;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildFunc;
import com.custom.castlefight.custom_castlefight.Network.Packets.RequestToScanSectionC2SPacket;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.SCANSCREEN_TYPE;

public class ScanScreen extends ScreenHandler {


    private BlockPos startPos = BlockPos.ORIGIN;

    public ScanScreen(int syncId,PlayerInventory playerInventory, BlockPos blockPos) {
        super( SCANSCREEN_TYPE,syncId);

    }

    public ScanScreen(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId,playerInventory,buf.readBlockPos());
    }

    public void setBlockPos(BlockPos pos){
        this.startPos = pos;
    }
    public BlockPos getBlockPos() {
        return startPos;
    }
    protected void onScanClicked(PlayerEntity player,int id){
        if (!player.getEntityWorld().isClient()){
            if (id == 1){
                List<List<Pair<BlockState,BlockPos>>> p = BuildFunc.scanSection(this.startPos,(ServerWorld) player.getEntityWorld());
            }
        }

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
