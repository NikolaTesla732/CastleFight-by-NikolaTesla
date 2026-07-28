package com.custom.castlefight.custom_castlefight.client.screen;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToDoClientMatchActionC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;
import static com.custom.castlefight.custom_castlefight.client.Custom_castlefightClient.CLIENT_TEMP;

public class MatchManagerScreen extends CastleFightBaseScreen {
    private GridWidget grid;
    private List<UUID> matches = new ArrayList<>();

    protected MatchManagerScreen() {
        super(Text.literal("Менеджер матчей"));
    }

    @Override
    public void onStorageUpdate(MatchUtilities.MatchData data) {
        switch (data){
            case MATCHES -> this.matches = CLIENT_TEMP.getMatches();
        }
        clearAndInit();
    }

    public void setMatches(List<UUID> matchList){
        this.matches = matchList;
    }
    @Override
    protected void init() {
        super.init();
        if (matches.isEmpty()){
            MatchUtilities.MatchAction action = new MatchUtilities.MatchAction();
            action.setAction(MatchUtilities.ActionPlayer.GET_MATCHES);
            ClientPlayNetworking.send(new RequestToDoClientMatchActionC2SPacket(action));
        }
        int column = 0;
        int row = 1;
        this.grid = new GridWidget();
        int maxButtonsInRow = 5;
        this.grid.setColumnSpacing(8);
        this.grid.setRowSpacing(6);
        for (UUID uuid : matches) {
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

        ButtonWidget returnButton = ButtonWidget.builder(Text.literal("Назад"),(button) -> {
            client.setScreen(new AdminScreen(Text.literal("Admin")));
                }).size(100,20).build();
        this.grid.add(returnButton,0,0);
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
