package com.custom.castlefight.custom_castlefight;

import com.custom.castlefight.custom_castlefight.CustomFunc.Construction;
import com.custom.castlefight.custom_castlefight.CustomFunc.GlobalBuildTemplateStorage;
import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.Network.CastlefightNetworking;
import com.custom.castlefight.custom_castlefight.blocks.BuildingBlock;
import com.custom.castlefight.custom_castlefight.blocks.ConstructionBlock;
import com.custom.castlefight.custom_castlefight.blocks.ScanBlock;
import com.custom.castlefight.custom_castlefight.blocks.blockentity.CastlefightBlockEntities;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ActionResult;
import net.minecraft.world.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Custom_castlefight implements ModInitializer {
    public static final String MOD_ID = "custom_castlefight";
    public  static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static GlobalBuildTemplateStorage TEMPLATES;

    @Override
    public void onInitialize() {
        ScanBlock.register();
        BuildingBlock.register();
        ConstructionBlock.register();
        CastlefightNetworking.registerC2SPackets();
        CastlefightNetworking.registerS2CPackets();
        CastlefightBlockEntities.register();
        CastlefightNetworking.registerScreenHandler();

        ServerLifecycleEvents.SERVER_STARTED.register(server ->{
            RegistryWrapper.WrapperLookup lookup = server.getRegistryManager();
            Construction.ConstructionTicker.register();
            this.TEMPLATES = new GlobalBuildTemplateStorage();
            TEMPLATES.load(lookup);
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(minecraftServer -> {
            TEMPLATES.save();
        });
        ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> {
            MatchUtilities.MatchManager.getInstance().tick(minecraftServer);
        });
        ServerPlayConnectionEvents.JOIN.register(((serverPlayNetworkHandler, packetSender, minecraftServer) -> {
            PlayerAbilities abilities = serverPlayNetworkHandler.getPlayer().getAbilities();
            EntityAttributeInstance attribute = serverPlayNetworkHandler.getPlayer().getAttributeInstance(EntityAttributes.BLOCK_INTERACTION_RANGE);
            if (attribute != null) {
                attribute.setBaseValue(8.0);
            }
            abilities.allowFlying = true;
            abilities.invulnerable = true;
        }));
        PlayerBlockBreakEvents.BEFORE.register(((world, playerEntity, blockPos, blockState, blockEntity) -> false));
        AttackBlockCallback.EVENT.register(((playerEntity, world, hand, blockPos, direction) -> ActionResult.FAIL));
        UseBlockCallback.EVENT.register(((playerEntity, world, hand, blockHitResult) -> {
            ItemStack stack = playerEntity.getStackInHand(hand);
            if (stack.isOf(ConstructionBlock.CONSTRUCTION_BLOCK_ITEM)) return ActionResult.PASS;
            return ActionResult.FAIL;
        }));
    }
}
