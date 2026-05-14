package com.custom.castlefight.custom_castlefight.Network.PacketsS2C;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public record SendBuildsSetS2CPacket(Set<String> buildsSet) implements CustomPayload{
    private static Identifier RAW_ID = Identifier.of(MOD_ID,"send_builds_set");
    public static CustomPayload.Id<SendBuildsSetS2CPacket> ID = new CustomPayload.Id<>(RAW_ID);
    public static PacketCodec<RegistryByteBuf,SendBuildsSetS2CPacket> CODEC = PacketCodec.of(
            SendBuildsSetS2CPacket::write,
            SendBuildsSetS2CPacket::read
    );
    public static void register(){
        PayloadTypeRegistry.playS2C().register(ID,CODEC);
        }
    public void write(RegistryByteBuf buf){
        buf.writeInt(buildsSet.size());
        for (String race : buildsSet){
            buf.writeString(race);
        }
    }

    public static SendBuildsSetS2CPacket read(RegistryByteBuf buf){
        int size = buf.readInt();
        Set<String> buildsSet = new HashSet<>();
        for(int i = 0;i<size;i++){
            buildsSet.add(buf.readString());
        }
        return new SendBuildsSetS2CPacket(buildsSet);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
