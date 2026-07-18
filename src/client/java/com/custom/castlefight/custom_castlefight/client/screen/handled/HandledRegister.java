package com.custom.castlefight.custom_castlefight.client.screen.handled;

import com.custom.castlefight.custom_castlefight.Network.screenhandler.LobbyScreen;
import com.custom.castlefight.custom_castlefight.Network.screenhandler.MainGameScreen;
import com.custom.castlefight.custom_castlefight.Network.screenhandler.ScanScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;


public class HandledRegister {
    public static void register(){
        HandledScreens.register(ScanScreen.SCANSCREEN_TYPE, ScanScreenHandled::new);
        HandledScreens.register(LobbyScreen.LOBBYSCREEN_TYPE,LobbyScreenHandled::new);
        HandledScreens.register(MainGameScreen.MAINGAMESCREEN_TYPE,MainGameScreenHandled::new);
    }
}
