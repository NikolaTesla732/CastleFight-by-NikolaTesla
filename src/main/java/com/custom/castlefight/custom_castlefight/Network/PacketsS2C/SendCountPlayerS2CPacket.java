package com.custom.castlefight.custom_castlefight.Network.PacketsS2C;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.Map;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public record SendCountPlayerS2CPacket(Map<MatchUtilities.MatchFormat,Integer> countPlayer) implements CustomPayload {
    public static Identifier RAW_ID = Identifier.of(MOD_ID,"send_count_player");
    public static CustomPayload.Id<SendCountPlayerS2CPacket> ID = new Id<>(RAW_ID);
    public static PacketCodec<RegistryByteBuf,SendCountPlayerS2CPacket> PACKET_CODEC = PacketCodec.of(
            SendCountPlayerS2CPacket::write,
            SendCountPlayerS2CPacket::read
    );
    public void write(RegistryByteBuf buf){
        buf.writeMap(this.countPlayer,(PacketByteBuf::writeEnumConstant),PacketByteBuf::writeInt);
    }
    public static SendCountPlayerS2CPacket read(RegistryByteBuf buf){
        Map<MatchUtilities.MatchFormat,Integer> countMap = buf.readMap(
            (buf1 -> buf1.readEnumConstant(MatchUtilities.MatchFormat.class)),PacketByteBuf::readInt);
        return new SendCountPlayerS2CPacket(countMap);
    }
    public static void register(){
        PayloadTypeRegistry.playS2C().register(ID,PACKET_CODEC);
    }
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
