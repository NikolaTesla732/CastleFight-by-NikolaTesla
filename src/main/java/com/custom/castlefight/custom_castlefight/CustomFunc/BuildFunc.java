package com.custom.castlefight.custom_castlefight.CustomFunc;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;


import java.util.*;

public class BuildFunc {
    public record BlockWithData(int x, int y, int z, BlockState state){}
    public static class BuildTemplate {
        private final String id;
        private final String name;
        private final List<BlockWithData> blocks;
        public BuildTemplate(String id, String name,
                             List<BlockWithData> blocks
        ){
            this.id = id;
            this.blocks = blocks;
            this.name = name;
        }
        public BuildTemplate(NbtCompound data, RegistryWrapper.WrapperLookup lookup){
            this.id = data.getString("id","");
            this.name = data.getString("name","");
            NbtList list = data.getListOrEmpty("blocks");
            List<BlockWithData> blocks = new ArrayList<>() ;
            RegistryEntryLookup<Block> blockLookup = lookup.getOrThrow(RegistryKeys.BLOCK);
            for (int i = 0; i < list.size(); i++) {
                NbtCompound nbtTime = list.getCompoundOrEmpty(i);
                int x,y,z;
                x = nbtTime.getInt("x",0);
                y = nbtTime.getInt("y",0);
                z = nbtTime.getInt("z",0);
                BlockState state = NbtHelper.toBlockState(blockLookup,nbtTime.getCompoundOrEmpty("state"));
                blocks.add(new BlockWithData(x,y,z,state));
            }
            this.blocks = blocks;

        }
        public String getId() {
            return id;
        }

        public List<BlockWithData> getBlocks() {
            return blocks;
        }

        public String getName() {
            return name;
        }
        public NbtCompound toNbt(){
            NbtCompound nbt = new NbtCompound();
            nbt.putString("id",this.id);
            nbt.putString("name",this.name);

            NbtList nbtList = new NbtList();
            for (BlockWithData block : this.blocks){
                NbtCompound nbtTime = new NbtCompound();
                nbtTime.putInt("x",block.x());
                nbtTime.putInt("y",block.y());
                nbtTime.putInt("z",block.z());
                nbtTime.put("state", NbtHelper.fromBlockState(block.state));
                nbtList.add(nbtTime);
            }
            nbt.put("blocks",nbtList);
            return nbt;
        }

    }

    public static void build(ServerWorld world, BlockWithData block, BlockPos origin){
        BlockState state = block.state;
        world.setBlockState(origin.add(block.x,block.y,block.z),state);
        if (world.isClient())return;
        world.syncWorldEvent(
                WorldEvents.BLOCK_BROKEN,
                origin.add(block.x,block.y,block.z),
                Block.getRawIdFromState(state)
        );
    }
    private static class BuildTask {
        final ServerWorld world;
        final Queue<BlockWithData> queue;
        final int delayTicks;
        int timer;
        final BlockPos origin;

        BuildTask(ServerWorld world, Queue<BlockWithData> queue, int delayTicks, BlockPos origin) {
            this.world = world;
            this.queue = queue;
            this.delayTicks = delayTicks;
            this.timer = 0;
            this.origin = origin;
        }
    }

    private static final List<BuildTask> TASKS = new ArrayList<>();
    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(BuildFunc::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        Iterator<BuildTask> it = TASKS.iterator();
        while (it.hasNext()) {
            BuildTask task = it.next();
            if (task.queue.isEmpty()) {
                it.remove();
                continue;
            }

            if (task.timer > 0) {
                task.timer--;
                continue;
            }

            BlockWithData block = task.queue.poll();
            build(task.world, block,task.origin);
            task.timer = task.delayTicks;
        }
    }

    public static void buildSection(ServerWorld world,BlockPos origin,
                                    List<BlockWithData> ListBlocks,
                                        int delayTicks) {

        Queue<BlockWithData> queue = new ArrayDeque<>();
        queue.addAll(ListBlocks);


        TASKS.add(new BuildTask(world, queue, delayTicks,origin));
    }

    public static List<BlockWithData> scanSection(BlockPos startpos, ServerWorld world){
        List<BlockWithData> ans = new ArrayList<>();
        for (int y = 0;y < 5;y++){
            for (int x = 0;x < 3;x++){
                for (int z = 0;z < 3;z++){
                    BlockPos pos2 = startpos.add(x,y,z);
                    BlockState block = world.getBlockState(pos2);
                    ans.add(new BlockWithData(x,y,z,block));
                }
            }
        }
        return ans;
    }
}
