package com.custom.castlefight.custom_castlefight.client.screen;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchesUtilities;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;

import java.util.UUID;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;

public class MatchManagerScreen extends Screen {
    private GridWidget grid;

    protected MatchManagerScreen() {
        super(Text.literal("Менеджер матчей"));

    }

    @Override
    protected void init() {
        super.init();
        int column = 0;
        int row = 0;
        this.grid = new GridWidget();
        int maxButtonsInRow = 5;
        this.grid.setColumnSpacing(8);
        this.grid.setRowSpacing(6);
        for (UUID uuid : MatchesUtilities.MatchManager.getInstance().getMatches().keySet()) {
            ButtonWidget buttonWidget = ButtonWidget.builder(
                    Text.literal(uuid.toString()),
                    (button -> {
                        LOGGER.info(uuid.toString());
                    })
            ).size(100,20).build();
            this.grid.add(buttonWidget, row, column++);
            if (column >= maxButtonsInRow) {
                column = 0;
                row++;
            }
        }
        this.grid.setPosition(this.width / 2 - 210, this.height / 2 - 110);
        this.grid.refreshPositions();
        this.grid.forEachChild(this::addDrawableChild);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x88000000);
        super.render(context, mouseX, mouseY, delta);
    }
}
