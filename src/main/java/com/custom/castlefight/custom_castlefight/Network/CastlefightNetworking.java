package com.custom.castlefight.custom_castlefight.Network;

import com.custom.castlefight.custom_castlefight.Custom_castlefight;
import com.custom.castlefight.custom_castlefight.Network.Packets.RequestToGiveC2SPacket;
import com.custom.castlefight.custom_castlefight.Network.Packets.RequestToScanSectionC2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public class CastlefightNetworking {

    public static void registerC2SPackets() {
        RequestToGiveC2SPacket.register();
        RequestToScanSectionC2SPacket.register();
    }
    public static void registerS2CPackets(){

    }
}
