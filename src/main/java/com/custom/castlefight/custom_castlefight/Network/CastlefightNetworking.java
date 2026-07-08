package com.custom.castlefight.custom_castlefight.Network;

import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.*;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.*;
import com.custom.castlefight.custom_castlefight.Network.screenhandler.LobbyScreen;
import com.custom.castlefight.custom_castlefight.Network.screenhandler.ScanScreen;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;

import static com.custom.castlefight.custom_castlefight.Network.screenhandler.ScanScreen.SCANSCREEN_ID;


public class CastlefightNetworking {

    public static void registerC2SPackets() {
        RequestToGiveC2SPacket.register();
        RequestToScanC2SPacket.register();
        RequestToDoActionWithTemplatesC2SPacket.register();
        RequestToDoClientMatchActionC2SPacket.register();
        RequestToDoAdminActionC2SPacket.register();
    }
    public static void registerS2CPackets(){
        SendRacesSetS2CPacket.register();
        SendBuildS2CPacket.register();
        SendBuildsSetS2CPacket.register();
        SendLevelsSetS2CPacket.register();
        SendMatchAnswerS2CPacket.register();
        SendMatchesS2CPacket.register();
        SendCountPlayerS2CPacket.register();
    }
    public static void registerScreenHandler(){
        ScanScreen.register();
        LobbyScreen.register();
    }
}
