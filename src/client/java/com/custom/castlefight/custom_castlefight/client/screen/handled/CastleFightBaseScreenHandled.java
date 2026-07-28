package com.custom.castlefight.custom_castlefight.client.screen.handled;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.CustomFunc.StorageUpdateListener;
import com.custom.castlefight.custom_castlefight.Network.screenhandler.CastleFightBaseScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

import static com.custom.castlefight.custom_castlefight.client.Custom_castlefightClient.CLIENT_TEMP;

public abstract class CastleFightBaseScreenHandled<T extends CastleFightBaseScreenHandler>
                                                extends HandledScreen<T>
                                                implements StorageUpdateListener {
    public CastleFightBaseScreenHandled(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }
    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(
                this.textRenderer,
                this.title,
                this.width / 2,
                this.height - 30,
                0x404040,
                false
        );
    }
    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        context.fill(0, 0, this.width, this.height, 0x88000000);
    }

    @Override
    protected void init() {
        super.init();
        CLIENT_TEMP.subscribe(this);
    }

    @Override
    public void removed() {
        super.removed();
        CLIENT_TEMP.unsubscribe(this);
    }

    @Override
    public abstract void onStorageUpdate(MatchUtilities.MatchData data);
}
