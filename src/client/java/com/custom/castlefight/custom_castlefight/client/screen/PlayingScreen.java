package com.custom.castlefight.custom_castlefight.client.screen;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildUtilities;
import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.client.screen.tab.BuildShopTab;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.text.Text;

public class PlayingScreen extends CastleFightBaseScreen {
    private TabManager tabManager;
    private TabNavigationWidget tabNavigation;
    private BuildShopTab buildShopTab;
    public PlayingScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        tabManager = new TabManager(this::addDrawableChild,this::remove);
        buildShopTab = new BuildShopTab(textRenderer);
        tabNavigation = TabNavigationWidget.builder(tabManager,width).tabs(new Tab[]{buildShopTab}).build();
        tabNavigation.init();
        this.addDrawableChild(tabNavigation);
        tabManager.setTabArea(new ScreenRect(
                10,
                20,
                this.width - 20,
                this.height - 20
        ));
        tabManager.setCurrentTab(buildShopTab,true);
    }

    @Override
    public void onStorageUpdate(MatchUtilities.MatchData data) {

    }
}
