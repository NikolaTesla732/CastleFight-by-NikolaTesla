package com.custom.castlefight.custom_castlefight.client.screen;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildTemplateAction;
import com.custom.castlefight.custom_castlefight.CustomFunc.MapUtilities;
import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.Custom_castlefight;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToDoActionWithTemplatesC2SPacket;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToDoAdminActionC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import java.util.*;

import static com.custom.castlefight.custom_castlefight.client.Custom_castlefightClient.CLIENT_TEMP;
import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;

public class AdminScreen extends CastleFightBaseScreen {
    private TabManager tab_manager;
    private TabNavigationWidget tabNavigation;
    private MainTab main_tab;
    private Tab2 tab2;
    private Set<String> raceSet = new HashSet<>();
    private List<UUID> matches = new ArrayList<>();
    private ButtonWidget modeBuild;
    private boolean removeMode = false, waitingRace = false;

    public AdminScreen(Text title) {
        super(title);
    }

    public static boolean isInt(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void onStorageUpdate(MatchUtilities.MatchData data) {
        switch (data){
            case RACES_SET -> {
                raceSet = CLIENT_TEMP.getRacesSet();
                LOGGER.info(raceSet.toString());
            }
        }
        clearAndInit();
    }
    @Override
    protected void init() {
        super.init();
        //Объявление логики работы TabManager
        this.tab_manager = new TabManager(
                this::addDrawableChild,// При открытии вкладки-показать её widget
                this::remove// при закрытии вкладки-закрыть её вкладки
        );
        //Объявление вкладок
        this.main_tab = new MainTab();
        this.tab2 = new Tab2();
        //Объявление полоски вкладок
        this.tabNavigation = TabNavigationWidget.builder(this.tab_manager, this.width)
                .tabs(new Tab[]{this.main_tab, this.tab2})
                .build();
        this.tabNavigation.init();
        this.addDrawableChild(this.tabNavigation); // Показываем полоску навигации

        // Задаём область вкладки
        this.tab_manager.setTabArea(new ScreenRect(
                10,
                20,
                this.width - 20,
                this.height - 20
        ));
        this.tab_manager.setCurrentTab(this.main_tab, true);
    }



    //Основная вкладка, открывается первой
    class MainTab extends GridScreenTab {
        public void modeAction() {
            removeMode = !removeMode;
            if (removeMode) modeBuild.setMessage(Text.literal("Режим удаления"));
            else modeBuild.setMessage(Text.literal("Режим редактирования"));
        }

        MainTab() {
            super(Text.literal("Шаблоны"));
            this.grid.setColumnSpacing(8);
            this.grid.setRowSpacing(6);
            if (raceSet.isEmpty() && !waitingRace) {
                BuildTemplateAction action = new BuildTemplateAction();
                action.setActionGetAllRaces();
                ClientPlayNetworking.send(new RequestToDoActionWithTemplatesC2SPacket(action));
                waitingRace = true;
                return;
            }
            modeBuild = ButtonWidget.builder(
                    Text.literal((removeMode) ? "Режим удаления" : "Режим редактирования"),
                    (ButtonWidget.PressAction) b -> {
                        modeAction();
                    }
            ).build();
            this.grid.add(modeBuild, 0, 0);
            int row = 1;
            int column = 0;
            int maxRaceInRow = 5;
            for (String race : raceSet) {
                ButtonWidget button = ButtonWidget.builder(
                        Text.literal(race),
                        (ButtonWidget.PressAction) b -> {
                            if (removeMode) {
                                BuildTemplateAction action = new BuildTemplateAction();
                                action.setActionRemoveRace();
                                action.setRace(race);
                                ClientPlayNetworking.send(new RequestToDoActionWithTemplatesC2SPacket(action));
                                raceSet.clear();
                                clearAndInit();
                                return;
                            }
                            client.setScreen(new AdminBuildsScreen(AdminScreen.this, race));
                        }
                ).build();
                this.grid.add(button, row, column++);
                if (column > maxRaceInRow) {
                    row++;
                    column = 0;
                }
            }

        }

    }

    //Вторая вкладка
    class Tab2 extends GridScreenTab {
        private final ButtonWidget buttonOpenMatchManager;
        private final ButtonWidget buttonStartGame;
        private final TextWidget mapText;
        private final TextWidget PlayerInTeamText;
        private final TextFieldWidget mapField;
        private final TextFieldWidget playerInTeamField;

        private MatchUtilities.Match match;

        Tab2() {
            super(Text.literal("Игра"));
            this.grid.setColumnSpacing(8);
            this.grid.setRowSpacing(6);
            this.mapText = new TextWidget(Text.literal("Название карты"),textRenderer);
            this.PlayerInTeamText = new TextWidget(Text.literal("Количество игроков в команде"),textRenderer);
            this.mapField = new TextFieldWidget(textRenderer,100,20,Text.literal("Название карты"));
            this.playerInTeamField = new TextFieldWidget(textRenderer,100,20,Text.literal(("Количество игроков в команде")));

            this.buttonOpenMatchManager = ButtonWidget.builder(
                    Text.literal("Открыть менеджер матчей"),
                    (button -> {
                        client.setScreen(new MatchManagerScreen());
                    })
            ).build();
            this.buttonStartGame = ButtonWidget.builder(
                    Text.literal("Начать новый матч"),
                    (button -> {
                        if (!this.mapField.getText().isBlank() && !this.playerInTeamField.getText().isBlank()){
                            if (isInt(this.playerInTeamField.getText())){
                                String map = this.mapField.getText();
                                int maxPlayer = Integer.parseInt(this.playerInTeamField.getText());
                                match = new MatchUtilities.Match(UUID.randomUUID(),maxPlayer);
                                match.setMap(new MapUtilities.GamingMap(map));
                                match.addTeam(MatchUtilities.TeamColor.BLUE);
                                match.addTeam(MatchUtilities.TeamColor.RED);
                                MatchUtilities.AdminMatchAction action = new MatchUtilities.AdminMatchAction(MatchUtilities.ActionAdmin.ADD_MATCH);
                                action.setMatch(match);
                                ClientPlayNetworking.send(new RequestToDoAdminActionC2SPacket(action));
                            }else {
                                Custom_castlefight.LOGGER.info("Не числовые данные "+this.playerInTeamField.getText());
                            }
                        }else{
                            Custom_castlefight.LOGGER.info("Недостаточно данных");
                        }
                    })
            ).build();
            ButtonWidget skipButton = ButtonWidget.builder(
                    Text.literal("Пропустить текущую стадию"),
                    (button -> {
                        MatchUtilities.AdminMatchAction action = new MatchUtilities.AdminMatchAction(MatchUtilities.ActionAdmin.NEXT_STAGE);
                        ClientPlayNetworking.send(new RequestToDoAdminActionC2SPacket(action));
                    })

            ).build();
            this.grid.add(skipButton,1,0);
            this.grid.add(mapText,1,1);
            this.grid.add(PlayerInTeamText,0,1);
            this.grid.add(mapField,1,2);
            this.grid.add(playerInTeamField,0,2);
            this.grid.add(this.buttonOpenMatchManager, 0, 0);
            this.grid.add(this.buttonStartGame, 2, 2);
        }
    }

}

