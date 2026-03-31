package com.custom.castlefight.custom_castlefight.client.screen;

import com.custom.castlefight.custom_castlefight.Network.Packets.RequestToScanC2SPacket;
import com.custom.castlefight.custom_castlefight.screenhandler.ScanScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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
    private TextFieldWidget name_input;
    private TextFieldWidget level_input;
    private TextFieldWidget cost_input;
    private TextFieldWidget cd_input;
    private TextFieldWidget income_input;
    private TextWidget name_text;
    private TextWidget level_text;
    private TextWidget cost_text;
    private TextWidget income_text;
    private TextWidget cd_text;
    public ScanHandlerScreen(ScanScreen handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
         pos = handler.getBlockPos();

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
    public static boolean isInt(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void OnScanClicked(){

        if (!this.name_input.getText().isBlank() && !this.level_input.getText().isBlank() &&
                !this.cost_input.getText().isBlank() && !this.income_input.getText().isBlank() &&
                !this.cd_input.getText().isBlank()){
            String name = this.name_input.getText();
            if (isInt(this.level_input.getText()) && isInt(this.cost_input.getText())&&
                    isInt(this.income_input.getText())&&isInt(this.cd_input.getText())){
                int level = Integer.parseInt(this.level_input.getText());
                int cost = Integer.parseInt(this.cost_input.getText());
                int income = Integer.parseInt(this.income_input.getText());
                int cooldown = Integer.parseInt(this.cd_input.getText());
                ClientPlayNetworking.send(new RequestToScanC2SPacket(name, level, cost, income, cooldown));
                client.currentScreen.close();
            }
        }
    }
    @Override
    protected void init() {
        super.init();
        this.grid = new GridWidget();
        this.grid.setRowSpacing(6);
        this.grid.setColumnSpacing(8);
        int w = 100;
        int h = 20;
        this.name_text = new TextWidget(Text.literal("Название сканируемого здания:"),this.textRenderer);
        this.level_text = new TextWidget(Text.literal("Уровень здания:"),this.textRenderer);
        this.cost_text = new TextWidget(Text.literal("Введите стоимость здания:"),this.textRenderer);
        this.income_text = new TextWidget(Text.literal("Введите доход здания:"),this.textRenderer);
        this.cd_text = new TextWidget(Text.literal("Введите кулдаун здания:"),this.textRenderer);

        ButtonWidget scan_button = ButtonWidget.builder(
                Text.literal("Отсканировать"),
                btn -> {
                    if (client.interactionManager != null){
                        OnScanClicked();
                    }

                }
        ).size(100,20).build();

        this.name_input = new TextFieldWidget(this.textRenderer,100,20,Text.literal("Введите желаемое название для здания:"));
        this.level_input = new TextFieldWidget(this.textRenderer,30,20,Text.literal("Введите уровень здания:"));
        this.cost_input = new TextFieldWidget(this.textRenderer,30,20,Text.literal("Введите стоимость здания"));
        this.cd_input = new TextFieldWidget(this.textRenderer,30,20,Text.literal("Введите кулдаун здания:"));
        this.income_input = new TextFieldWidget(this.textRenderer,30,20,Text.literal("Введите доход здания:"));
        this.grid.add(this.name_text,0,0);
        this.grid.add(this.level_text,1,0);
        this.grid.add(this.cost_text,2,0);
        this.grid.add(this.income_text,3,0);
        this.grid.add(this.cd_text,4,0);

        this.grid.add(this.name_input,0,1);
        this.grid.add(this.level_input,1,1);
        this.grid.add(this.cost_input,2,1);
        this.grid.add(this.income_input,3,1);
        this.grid.add(this.cd_input,4,1);

        this.grid.add(scan_button,6,0);
        this.grid.setPosition(this.width/2-210,this.height/2-110);
        this.grid.refreshPositions();
        this.grid.forEachChild(this::addDrawableChild);
    }



    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        context.fill(0,0,this.width,this.height, 0x88000000);
    }


}
