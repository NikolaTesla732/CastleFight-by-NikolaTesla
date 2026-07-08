package com.custom.castlefight.custom_castlefight.client.screen;

import com.custom.castlefight.custom_castlefight.Network.screenhandler.LobbyScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

import static com.custom.castlefight.custom_castlefight.Network.screenhandler.ScanScreen.SCANSCREEN_TYPE;

public class HandledRegister {
    public static void register(){
        HandledScreens.register(SCANSCREEN_TYPE, ScanScreenHandled::new);
        HandledScreens.register(LobbyScreen.LOBBYSCREEN_TYPE,LobbyScreenHandled::new);
    }
}
