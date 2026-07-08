package com.custom.castlefight.custom_castlefight.client.screen;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToDoClientMatchActionC2SPacket;
import com.custom.castlefight.custom_castlefight.Network.screenhandler.LobbyScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.*;

import static com.custom.castlefight.custom_castlefight.client.Custom_castlefightClient.CLIENT_TEMP;
import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;

public class LobbyScreenHandled extends HandledScreen<LobbyScreen> {
    private GridWidget grid;
    private boolean waiting = false;
    private Map<MatchUtilities.MatchFormat, Integer> playersCount;

    public LobbyScreenHandled(LobbyScreen handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.playersCount = new HashMap<>(handler.playerCount);
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        if (!CLIENT_TEMP.getChanges()) return;
        if ( CLIENT_TEMP.hasAnswer()) {
            LOGGER.info(String.valueOf(CLIENT_TEMP.getAnswerWithClean()));
        }
        LOGGER.info(String.valueOf(CLIENT_TEMP.hasCountPlayer()));
        if (CLIENT_TEMP.hasCountPlayer()){
            this.playersCount = CLIENT_TEMP.getCountPlayerWithClean();
            LOGGER.info(String.valueOf(playersCount.get(MatchUtilities.MatchFormat.OneVsOne)));
            clearAndInit();
        }
    }
    @Override
    protected void drawForeground(DrawContext context,int mouseX,int mouseY){
        context.drawText(
                this.textRenderer,
                this.title,
                this.width/2,
                this.height-30,
                0x404040,
                false
        );
    }

    @Override
    protected void init() {
        super.init();
        int column = 0;
        int row = 0;
        int maxModeInRow = 5;
        this.grid = new GridWidget();
        this.grid.setColumnSpacing(8);
        this.grid.setRowSpacing(6);
        for (MatchUtilities.MatchFormat format : MatchUtilities.MatchFormat.values()) {
            TextWidget modeText = new TextWidget(Text.literal(MatchUtilities.formatDDescription.get(format)), this.textRenderer);
            TextWidget countPlayer = new TextWidget(Text.literal(String.valueOf(playersCount.get(format))), this.textRenderer);
            ButtonWidget button = ButtonWidget.builder(
                Text.literal("Присоединиться"),
                    (button1 -> {
                        LOGGER.info("Нажатие");
                        MatchUtilities.MatchAction action = new MatchUtilities.MatchAction();
                        action.setAction(MatchUtilities.ActionPlayer.JOIN_MATCH);
                        action.setFormat(format);
                        ClientPlayNetworking.send(new RequestToDoClientMatchActionC2SPacket(action));
                    })
            ).size(100,20).build();
            this.grid.add(modeText, row++, column);
            this.grid.add(countPlayer, row++, column);
            this.grid.add(button,row--,column++);
            row--;
            if (column > maxModeInRow) {
                row++;
                column = 0;
            }
        }
        this.grid.setPosition(this.width / 2 - 210, this.height / 2 - 110);
        this.grid.refreshPositions();
        this.grid.forEachChild(this::addDrawableChild);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        context.fill(0, 0, this.width, this.height, 0x88000000);
    }
}
