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

public class MainGameScreenHandled extends HandledScreen<MainGameScreen> {
    private Map<MatchUtilities.TeamColor, List<String>> playerTeamList = new HashMap<>();
    public MatchUtilities.MatchState state;
    public IScreenFragment fragment;
    private GridWidget grid;
    private MatchUtilities.MatchAnswer answer = MatchUtilities.MatchAnswer.NONE;
    private boolean needUpdate = false;
    private int timer;
    private int fullTimer;

    public MatchUtilities.ActionPlayer waitings = MatchUtilities.ActionPlayer.NONE;

    public MainGameScreenHandled(MainGameScreen handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        state = handler.data.state();
        timer = handler.data.timer();
        fullTimer = handler.data.absoluteTimer();
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        if (timer > 0) this.timer--;
        if (waitings == null || !CLIENT_TEMP.hasChanges()) {
            return;
        }
        switch (waitings) {
            case GET_TEAMS -> {
                if (CLIENT_TEMP.hasPlayersTeam()) {
                    this.playerTeamList = CLIENT_TEMP.getPlayersTeamWithClean();
                    setDefaultWaiting();
                    LOGGER.info("Получены команды игроков от сервера " + playerTeamList.keySet().size());
                    needUpdate = true;
                }
            }
            case NONE -> {
                if (CLIENT_TEMP.hasAnswer()) {
                    this.answer = CLIENT_TEMP.getAnswerWithClean();
                    setDefaultWaiting();
                    LOGGER.info("Получен ответ от сервера:" + answer+" "+CLIENT_TEMP.getChanges());
                    switch (answer) {
                        case NEED_UPDATE -> {
                            updateData();
                            needUpdate = true;
                        }
                    }

                }
            }
        }
        switch (answer) {
            case NEED_UPDATE -> {
                if (CLIENT_TEMP.hasChanges() && CLIENT_TEMP.hasMatchState()) {
                    state = CLIENT_TEMP.getMatchStateWithClean();
                    needUpdate = true;
                }
            }
        }
        if (needUpdate) {
            needUpdate = false;
            clearAndInit();
        }
    }

    public void setDefaultWaiting() {
        waitings = MatchUtilities.ActionPlayer.NONE;
    }

    public void clearAndInitPublic() {
        clearAndInit();
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(
                this.textRenderer,
                this.title,
                this.width / 2,
                this.height - 30,
                0x404040,
                false
        );
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
                waitings = MatchUtilities.ActionPlayer.GET_TEAMS;
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

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        context.fill(0, 0, this.width, this.height, 0x88000000);
    }
}
