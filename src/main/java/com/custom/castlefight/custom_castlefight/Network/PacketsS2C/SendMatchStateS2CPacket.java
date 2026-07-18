package com.custom.castlefight.custom_castlefight.Network.PacketsS2C;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public record SendMatchStateS2CPacket(MatchUtilities.MatchState matchState) implements CustomPayload {
    public static Identifier RAW_ID = Identifier.of(MOD_ID,"send_match_state");
    public static CustomPayload.Id<SendMatchStateS2CPacket> ID = new Id<>(RAW_ID);
    public static PacketCodec<RegistryByteBuf,SendMatchStateS2CPacket> PACKET_CODEC = PacketCodec.of(
            ((value, buf) -> {
                buf.writeEnumConstant(value.matchState);
            }),
            (buf -> new SendMatchStateS2CPacket(buf.readEnumConstant(MatchUtilities.MatchState.class)))
    );

    public static void register(){
        PayloadTypeRegistry.playS2C().register(ID,PACKET_CODEC);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
