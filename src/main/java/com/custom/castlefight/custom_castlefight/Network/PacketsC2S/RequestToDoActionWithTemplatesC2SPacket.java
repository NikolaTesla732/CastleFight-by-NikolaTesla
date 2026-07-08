package com.custom.castlefight.custom_castlefight.Network.PacketsC2S;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildUtilities;
import com.custom.castlefight.custom_castlefight.CustomFunc.BuildTemplateAction;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendBuildS2CPacket;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendBuildsSetS2CPacket;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendLevelsSetS2CPacket;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendRacesSetS2CPacket;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;
import static com.custom.castlefight.custom_castlefight.Custom_castlefight.TEMPLATES;

public record RequestToDoActionWithTemplatesC2SPacket(BuildTemplateAction templateAction) implements CustomPayload {
    public static final Identifier RAW_ID = Identifier.of(MOD_ID,"request_to_do_action_with_templates");
    public static final CustomPayload.Id<RequestToDoActionWithTemplatesC2SPacket> ID = new CustomPayload.Id<>(RAW_ID);
    public static final PacketCodec<RegistryByteBuf,RequestToDoActionWithTemplatesC2SPacket> CODEC = BuildTemplateAction.PACKET_CODEC.xmap(
            RequestToDoActionWithTemplatesC2SPacket::new,
            RequestToDoActionWithTemplatesC2SPacket::templateAction
    );

    public static void register(){
        PayloadTypeRegistry.playC2S().register(ID,CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ID,RequestToDoActionWithTemplatesC2SPacket::receiver);
    }
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void receiver(RequestToDoActionWithTemplatesC2SPacket payload, ServerPlayNetworking.Context context){
        BuildTemplateAction action = payload.templateAction;
        context.server().execute(() ->{
            if (!action.can())return;
            String race = action.getRace(),name = action.getName();
            BuildUtilities.BuildTemplate newBuild = action.getNewBuild(),oldBuild = action.getOldBuild();
            int level = action.getLevel();
            switch (action.getAction()){
                case REMOVE_RACE -> {
                    if (newBuild != null)TEMPLATES.removeRace(newBuild.getRace());
                    else TEMPLATES.removeRace(race);
                }
                case REMOVE_BUILD -> {
                    if (newBuild != null)TEMPLATES.removeAllLevelsBuild(newBuild);
                    else TEMPLATES.removeAllLevelsBuild(race,name);
                }
                case REMOVE_LEVEL -> {
                    if (newBuild != null)TEMPLATES.removeBuild(newBuild);
                    else TEMPLATES.removeBuild(race,name,level);
                }
                case PUT_BUILD -> {
                    if (newBuild != null)TEMPLATES.put(newBuild);
                }
                case PUT_RACE -> {
                    if (newBuild != null)TEMPLATES.putRace(newBuild.getRace());
                    else TEMPLATES.putRace(race);
                }
                case REPLACE_BUILD -> {
                    TEMPLATES.removeBuild(oldBuild);
                    TEMPLATES.put(newBuild);
                }
                case GET_BUILDS_SET -> {
                    Set<String> buildsSet;
                    if (newBuild != null) buildsSet = TEMPLATES.getRaceBuilds(newBuild.getRace()).keySet();
                    else buildsSet = TEMPLATES.getRaceBuilds(race).keySet();
                    ServerPlayNetworking.send(
                            context.player(),
                            new SendBuildsSetS2CPacket(buildsSet)
                    );
                }
                case GET_BUILD -> {
                    BuildUtilities.BuildTemplate build;
                    if (newBuild != null) build = TEMPLATES.getBuild(newBuild);
                    else build = TEMPLATES.getBuild(race,name,level);
                    ServerPlayNetworking.send(
                            context.player(),
                            new SendBuildS2CPacket(build)
                    );
                }
                case GET_ALL_RACES -> {
                    Set<String> raceSet = TEMPLATES.getRace();
                    ServerPlayNetworking.send(
                            context.player(),
                            new SendRacesSetS2CPacket(raceSet)
                    );
                }
                case GET_LEVELS_SET -> {
                    Set<Integer> levelsSet;
                    if (newBuild != null) levelsSet = TEMPLATES.getLevels(newBuild.getRace(), newBuild.getName());
                    else levelsSet = TEMPLATES.getLevels(race,name);
                    ServerPlayNetworking.send(
                            context.player(),
                            new SendLevelsSetS2CPacket(levelsSet)
                    );
                }
                case GET_BUIDS_SET_LEVEL -> {
                    Set<String> buildsSet,namesSet = new HashSet<>();
                    if (newBuild != null) {
                        buildsSet = TEMPLATES.getRaceBuilds(newBuild.getRace()).keySet();
                        for (String buildName : buildsSet){
                            if (TEMPLATES.contains(newBuild.getRace(),buildName,level)){
                                namesSet.add(buildName);
                            }
                        }
                    } else buildsSet = TEMPLATES.getRaceBuilds(race).keySet();
                    for (String buildName : buildsSet){
                        if (TEMPLATES.contains(race,buildName,level)){
                            namesSet.add(buildName);
                        }
                    }
                    ServerPlayNetworking.send(context.player(),
                            new SendBuildsSetS2CPacket(namesSet));
                }
            }
                }
                );
    }

}
