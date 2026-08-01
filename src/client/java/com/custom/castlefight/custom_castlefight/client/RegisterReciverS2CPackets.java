package com.custom.castlefight.custom_castlefight.client;

import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Util;
import org.apache.commons.compress.archivers.sevenz.CLI;

import static com.custom.castlefight.custom_castlefight.client.Custom_castlefightClient.CLIENT_TEMP;

public class RegisterReciverS2CPackets {
    public static void register() {
        registerPlayerCountReceiver();
        registerBuildsSetReceiver();
        registerBuildTemplateReceiver();
        registerRacesSetReceiver();
        registerLevelsSetReceiver();
        registerMatchesReceiver();
        registerMatchAnswerReceiver();
        registerTeamsReceiver();
        registerMatchStateReceiver();
        registerTimerReceiver();
        registerPlayerDataReceiver();
        registerGoldTimerReceiver();
    }
    public static void registerMatchActiveReceiver(){
        ClientPlayNetworking.registerGlobalReceiver(SendMatchActiveS2CPacket.ID,(payload,context) ->{
           context.client().execute(()->{
               CLIENT_TEMP.setPlayerInMatch(payload.active());
           });
        });
    }
    public static void registerGoldTimerReceiver(){
        ClientPlayNetworking.registerGlobalReceiver(SendGoldTimerS2CPacket.ID,(payload,context)->{
            context.client().execute(()->{
                CLIENT_TEMP.setGoldTimer(payload.timer());
            });
        });
    }
    public static void registerPlayerDataReceiver(){
        ClientPlayNetworking.registerGlobalReceiver(
            SendPlayerDataS2CPacket.ID,
            (payload,context) -> {
                context.client().execute(()->{
                    if (CLIENT_TEMP.getPlayerData() == null) CLIENT_TEMP.setPlayerData(payload.data());
                    else{
                        if (CLIENT_TEMP.getPlayerData().gold < payload.data().gold){
                            CLIENT_TEMP.lastGoldEarn = Util.getMeasuringTimeMs();
                        }
                        CLIENT_TEMP.setPlayerData(payload.data());
                    }
                });
            });
    }
    public static void registerTimerReceiver(){
        ClientPlayNetworking.registerGlobalReceiver(
                SendTimerS2CPacket.ID,
                ((payload,context) -> {
                    context.client().execute(()->{
                        if (payload.full())CLIENT_TEMP.setFullTimer(payload.timer());
                        else CLIENT_TEMP.setTimer(payload.timer());
                    });
                })
        );
    }
    public static void registerMatchStateReceiver(){
        ClientPlayNetworking.registerGlobalReceiver(
                SendMatchStateS2CPacket.ID,
                ((payload,context) -> {
                    context.client().execute(() -> {
                        CLIENT_TEMP.setMatchState(payload.matchState());
                    });
                })
        );
    }
    public static void registerTeamsReceiver(){
        ClientPlayNetworking.registerGlobalReceiver(SendTeamsS2CPacket.ID,
                ((payload, context) -> {
                    context.client().execute(()->{
                        CLIENT_TEMP.setPlayersTeam(payload.teams());
                    });
                }));
    }
    public static void registerPlayerCountReceiver(){
        ClientPlayNetworking.registerGlobalReceiver(SendCountPlayerS2CPacket.ID,
                ((payload, context) -> {
                    context.client().execute(()->{
                        CLIENT_TEMP.setCountPlayer(payload.countPlayer());
                    });
                }));
    }
    public static void registerBuildTemplateReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(SendBuildS2CPacket.ID, ((payload, context) -> {
            context.client().execute(() -> {
                CLIENT_TEMP.setNewBuild(payload.build());
            });
        }));
    }

    public static void registerRacesSetReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(
                SendRacesSetS2CPacket.ID, ((payload, context) -> {
                    context.client().execute(() -> {
                        CLIENT_TEMP.setRacesSet(payload.racesSet());
                    });
                })
        );
    }

    public static void registerBuildsSetReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(
                SendBuildsSetS2CPacket.ID,
                ((payload, context) -> {
                    context.client().execute(() -> {
                        CLIENT_TEMP.setNamesSet(payload.buildsSet());
                    });
                })
        );
    }

    public static void registerLevelsSetReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(SendLevelsSetS2CPacket.ID,
                (payload, context) -> {
                    context.client().execute(() -> {
                        CLIENT_TEMP.setLevelsSet(payload.levelsSet());
                    });
                });
    }

    public static void registerMatchAnswerReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(SendMatchAnswerS2CPacket.ID,
                ((payload, context) -> {
                    context.client().execute(() -> {
                        CLIENT_TEMP.setAnswer(payload.answer());
                    });
                }));
    }

    public static void registerMatchesReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(SendMatchesS2CPacket.ID,
                ((payload, context) -> {
                    context.client().execute(() -> {
                        CLIENT_TEMP.setMatches(payload.matches());
                    });
                }));
    }


}
