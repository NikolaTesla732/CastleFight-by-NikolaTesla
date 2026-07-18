package com.custom.castlefight.custom_castlefight.Network.screenhandler;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public class MainGameScreen extends ScreenHandler {

    public static final Identifier MAINGAMESCREEN_ID = Identifier.of(MOD_ID,"main_game_screen");
    public static ScreenHandlerType<MainGameScreen> MAINGAMESCREEN_TYPE;
    public MatchUtilities.MatchState state;
    public static void register(){
        MAINGAMESCREEN_TYPE = Registry.register(
                Registries.SCREEN_HANDLER,
                MAINGAMESCREEN_ID,
                new ExtendedScreenHandlerType<>(
                        MainGameScreen::new,
                        PacketCodec.of(
                                ((value, buf) -> buf.writeEnumConstant(value)), (buf -> buf.readEnumConstant(MatchUtilities.MatchState.class))
                        )
                )
        );
    }

    public MainGameScreen(int syncId, PlayerInventory inventory) {
        super(MAINGAMESCREEN_TYPE, syncId);
        UUID matchId = MatchUtilities.MatchManager.getInstance().getPlayerMatch(inventory.player.getUuid());
        state = MatchUtilities.MatchManager.getInstance().getMatch(matchId).getMatchState();
        if (state == null){
            state = MatchUtilities.MatchState.NOT_ACTIVE;
        }
    }
    public MainGameScreen(int syncId, PlayerInventory inventory,MatchUtilities.MatchState matchState) {
        super(MAINGAMESCREEN_TYPE, syncId);
        state = matchState;
        if (state == null){
            state = MatchUtilities.MatchState.NOT_ACTIVE;
        }
    }
    public static ExtendedScreenHandlerFactory<MatchUtilities.MatchState> getFactory(){
        MatchUtilities.MatchManager manager = MatchUtilities.MatchManager.getInstance();
        return new ExtendedScreenHandlerFactory<MatchUtilities.MatchState>() {
            @Override
            public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new MainGameScreen(syncId,playerInventory);
            }

            @Override
            public Text getDisplayName() {
                return Text.literal("Основная игра");
            }

            @Override
            public MatchUtilities.MatchState getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
                return manager.getMatch(manager.getPlayerMatch(serverPlayerEntity.getUuid())).getMatchState();
            }
        };
    }
    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
