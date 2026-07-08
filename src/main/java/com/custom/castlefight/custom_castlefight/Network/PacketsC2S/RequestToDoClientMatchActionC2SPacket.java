package com.custom.castlefight.custom_castlefight.Network.PacketsC2S;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendMatchAnswerS2CPacket;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendMatchesS2CPacket;
import com.custom.castlefight.custom_castlefight.Network.screenhandler.LobbyScreen;
import com.custom.castlefight.custom_castlefight.Network.screenhandler.ScanScreen;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;
import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public record RequestToDoClientMatchActionC2SPacket(MatchUtilities.MatchAction matchAction) implements CustomPayload {
    public static final Identifier RAW_ID = Identifier.of(MOD_ID,"request_to_do_client_match_action");
    public static final CustomPayload.Id<RequestToDoClientMatchActionC2SPacket> ID = new CustomPayload.Id<>(RAW_ID);
    public static final PacketCodec<RegistryByteBuf,RequestToDoClientMatchActionC2SPacket> PACKET_CODEC = MatchUtilities.MatchAction.PACKET_CODEC.xmap(
            RequestToDoClientMatchActionC2SPacket::new,
            RequestToDoClientMatchActionC2SPacket::matchAction
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void register(){
        PayloadTypeRegistry.playC2S().register(ID,PACKET_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ID,RequestToDoClientMatchActionC2SPacket::receiver);
    }
    public static void receiver(RequestToDoClientMatchActionC2SPacket payload, ServerPlayNetworking.Context context){
         context.server().execute(()->{
             MatchUtilities.MatchAction action = payload.matchAction;
             UUID playerId = context.player().getUuid();
             MatchUtilities.MatchManager manager = MatchUtilities.MatchManager.getInstance();
             LOGGER.info("Пакет пришёл");
             if (!action.can()) return;
             switch (action.getAction()){
                 case GET_MATCHES -> {
                     ServerPlayNetworking.send(context.player(),new SendMatchesS2CPacket(new ArrayList<>(manager.getMatches().keySet())));
                 }
                 case JOIN_MATCH -> {
                   if (manager.playerInMatch(playerId)) {
                       ServerPlayNetworking.send(context.player(),new SendMatchAnswerS2CPacket(MatchUtilities.MatchAnswer.ALREADY_IN_GAME));
                       return;
                   }
                   manager.addWaiting(playerId, payload.matchAction().getFormat());
                 }
                 case JOIN_TEAM -> {
                     if (!manager.playerInMatch(playerId)) {
                         ServerPlayNetworking.send(context.player(),new SendMatchAnswerS2CPacket(MatchUtilities.MatchAnswer.NOT_FOUND_MATCH));
                         return;
                     }
                     MatchUtilities.Match match = manager.getMatch(manager.getPlayerMatch(playerId));
                     MatchUtilities.MatchAnswer answer = match.addPlayerToTeam(playerId,payload.matchAction.getTeam());
                 }
                 case OPEN_LOBBY -> {
                     if (!manager.playerCanViewLobby(playerId)) return;
                     context.player().openHandledScreen(new ExtendedScreenHandlerFactory<Map<MatchUtilities.MatchFormat, Integer>>() {
                         @Override
                         public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                             return new LobbyScreen(syncId, playerInventory);
                         }

                         @Override
                         public Text getDisplayName() {
                             return Text.literal("Лобби");
                         }

                         @Override
                         public Map<MatchUtilities.MatchFormat, Integer> getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
                             Map<MatchUtilities.MatchFormat, Integer> playerCount = new HashMap<>();
                             for (MatchUtilities.MatchFormat format : MatchUtilities.MatchFormat.values()) {
                                 playerCount.put(format,
                                         MatchUtilities.MatchManager.getInstance().getNonActiveMatches().get(format).getAllPlayer().size()
                                 );
                             }
                             return playerCount;
                         }
                     }
                     );
                 }
                 case BAN_RACE -> {}
                 case CHOOSE_RACE -> {}
             }
         });


    }
}
