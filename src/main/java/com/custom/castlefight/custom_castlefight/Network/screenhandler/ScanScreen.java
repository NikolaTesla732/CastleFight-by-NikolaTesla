package com.custom.castlefight.custom_castlefight.Network.screenhandler;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildUtilities;
import com.custom.castlefight.custom_castlefight.CustomFunc.BuildUtilities.BuildTemplate;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.*;

public class ScanScreen extends CastleFightBaseScreenHandler {


    private final BlockPos startPos;
    private final World world;
    public static final Identifier SCANSCREEN_ID = Identifier.of(MOD_ID,"scan_screen");
    public static ScreenHandlerType<ScanScreen> SCANSCREEN_TYPE;

    public ScanScreen(int syncId,PlayerInventory playerInventory, BlockPos blockPos) {
        super(SCANSCREEN_TYPE,syncId);
        this.startPos = blockPos.add(2,0,2);
        this.world = playerInventory.player.getEntityWorld();
    }

    public static void register(){
        SCANSCREEN_TYPE = Registry.register(
                Registries.SCREEN_HANDLER,
                SCANSCREEN_ID,
                new ExtendedScreenHandlerType<ScanScreen,BlockPos>(
                        ScanScreen::new,
                        BlockPos.PACKET_CODEC
                )
        );
    }
    public void OnScanClicked(String name,String race,int level,int cost, int income,int cooldown){
        if (this.world instanceof ServerWorld serverWorld){
                    var BlockList = BuildUtilities.scanSection(this.startPos, serverWorld);
                    BuildTemplate build = new BuildTemplate(
                        name,race,level,BlockList,income,cooldown,cost
                    );
                    TEMPLATES.put(build);

            }
            LOGGER.info("Сохранено новое здание"+name);
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
