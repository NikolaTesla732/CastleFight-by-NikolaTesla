package com.custom.castlefight.custom_castlefight.Network;

import com.custom.castlefight.custom_castlefight.Network.Packets.RequestToGiveC2SPacket;
import com.custom.castlefight.custom_castlefight.Network.Packets.RequestToScanC2SPacket;

public class CastlefightNetworking {

    public static void registerC2SPackets() {
        RequestToGiveC2SPacket.register();
        RequestToScanC2SPacket.register();
    }
    public static void registerS2CPackets(){

    }
}
