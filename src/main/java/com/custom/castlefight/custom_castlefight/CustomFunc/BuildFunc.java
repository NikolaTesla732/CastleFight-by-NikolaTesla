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
    public static boolean NeedBlocks(int need ,List<List<Pair<BlockState, position>>> ListBlocks){
        int cnt = 0;
        for (int i = 0;i < ListBlocks.size();i++){
            for (Pair<BlockState, position> j : ListBlocks.get(i)){
                cnt++;
            }
        }
        return cnt == need;
    }
    public static void build(ServerWorld world,Pair<BlockState,position> block,BlockPos origin){
        BlockState state = block.getLeft();
        position pos = block.getRight();
        world.setBlockState(origin.add(pos.x,pos.y,pos.z),state);
        if (world.isClient())return;
        world.syncWorldEvent(
                WorldEvents.BLOCK_BROKEN,
                origin.add(pos.x,pos.y,pos.z),
                Block.getRawIdFromState(state)
        );
    }
    private static class BuildTask {
        final ServerWorld world;
        final Queue<Pair<BlockState, position>> queue;
        final int delayTicks;
        int timer;
        final BlockPos origin;

        BuildTask(ServerWorld world, Queue<Pair<BlockState, position>> queue, int delayTicks,BlockPos origin) {
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

            Pair<BlockState, position> block = task.queue.poll();
            build(task.world, block,task.origin);
            task.timer = task.delayTicks;
        }
    }
    public record position(int x,int y,int z){}
    public static void buildSection(ServerWorld world,BlockPos origin,
                                        List<List<Pair<BlockState, position>>> ListBlocks,
                                        int delayTicks) {

        if (!NeedBlocks(45, ListBlocks)) {
            return;
        }

        Queue<Pair<BlockState, position>> queue = new ArrayDeque<>();
        for (List<Pair<BlockState, position>> row : ListBlocks) {
            queue.addAll(row);
        }

        TASKS.add(new BuildTask(world, queue, delayTicks,origin));
    }

    public static List<List<Pair<BlockState,position>>> scanSection(BlockPos startpos, ServerWorld world){
        List<List<Pair<BlockState,position>>> ans = new ArrayList<>();
        for (int y = 0;y < 5;y++){
            List<Pair<BlockState,position>> bp = new ArrayList<>();
            for (int x = 0;x < 3;x++){
                for (int z = 0;z < 3;z++){
                    BlockPos pos2 = startpos.add(x,y,z);
                    BlockState block = world.getBlockState(pos2);
                    bp.add(new Pair<>(block,new position(x,y,z)));
                }
            }
            ans.add(bp);
        }
        LOGGER.info(ans.get(0).get(0).toString());
        LOGGER.info("asd");
        return ans;
    }
}
