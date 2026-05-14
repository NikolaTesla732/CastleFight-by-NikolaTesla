package com.custom.castlefight.custom_castlefight.client;

import com.custom.castlefight.custom_castlefight.Custom_castlefight;
import com.custom.castlefight.custom_castlefight.Network.CastlefightNetworking;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendBuildS2CPacket;
import com.custom.castlefight.custom_castlefight.client.bind.AdminKey;
import com.custom.castlefight.custom_castlefight.client.bind.RaceKey;
import com.custom.castlefight.custom_castlefight.client.bind.ShopKey;
import com.custom.castlefight.custom_castlefight.client.clientFunc.ClientTempStorage;
import com.custom.castlefight.custom_castlefight.client.render.Draw;
import com.custom.castlefight.custom_castlefight.client.screen.AdminLevelScreen;
import com.custom.castlefight.custom_castlefight.client.screen.RaceBuildsScreen;
import com.custom.castlefight.custom_castlefight.client.screen.ScanHandlerScreen;
import com.custom.castlefight.custom_castlefight.screenhandler.ScanScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.world.ClientWorld;

public class Custom_castlefightClient implements ClientModInitializer {
    public static ClientTempStorage CLIENT_TEMP;
    @Override
    public void onInitializeClient() {
        ShopKey.register();
        AdminKey.register();
        RaceKey.register();
        WorldRenderEvents.END_MAIN.register(Draw::SelectBlock);
        RegisterReciverS2CPackets.register();
        HandledScreens.<ScanScreen,ScanHandlerScreen>register(Custom_castlefight.SCANSCREEN_TYPE,
                (handler, inventory, title) -> new ScanHandlerScreen(handler, inventory, title));
        ClientLifecycleEvents.CLIENT_STARTED.register( minecraftClient -> {
            CLIENT_TEMP = new ClientTempStorage();
        });

    }

}
