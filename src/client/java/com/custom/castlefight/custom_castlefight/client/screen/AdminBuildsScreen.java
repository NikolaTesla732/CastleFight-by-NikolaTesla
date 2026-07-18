package com.custom.castlefight.custom_castlefight.client.screen;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildTemplateAction;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToDoActionWithTemplatesC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;

import static com.custom.castlefight.custom_castlefight.client.Custom_castlefightClient.CLIENT_TEMP;

public class AdminBuildsScreen extends Screen {
    private final Screen adminScreen;
    private GridWidget grid;
    private String race;
    private ButtonWidget modeButton;
    private boolean removeMode = false;
    private Set<String> raceBuilds = new HashSet<>();
    public AdminBuildsScreen(Screen adminScreen,String race) {
        super(Text.literal("AdminBuildsScreen"));
        this.adminScreen = adminScreen;
        this.race = race;
    }
    public Screen getAdminScreen(){
        return this.adminScreen;
    }
    private void modeAction(){
        removeMode = !removeMode;
        clearAndInit();
    }

    @Override
    public void tick() {
        super.tick();
        if (CLIENT_TEMP.hasChanges() && CLIENT_TEMP.hasNamesSet()){
            this.raceBuilds = CLIENT_TEMP.getNamesSetWithClean();
            clearAndInit();
        }
    }

    @Override
    protected void init() {
        super.init();
        int w = 100;
        int h = 20;
        this.grid = new GridWidget();
        this.grid.setColumnSpacing(5);
        this.grid.setRowSpacing(7);
        int row = 1;
        int column = 0;
        int maxBuildInRow = 5;
        this.modeButton = ButtonWidget.builder(
                Text.literal((removeMode) ? "Режим удаления" : "Режим редактирования"),
                (ButtonWidget.PressAction) b ->{
                    modeAction();
                }
        ).build();
        ButtonWidget returnButton = ButtonWidget.builder(
                Text.literal("Назад"),
                (ButtonWidget.PressAction) b -> {
                    client.setScreen(getAdminScreen());
                }
        ).build();
        this.grid.add(returnButton,0,0);
        this.grid.add(modeButton,0,1);
        if (this.raceBuilds.isEmpty()){
            BuildTemplateAction action = new BuildTemplateAction();
            action.setActionGetBuildsSet();
            action.setRace(this.race);
            ClientPlayNetworking.send(new RequestToDoActionWithTemplatesC2SPacket(action));
        }
        for (String buildName : raceBuilds){
            ButtonWidget buildButtonBuy = ButtonWidget.builder(
                    Text.literal(buildName),
                    (ButtonWidget.PressAction) b -> {
                        if (removeMode){
                            BuildTemplateAction action = new BuildTemplateAction();
                            action.setActionRemoveBuild();
                            action.setRace(this.race);
                            action.setName(buildName);
                            ClientPlayNetworking.send(
                                    new RequestToDoActionWithTemplatesC2SPacket(action)
                            );
                            this.raceBuilds.clear();
                            clearAndInit();
                            return;
                        }
                        client.setScreen(new AdminLevelScreen(this,race,buildName));
                    }
            ).build();
            grid.add(buildButtonBuy,row,column++);
            if(column > maxBuildInRow){
                row++;
                column=0;
            }
        }
        this.grid.setPosition(this.width/2-210,this.height/2-110);
        this.grid.refreshPositions();
        this.grid.forEachChild(this::addDrawableChild);
    }
}
