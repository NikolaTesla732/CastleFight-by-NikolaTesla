package com.custom.castlefight.custom_castlefight.Network.PacketsS2C;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public record SendPlayerDataS2CPacket(MatchUtilities.PlayerData data) implements CustomPayload {
    public static final Identifier RAW_ID = Identifier.of(MOD_ID,"send_playerdata");
    public static CustomPayload.Id<SendPlayerDataS2CPacket> ID = new Id<>(RAW_ID);
    public static PacketCodec<RegistryByteBuf,SendPlayerDataS2CPacket> PACKET_CODEC = MatchUtilities.PlayerData.PACKET_CODEC.xmap(
            SendPlayerDataS2CPacket::new,
            SendPlayerDataS2CPacket::data
    );

    public static void register(){
        PayloadTypeRegistry.playS2C().register(ID,PACKET_CODEC);
    }
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
