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

public class ChoiseRaceFragment implements IScreenFragment {
    private final List<String> racesForChoise;
    private String chooseRace = "";

    public ChoiseRaceFragment(List<String> race1) {
        racesForChoise = race1;
    }

    @Override
    public GridWidget buildWidgets(Screen screen) {
        GridWidget grid = new GridWidget();
        if (screen instanceof MainGameScreenHandled mainScreen) {
            int column = 0;
            int row = 2;
            int maxRaceInRow = 5;
            int finalRow = racesForChoise.size() / maxRaceInRow + 2;
            MatchUtilities.PlayerData data = mainScreen.getPlayerData();
            if (data.race != null && !data.race.isBlank()) {
                TextWidget raceText = new TextWidget(Text.literal(data.race), mainScreen.getTextRenderer());
                grid.add(raceText, 0, 0);
            }
            ButtonWidget choiseButton = ButtonWidget.builder(
                    Text.literal(chooseRace),
                    (button -> {
                        MatchUtilities.MatchAction action = new MatchUtilities.MatchAction();
                        action.setAction(MatchUtilities.ActionPlayer.CHOOSE_RACE);
                        action.setRace(chooseRace);
                        ClientPlayNetworking.send(new RequestToDoClientMatchActionC2SPacket(action));
                    })
            ).build();
            choiseButton.visible = false;
            choiseButton.active = false;
            grid.add(choiseButton, finalRow + 1, 0);
            for (String race : racesForChoise) {
                ButtonWidget raceButton = ButtonWidget.builder(
                        Text.literal(race),
                        (button -> {
                            if (data.race == null || race.isBlank()) {
                                chooseRace = race;
                                choiseButton.visible = true;
                                choiseButton.active = true;
                                choiseButton.setMessage(Text.literal("Выбрать расу "+chooseRace));
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
            return grid;
        }
        return null;
    }
}
