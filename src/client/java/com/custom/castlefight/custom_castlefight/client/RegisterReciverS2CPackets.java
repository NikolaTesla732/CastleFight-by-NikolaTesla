package com.custom.castlefight.custom_castlefight.client;

import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.*;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.packet.CustomPayload;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;
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
                        LOGGER.info("данные пришли");
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
