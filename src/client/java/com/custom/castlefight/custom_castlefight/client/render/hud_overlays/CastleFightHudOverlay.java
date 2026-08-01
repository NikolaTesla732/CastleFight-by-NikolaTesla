package com.custom.castlefight.custom_castlefight.client.render.hud_overlays;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.client.render.Draw;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import net.minecraft.util.Util;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;
import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;
import static com.custom.castlefight.custom_castlefight.client.Custom_castlefightClient.CLIENT_TEMP;
import static org.apache.commons.lang3.math.IEEE754rUtils.min;

public class CastleFightHudOverlay implements HudElement {
    public static Identifier ID = Identifier.of(MOD_ID, "castle_fight_hud");

    @Override
    public void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (CLIENT_TEMP.getMatchState() == null
                || CLIENT_TEMP.getMatchState() != MatchUtilities.MatchState.PLAYING) {
            return;
        }
        int goldTimer = CLIENT_TEMP.getGoldTimer();
        MatchUtilities.PlayerData data = CLIENT_TEMP.getPlayerData();
        int wight = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();
        float deltaTicks = tickCounter.getTickProgress(false);
        float progress = (goldTimer - deltaTicks) / (float) (MatchUtilities.Match.fullGoldTimer);
        int x = (wight / 2) - 70, y = height - 28;
        Draw.drawSimpleProgressBar(context, x, y, 161, 5, progress, 0xFFF2EE05);
        long elapsed = Util.getMeasuringTimeMs() - CLIENT_TEMP.lastGoldEarn;
        int clip = 0;
        if (elapsed <= CLIENT_TEMP.animationTime) {
            clip = (int) (elapsed / CLIENT_TEMP.goldEarnAnimationTime);
        }

        context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(MOD_ID, "textures/other/gold.png"), x - 21, (int) (y - 11 - min(clip,7-clip)), (float) 0.0 + 16 * clip, (float) 0.0, 16, 16, 128, 16);
        context.drawText(client.textRenderer,String.valueOf(data.gold),x-3,y - 9,0xFFFFFFFF,false);
        context.drawTexture(RenderPipelines.GUI_TEXTURED,Identifier.of(MOD_ID,"textures/other/log.png"),x+37,y-11,(float) 0.0,(float) 0.0,10,10,10,10);
        context.drawText(client.textRenderer,String.valueOf(data.wood),x+48,y - 9,0xFFFFFFFF,true);
        if (data.race != null)context.drawText(client.textRenderer,data.race,x+90,y-9,0xFFFFFFFF,true);
    }
}
