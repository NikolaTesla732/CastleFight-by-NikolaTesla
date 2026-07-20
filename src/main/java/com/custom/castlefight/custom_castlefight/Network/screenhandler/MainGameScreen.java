package com.custom.castlefight.custom_castlefight.Network.screenhandler;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
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
    public MainGameData data;
    public static record MainGameData(MatchUtilities.MatchState state,Integer timer,Integer absoluteTimer){
        static PacketCodec<RegistryByteBuf,MainGameData> PACKET_CODEC = PacketCodec.of(
                (((value, buf) -> {
                    buf.writeEnumConstant(value.state());
                    buf.writeInt(value.timer());
                    buf.writeInt(value.absoluteTimer);
                })),
                ((buf -> new MainGameData(buf.readEnumConstant(MatchUtilities.MatchState.class), buf.readInt(),buf.readInt())))
        );
    }
    public static void register(){
        MAINGAMESCREEN_TYPE = Registry.register(
                Registries.SCREEN_HANDLER,
                MAINGAMESCREEN_ID,
                new ExtendedScreenHandlerType<>(
                        MainGameScreen::new,
                        MainGameData.PACKET_CODEC
                )
        );
    }

    public MainGameScreen(int syncId, PlayerInventory inventory) {
        super(MAINGAMESCREEN_TYPE, syncId);
        MatchUtilities.MatchManager manager = MatchUtilities.MatchManager.getInstance();
        UUID matchId = manager.getPlayerMatch(inventory.player.getUuid());
        MatchUtilities.Match match = manager.getMatch(matchId);
        data = new MainGameData(match.getMatchState(),match.getMatchTime(),match.getAbsoluteMatchTime());
    }
    public MainGameScreen(int syncId, PlayerInventory inventory,MainGameData data1) {
        super(MAINGAMESCREEN_TYPE, syncId);
        data = data1;
    }
    public static ExtendedScreenHandlerFactory<MainGameData> getFactory(){
        MatchUtilities.MatchManager manager = MatchUtilities.MatchManager.getInstance();
        return new ExtendedScreenHandlerFactory<MainGameData>() {
            @Override
            public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new MainGameScreen(syncId,playerInventory);
            }

            @Override
            public Text getDisplayName() {
                return Text.literal("Основная игра");
            }

            @Override
            public MainGameData getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
                MatchUtilities.Match match = manager.getMatch(manager.getPlayerMatch(serverPlayerEntity.getUuid()));
                return new MainGameData(match.getMatchState(),
                                        match.getMatchTime(),
                                        match.getAbsoluteMatchTime()
                );
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
