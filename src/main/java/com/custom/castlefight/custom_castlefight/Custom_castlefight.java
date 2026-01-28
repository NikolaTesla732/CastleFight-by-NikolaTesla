package com.custom.castlefight.custom_castlefight;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildFunc;
import com.custom.castlefight.custom_castlefight.Network.CastlefightNetworking;
import com.custom.castlefight.custom_castlefight.blocks.BuildBlock;
import com.custom.castlefight.custom_castlefight.blocks.ScanBlock;
import com.custom.castlefight.custom_castlefight.screenhandler.ScanScreen;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Custom_castlefight implements ModInitializer {
    public static final String MOD_ID = "custom_castlefight";
    public  static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static ScreenHandlerType<ScanScreen> SCANSCREEN_TYPE;
    public static final Identifier SCANSCREEN_ID = Identifier.of(MOD_ID,"scan_screen");
    @Override
    public void onInitialize() {
        ScanBlock.register();
        BuildBlock.register();
        CastlefightNetworking.registerS2CPackets();
        BuildFunc.init();
        SCANSCREEN_TYPE = Registry.register(Registries.SCREEN_HANDLER,
                SCANSCREEN_ID,
                new ScreenHandlerType<>(((syncId, playerInventory) -> ), FeatureSet.empty())
        );
    }
}
