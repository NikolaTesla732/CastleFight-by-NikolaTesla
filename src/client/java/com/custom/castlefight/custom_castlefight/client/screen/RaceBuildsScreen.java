package com.custom.castlefight.custom_castlefight.client.screen;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildUtilities;
import com.custom.castlefight.custom_castlefight.CustomFunc.BuildTemplateAction;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToDoActionWithTemplatesC2SPacket;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToGiveC2SPacket;
import com.custom.castlefight.custom_castlefight.blocks.ConstructionBlock;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

import static com.custom.castlefight.custom_castlefight.client.Custom_castlefightClient.CLIENT_TEMP;

public class RaceBuildsScreen extends Screen {
    private String race;
    private GridWidget grid;
    private Set<String> namesSet = new HashSet<>();
    @Nullable
    private BuildUtilities.BuildTemplate build;
    private boolean needGiveBuild = false,canBuildButtons = false;
    public RaceBuildsScreen(String race) {
        super(Text.of("Экран покупки здания"));
        this.race = race;
    }

    @Override
    public void tick() {
        super.tick();
        if (CLIENT_TEMP.getChanges() ){
            if (CLIENT_TEMP.hasNamesSet()) this.namesSet = CLIENT_TEMP.getNamesSetWithClean();
            if (CLIENT_TEMP.hasNewBuild()) {
                this.needGiveBuild = true;
                this.build = CLIENT_TEMP.getNewBuildWithClean();
                giveBuild();
            }
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
        int row = 0;
        int column = 0;
        int maxBuildInRow = 5;
        if (this.namesSet.isEmpty()){
            BuildTemplateAction action = new BuildTemplateAction();
            action.setActionGetBuildsSetLevelN();
            action.setRace(this.race);
            action.setLevel(1);
            ClientPlayNetworking.send(new RequestToDoActionWithTemplatesC2SPacket(action));
        }
        for (String buildName : namesSet) {
            ButtonWidget buildButtonBuy = ButtonWidget.builder(
                    Text.literal(buildName),
                    (ButtonWidget.PressAction) b -> {
                        if (build == null) {
                            BuildTemplateAction action = new BuildTemplateAction();
                            action.setActionGetBuild();
                            action.setRace(race);
                            action.setName(buildName);
                            action.setLevel(1);
                            ClientPlayNetworking.send(new RequestToDoActionWithTemplatesC2SPacket(action));
                            return;
                        }
                        giveBuild();

                    }
            ).build();
            grid.add(buildButtonBuy, row, column++);
            if (column > maxBuildInRow) {
                row++;
                column = 0;
            }
        }

        this.grid.setPosition(this.width/2-210,this.height/2-110);
        this.grid.refreshPositions();
        this.grid.forEachChild(this::addDrawableChild);
    }
    private void giveBuild(){
        String buildId = build.getRace()+":"+build.getName()+":"+build.getLevel();
        NbtCompound nbt = new NbtCompound();
        nbt.putString("buildId", buildId);
        ItemStack stack = new ItemStack(ConstructionBlock.constructionBlock);
        stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        ClientPlayNetworking.send(new RequestToGiveC2SPacket(stack));
        client.player.closeScreen();
    }
}
