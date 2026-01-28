package com.custom.castlefight.custom_castlefight.blocks;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildFunc;
import com.custom.castlefight.custom_castlefight.blocks.blockitems.BuildItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;
import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;

public class BuildBlock extends Block {
    public BuildBlock(Settings settings) {
        super(settings);
    }
    private static final Identifier ID = Identifier.of(MOD_ID,"buildblock");
    public static final RegistryKey<Block> Key = RegistryKey.of(RegistryKeys.BLOCK,ID);
    public static final RegistryKey<Item> Item_Key = RegistryKey.of(RegistryKeys.ITEM,ID);

    public static final Block BuildBlock = Blocks.register(Key,
            BuildBlock::new,
            Settings.create());
    public static final Item BuildBlockItem = Items.register(BuildBlock,
            BuildItem::new,
            new Item.Settings().maxCount(1)
            );

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        List<List<Pair<BlockState,BlockPos>>> BlocksList = new ArrayList<>();
        BlockPos np = new BlockPos(pos.getX()-1,pos.getY(),pos.getZ()-1);
        for (int y = 0;y <5;y++){
            List<Pair<BlockState,BlockPos>> blocks = new ArrayList<>();
            for (int x = 0;x <3;x++){
                for (int z = 0;z <3;z++){
                    blocks.add(new Pair<>(Blocks.STONE.getDefaultState(),new BlockPos(
                            np.getX()+x,np.getY()+y,np.getZ()+z
                    )));
                }

            }
            BlocksList.add(blocks);
        }
        if (!world.isClient()) BuildFunc.buildSection((ServerWorld) world,BlocksList,2);
    }

    public static void register(){

    }
}
