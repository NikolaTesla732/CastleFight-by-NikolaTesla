package com.custom.castlefight.custom_castlefight.client.bind;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToDoClientMatchActionC2SPacket;
import com.custom.castlefight.custom_castlefight.Network.screenhandler.LobbyScreen;
import com.custom.castlefight.custom_castlefight.client.screen.LobbyScreenHandled;
import com.custom.castlefight.custom_castlefight.client.screen.RaceBuildsScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class LobbyKey {
    public static KeyBinding OPEN_LOBBY_SCREEN;
    public static void register(){
        OPEN_LOBBY_SCREEN = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.castlefight.open_lobby",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Y,
                AllKeyUtils.category
            )
        );
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            while (OPEN_LOBBY_SCREEN.wasPressed()){
                if (minecraftClient.player != null && minecraftClient.currentScreen == null){
                    MatchUtilities.MatchAction action = new MatchUtilities.MatchAction();
                    action.setAction(MatchUtilities.ActionPlayer.OPEN_LOBBY);
                    ClientPlayNetworking.send(new RequestToDoClientMatchActionC2SPacket(action));
                }
            }
        });
    }
}
