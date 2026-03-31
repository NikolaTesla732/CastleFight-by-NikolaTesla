package com.custom.castlefight.custom_castlefight.Network.Packets;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildFunc.BuildTemplate;
import com.custom.castlefight.custom_castlefight.screenhandler.ScanScreen;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;

public record RequestToScanC2SPacket(String name,int level,int cost, int income,int cooldown) implements CustomPayload {
    public static final Identifier RAW_ID = Identifier.of(MOD_ID, "request_to_scan");
    public static final CustomPayload.Id<RequestToScanC2SPacket> ID =
            new CustomPayload.Id<>(RAW_ID);
    public void write(RegistryByteBuf buf){
        buf.writeString(this.name);
        buf.writeInt(this.level);
        buf.writeInt(this.cost);
        buf.writeInt(this.income);
        buf.writeInt(this.cooldown);
    }

    public static RequestToScanC2SPacket read(RegistryByteBuf buf){
        String name = buf.readString();
        int level = buf.readInt();
        int cost = buf.readInt();
        int income = buf.readInt();
        int cooldown = buf.readInt();
        return new RequestToScanC2SPacket(name,level,cost,income,cooldown);
    }
    public static final PacketCodec<RegistryByteBuf,RequestToScanC2SPacket> CODEC = PacketCodec.of(
            RequestToScanC2SPacket::write,
            RequestToScanC2SPacket::read
    );
    public static void register(){
        PayloadTypeRegistry.playC2S().register(ID,CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ID,RequestToScanC2SPacket::receive);
    }
    public static void receive(RequestToScanC2SPacket payload,
                               ServerPlayNetworking.Context context){
        if (context.player().currentScreenHandler instanceof ScanScreen screen){
            screen.OnScanClicked(
                     payload.name(), payload.level(),payload.cost(), payload.income(),payload.cooldown()
                    );
        }
    }
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

}
