package com.custom.castlefight.custom_castlefight.Network.screenhandler;

import com.custom.castlefight.custom_castlefight.CustomFunc.MapUtilities;
import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketEncoder;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;


public class LobbyScreen extends ScreenHandler {
    public static final Identifier LOBBYSCREEN_ID = Identifier.of(MOD_ID,"lobby_screen");
    public static ScreenHandlerType<LobbyScreen> LOBBYSCREEN_TYPE;
    public Map<MatchUtilities.MatchFormat,Integer> playerCount = new HashMap<>();

    public LobbyScreen(int syncId, PlayerInventory playerInventory) {
        super(LOBBYSCREEN_TYPE,syncId);
        for (MatchUtilities.MatchFormat format : MatchUtilities.MatchFormat.values()){
            this.playerCount.put(format,
                    MatchUtilities.MatchManager.getInstance().getNonActiveMatches().get(format).getAllPlayer().size()
            );
        }
    }

    public LobbyScreen(int syncId, PlayerInventory playerInventory, Map<MatchUtilities.MatchFormat, Integer> players) {
        super(LOBBYSCREEN_TYPE,syncId);
        this.playerCount = players;
    }

    public static void register(){
        LOBBYSCREEN_TYPE = Registry.register(
                Registries.SCREEN_HANDLER,
                LOBBYSCREEN_ID,
                new ExtendedScreenHandlerType<LobbyScreen,Map<MatchUtilities.MatchFormat,Integer>>(
                        LobbyScreen::new,
                        PacketCodec.of(
                                ((value, buf) -> {
                                    buf.writeMap((Map<MatchUtilities.MatchFormat,Integer>)value,(buf1, value1) -> {buf1.writeEnumConstant((MatchUtilities.MatchFormat) value1);},
                                            (buf1,value1) -> {buf1.writeInt((int)value1);});
                                }),
                                (buf -> {
                                    return buf.readMap((buf1 -> buf1.readEnumConstant(MatchUtilities.MatchFormat.class)),
                                            PacketByteBuf::readInt);
                                })
                        )
                )
        );
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
