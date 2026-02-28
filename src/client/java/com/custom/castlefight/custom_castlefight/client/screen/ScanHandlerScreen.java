package com.custom.castlefight.custom_castlefight.client.screen;

import com.custom.castlefight.custom_castlefight.screenhandler.ScanScreen;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
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
    private ServerWorld world;
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
        int x = this.width/2;
        int y = this.height/2;
        int w = 100;
        int h = 20;
        super.init();
        ButtonWidget button = ButtonWidget.builder(
                Text.literal("камшот"),
                btn -> {
                    if (client.interactionManager != null){
                        client.interactionManager.clickButton(handler.syncId,1);
                    }
                    client.currentScreen.close();
                }
        ).dimensions(x,y,w,h).build();
        this.addDrawableChild(button);
    }



    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        context.fill(0,0,this.width,this.height, 0x88000000);
    }


}
