package com.custom.castlefight.custom_castlefight.client.bind;

import com.custom.castlefight.custom_castlefight.client.screen.RaceBuildsScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;



public class RaceKey {
    public static KeyBinding OPEN_RACE_BUILDS;

    public static void register(){
        OPEN_RACE_BUILDS = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.castlefight.open_race",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                AllKeyUtils.category
                ));
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            while (OPEN_RACE_BUILDS.wasPressed()){
                if (minecraftClient.player != null && minecraftClient.currentScreen == null){
                    minecraftClient.setScreen(new RaceBuildsScreen("тьма"));
                }
            }
        });
    }

}
