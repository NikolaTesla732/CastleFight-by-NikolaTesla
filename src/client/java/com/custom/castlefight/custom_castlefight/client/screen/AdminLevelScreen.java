package com.custom.castlefight.custom_castlefight.client.screen;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildUtilities.BuildTemplate;
import com.custom.castlefight.custom_castlefight.CustomFunc.BuildTemplateAction;
import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToDoActionWithTemplatesC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.Set;

import static com.custom.castlefight.custom_castlefight.client.Custom_castlefightClient.CLIENT_TEMP;

public class AdminLevelScreen extends CastleFightBaseScreen {
    private GridWidget grid;
    private String race, name;
    private AdminBuildsScreen buildScreen;
    private boolean remove = false, needShowBuild = false;
    private ButtonWidget modeButton;
    private Set<Integer> levelsSet = new HashSet<>();
    private BuildTemplate build;

    protected AdminLevelScreen(AdminBuildsScreen buildScreen, String race, String name) {
        super(Text.literal("AdminLevelScreen"));
        this.race = race;
        this.name = name;
        this.buildScreen = buildScreen;
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
        switch (data) {
            case LEVELS_SET -> levelsSet = CLIENT_TEMP.getLevelsSet();
            case NEW_BUILD -> {
                build = CLIENT_TEMP.getNewBuild();
                onLevelClicked();
                return;
            }
        }
        clearAndInit();
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
        this.modeButton = ButtonWidget.builder(
                Text.literal((this.remove) ? "Режим удаления" : "Режим редактирования"),
                (ButtonWidget.PressAction) b -> {
                    onModeClicked();
                }
        ).build();
        this.grid.add(this.modeButton, 0, 1);
        ButtonWidget returnButton = ButtonWidget.builder(
                Text.literal("Назад"),
                (ButtonWidget.PressAction) b -> {
                    client.setScreen(this.buildScreen);
                }
        ).build();
        this.grid.add(returnButton, 0, 0);
        if (build != null){
            buildEditWidgets();
        }
        if (this.levelsSet.isEmpty()) {
            BuildTemplateAction action = new BuildTemplateAction();
            action.setActionGetLevelsSet();
            action.setRace(race);
            action.setName(name);
            ClientPlayNetworking.send(new RequestToDoActionWithTemplatesC2SPacket(action));
        }

        for (int level : levelsSet) {
            ButtonWidget buildButtonBuy = ButtonWidget.builder(
                    Text.literal(String.valueOf(level)),
                    (ButtonWidget.PressAction) b -> {
                        BuildTemplateAction action = new BuildTemplateAction();
                        action.setActionGetBuild();
                        action.setRace(race);
                        action.setName(name);
                        action.setLevel(level);
                        ClientPlayNetworking.send(new RequestToDoActionWithTemplatesC2SPacket(action));
                    }
            ).width(30).build();
            grid.add(buildButtonBuy, row++, column);
        }
        this.grid.setPosition(this.width / 2 - 220, this.height / 2 - 110);
        this.grid.refreshPositions();
        this.grid.forEachChild(this::addDrawableChild);
    }

