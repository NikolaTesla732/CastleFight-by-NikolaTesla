package com.custom.castlefight.custom_castlefight.Network.PacketsS2C;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildFunc;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public record SendBuildS2CPacket(BuildFunc.BuildTemplate build) implements CustomPayload {
    public static final Identifier RAW_ID = Identifier.of(MOD_ID,"send_build");
    public static final CustomPayload.Id<SendBuildS2CPacket> ID = new Id<>(RAW_ID);
    public static final PacketCodec<RegistryByteBuf,SendBuildS2CPacket> CODEC = BuildFunc.BuildTemplate.PACKET_CODEC.xmap(
            SendBuildS2CPacket::new,
            SendBuildS2CPacket::build
    );
    public static void register(){
        PayloadTypeRegistry.playS2C().register(ID,CODEC);
    }
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
