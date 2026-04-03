package com.custom.castlefight.custom_castlefight.client.screen;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildFunc;
import com.custom.castlefight.custom_castlefight.Custom_castlefight;
import com.custom.castlefight.custom_castlefight.Network.Packets.RequestToGiveC2SPacket;
import com.custom.castlefight.custom_castlefight.blocks.BuildBlock;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.AbstractBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.Map;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.TEMPLATES;

public class RaceBuildsScreen extends Screen {
    private String race;
    private GridWidget grid;
    public RaceBuildsScreen(String race) {
        super(Text.of("Экран покупки здания"));
        this.race = race;
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
        Map<String, Map<Integer, BuildFunc.BuildTemplate>> raceBuilds =
                TEMPLATES.getRaceBuilds(race);
        for (String buildName : raceBuilds.keySet()){
            BuildFunc.BuildTemplate build = raceBuilds.get(buildName).get(1);
            String buildId = build.getRace()+":"+build.getName()+":"+build.getLevel();
            NbtCompound nbt = new NbtCompound();
            nbt.putString("buildId", buildId);
            ItemStack stack = new ItemStack(BuildBlock.BuildBlock);
            stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
            ButtonWidget buildButtonBuy = ButtonWidget.builder(
                    Text.literal(build.getName()),
                    (ButtonWidget.PressAction) b -> {
                        ClientPlayNetworking.send(new RequestToGiveC2SPacket(stack));
                        client.player.closeScreen();
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
