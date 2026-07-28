package com.custom.castlefight.custom_castlefight.client.screen.screen_fragment;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToDoClientMatchActionC2SPacket;
import com.custom.castlefight.custom_castlefight.client.screen.handled.MainGameScreenHandled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.MultilineTextWidget;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Map;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;

public class ChoiseTeamFragment implements IScreenFragment{
    Map<MatchUtilities.TeamColor, List<String>> teamList;
    public ChoiseTeamFragment(Map<MatchUtilities.TeamColor, List<String>> teams){
        teamList = teams;
    }
    @Override
    public GridWidget buildWidgets(Screen screen) {
        GridWidget grid = new GridWidget();
        if (screen instanceof MainGameScreenHandled mainScreen){
            int column = 0;
            int row = 0;
            for (MatchUtilities.TeamColor color: teamList.keySet()){
                StringBuilder teamToRender = new StringBuilder();
                for (String player : teamList.get(color)){
                    teamToRender.append(player);
                    teamToRender.append('\n');
                }
                MultilineTextWidget teamText = new MultilineTextWidget(Text.literal(teamToRender.toString()),screen.getTextRenderer()).setMaxWidth(150);
                grid.add(teamText,row++,column);
                grid.add(ButtonWidget.builder(
                        Text.literal(color.toString()),
                        (button -> {
                            MatchUtilities.MatchAction action = new MatchUtilities.MatchAction();
                            action.setAction(MatchUtilities.ActionPlayer.JOIN_TEAM);
                            action.setTeam(color);
                            ClientPlayNetworking.send(new RequestToDoClientMatchActionC2SPacket(action));
                    })
                ).size(120,20).build(),row--,column,grid.copyPositioner().alignHorizontalCenter());
                column+=2;
            }
            return grid;
        }
        return null;
    }
}
