package com.custom.castlefight.custom_castlefight.client.screen;

import com.custom.castlefight.custom_castlefight.Network.Packets.RequestToScanC2SPacket;
import com.custom.castlefight.custom_castlefight.screenhandler.ScanScreen;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class ScanHandlerScreen extends HandledScreen<ScanScreen> {
    private BlockPos pos;
    private GridWidget grid;
    private TextFieldWidget nameInput;
    private TextFieldWidget levelInput;
    private TextFieldWidget costInput;
    private TextFieldWidget cdInput;
    private TextFieldWidget incomeInput;
    private TextWidget nameText;
    private TextWidget levelText;
    private TextWidget costText;
    private TextWidget incomeText;
    private TextWidget cdText;
    private TextWidget raceText;
    private TextFieldWidget raceInput;
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

        if (!this.nameInput.getText().isBlank() && !this.levelInput.getText().isBlank() &&
                !this.costInput.getText().isBlank() && !this.incomeInput.getText().isBlank() &&
                !this.cdInput.getText().isBlank() && !this.raceInput.getText().isBlank()){
            String name = this.nameInput.getText();
            String race = this.raceInput.getText();
            if (isInt(this.levelInput.getText()) && isInt(this.costInput.getText())&&
                    isInt(this.incomeInput.getText())&&isInt(this.cdInput.getText())){
                int level = Integer.parseInt(this.levelInput.getText());
                int cost = Integer.parseInt(this.costInput.getText());
                int income = Integer.parseInt(this.incomeInput.getText());
                int cooldown = Integer.parseInt(this.cdInput.getText());
                ClientPlayNetworking.send(new RequestToScanC2SPacket(name,race, level, cost, income, cooldown));
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
        this.nameText = new TextWidget(Text.literal("Название сканируемого здания:"),this.textRenderer);
        this.levelText = new TextWidget(Text.literal("Уровень здания:"),this.textRenderer);
        this.costText = new TextWidget(Text.literal("Введите стоимость здания:"),this.textRenderer);
        this.incomeText = new TextWidget(Text.literal("Введите доход здания:"),this.textRenderer);
        this.cdText = new TextWidget(Text.literal("Введите кулдаун здания:"),this.textRenderer);
        this.raceText = new TextWidget(Text.literal("Введите расу здания:"),this.textRenderer);
        ButtonWidget scan_button = ButtonWidget.builder(
                Text.literal("Отсканировать"),
                btn -> {
                    if (client.interactionManager != null){
                        OnScanClicked();
                    }

                }
        ).size(100,20).build();

        this.nameInput = new TextFieldWidget(this.textRenderer,100,20,Text.literal("Введите желаемое название для здания:"));
        this.levelInput = new TextFieldWidget(this.textRenderer,30,20,Text.literal("Введите уровень здания:"));
        this.costInput = new TextFieldWidget(this.textRenderer,30,20,Text.literal("Введите стоимость здания"));
        this.cdInput = new TextFieldWidget(this.textRenderer,30,20,Text.literal("Введите кулдаун здания:"));
        this.incomeInput = new TextFieldWidget(this.textRenderer,30,20,Text.literal("Введите доход здания:"));
        this.raceInput = new TextFieldWidget(this.textRenderer,100,20,Text.literal("Введите расу здания:"));
        this.grid.add(this.nameText,0,0);
        this.grid.add(this.raceText,1,0);
        this.grid.add(this.levelText,2,0);
        this.grid.add(this.costText,3,0);
        this.grid.add(this.incomeText,4,0);
        this.grid.add(this.cdText,5,0);

        this.grid.add(this.nameInput,0,1);
        this.grid.add(this.raceInput,1,1);
        this.grid.add(this.levelInput,2,1);
        this.grid.add(this.costInput,3,1);
        this.grid.add(this.incomeInput,4,1);
        this.grid.add(this.cdInput,5,1);

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
