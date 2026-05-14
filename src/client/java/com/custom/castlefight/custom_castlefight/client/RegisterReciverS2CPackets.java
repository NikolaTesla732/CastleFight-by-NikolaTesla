package com.custom.castlefight.custom_castlefight.client;

import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendBuildS2CPacket;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendBuildsSetS2CPacket;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendLevelsSetS2CPacket;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendRacesSetS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.packet.CustomPayload;

import static com.custom.castlefight.custom_castlefight.client.Custom_castlefightClient.CLIENT_TEMP;

public class RegisterReciverS2CPackets {
    public static void register(){
        registerBuildsSetReceiver();
        registerBuildTemplateReceiver();
        registerRacesSetReceiver();
        registerLevelsSetReceiver();
    }
    public static void registerBuildTemplateReceiver(){
        ClientPlayNetworking.registerGlobalReceiver(SendBuildS2CPacket.ID,((payload, context) -> {
            context.client().execute(()->{
                CLIENT_TEMP.setNewBuild(payload.build());
            });
        }) );
    }
    public static void registerRacesSetReceiver(){
        ClientPlayNetworking.registerGlobalReceiver(
                SendRacesSetS2CPacket.ID, ((payload, context) -> {
                    context.client().execute(()->{
                        CLIENT_TEMP.setRacesSet(payload.racesSet());
                    });
                })
        );
    }
    public static void registerBuildsSetReceiver(){
        ClientPlayNetworking.registerGlobalReceiver(
                SendBuildsSetS2CPacket.ID,
                ((payload,context) -> {
                    context.client().execute(()->{
                        CLIENT_TEMP.setNamesSet(payload.buildsSet());
                    });
                })
        );
    }
    public static void registerLevelsSetReceiver(){
        ClientPlayNetworking.registerGlobalReceiver(SendLevelsSetS2CPacket.ID,
                (payload,context) ->{
                    context.client().execute(()->{
                        CLIENT_TEMP.setLevelsSet(payload.levelsSet());
                    });
                });
    }
    
}
