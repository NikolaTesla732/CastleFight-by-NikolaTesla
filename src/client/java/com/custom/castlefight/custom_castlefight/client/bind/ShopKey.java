package com.custom.castlefight.custom_castlefight.client.bind;

import com.custom.castlefight.custom_castlefight.client.screen.ShopScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class ShopKey {
    public static KeyBinding OPEN_SHOP;

    public static void register() {
        OPEN_SHOP = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.castlefight.open_shop",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                KeyBinding.Category.create(Identifier.of("castlefight:shop_key"))
        ));
        ClientTickEvents.END_CLIENT_TICK.register(minecraftClient -> {
            while (OPEN_SHOP.wasPressed()){
                if (minecraftClient.player != null && minecraftClient.currentScreen == null){
                    minecraftClient.setScreen(new ShopScreen());
                }
            }

        });
    }


}
