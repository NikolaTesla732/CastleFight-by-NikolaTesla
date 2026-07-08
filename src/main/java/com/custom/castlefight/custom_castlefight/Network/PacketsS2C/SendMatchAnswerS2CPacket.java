package com.custom.castlefight.custom_castlefight.Network.PacketsS2C;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public record SendMatchAnswerS2CPacket(MatchUtilities.MatchAnswer answer) implements CustomPayload {
    public static Identifier RAW_ID =Identifier.of(MOD_ID,"sen_match_answer");
    public static CustomPayload.Id<SendMatchAnswerS2CPacket> ID = new CustomPayload.Id<>(RAW_ID);
    public static PacketCodec<RegistryByteBuf,SendMatchAnswerS2CPacket> PACKET_CODEC = PacketCodec.of(
            SendMatchAnswerS2CPacket::write,
            SendMatchAnswerS2CPacket::read
    );
    public static SendMatchAnswerS2CPacket read(RegistryByteBuf buf){
        return new SendMatchAnswerS2CPacket(buf.readEnumConstant(MatchUtilities.MatchAnswer.class));
    }
    public void write(RegistryByteBuf buf){
        buf.writeEnumConstant(answer);
    }

    public static void register(){
        PayloadTypeRegistry.playS2C().register(ID,PACKET_CODEC);
    }
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
