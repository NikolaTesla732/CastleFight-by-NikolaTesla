package com.custom.castlefight.custom_castlefight.client.screen;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildTemplateAction;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToDoActionWithTemplatesC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;

import static com.custom.castlefight.custom_castlefight.client.Custom_castlefightClient.CLIENT_TEMP;

public class AdminScreen extends Screen {
    private TabManager tab_manager;
    private TabNavigationWidget tab_navigation;
    private MainTab main_tab;
    private Tab2 tab2;
    private Set<String> raceSet = new HashSet<>();
    private ButtonWidget modeBuild;
    private boolean removeMode = false;

    public AdminScreen(Text title) {
        super(title);
    }
    public void setRaces(Set<String> races){
        this.raceSet = races;
    }

    @Override
    public void tick() {
        super.tick();
        if (CLIENT_TEMP.getChanges() && CLIENT_TEMP.hasRacesSet() ){
            setRaces(CLIENT_TEMP.getRacesSetWithClean());
            clearAndInit();
        }
    }

    @Override
    protected void init() {
        super.init();
        //Объявление логики работы TabManager
        this.tab_manager = new TabManager(
                widget -> this.addDrawableChild(widget),// При открытии вкладки-показать её widget
                widget -> this.remove(widget)// при закрытии вкладки-закрыть её вкладки
        );
        //Объявление вкладок
        this.main_tab = new MainTab();
        this.tab2 = new Tab2();
        //Объявление полоски вкладок
        this.tab_navigation = TabNavigationWidget.builder(this.tab_manager, this.width)
                .tabs(new Tab[]{this.main_tab, this.tab2})
                .build();
        this.tab_navigation.init();
        this.addDrawableChild(this.tab_navigation); // Показываем полоску навигации

        // Задаём область вкладки
        this.tab_manager.setTabArea(new ScreenRect(
                20,
                40,
                this.width - 40,
                this.height - 70
        ));
        this.tab_manager.setCurrentTab(this.main_tab,true);
    }

    //Основная вкладка, открывается первой
    class MainTab extends GridScreenTab {
        public void modeAction(){
            removeMode = !removeMode;
            if (removeMode) modeBuild.setMessage(Text.literal("Режим удаления"));
            else modeBuild.setMessage(Text.literal("Режим редактирования" ));
        }
        MainTab() {
            super(Text.literal("BuildEdit"));
            this.grid.setColumnSpacing(8);
            this.grid.setRowSpacing(6);
            if (raceSet.isEmpty()){
                BuildTemplateAction action = new BuildTemplateAction();
                action.setActionGetAllRaces();
                ClientPlayNetworking.send(new RequestToDoActionWithTemplatesC2SPacket(action));
            }
            modeBuild = ButtonWidget.builder(
                    Text.literal((removeMode)?"Режим удаления":"Режим редактирования"),
                    (ButtonWidget.PressAction) b -> {
                       modeAction();
                    }
            ).build();
            this.grid.add(modeBuild,0,0);
            int row = 1;
            int column = 0;
            int maxRaceInRow = 5;
            for (String race : raceSet){
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
                            client.setScreen(new AdminBuildsScreen(AdminScreen.this,race));
                        }
                ).build();
                this.grid.add(button,row,column++);
                if (column > maxRaceInRow){
                    row++;
                    column=0;
                }
            }

        }

    }
    //Вторая вкладка,пока тестовая
    class Tab2 extends GridScreenTab {
        private final TextFieldWidget textField;
        private final TextFieldWidget textField2;

        Tab2() {
            super(Text.literal("Tab2"));
            this.grid.setColumnSpacing(8);
            this.grid.setRowSpacing(6);
            this.textField = new TextFieldWidget(AdminScreen.this.getTextRenderer(),
                    120, 20, Text.literal("Test"));
            this.textField2 = new TextFieldWidget(AdminScreen.this.getTextRenderer(),
                    120, 20, Text.literal("Test2"));
            this.grid.add(this.textField, 0, 0);
            this.grid.add(this.textField2, 1, 0);

        }
    }

}

