package com.custom.castlefight.custom_castlefight.client;

import com.custom.castlefight.custom_castlefight.Custom_castlefight;
import com.custom.castlefight.custom_castlefight.Network.CastlefightNetworking;
import com.custom.castlefight.custom_castlefight.client.bind.AdminKey;
import com.custom.castlefight.custom_castlefight.client.bind.ShopKey;
import com.custom.castlefight.custom_castlefight.client.render.Draw;
import com.custom.castlefight.custom_castlefight.client.screen.ScanHandlerScreen;
import com.custom.castlefight.custom_castlefight.screenhandler.ScanScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.world.ClientWorld;

public class Custom_castlefightClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ShopKey.register();
        AdminKey.register();
        CastlefightNetworking.registerS2CPackets();
        WorldRenderEvents.END_MAIN.register(Draw::SelectBlock);
        HandledScreens.<ScanScreen,ScanHandlerScreen>register(Custom_castlefight.SCANSCREEN_TYPE,
                (handler, inventory, title) -> new ScanHandlerScreen(handler, inventory, title));
    }
}
