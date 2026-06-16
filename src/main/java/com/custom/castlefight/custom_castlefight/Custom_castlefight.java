package com.custom.castlefight.custom_castlefight;

import com.custom.castlefight.custom_castlefight.CustomFunc.Construction;
import com.custom.castlefight.custom_castlefight.CustomFunc.GlobalBuildTemplateStorage;
import com.custom.castlefight.custom_castlefight.Network.CastlefightNetworking;
import com.custom.castlefight.custom_castlefight.blocks.BuildingBlock;
import com.custom.castlefight.custom_castlefight.blocks.ConstructionBlock;
import com.custom.castlefight.custom_castlefight.blocks.ScanBlock;
import com.custom.castlefight.custom_castlefight.blocks.blockentity.CastlefightBlockEntities;
import com.custom.castlefight.custom_castlefight.screenhandler.ScanScreen;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Custom_castlefight implements ModInitializer {
    public static final String MOD_ID = "custom_castlefight";
    public  static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static ScreenHandlerType<ScanScreen> SCANSCREEN_TYPE;
    public static final Identifier SCANSCREEN_ID = Identifier.of(MOD_ID,"scan_screen");
    public static GlobalBuildTemplateStorage TEMPLATES;
    @Override
    public void onInitialize() {
        ScanBlock.register();
        BuildingBlock.register();
        ConstructionBlock.register();
        CastlefightNetworking.registerC2SPackets();
        CastlefightNetworking.registerS2CPackets();
        CastlefightBlockEntities.register();

        SCANSCREEN_TYPE = Registry.register(
                Registries.SCREEN_HANDLER,
                SCANSCREEN_ID,
                new ExtendedScreenHandlerType<ScanScreen,BlockPos>(
                   ScanScreen::new,
                   BlockPos.PACKET_CODEC
                )
        );
        ServerLifecycleEvents.SERVER_STARTED.register(server ->{
            RegistryWrapper.WrapperLookup lookup = server.getRegistryManager();
            Construction.ConstructionTicker.register();
            this.TEMPLATES = new GlobalBuildTemplateStorage();
            TEMPLATES.load(lookup);
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(minecraftServer -> {
            TEMPLATES.save();
        });
    }
}
