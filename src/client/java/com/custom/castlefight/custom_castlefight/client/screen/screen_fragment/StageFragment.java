package com.custom.castlefight.custom_castlefight.client.screen.screen_fragment;

import com.custom.castlefight.custom_castlefight.client.screen.handled.MainGameScreenHandled;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.GridWidget;

public class StageFragment implements IScreenFragment{
    @Override
    public GridWidget buildWidgets(Screen screen) {
        GridWidget grid = new GridWidget();
        if (screen instanceof MainGameScreenHandled){
            return grid;
        }
        return null;
    }
}
