package com.custom.castlefight.custom_castlefight.Network.PacketsS2C;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public record SendLevelsSetS2CPacket(Set<Integer> levelsSet) implements CustomPayload {
    private static final Identifier RAW_ID = Identifier.of(MOD_ID,"send_levels_set");
    public static final CustomPayload.Id<SendLevelsSetS2CPacket> ID = new Id<>(RAW_ID);
    public static final PacketCodec<RegistryByteBuf,SendLevelsSetS2CPacket> CODEC = PacketCodec.of(
            SendLevelsSetS2CPacket::write,
            SendLevelsSetS2CPacket::read
    );
    public static void register(){
        PayloadTypeRegistry.playS2C().register(ID,CODEC);
    }

    private void write(RegistryByteBuf buf){
        buf.writeInt(levelsSet.size());
        for (int level : levelsSet){
            buf.writeInt(level);
        }
    }
    private static SendLevelsSetS2CPacket read(RegistryByteBuf buf){
        int size = buf.readInt();
        Set<Integer> levels = new HashSet<>();
        for (int i = 0;i < size; i++){
            levels.add(buf.readInt());
        }
        return new SendLevelsSetS2CPacket(levels);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
