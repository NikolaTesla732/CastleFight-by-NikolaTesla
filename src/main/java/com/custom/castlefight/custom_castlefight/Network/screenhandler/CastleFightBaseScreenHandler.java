package com.custom.castlefight.custom_castlefight.Network.screenhandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;

public abstract class CastleFightBaseScreenHandler extends ScreenHandler {
    protected CastleFightBaseScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type,syncId);
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
