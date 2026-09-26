package com.custom.castlefight.custom_castlefight.client.screen.handled;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToDoAdminActionC2SPacket;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToDoClientMatchActionC2SPacket;
import com.custom.castlefight.custom_castlefight.Network.screenhandler.LobbyScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.*;

import static com.custom.castlefight.custom_castlefight.client.Custom_castlefightClient.CLIENT_TEMP;
import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;

public class LobbyScreenHandled extends CastleFightBaseScreenHandled<LobbyScreen>{
    private GridWidget grid;
    private boolean nextStageMode = false;
    private Map<MatchUtilities.MatchFormat, Integer> playersCount;

    public LobbyScreenHandled(LobbyScreen handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.playersCount = new HashMap<>(handler.playerCount);
    }

    @Override
    public void onStorageUpdate(MatchUtilities.MatchData data) {
        switch (data){
            case ANSWER -> {
                LOGGER.info(String.valueOf(CLIENT_TEMP.getAnswer()));
            }
            case COUNT_PLAYER -> {
                this.playersCount = CLIENT_TEMP.getCountPlayer();
                LOGGER.info(String.valueOf(playersCount.get(MatchUtilities.MatchFormat.OneVsOne)));
                clearAndInit();
            }
        }
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
                        if (nextStageMode){
                            MatchUtilities.AdminMatchAction action = new MatchUtilities.AdminMatchAction(MatchUtilities.ActionAdmin.RUN_MATCH);
                            action.setMatchFormat(format);
                            ClientPlayNetworking.send(new RequestToDoAdminActionC2SPacket(action));
                            return;
                        }
                        MatchUtilities.MatchAction action = new MatchUtilities.MatchAction();
                        action.setAction(MatchUtilities.ActionPlayer.JOIN_MATCH);
                        action.setFormat(format);
                        ClientPlayNetworking.send(new RequestToDoClientMatchActionC2SPacket(action));
                    })
            ).size(100,20).build();
            ButtonWidget modeButton = ButtonWidget.builder(
                    (nextStageMode) ?Text.literal("Режим прокрутки стадии") : Text.literal("Режим присоединения"),
                    (button1 -> {
                        nextStageMode = !nextStageMode;
                        LOGGER.info((nextStageMode) ?"Режим прокрутки стадии" : "Режим присоединения");
                        clearAndInit();
                    })
            ).build();
            this.grid.add(modeButton,10,0);
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

}
