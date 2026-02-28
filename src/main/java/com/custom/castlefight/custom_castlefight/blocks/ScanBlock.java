package com.custom.castlefight.custom_castlefight.blocks;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildFunc;
import com.custom.castlefight.custom_castlefight.Custom_castlefight;
import com.custom.castlefight.custom_castlefight.screenhandler.ScanScreen;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ScanBlock extends Block {
    public ScanBlock(Settings settings) {
        super(settings);
    }

    private static final Identifier ScanBlock_ID = Identifier.of(Custom_castlefight.MOD_ID,"scanblock");
    public static final RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK,ScanBlock_ID);
    public static final Block ScanBlock = Blocks.register(key,
            ScanBlock::new,
            AbstractBlock.Settings.create()
    );
    public static final RegistryKey<Item> key_item =
            RegistryKey.of(RegistryKeys.ITEM, ScanBlock_ID);

    public static final Item ScanItems = Items.register(ScanBlock,
            BlockItem::new,
            new Item.Settings().maxCount(1)
    );

//    @Override
//    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
//        super.onPlaced(world, pos, state, placer, itemStack);
//        if (world.isClient()) return;
//        List<List<Pair<BlockState,BlockPos>>> blocks = BuildFunc.scanSection(pos.add(1,0,1),(ServerWorld) world);
//        BuildFunc.buildSection((ServerWorld) world,blocks,2);
//    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world. isClient()) {
            player.openHandledScreen(
                    new ExtendedScreenHandlerFactory<BlockPos>(){
                        @Override
                        public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                            return new ScanScreen(syncId,playerInventory,pos);
                        }

                        @Override
                        public Text getDisplayName() {
                            return Text.translatable("Привет");
                        }

                        @Override
                        public BlockPos getScreenOpeningData(ServerPlayerEntity player){
                            return pos;
                        }
                    }

            );
        }
        return super.onUse(state, world, pos, player, hit);
    }

    public static void register() {

    }
    
}
