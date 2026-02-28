package com.custom.castlefight.custom_castlefight.CustomFunc;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;


import java.util.*;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;

public class BuildFunc {
    public static boolean NeedBlocks(int need ,List<List<Pair<BlockState, BlockPos>>> ListBlocks){
        int cnt = 0;
        for (int i = 0;i < ListBlocks.size();i++){
            for (Pair<BlockState, BlockPos> j : ListBlocks.get(i)){
                cnt++;
            }
        }
        return cnt == need;
    }
    public static void build(ServerWorld world,Pair<BlockState,BlockPos> block){
        BlockState state = block.getLeft();
        BlockPos pos = block.getRight();
        world.setBlockState(pos,state);
        if (world.isClient())return;
        world.syncWorldEvent(
                WorldEvents.BLOCK_BROKEN,
                block.getRight(),
                Block.getRawIdFromState(state)
        );
    }
    private static class BuildTask {
        final ServerWorld world;
        final Queue<Pair<BlockState, BlockPos>> queue;
        final int delayTicks;
        int timer;

        BuildTask(ServerWorld world, Queue<Pair<BlockState, BlockPos>> queue, int delayTicks) {
            this.world = world;
            this.queue = queue;
            this.delayTicks = delayTicks;
            this.timer = 0;
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

            Pair<BlockState, BlockPos> block = task.queue.poll();
            build(task.world, block);
            task.timer = task.delayTicks;
        }
    }

    public static void buildSection(ServerWorld world,
                                        List<List<Pair<BlockState, BlockPos>>> ListBlocks,
                                        int delayTicks) {

        if (!NeedBlocks(45, ListBlocks)) {
            return;
        }

        Queue<Pair<BlockState, BlockPos>> queue = new ArrayDeque<>();
        for (List<Pair<BlockState, BlockPos>> row : ListBlocks) {
            for (Pair<BlockState, BlockPos> block : row) {
                queue.add(block);
            }
        }

        TASKS.add(new BuildTask(world, queue, delayTicks));
    }

    public static List<List<Pair<BlockState,BlockPos>>> scanSection(BlockPos startpos, ServerWorld world){
        List<List<Pair<BlockState,BlockPos>>> ans = new ArrayList<>();
        for (int i = 0;i < 5;i++){
            List<Pair<BlockState,BlockPos>> bp = new ArrayList<>();
            for (int j = 0;j < 3;j++){
                for (int k = 0;k < 3;k++){
                    BlockPos pos2 = startpos.add(j,i,k);
                    BlockState block = world.getBlockState(pos2);
                    bp.add(new Pair<>(block,pos2.add(0,0,3)));
                }
            }
            ans.add(bp);
        }
        LOGGER.info(ans.get(0).get(0).toString());
        LOGGER.info("asd");
        return ans;
    }
}
