package com.custom.castlefight.custom_castlefight.client.bind;

import com.custom.castlefight.custom_castlefight.client.screen.AdminScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

//Класс для бинда менюшки админа
public class AdminKey {
    public static KeyBinding OPEN_ADMIN;

    public static void register(){
            OPEN_ADMIN = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key.castlefight.open_admin",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_P,
                    AllKeyUtils.category
            ));
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient ->
        {
            while (OPEN_ADMIN.wasPressed()){
                if (minecraftClient.player != null && minecraftClient.currentScreen == null){
                    minecraftClient.setScreen(new AdminScreen(Text.literal("Admin")));
                }
            }
        });
    }
}
