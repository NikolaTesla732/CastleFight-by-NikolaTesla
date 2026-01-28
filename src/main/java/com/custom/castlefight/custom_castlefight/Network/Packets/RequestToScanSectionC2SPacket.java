package com.custom.castlefight.custom_castlefight.Network.Packets;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildFunc;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public record RequestToScanSectionC2SPacket(BlockPos startPos) implements CustomPayload {
    private static final Identifier RAW_ID = Identifier.of(MOD_ID,"requesttoscansection");
    private static final CustomPayload.Id<RequestToScanSectionC2SPacket> ID = new CustomPayload.Id<>(RAW_ID);
    public static final PacketCodec<RegistryByteBuf, RequestToScanSectionC2SPacket> CODEC =
            PacketCodec.of(
                    (packet,buf) ->{
                buf.writeBlockPos(packet.startPos);
            },
                    (buf) -> {
                        return new RequestToScanSectionC2SPacket(buf.readBlockPos());
            });
    public static void register(){
        PayloadTypeRegistry.playC2S().register(ID,CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ID,RequestToScanSectionC2SPacket::receive);
    }

    public static List<List<Pair<BlockState,BlockPos>>> receive(RequestToScanSectionC2SPacket payload, ServerPlayNetworking.Context context){
        return BuildFunc.scanSection(payload.startPos,context.server().getSpawnWorld());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return null;
    }
}
