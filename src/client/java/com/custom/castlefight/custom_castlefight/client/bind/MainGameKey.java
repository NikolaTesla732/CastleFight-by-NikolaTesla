package com.custom.castlefight.custom_castlefight.client.bind;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToDoClientMatchActionC2SPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class MainGameKey {
    public static KeyBinding OPEN_MAIN_GAME_SCREEN;

    public static void register(){
        OPEN_MAIN_GAME_SCREEN = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "key.castlefight.open_main_game",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_P,
                    AllKeyUtils.category
            )
        );
        ClientTickEvents.END_CLIENT_TICK.register((minecraftClient -> {
            while (OPEN_MAIN_GAME_SCREEN.wasPressed()){
                if (minecraftClient.player != null && minecraftClient.currentScreen == null){
                    MatchUtilities.MatchAction action = new MatchUtilities.MatchAction();
                    action.setAction(MatchUtilities.ActionPlayer.OPEN_MAIN);
                    ClientPlayNetworking.send(new RequestToDoClientMatchActionC2SPacket(action));
                }
            }
        }));
    }
}
