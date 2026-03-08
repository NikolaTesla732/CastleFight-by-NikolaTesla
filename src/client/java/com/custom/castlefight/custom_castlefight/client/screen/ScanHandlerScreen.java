package com.custom.castlefight.custom_castlefight.client.screen;

import com.custom.castlefight.custom_castlefight.screenhandler.ScanScreen;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import com.custom.castlefight.custom_castlefight.CustomFunc.BuildFunc;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;

import java.util.List;

public class ScanHandlerScreen extends HandledScreen<ScanScreen> {
    private BlockPos pos;
    private GridWidget grid;
    public ScanHandlerScreen(ScanScreen handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
         pos = handler.getBlockPos();
         LOGGER.info(pos.toString());
    }

    @Override
    protected void drawForeground(DrawContext context,int mouseX,int mouseY){
        context.drawText(
                this.textRenderer,
                this.title,
                this.width/2,
                this.height-30,
                0x404040,
                false
        );
    }

    @Override
    protected void init() {
        super.init();
        this.grid = new GridWidget();
        this.grid.setRowSpacing(6);
        this.grid.setColumnSpacing(8);
        int w = 100;
        int h = 20;
        TextWidget name_text = new TextWidget(Text.literal("Название сканируемого здания"),this.textRenderer);
        TextWidget level_text = new TextWidget(Text.literal("Уровень здания"),this.textRenderer);

        ButtonWidget scan_button = ButtonWidget.builder(
                Text.literal("Отсканировать"),
                btn -> {
                    if (client.interactionManager != null){
                        client.interactionManager.clickButton(handler.syncId,1);
                    }
                    client.currentScreen.close();
                }
        ).size(100,20).build();

        TextFieldWidget name_input = new TextFieldWidget(this.textRenderer,100,20,Text.literal("Введите желаемое название для здания"));
        TextFieldWidget level_input = new TextFieldWidget(this.textRenderer,30,20,Text.literal("Введите уровень здания"));

        this.grid.add(name_text,0,0);
        this.grid.add(name_input,1,0);
        this.grid.add(level_text,2,0);
        this.grid.add(level_input,3,0);
        this.grid.add(scan_button,4,0);

        this.grid.setPosition(this.width/2-210,this.height/2-110);
        this.grid.refreshPositions();
        this.grid.forEachChild(this::addDrawableChild);
    }



    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        context.fill(0,0,this.width,this.height, 0x88000000);
    }


}
