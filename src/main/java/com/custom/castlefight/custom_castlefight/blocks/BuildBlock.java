package com.custom.castlefight.custom_castlefight.blocks;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildFunc;
import com.custom.castlefight.custom_castlefight.CustomFunc.BuildFunc.BuildTemplate;
import com.custom.castlefight.custom_castlefight.Custom_castlefight;
import com.custom.castlefight.custom_castlefight.blocks.blockitems.BuildItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
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
import static com.custom.castlefight.custom_castlefight.Custom_castlefight.TEMPLATES;
import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;
import static net.minecraft.block.Blocks.STONE;

public class BuildBlock extends Block {
    public BuildBlock(Settings settings) {
        super(settings);
    }

    public void setBuild(BuildTemplate build){
        this.buildTemplate = build;
    }
    private BuildTemplate buildTemplate = null;
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

        if (world.isClient()){return;}
        NbtComponent customData = itemStack.get(DataComponentTypes.CUSTOM_DATA);
        if (customData == null){
            LOGGER.debug("Не удалось получить данные о конкретном строителе");
            return;
        }
        NbtCompound nbt = customData.copyNbt();
        String buildId = nbt.getString("buildId","тьма:кладбище:1");
        if (buildId.isBlank()) {
            LOGGER.debug("идентификатор здания отсутствует");
            return;
        }
        String[] parts = buildId.split(":");
        if(parts.length < 3) {
            LOGGER.debug("Недостаточно данных для поиска шаблона");
            return;
        }
        LOGGER.info("Id: "+buildId);
        String race = parts[0], name = parts[1];
        int level = Integer.parseInt(parts[2]);
        BuildTemplate build = TEMPLATES.getBuild(race,name,level);
        BuildFunc.buildSection((ServerWorld) world,pos,build,2);
    }

    public static void register(){

    }
}
