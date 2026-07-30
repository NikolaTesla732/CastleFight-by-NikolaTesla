package com.custom.castlefight.custom_castlefight.Network.PacketsC2S;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.*;
import com.custom.castlefight.custom_castlefight.Network.screenhandler.LobbyScreen;
import com.custom.castlefight.custom_castlefight.Network.screenhandler.MainGameScreen;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;
import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public record RequestToDoClientMatchActionC2SPacket(MatchUtilities.MatchAction matchAction) implements CustomPayload {
    public static final Identifier RAW_ID = Identifier.of(MOD_ID, "request_to_do_client_match_action");
    public static final CustomPayload.Id<RequestToDoClientMatchActionC2SPacket> ID = new CustomPayload.Id<>(RAW_ID);
    public static final PacketCodec<RegistryByteBuf, RequestToDoClientMatchActionC2SPacket> PACKET_CODEC = MatchUtilities.MatchAction.PACKET_CODEC.xmap(
            RequestToDoClientMatchActionC2SPacket::new,
            RequestToDoClientMatchActionC2SPacket::matchAction
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void register() {
        PayloadTypeRegistry.playC2S().register(ID, PACKET_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ID, RequestToDoClientMatchActionC2SPacket::receiver);
    }

    public static void receiver(RequestToDoClientMatchActionC2SPacket payload, ServerPlayNetworking.Context context) {
        context.server().execute(() -> {
            MatchUtilities.MatchAction action = payload.matchAction;
            ServerPlayerEntity player = context.player();
            UUID playerId = player.getUuid();
            MatchUtilities.MatchManager manager = MatchUtilities.MatchManager.getInstance();
            MatchUtilities.Match match = manager.getMatch(manager.getPlayerMatch(playerId));
            LOGGER.info("Пакет пришёл, действие: " + action.getAction().toString());
            if (!action.can()) return;
            switch (action.getAction()) {
                case GET_MATCHES -> {
                    ServerPlayNetworking.send(player, new SendMatchesS2CPacket(new ArrayList<>(manager.getMatches().keySet())));
                }
                case JOIN_MATCH -> {
                    if (manager.playerInMatch(playerId)) {
                        ServerPlayNetworking.send(player, new SendMatchAnswerS2CPacket(MatchUtilities.MatchAnswer.ALREADY_IN_GAME));
                        return;
                    }
                    manager.addWaiting(playerId, payload.matchAction().getFormat());
                }
                case JOIN_TEAM -> {
                    if (!manager.playerInMatch(playerId)) {
                        LOGGER.info("Игрок: " + player.getName() + " не находится в матче, и не может присоединиться к команде");
                        ServerPlayNetworking.send(player, new SendMatchAnswerS2CPacket(MatchUtilities.MatchAnswer.NOT_FOUND_MATCH));
                        return;
                    }
                    MatchUtilities.MatchAnswer answer = match.addPlayerToTeam(playerId, payload.matchAction.getTeam());
                    if (answer == MatchUtilities.MatchAnswer.NONE) {
                        Map<MatchUtilities.TeamColor, List<String>> teamMap = new HashMap<>();
                        for (MatchUtilities.Team team : match.getTeams()) {
                            teamMap.put(team.getColor(), team.getPlayersName(context.server()));
                        }
                        ServerPlayNetworking.send(player, new SendTeamsS2CPacket(teamMap));
                    } else ServerPlayNetworking.send(player, new SendMatchAnswerS2CPacket(answer));

                }
                case OPEN_MAIN -> {
                    if (!manager.playerInMatch(playerId)) return;
                    player.openHandledScreen(MainGameScreen.getFactory());
                }
                case OPEN_LOBBY -> {
                    if (!manager.playerCanViewLobby(playerId)) return;
                    player.openHandledScreen(LobbyScreen.getFactory());
                }
                case GET_TEAMS -> {
                    List<MatchUtilities.Team> teamList = manager.getMatch(manager.getPlayerMatch(player.getUuid())).getTeams();
                    Map<MatchUtilities.TeamColor, List<String>> teamMap = new HashMap<>();
                    for (MatchUtilities.Team team : teamList) {
                        teamMap.put(team.getColor(), team.getPlayersName(context.server()));
                    }
                    ServerPlayNetworking.send(player, new SendTeamsS2CPacket(teamMap));
                }
                case GET_RACES -> {
                    ServerPlayNetworking.send(player, new SendRacesSetS2CPacket(match.getRaces()));
                }
                case BAN_RACE -> {
                    MatchUtilities.MatchAnswer answer = match.banRace(action.getRace(),playerId);
                    if (answer == MatchUtilities.MatchAnswer.NONE) {
                        ServerPlayNetworking.send(player, new SendRacesSetS2CPacket(match.getRaces()));
                    } else ServerPlayNetworking.send(player, new SendMatchAnswerS2CPacket(answer));
                }
                case CHOOSE_RACE -> {
                }
                case GET_PLAYER_DATA -> {
                    if (playerId == match.getPlayerData(playerId).getPlayerId()){
                        MatchUtilities.PlayerData data = match.getPlayerData(playerId);

                    }
                }
            }
        });


    }
}
