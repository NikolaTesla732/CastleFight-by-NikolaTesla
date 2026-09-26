package com.custom.castlefight.custom_castlefight.client;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToGetRacesManagerC2SPacket;
import com.custom.castlefight.custom_castlefight.blocks.BuildingBlock;
import com.custom.castlefight.custom_castlefight.client.bind.AllKeyUtils;
import com.custom.castlefight.custom_castlefight.client.clientFunc.ClientTempStorage;
import com.custom.castlefight.custom_castlefight.client.screen.render.Draw;
import com.custom.castlefight.custom_castlefight.client.screen.render.blockModel.BuildingBlockModel;
import com.custom.castlefight.custom_castlefight.client.screen.render.hud_overlays.CastleFightHudOverlay;
import com.custom.castlefight.custom_castlefight.client.screen.handled.HandledRegister;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.render.BlockRenderLayer;

import java.util.Random;

public class Custom_castlefightClient implements ClientModInitializer {
    public static ClientTempStorage CLIENT_TEMP;
    public static Random RANDOM = new Random();

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.putBlock(BuildingBlock.BUILDING_BLOCK, BlockRenderLayer.CUTOUT);
        ModelLoadingPlugin.register(pluginContext -> {
            pluginContext.modifyBlockModelAfterBake().register(((model, context) -> {
                if (context.state().isOf(BuildingBlock.BUILDING_BLOCK)){
                    return new BuildingBlockModel();
                }
                return model;
            }));
        });
        AllKeyUtils.register();
        WorldRenderEvents.END_MAIN.register(Draw::SelectBlock);
        RegisterReciverS2CPackets.register();
        HandledRegister.register();
        ClientLifecycleEvents.CLIENT_STARTED.register( minecraftClient -> {
            CLIENT_TEMP = ClientTempStorage.getInstance();
        });
        ClientTickEvents.END_CLIENT_TICK.register((client -> {
            if (CLIENT_TEMP.getMatchState() == MatchUtilities.MatchState.PLAYING && CLIENT_TEMP.getGoldTimer() > 0){
                CLIENT_TEMP.reduceTheGoldTimer();
            }
        }));
        HudElementRegistry.removeElement(VanillaHudElements.ARMOR_BAR);
        HudElementRegistry.removeElement(VanillaHudElements.EXPERIENCE_LEVEL);
        HudElementRegistry.removeElement(VanillaHudElements.HEALTH_BAR);
        HudElementRegistry.removeElement(VanillaHudElements.FOOD_BAR);
        HudElementRegistry.removeElement(VanillaHudElements.INFO_BAR);
        HudElementRegistry.attachElementAfter(VanillaHudElements.HOTBAR,CastleFightHudOverlay.ID,new CastleFightHudOverlay());
    }
}
