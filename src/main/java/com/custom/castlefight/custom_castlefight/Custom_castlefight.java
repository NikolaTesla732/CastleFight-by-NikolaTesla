package com.custom.castlefight.custom_castlefight;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildUtilities;
import com.custom.castlefight.custom_castlefight.CustomFunc.Construction;
import com.custom.castlefight.custom_castlefight.CustomFunc.GlobalBuildTemplateStorage;
import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.Network.CastlefightNetworking;
import com.custom.castlefight.custom_castlefight.Network.PacketsC2S.RequestToGetRacesManagerC2SPacket;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendRacesManagerS2CPacket;
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
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.ActionResult;
import net.minecraft.world.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Custom_castlefight implements ModInitializer {
    public static final String MOD_ID = "custom_castlefight";
    public  static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static GlobalBuildTemplateStorage TEMPLATES;
    public static final Random RANDOM = new Random();

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
            BuildUtilities.racesManager racesManager = BuildUtilities.racesManager.getInstance();
            for (String race : TEMPLATES.getRace()){
                List<BuildUtilities.rawBuildTemplate> templatesBuilds = new ArrayList<>();
                for (String build : TEMPLATES.getRaceBuilds(race).keySet()){
                    for ( int level : TEMPLATES.getLevels(race,build)){
                        BuildUtilities.rawBuildTemplate rawBuildTemplate = TEMPLATES.getBuild(race,build,level).getRaw();
                        templatesBuilds.add(rawBuildTemplate);
                    }
                }
                racesManager.races.put(race,new BuildUtilities.raceBuilds(templatesBuilds));
            }
        });
        ServerPlayConnectionEvents.JOIN.register(
                (handler, sender, minecraftServer) -> {
                    minecraftServer.execute(()->{
                        sender.sendPacket(new SendRacesManagerS2CPacket(BuildUtilities.racesManager.getInstance()));
                    });
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

        PlayerBlockBreakEvents.BEFORE.register(((world, playerEntity, blockPos, blockState, blockEntity) ->
                playerEntity.getGameMode() == GameMode.CREATIVE));

        AttackBlockCallback.EVENT.register(((playerEntity, world, hand, blockPos, direction) -> {
            if (playerEntity.getGameMode() == GameMode.CREATIVE) return ActionResult.PASS;
            return ActionResult.FAIL;
        }));

        UseBlockCallback.EVENT.register(((playerEntity, world, hand, blockHitResult) -> {
            if (playerEntity.getGameMode() == GameMode.CREATIVE) return ActionResult.PASS;
            ItemStack stack = playerEntity.getStackInHand(hand);
            BlockState state = world.getBlockState(blockHitResult.getBlockPos());
            if (stack.isOf(ConstructionBlock.CONSTRUCTION_BLOCK_ITEM) ||
                stack.isOf(ScanBlock.SCAN_ITEMS) || state.isOf(ScanBlock.SCAN_BLOCK) ||
                    state.isOf(BuildingBlock.BUILDING_BLOCK)) return ActionResult.PASS;


            return ActionResult.FAIL;
        }));
    }
}
