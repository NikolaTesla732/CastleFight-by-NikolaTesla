package com.custom.castlefight.custom_castlefight.Network.PacketsS2C;

import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public record SendTeamsS2CPacket(Map<MatchUtilities.TeamColor, List<String>> teams) implements CustomPayload {
    public static final Identifier RAW_ID = Identifier.of(MOD_ID,"send_teams");
    public static final CustomPayload.Id<SendTeamsS2CPacket> ID = new Id<>(RAW_ID);
    public static final PacketCodec<RegistryByteBuf,SendTeamsS2CPacket> PACKET_CODEC = PacketCodec.of(
        SendTeamsS2CPacket::write,
        SendTeamsS2CPacket::read
    );
    public void write(RegistryByteBuf buf){
        buf.writeMap(
                teams,
                (PacketByteBuf::writeEnumConstant),
                ((buf1, value) -> {
                    buf1.writeInt(value.size());
                    for (String player : value){
                        buf1.writeString(player);
                    }
                })
        );
    }
    public static SendTeamsS2CPacket read(RegistryByteBuf buf){
        Map<MatchUtilities.TeamColor, List<String>> teamMap = buf.readMap(
                (buf1 -> {
                   return buf1.readEnumConstant(MatchUtilities.TeamColor.class);
                }),
                (buf1 -> {
                    List<String> ans = new ArrayList<>();
                    int n = buf1.readInt();
                    for (int i = 0;i<n;i++){
                        ans.add(buf1.readString());
                    }
                    return ans;
                })
        );
        return new SendTeamsS2CPacket(teamMap);
    }
    public static void register(){
        PayloadTypeRegistry.playS2C().register(ID,PACKET_CODEC);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
