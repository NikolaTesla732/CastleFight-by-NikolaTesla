package com.custom.castlefight.custom_castlefight.screenhandler;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildFunc;
import com.custom.castlefight.custom_castlefight.CustomFunc.BuildFunc.BuildTemplate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

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
        this.startPos = blockPos.add(2,0,2);
        this.world = playerInventory.player.getEntityWorld();
    }

    public BuildTemplate OnScanClicked(String name,int level,int cost, int income,int cooldown){
            if (this.world instanceof ServerWorld serverWorld){
                    var BlockList = BuildFunc.scanSection(this.startPos, serverWorld);
                    BuildTemplate build = new BuildTemplate(
                        name,level,BlockList,income,cooldown,cost
                    );
                    BuildFunc.buildSection(serverWorld,startPos, build, 2);
                    return build;

            }
            LOGGER.info("Сохранено новое здание"+name);
        return null;
    }
    public BlockPos getBlockPos() {
        return startPos;
    }
    @Override
    public boolean onButtonClick(PlayerEntity player,int id){
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
