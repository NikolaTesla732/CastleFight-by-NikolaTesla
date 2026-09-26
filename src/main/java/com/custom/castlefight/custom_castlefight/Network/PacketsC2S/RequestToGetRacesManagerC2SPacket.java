package com.custom.castlefight.custom_castlefight.Network.PacketsC2S;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildUtilities;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendRacesManagerS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public record RequestToGetRacesManagerC2SPacket() implements CustomPayload {
    public static Identifier RAW_ID = Identifier.of(MOD_ID,"get_races_manager");
    public static Id ID = new Id(RAW_ID);
    public static PacketCodec<RegistryByteBuf,RequestToGetRacesManagerC2SPacket> PACKET_CODEC = PacketCodec.of(
            ((value, buf) -> {}),
            (buf -> new RequestToGetRacesManagerC2SPacket()));

    public static void register(){
        PayloadTypeRegistry.playC2S().register(ID,PACKET_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ID,RequestToGetRacesManagerC2SPacket::receiver);
    }

    public static void receiver(RequestToGetRacesManagerC2SPacket payload,
                               ServerPlayNetworking.Context context){
        context.server().execute(() -> {
            ServerPlayNetworking.send(context.player(),new SendRacesManagerS2CPacket(BuildUtilities.racesManager.getInstance()));
        });
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }



}