    private void onModeClicked() {
        this.remove = !this.remove;
        clearAndInit();
    }
    protected void onLevelClicked() {
        if (this.remove) {
            BuildTemplateAction action = new BuildTemplateAction();
            action.setActionRemoveLevel();
            action.setNewBuild(build);
            ClientPlayNetworking.send(new RequestToDoActionWithTemplatesC2SPacket(action));
            this.levelsSet.clear();
            this.build = null;
        }
        clearAndInit();
    }
    protected void buildEditWidgets() {
        TextWidget nameText = new TextWidget(
                Text.literal("Название " + build.getDisplayName()),
                this.getTextRenderer()
        );
        TextWidget raceText = new TextWidget(
                Text.literal("Раса " + build.getRace()),
                this.getTextRenderer()
        );
        TextWidget levelText = new TextWidget(
                Text.literal("уровень " + build.getLevel()),
                this.getTextRenderer()
        );
        TextWidget costText = new TextWidget(
                Text.literal("Стоимость " + build.getCost()),
                this.getTextRenderer()
        );
        TextWidget incomeText = new TextWidget(
                Text.literal("Доход " + build.getIncome()),
                this.getTextRenderer()
        );
        TextWidget cdText = new TextWidget(
                Text.literal("Задержка спавна " + build.getSpawnCD()),
                this.getTextRenderer()
        );

        this.grid.add(nameText, 1, 1);
        this.grid.add(raceText, 2, 1);
        this.grid.add(levelText, 3, 1);
        this.grid.add(costText, 4, 1);
        this.grid.add(incomeText, 5, 1);
        this.grid.add(cdText, 6, 1);

        TextFieldWidget nameInput = new TextFieldWidget(this.textRenderer, 100, 20, Text.literal("Введите желаемое название для здания:"));
        TextFieldWidget levelInput = new TextFieldWidget(this.textRenderer, 30, 20, Text.literal("Введите уровень здания:"));
        TextFieldWidget costInput = new TextFieldWidget(this.textRenderer, 30, 20, Text.literal("Введите стоимость здания"));
        TextFieldWidget cdInput = new TextFieldWidget(this.textRenderer, 30, 20, Text.literal("Введите кулдаун здания:"));
        TextFieldWidget incomeInput = new TextFieldWidget(this.textRenderer, 30, 20, Text.literal("Введите доход здания:"));
        TextFieldWidget raceInput = new TextFieldWidget(this.textRenderer, 100, 20, Text.literal("Введите расу здания:"));

        this.grid.add(nameInput, 1, 2);
        this.grid.add(raceInput, 2, 2);
        this.grid.add(levelInput, 3, 2);
        this.grid.add(costInput, 4, 2);
        this.grid.add(incomeInput, 5, 2);
        this.grid.add(cdInput, 6, 2);

        ButtonWidget button = ButtonWidget.builder(
                Text.literal("Сохранить"),
                (ButtonWidget.PressAction) b -> {
                    String name = nameInput.getText(), race = raceInput.getText();
                    String level1 = levelInput.getText(),
                            income1 = incomeInput.getText(),
                            cooldown1 = cdInput.getText(),
                            cost1 = costInput.getText();
                    int level, income, cooldown, cost;
                    if (name.isBlank()) name = build.getName();
                    if (race.isBlank()) race = build.getRace();
                    if (level1.isBlank() || !isInt(level1)) level = build.getLevel();
                    else level = Integer.parseInt(level1);
                    if (income1.isBlank() || !isInt(income1)) income = build.getIncome();
                    else income = Integer.parseInt(income1);
                    if (cost1.isBlank() || !isInt(cost1)) cost = build.getCost();
                    else cost = Integer.parseInt(cost1);
                    if (cooldown1.isBlank() || !isInt(cooldown1)) cooldown = build.getSpawnCD();
                    else cooldown = Integer.parseInt(cooldown1);
                    BuildTemplate newBuild = new BuildTemplate(
                            name, race, level, build.getBlocks(), income, cooldown, cost
                    );
                    BuildTemplateAction action = new BuildTemplateAction();
                    action.setActionReplaceBuild();
                    action.setNewBuild(newBuild);
                    action.setOldBuild(build);
                    ClientPlayNetworking.send(new RequestToDoActionWithTemplatesC2SPacket(action));
                    if (!raceInput.getText().isBlank() && !raceInput.getText().equals(build.getRace())) {
                        client.setScreen(new AdminScreen(Text.literal("Admin")));
                        this.build = null;
                        return;
                    }
                    if (!nameInput.getText().isBlank() &&
                            !BuildTemplate.normalize(nameInput.getText()).equals(build.getName())) {
                        client.setScreen(new AdminBuildsScreen(
                                this.buildScreen.getAdminScreen(),
                                BuildTemplate.normalize(this.race))
                        );
                        this.build = null;
                        return;
                    }
                    this.build = null;
                    clearAndInit();
                }
        ).build();
        this.grid.add(button, 7, 2);
    }
}
