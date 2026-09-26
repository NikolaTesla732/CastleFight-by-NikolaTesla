package com.custom.castlefight.custom_castlefight.blocks.blockitems;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildUtilities;
import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendMatchAnswerS2CPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.TEMPLATES;

public class ConstructionItem extends BlockItem {

    public ConstructionItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    protected boolean canPlace(ItemPlacementContext context, BlockState state) {
        if (context.getPlayer().getEntityWorld().isClient()) return false;
        PlayerEntity player = context.getPlayer();
        MatchUtilities.MatchManager manager = MatchUtilities.MatchManager.getInstance();
        MatchUtilities.Match match = manager.getMatch(
                manager.getPlayerMatch(player.getUuid())
        );
        ItemStack stack = context.getStack();
        String buildId = stack.get(DataComponentTypes.CUSTOM_DATA).copyNbt().getString("buildId","тьма:кладбище:1");
        String[] parts = buildId.split(":");
        String race = parts[0], name = parts[1];
        int level = Integer.parseInt(parts[2]);
        BuildUtilities.BuildTemplate template = TEMPLATES.getBuild(race,name,level);
        MatchUtilities.PlayerData data = match.getPlayerData(player.getUuid());
        MatchUtilities.MatchAnswer answer = match.buyBuild(template.getCost(),template.getWood(),player.getUuid(),template.getIncome());
        if (answer == MatchUtilities.MatchAnswer.NONE) return true;
        ServerPlayNetworking.send((ServerPlayerEntity) player,new SendMatchAnswerS2CPacket(answer));
        return false;
    }
}
