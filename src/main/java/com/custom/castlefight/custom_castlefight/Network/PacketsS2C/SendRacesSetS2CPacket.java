package com.custom.castlefight.custom_castlefight.Network.PacketsS2C;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public record SendRacesSetS2CPacket(Set<String> racesSet) implements CustomPayload {
    private static Identifier RAW_ID = Identifier.of(MOD_ID,"send_races_set");
    public static CustomPayload.Id<SendRacesSetS2CPacket> ID = new Id<>(RAW_ID);
    public static PacketCodec<RegistryByteBuf, SendRacesSetS2CPacket> PACKET_CODEC = PacketCodec.of(
            SendRacesSetS2CPacket::write,
            SendRacesSetS2CPacket::read
    );
    public static void register(){
        PayloadTypeRegistry.playS2C().register(ID, PACKET_CODEC);
    }
    public void write(RegistryByteBuf buf){
        buf.writeInt(racesSet.size());
        for (String race : racesSet){
            buf.writeString(race);
        }
    }

    public static SendRacesSetS2CPacket read(RegistryByteBuf buf){
        int size = buf.readInt();
        Set<String> racesSet = new HashSet<>();
        for(int i = 0;i<size;i++){
            racesSet.add(buf.readString());
        }
        return new SendRacesSetS2CPacket(racesSet);
    }
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
