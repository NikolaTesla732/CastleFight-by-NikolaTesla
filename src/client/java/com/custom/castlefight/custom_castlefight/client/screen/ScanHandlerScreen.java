package com.custom.castlefight.custom_castlefight.client.screen;

import com.custom.castlefight.custom_castlefight.screenhandler.ScanScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;

public class ScanHandlerScreen extends HandledScreen<ScanScreen> {
    public ScanHandlerScreen(ScanScreen handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
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
    private void OnscanClicked(){
        client.player.closeScreen();
    }
    @Override
    protected void init() {
        int x = this.width/2;
        int y = this.height/2;
        int w = 100;
        int h = 20;
        super.init();
        ButtonWidget button = ButtonWidget.builder(
                Text.literal("камшот"),
                btn -> OnscanClicked()
        ).dimensions(x,y,w,h).build();
        this.addDrawableChild(button);
    }



    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        context.fill(0,0,this.width,this.height, 0x88000000);
    }
}
