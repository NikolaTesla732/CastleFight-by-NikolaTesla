package com.custom.castlefight.custom_castlefight.Network.PacketsS2C;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public record SendMatchesS2CPacket(List<UUID> matches) implements CustomPayload {
    public static Identifier RAW_ID = Identifier.of(MOD_ID,"send_matches");
    public static CustomPayload.Id<SendMatchesS2CPacket> ID = new  CustomPayload.Id<>(RAW_ID);
    public static PacketCodec<RegistryByteBuf,SendMatchesS2CPacket> PACKET_CODEC = PacketCodec.of(
            SendMatchesS2CPacket::write,
            SendMatchesS2CPacket::read
    );
    public static SendMatchesS2CPacket read(RegistryByteBuf buf){
        List<UUID> ans = new ArrayList<>();
        int n = buf.readInt();
        for (int i = 0; i < n;i++){
            ans.add(buf.readUuid());
        }
        return new SendMatchesS2CPacket(ans);
    }
    public void write(RegistryByteBuf buf){
        buf.writeInt(matches.size());
        for (UUID match : matches) {
            buf.writeUuid(match);
        }
    }
    public static void register(){
        PayloadTypeRegistry.playS2C().register(ID,PACKET_CODEC);
    }
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
