package com.custom.castlefight.custom_castlefight.Network.PacketsC2S;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;
import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public record RequestToDoAdminActionC2SPacket(MatchUtilities.AdminMatchAction adminMatchAction) implements CustomPayload {
    public static final Identifier RAW_ID = Identifier.of(MOD_ID,"request_admin_action");
    public static final CustomPayload.Id<RequestToDoAdminActionC2SPacket> ID = new CustomPayload.Id<RequestToDoAdminActionC2SPacket>(RAW_ID);
    public static final PacketCodec<RegistryByteBuf,RequestToDoAdminActionC2SPacket> PACKET_CODEC = MatchUtilities.AdminMatchAction.PACKET_CODEC.xmap(
            RequestToDoAdminActionC2SPacket::new,
            RequestToDoAdminActionC2SPacket::adminMatchAction
    );

    public static void register(){
        PayloadTypeRegistry.playC2S().register(ID,PACKET_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ID,RequestToDoAdminActionC2SPacket::receiver);
    }
    public static void receiver(RequestToDoAdminActionC2SPacket payload, ServerPlayNetworking.Context context){
        context.server().execute(()->{
            if(!payload.adminMatchAction.can())return;
            switch (payload.adminMatchAction.getAction()){
                case ADD_MATCH -> {
                    MatchUtilities.MatchManager.getInstance().addMatch(payload.adminMatchAction.getMatch());
                    LOGGER.info(payload.adminMatchAction.getMatch().getId().toString());
                }
            }
        });
    }
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
