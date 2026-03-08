package com.custom.castlefight.custom_castlefight.client.screen;

import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tab.GridScreenTab;
import net.minecraft.client.gui.tab.Tab;
import net.minecraft.client.gui.tab.TabManager;
import net.minecraft.client.gui.widget.TabNavigationWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AdminScreen extends Screen {
    private TabManager tab_manager;
    private TabNavigationWidget tab_navigation;
    private MainTab main_tab;
    private Tab2 tab2;

    public AdminScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        //Объявление логики работы TabManager
        this.tab_manager = new TabManager(
                widget -> this.addDrawableChild(widget),// При открытии вкладки-показать её widget
                widget -> this.remove(widget)// при закрытии вкладки-закрыть её вкладки
        );
        //Объявление вкладок
        this.main_tab = new MainTab();
        this.tab2 = new Tab2();
        //Объявление полоски вкладок
        this.tab_navigation = TabNavigationWidget.builder(this.tab_manager, this.width)
                .tabs(new Tab[]{this.main_tab, this.tab2})
                .build();
        this.tab_navigation.init();
        this.addDrawableChild(this.tab_navigation); // Показываем полоску навигации

        // Задаём область вкладки
        this.tab_manager.setTabArea(new ScreenRect(
                20,
                40,
                this.width - 40,
                this.height - 70
        ));
        this.tab_manager.setCurrentTab(this.main_tab,true);
    }

    //Основная вкладка, открывается первой
    class MainTab extends GridScreenTab {
        private final TextFieldWidget textField;
        private final TextFieldWidget textField2;

        MainTab() {
            super(Text.literal("Main"));
            this.grid.setColumnSpacing(8);
            this.grid.setRowSpacing(6);
            this.textField = new TextFieldWidget(AdminScreen.this.getTextRenderer(),
                    120, 20, Text.literal("Test"));
            this.textField2 = new TextFieldWidget(AdminScreen.this.getTextRenderer(),
                    120, 20, Text.literal("Test2"));
            this.grid.add(this.textField, 0, 0);
            this.grid.add(this.textField2, 1, 0);

        }
    }
    //Вторая вкладка,пока тестовая
    class Tab2 extends GridScreenTab {
        private final TextFieldWidget textField;
        private final TextFieldWidget textField2;

        Tab2() {
            super(Text.literal("Tab2"));
            this.grid.setColumnSpacing(8);
            this.grid.setRowSpacing(6);
            this.textField = new TextFieldWidget(AdminScreen.this.getTextRenderer(),
                    120, 20, Text.literal("Test"));
            this.textField2 = new TextFieldWidget(AdminScreen.this.getTextRenderer(),
                    120, 20, Text.literal("Test2"));
            this.grid.add(this.textField, 0, 0);
            this.grid.add(this.textField2, 1, 0);

        }
    }

}

