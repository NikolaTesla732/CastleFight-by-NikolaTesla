package com.custom.castlefight.custom_castlefight.Network;

import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToDoActionWithTemplatesC2SPacket;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToGiveC2SPacket;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToScanC2SPacket;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendBuildS2CPacket;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendBuildsSetS2CPacket;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendLevelsSetS2CPacket;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendRacesSetS2CPacket;


public class CastlefightNetworking {

    public static void registerC2SPackets() {
        RequestToGiveC2SPacket.register();
        RequestToScanC2SPacket.register();
        RequestToDoActionWithTemplatesC2SPacket.register();
    }
    public static void registerS2CPackets(){
        SendRacesSetS2CPacket.register();
        SendBuildS2CPacket.register();
        SendBuildsSetS2CPacket.register();
        SendLevelsSetS2CPacket.register();
    }
}
