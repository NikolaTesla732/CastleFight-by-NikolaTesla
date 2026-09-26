package com.custom.castlefight.custom_castlefight.client.bind;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Identifier;

public class AllKeyUtils {
    public static final KeyBinding.Category category = KeyBinding.Category.create(Identifier.of("castlefight:key"));

    public static void register(){
        AdminKey.register();
        LobbyKey.register();
        ShopKey.register();
        MainGameKey.register();
    }
}
