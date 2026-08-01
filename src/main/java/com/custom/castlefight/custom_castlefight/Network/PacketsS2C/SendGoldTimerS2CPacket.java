package com.custom.castlefight.custom_castlefight.Network.PacketsS2C;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public record SendGoldTimerS2CPacket(int timer) implements CustomPayload {
    public static final Identifier RAW_ID = Identifier.of(MOD_ID,"send_gold_timer");
    public static final CustomPayload.Id<SendGoldTimerS2CPacket> ID = new Id<>(RAW_ID);
    public static final PacketCodec<RegistryByteBuf,SendGoldTimerS2CPacket> PACKET_CODEC = PacketCodec.of(((value, buf) -> buf.writeInt(value.timer())),((buf -> new SendGoldTimerS2CPacket(buf.readInt()))));

    public static void register(){
        PayloadTypeRegistry.playS2C().register(ID,PACKET_CODEC);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
