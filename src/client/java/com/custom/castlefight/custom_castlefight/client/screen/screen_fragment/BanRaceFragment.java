package com.custom.castlefight.custom_castlefight.client.screen.screen_fragment;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToDoClientMatchActionC2SPacket;
import com.custom.castlefight.custom_castlefight.client.screen.handled.MainGameScreenHandled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.UUID;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;

public class BanRaceFragment implements IScreenFragment {
    final List<String> races;
    private String chooseRace = "";


    public BanRaceFragment(List<String> data) {
        this.races = data;
    }

    @Override
    public GridWidget buildWidgets(Screen screen) {
        GridWidget grid = new GridWidget();
        if (screen instanceof MainGameScreenHandled mainScreen) {
            int column = 0;
            int row = 0;
            int maxRaceInRow = 5;
            int finalRow = races.size() / maxRaceInRow + 1;
            MatchUtilities.PlayerData data = mainScreen.getPlayerData();
            ButtonWidget buttonBan = ButtonWidget.builder(
                    Text.literal(chooseRace),
                    (button1 -> {
                        MatchUtilities.MatchAction action = new MatchUtilities.MatchAction();
                        action.setAction(MatchUtilities.ActionPlayer.BAN_RACE);
                        action.setRace(chooseRace);
                        ClientPlayNetworking.send(new RequestToDoClientMatchActionC2SPacket(action));
                    })
            ).build();
            buttonBan.visible = false;
            buttonBan.active = false;
            grid.add(buttonBan, finalRow, 0);
            for (String race : races) {
                ButtonWidget raceButton = ButtonWidget.builder(
                        Text.literal(race),
                        (button -> {
                            if (data.canBan) {
                                chooseRace = race;
                                buttonBan.setMessage(Text.literal("Забанить " + chooseRace));
                                buttonBan.visible = true;
                                buttonBan.active = true;
                                mainScreen.updateGrid();
                            }
                        })
                ).size(100, 20).build();
                grid.add(raceButton, row, column++);
                if (column > maxRaceInRow) {
                    column = 0;
                    row++;
                }
            }
        }
        return grid;
    }
}
