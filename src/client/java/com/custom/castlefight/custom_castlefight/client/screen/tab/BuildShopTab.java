package com.custom.castlefight.custom_castlefight.client.screen.tab;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildUtilities;
import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToGiveC2SPacket;
import com.custom.castlefight.custom_castlefight.blocks.BuildingBlock;
import com.custom.castlefight.custom_castlefight.blocks.ConstructionBlock;
import com.custom.castlefight.custom_castlefight.client.screen.PlayingScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.widget.*;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;
import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;
import static com.custom.castlefight.custom_castlefight.client.Custom_castlefightClient.CLIENT_TEMP;

public class    BuildShopTab implements Tab {
    private final TextRenderer textRenderer;
    private DirectionalLayoutWidget verticalLayout;
    private BuildUtilities.rawBuildTemplate template;
    private TextWidget buildNameText;
    private TextWidget healthText,armorText;
    private TextWidget damageText,damageTypeText;
    private MultilineTextWidget descriptionText;
    private TextWidget costGoldText,costWoodText,incomeText,spawnText;
    private ButtonWidget buyBuild;
    private TexturedButtonWidget iconBuild;

    public BuildShopTab(TextRenderer textRenderer) {
        super();
        MatchUtilities.PlayerData data = CLIENT_TEMP.getPlayerData();
        this.textRenderer = textRenderer;
        if (data == null) return;
        verticalLayout = DirectionalLayoutWidget.vertical().spacing(2);
        BuildUtilities.racesManager manager = CLIENT_TEMP.getRaceManager();
        verticalLayout.add(new TextWidget(Text.literal(data.race),textRenderer));
        GridWidget grid = new GridWidget();
        grid.setColumnSpacing(3);
        grid.setRowSpacing(2);
        double compensation = 3.0 / MinecraftClient.getInstance().getWindow().getScaleFactor();
        int widgetSize = (int) (156 * compensation);
        int row = 0,column = 0, maxBuildInRow = 4;
        for (BuildUtilities.rawBuildTemplate template : manager.races.get(data.race).builds()){
            if (template.level() != 1) continue;
            grid.add(ButtonWidget.builder(Text.literal(template.displayName()),
                    (button)->{
                    this.template = template;
                    changeStats();
                    showStats(true);
            }).size(widgetSize,32).build(),row,column++);
            if (column >= maxBuildInRow){
                column = 0;
                row++;
            }
        }
        verticalLayout.add(grid);
        DirectionalLayoutWidget statsLine = DirectionalLayoutWidget.horizontal();
        DirectionalLayoutWidget line1 = DirectionalLayoutWidget.vertical();
        DirectionalLayoutWidget line2 = DirectionalLayoutWidget.vertical();
        line1.spacing(4);
        line2.spacing(4);
        statsLine.spacing(5);
        iconBuild = new TexturedButtonWidget(128,128, new ButtonTextures(Identifier.of(MOD_ID,"build")),
                (b) -> {},Text.empty());
        buildNameText = new TextWidget(Text.literal("Название: "),textRenderer);
        healthText = new TextWidget(Text.literal("Здоровье: "),textRenderer);
        armorText = new TextWidget(Text.literal("Тип защиты: "),textRenderer);
        damageText = new TextWidget(Text.literal("Урон: "),textRenderer);
        damageTypeText = new TextWidget(Text.literal("Тип урона: "),textRenderer);
        descriptionText = new MultilineTextWidget(Text.literal("Описание: "),textRenderer);
        costGoldText = new TextWidget(Text.literal("Золото: "),textRenderer);
        costWoodText = new TextWidget(Text.literal("Дерево: "),textRenderer);
        incomeText = new TextWidget(Text.literal("Доход: "),textRenderer);
        spawnText = new TextWidget(Text.literal("Время появления: "),textRenderer);
        buyBuild = ButtonWidget.builder(Text.empty(),(b)->{
            ItemStack stack = new ItemStack(ConstructionBlock.CONSTRUCTION_BLOCK);
            NbtCompound nbt = new NbtCompound();
            nbt.putString("buildId", template.race() + ":" + template.name() + ":" + String.valueOf(template.level()));
            stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
            ClientPlayNetworking.send(new RequestToGiveC2SPacket(stack));
            MinecraftClient.getInstance().player.closeScreen();
        }).build();
        statsLine.add(iconBuild);
        line1.add(buildNameText);
        line1.add(healthText);
        line1.add(armorText);
        line1.add(damageText);
        line1.add(damageTypeText);
        line2.add(new TextWidget(Text.empty(),textRenderer));
        line2.add(costGoldText);
        line2.add(costWoodText);
        line2.add(incomeText);
        line2.add(spawnText);
        statsLine.add(line1);
        statsLine.add(line2);
        statsLine.add(buyBuild);
        verticalLayout.add(statsLine);
        showStats(false);
    }
    public void showStats(boolean show){
        buildNameText.visible = show;
        healthText.visible = show ;
        armorText.visible = show ;
        damageText.visible = show ;
        damageTypeText.visible = show ;
        descriptionText.visible = show ;
        costGoldText.visible = show ;
        costWoodText.visible = show ;
        incomeText.visible = show ;
        spawnText.visible = show ;
        buyBuild.visible = show;
        buyBuild.active = show;
        iconBuild.visible = show;
        iconBuild.active = show;
    }
    public void changeStats(){
        buildNameText.setMessage(Text.literal("Название: " + template.displayName()));
        healthText.setMessage(Text.literal("Здоровье: "+String.valueOf(10.0)));
        armorText.setMessage(Text.literal("Тип защиты: нет"));
        damageText.setMessage(Text.literal("Урон: 2.0"));
        damageTypeText.setMessage(Text.literal("Тип урона: хаос"));
        descriptionText.setMessage(Text.literal(""));
        costGoldText.setMessage(Text.literal("Золото: "+String.valueOf(template.cost())));
        costWoodText.setMessage(Text.literal("Дерево: "+String.valueOf(0)));
        incomeText.setMessage(Text.literal("Доход: "+String.valueOf(template.income())));
        spawnText.setMessage(Text.literal("Время появления: "+String.valueOf(template.spawnCD())));
        buyBuild.setMessage(Text.literal("Купить " + template.displayName()));
        verticalLayout.refreshPositions();
    }
    @Override
    public Text getTitle() {
        return Text.literal("Магазин зданий");
    }

    @Override
    public Text getNarratedHint() {
        return Text.empty();
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
        verticalLayout.forEachChild(consumer);
    }

    @Override
    public void refreshGrid(ScreenRect tabArea) {
        int x = tabArea.getLeft() -5;
        int y = tabArea.getTop()+10;
        verticalLayout.setPosition(x,y);
        verticalLayout.refreshPositions();
    }

}
