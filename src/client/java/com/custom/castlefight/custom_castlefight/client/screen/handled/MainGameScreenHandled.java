package com.custom.castlefight.custom_castlefight.client.screen.handled;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToDoClientMatchActionC2SPacket;
import com.custom.castlefight.custom_castlefight.Network.screenhandler.MainGameScreen;
import com.custom.castlefight.custom_castlefight.client.render.Draw;
import com.custom.castlefight.custom_castlefight.client.screen.screen_fragment.ChoiseTeamFragment;
import com.custom.castlefight.custom_castlefight.client.screen.screen_fragment.IScreenFragment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.custom.castlefight.custom_castlefight.client.Custom_castlefightClient.CLIENT_TEMP;
import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;

public class MainGameScreenHandled extends CastleFightBaseScreenHandled<MainGameScreen> {
    private Map<MatchUtilities.TeamColor, List<String>> playerTeamList = new HashMap<>();
    public MatchUtilities.MatchState state;
    public IScreenFragment fragment;
    private GridWidget grid;
    private MatchUtilities.MatchAnswer answer = MatchUtilities.MatchAnswer.NONE;
    private int timer;
    private int fullTimer;

    public MainGameScreenHandled(MainGameScreen handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        state = handler.data.state();
        timer = handler.data.timer();
        fullTimer = handler.data.absoluteTimer();
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        if (timer > 0) timer--;
    }

    @Override
    public void onStorageUpdate(MatchUtilities.MatchData data) {
        if (timer > 0) this.timer--;
        switch (data){
            case PLAYERS_TEAM -> {
                this.playerTeamList = CLIENT_TEMP.getPlayersTeam();
                LOGGER.info("Получены команды игроков от сервера " + playerTeamList.keySet().size());
            }
            case ANSWER -> {
                this.answer = CLIENT_TEMP.getAnswer();
                LOGGER.info("Получен ответ от сервера: " + answer);
                switch (answer) {
                    case NEED_UPDATE -> {
                        this.playerTeamList = null;
                    }
                }
            }
            case MATCH_STATE -> state = CLIENT_TEMP.getMatchState();
        }
        clearAndInit();
    }

    public void updateData() {
        switch (state) {
            case NOT_ACTIVE, BAN_RACE -> {
                LOGGER.info("Экран закрыт, стадия:" + state);
                client.currentScreen.close();
            }
            case CHOICE_TEAM -> {
                playerTeamList = null;
                MatchUtilities.MatchAction action = new MatchUtilities.MatchAction();
                action.setAction(MatchUtilities.ActionPlayer.GET_TEAMS);
                ClientPlayNetworking.send(new RequestToDoClientMatchActionC2SPacket(action));
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        LOGGER.info("Экран основной игры открыт, стадия:" + state.toString());
        switch (state) {
            case NOT_ACTIVE, BAN_RACE -> {
                LOGGER.info("Экран закрыт, стадия:" + state);
                client.currentScreen.close();
            }
            case CHOICE_TEAM -> {
                if (playerTeamList == null || playerTeamList.isEmpty()) {
                    if (answer != MatchUtilities.MatchAnswer.NEED_UPDATE) updateData();
                    return;
                }
                fragment = new ChoiseTeamFragment(playerTeamList);
                grid = fragment.buildWidgets(this);
                grid.setRowSpacing(5);
                grid.setColumnSpacing(6);
                LOGGER.info("Виджеты размещены");
            }
        }
        if (grid == null) return;
//        grid.setPosition(this.width / 10, this.height / 10);
        grid.refreshPositions();
        SimplePositioningWidget.setPos(
                grid,
                0,0,
                this.width,
                this.height,
                0.5f,
                0.5f
        );
        grid.forEachChild(this::addDrawableChild);
    }

    private void drawTimer(DrawContext context,float deltaTicks){
        int x = (width - 200) / 2;
        int y = 20;
        float progress = (timer - deltaTicks) / (float)(fullTimer);
        Draw.drawSimpleProgressBar(context, x, y, 200, 10,progress );
    }
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
            switch (state){
                case CHOICE_TEAM -> drawTimer(context,deltaTicks);
            }
        super.render(context, mouseX, mouseY, deltaTicks);
    }


}
