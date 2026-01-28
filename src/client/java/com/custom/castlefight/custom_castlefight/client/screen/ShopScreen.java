package com.custom.castlefight.custom_castlefight.client.screen;

import com.custom.castlefight.custom_castlefight.Custom_castlefight;
import com.custom.castlefight.custom_castlefight.Network.Packets.RequestToGiveC2SPacket;
import com.custom.castlefight.custom_castlefight.blocks.BuildBlock;
import com.custom.castlefight.custom_castlefight.blocks.ScanBlock;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;


public class ShopScreen extends Screen {
    public ShopScreen() {
        super(Text.literal("Магазин"));
    }

    private void onBuyClicked(){
        client.player.closeScreen();
        ClientPlayNetworking.send(new RequestToGiveC2SPacket(new ItemStack(BuildBlock.BuildBlock)));
        ClientPlayNetworking.send(new RequestToGiveC2SPacket(new ItemStack(ScanBlock.ScanBlock)));
    }
    @Override
    protected void init(){
        int x = this.width/2;
        int y = this.height/2;
        int w = 100;
        int h = 20;
        super.init();
        ButtonWidget button_test = ButtonWidget.builder(
                Text.literal("Дать блок"),
                (ButtonWidget.PressAction) b -> onBuyClicked()
                ).dimensions(x,y,w,h).build();
        this.addDrawableChild(button_test);
    }
    @Override
    public void render(DrawContext context,int mouseX,int mouseY,float delta){
        context.fill(0,0,this.width,this.height, 0x88000000);
        super.render(context,mouseX,mouseY,delta);
    }
}
