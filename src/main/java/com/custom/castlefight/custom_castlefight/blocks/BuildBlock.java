package com.custom.castlefight.custom_castlefight.blocks;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildFunc;
import com.custom.castlefight.custom_castlefight.CustomFunc.BuildFunc.BuildTemplate;
import com.custom.castlefight.custom_castlefight.blocks.blockitems.BuildItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import com.custom.castlefight.custom_castlefight.CustomFunc.BuildFunc.BlockWithData;
import static com.custom.castlefight.custom_castlefight.Custom_castlefight.MOD_ID;
import static net.minecraft.block.Blocks.STONE;

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
        List<BlockWithData> BlocksList = new ArrayList<>();
        for (int y = 0;y <5;y++){
            for (int x = -1;x <2;x++){
                for (int z = -1;z <2;z++){
                    BlocksList.add(new BlockWithData(
                            x,y,z, STONE.getDefaultState()
                    ));
                }

            }
        }
        BuildTemplate template = new BuildTemplate(
                "build",1,BlocksList,20,30,200
        );
        if (!world.isClient()) BuildFunc.buildSection((ServerWorld) world,pos,template,2);
    }

    public static void register(){

    }
}
