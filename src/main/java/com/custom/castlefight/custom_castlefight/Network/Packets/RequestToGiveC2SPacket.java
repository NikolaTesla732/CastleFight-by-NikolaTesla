package com.custom.castlefight.custom_castlefight.Network.Packets;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public record RequestToGiveC2SPacket(ItemStack ToGive) implements CustomPayload {

    public static final Identifier RAW_ID = Identifier.of(MOD_ID, "request_to_give");
    public static final CustomPayload.Id<RequestToGiveC2SPacket> ID =
            new CustomPayload.Id<>(RAW_ID);

    public static final PacketCodec<RegistryByteBuf, RequestToGiveC2SPacket> CODEC =
            ItemStack.createExtraValidatingPacketCodec(ItemStack.PACKET_CODEC)
                    .xmap(
                            // из ItemStack -> RequestToGiveC2SPacket
                            RequestToGiveC2SPacket::new,
                            // из RequestToGiveC2SPacket -> ItemStack
                            RequestToGiveC2SPacket::ToGive
                    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void register() {
        PayloadTypeRegistry.playC2S().register(ID, CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ID, RequestToGiveC2SPacket::receive);
    }

    public static void receive(RequestToGiveC2SPacket payload,
                               ServerPlayNetworking.Context context) {
//        ServerPlayerEntity player = context.player();
        context.player().getInventory().insertStack(payload.ToGive);


    }
}
