package com.custom.castlefight.custom_castlefight.Network.PacketsS2C;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public record SendMatchActiveS2CPacket(boolean active) implements CustomPayload {
    public static Identifier RAW_ID = Identifier.of(MOD_ID,"send_match_active");
    public static CustomPayload.Id<SendMatchActiveS2CPacket> ID = new Id<>(RAW_ID);
    public static PacketCodec<RegistryByteBuf,SendMatchActiveS2CPacket> PACKET_CODEC = PacketCodec.of(
            ((value, buf) -> buf.writeBoolean(value.active)),
            (buf -> new SendMatchActiveS2CPacket(buf.readBoolean()))
    );

    public static void register(){
        PayloadTypeRegistry.playS2C().register(ID,PACKET_CODEC);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
