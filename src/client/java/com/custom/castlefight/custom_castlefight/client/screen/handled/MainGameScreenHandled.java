package com.custom.castlefight.custom_castlefight.client.screen.handled;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToDoClientMatchActionC2SPacket;
import com.custom.castlefight.custom_castlefight.Network.screenhandler.MainGameScreen;
import com.custom.castlefight.custom_castlefight.client.render.Draw;
import com.custom.castlefight.custom_castlefight.client.screen.screen_fragment.BanRaceFragment;
import com.custom.castlefight.custom_castlefight.client.screen.screen_fragment.ChoiseRaceFragment;
import com.custom.castlefight.custom_castlefight.client.screen.screen_fragment.ChoiseTeamFragment;
import com.custom.castlefight.custom_castlefight.client.screen.screen_fragment.IScreenFragment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.custom.castlefight.custom_castlefight.client.Custom_castlefightClient.CLIENT_TEMP;
import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;

public class MainGameScreenHandled extends CastleFightBaseScreenHandled<MainGameScreen> {
    private Map<MatchUtilities.TeamColor, List<String>> playerTeamList;
    private MatchUtilities.MatchState state;
    private IScreenFragment fragment;
    private GridWidget grid;
    private MatchUtilities.MatchAnswer answer = MatchUtilities.MatchAnswer.NONE;
    private List<String> races;
    private int timer;
    private int fullTimer;
    private MatchUtilities.MatchData waiting = MatchUtilities.MatchData.NONE;
    private boolean canBan = false;
    private MatchUtilities.PlayerData playerData;
    public MainGameScreenHandled(MainGameScreen handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        state = handler.data.state();
        timer = handler.data.timer();
        fullTimer = handler.data.absoluteTimer();
        playerData = handler.data.data();
    }
    public void updateGrid(){
        if (grid!=null){
            grid.refreshPositions();
            grid.forEachChild(this::addDrawableChild);
        }
    }
    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        if (timer > 0) timer--;

    }

    public MatchUtilities.PlayerData getPlayerData() {
        return playerData;
    }

    @Override
    public void onStorageUpdate(MatchUtilities.MatchData data) {
        String dataString = "NULL";
        switch (data) {
            case PLAYERS_TEAM -> {
                this.playerTeamList = CLIENT_TEMP.getPlayersTeam();
                dataString = String.valueOf(playerTeamList.keySet().size());
            }
            case ANSWER -> {
                this.answer = CLIENT_TEMP.getAnswer();
                dataString = answer.toString();
            }
            case RACES_SET -> {
                this.races = new ArrayList<>(CLIENT_TEMP.getRacesSet());
                dataString = races.toString();
            }
            case MATCH_STATE -> {
                state = CLIENT_TEMP.getMatchState();
                dataString = state.toString();
            }
            case TIMER -> {
                timer = CLIENT_TEMP.getTimer();
                dataString = String.valueOf(timer);
            }
            case FULL_TIMER -> {
                fullTimer = CLIENT_TEMP.getFullTimer();
                dataString = String.valueOf(fullTimer);
            }
            case PLAYER_DATA -> {
                playerData = CLIENT_TEMP.getPlayerData();
                dataString = "данные игрока";
            }
        }
        if (waiting == data) waiting = MatchUtilities.MatchData.NONE;
        LOGGER.info("Полученны данные: " + data.toString() + ": " + dataString);
        clearAndInit();
    }

    public void updateData() {
        switch (state) {
            case NOT_ACTIVE -> {
                LOGGER.info("Экран закрыт, стадия:" + state);
                client.currentScreen.close();
            }
            case CHOICE_TEAM -> {
                playerTeamList = null;
                MatchUtilities.MatchAction action = new MatchUtilities.MatchAction();
                action.setAction(MatchUtilities.ActionPlayer.GET_TEAMS);
                ClientPlayNetworking.send(new RequestToDoClientMatchActionC2SPacket(action));
                waiting = MatchUtilities.MatchData.PLAYERS_TEAM;
            }
            case BAN_RACE,CHOICE_RACE-> {
                races = null;
                MatchUtilities.MatchAction action = new MatchUtilities.MatchAction();
                action.setAction(MatchUtilities.ActionPlayer.GET_RACES);
                ClientPlayNetworking.send(new RequestToDoClientMatchActionC2SPacket(action));
                waiting = MatchUtilities.MatchData.RACES_SET;
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        LOGGER.info("Экран основной игры открыт, стадия:" + state.toString());
        switch (state) {
            case NOT_ACTIVE -> {
                LOGGER.info("Экран закрыт, стадия:" + state);
                client.currentScreen.close();
            }
            case CHOICE_TEAM -> {
                if (playerTeamList == null) {
                    if (waiting == MatchUtilities.MatchData.NONE) updateData();
                    return;
                }
                fragment = new ChoiseTeamFragment(playerTeamList);
            }
            case BAN_RACE -> {
                if (races == null) {
                    if (waiting == MatchUtilities.MatchData.NONE) updateData();
                    return;
                }
                fragment = new BanRaceFragment(races);
            }
            case CHOICE_RACE -> {
                if (races == null) {
                    if (waiting == MatchUtilities.MatchData.NONE) updateData();
                    return;
                }
                fragment = new ChoiseRaceFragment(races);
            }
        }
        grid = fragment.buildWidgets(this);
        if (grid == null) return;
        grid.setRowSpacing(5);
        grid.setColumnSpacing(6);
        grid.refreshPositions();
        SimplePositioningWidget.setPos(
                grid,
                0, 0,
                this.width,
                this.height,
                0.5f,
                0.5f
        );
        grid.forEachChild(this::addDrawableChild);
        LOGGER.info("Виджеты размещены");
    }

    private void drawTimer(DrawContext context, float deltaTicks) {
        int x = (width - 200) / 2;
        int y = 20;
        float progress = (timer - deltaTicks) / (float) (fullTimer);
        Draw.drawSimpleProgressBar(context, x, y, 200, 10, progress);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        switch (state) {
            case CHOICE_TEAM, BAN_RACE, CHOICE_RACE -> drawTimer(context, deltaTicks);
        }
        super.render(context, mouseX, mouseY, deltaTicks);
    }


}
