package com.custom.castlefight.custom_castlefight.CustomFunc;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;


import java.util.*;

public class BuildFunc {

    public record BlockWithData(int x, int y, int z, BlockState state){
        public void write(RegistryByteBuf buf){
            buf.writeInt(x);
            buf.writeInt(y);
            buf.writeInt(z);
            buf.writeInt(Block.getRawIdFromState(state));
        }
        public static BlockWithData read(RegistryByteBuf buf){
            int x = buf.readInt();
            int y = buf.readInt();
            int z = buf.readInt();
            int rawId = buf.readInt();
            BlockState state = Block.getStateFromRawId(rawId);
            return new BlockWithData(x,y,z,state);
        }
        public static final PacketCodec<RegistryByteBuf,BlockWithData> PACKET_CODEC =
                PacketCodec.of(
                        (value,buf) -> value.write(buf),
                        BlockWithData::read
                );
    }
    /**
     * Шаблон постройки, содержащий идентификатор, отображаемое имя и список блоков.
     *
     * <p>Экземпляр этого класса используется для хранения структуры здания в памяти,
     * а также для сериализации и десериализации шаблона в формат NBT.
     */
    public static class BuildTemplate {
        private final String name;
        private final int level;
        private final List<BlockWithData> blocks;
        private final int income;
        /**
         * Время между спавном юнитов здания в тиках
         */
        private final int spawnCD;
        private final int cost;
        private final String displayName;

        public BuildTemplate(String name,int level, List<BlockWithData> blocks,int income,int spawnCD,int cost) {
            this.blocks = blocks;
            this.name = normalizeName(name);
            this.level = level;
            this.income = income;
            this.spawnCD = spawnCD;
            this.cost = cost;
            this.displayName = name;
        }
        private static String normalizeName(String rawName){
            return rawName.toLowerCase(Locale.ROOT).replace(' ','_');
        }
        public BuildTemplate(NbtCompound data, RegistryWrapper.WrapperLookup lookup) {
            this.displayName = data.getString("display_name", "");
            this.name = normalizeName(displayName);
            this.income = data.getInt("income", 0);
            this.spawnCD = data.getInt("spawnCD", 40);
            this.cost = data.getInt("cost",100);
            NbtList list = data.getListOrEmpty("blocks");
            List<BlockWithData> blocks = new ArrayList<>();
            RegistryEntryLookup<Block> blockLookup = lookup.getOrThrow(RegistryKeys.BLOCK);
            this.level = data.getInt("level",1);
            for (int i = 0; i < list.size(); i++) {
                NbtCompound nbtTime = list.getCompoundOrEmpty(i);

                int x = nbtTime.getInt("x", 0);
                int y = nbtTime.getInt("y", 0);
                int z = nbtTime.getInt("z", 0);

                BlockState state = NbtHelper.toBlockState(
                        blockLookup,
                        nbtTime.getCompoundOrEmpty("state")
                );

                blocks.add(new BlockWithData(x, y, z, state));
            }

            this.blocks = blocks;
        }

        public List<BlockWithData> getBlocks() {
            return blocks;
        }
        public String getName() { return name;}
        public int getCost() { return cost;}
        public int getIncome() { return income;}
        public int getSpawnCD() { return spawnCD;}
        public int getLevel() {return level;}
        public String getDisplayName() {return displayName;}

        /**
         * Сериализует шаблон постройки в NBT.
         *
         * <p>В результирующий {@link NbtCompound} записываются идентификатор шаблона,
         * его имя и список блоков с координатами и состояниями.
         *
         * @return NBT-представление текущего шаблона
         */
        public NbtCompound toNbt() {
            NbtCompound nbt = new NbtCompound();
            nbt.putString("display_name", this.displayName);
            nbt.putInt("income",this.income);
            nbt.putInt("spawnCD",this.spawnCD);
            nbt.putInt("cost",this.cost);
            nbt.putInt("level",this.level);
            NbtList nbtList = new NbtList();
            for (BlockWithData block : this.blocks) {
                NbtCompound nbtTime = new NbtCompound();
                nbtTime.putInt("x", block.x());
                nbtTime.putInt("y", block.y());
                nbtTime.putInt("z", block.z());
                nbtTime.put("state", NbtHelper.fromBlockState(block.state()));
                nbtList.add(nbtTime);
            }

            nbt.put("blocks", nbtList);
            return nbt;
        }
        public void write(RegistryByteBuf buf){
            buf.writeString(this.displayName);
            buf.writeInt(this.level);
            buf.writeInt(this.income);
            buf.writeInt(this.spawnCD);
            buf.writeInt(this.cost);
            buf.writeInt(this.blocks.size());
            for (BlockWithData block:this.blocks){
                block.write(buf);
            }
        }
        public static BuildTemplate read(RegistryByteBuf buf){
            String display_name = buf.readString();
            int level = buf.readInt();
            int income = buf.readInt();
            int spawnCD = buf.readInt();
            int cost = buf.readInt();
            int size = buf.readInt();
            ArrayList<BlockWithData> blocks = new ArrayList<>();
            for (int i =0;i<size;i++){
                blocks.add(BlockWithData.read(buf));
            }
            return new BuildTemplate(display_name,level,blocks,income,spawnCD,cost);
        }
        public static final PacketCodec<RegistryByteBuf,BuildTemplate> PACKET_CODEC = PacketCodec.of(
                ((value, buf) -> value.write(buf)),
                BuildTemplate::read
        );
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
                                    BuildTemplate build,
                                        int delayTicks) {

        Queue<BlockWithData> queue = new ArrayDeque<>();
        queue.addAll(build.blocks);


        TASKS.add(new BuildTask(world, queue, delayTicks,origin));
    }

    public static List<BlockWithData> scanSection(BlockPos startpos, ServerWorld world){
        List<BlockWithData> ans = new ArrayList<>();
        for (int y = 0;y < 5;y++){
            for (int x = -1;x < 2;x++){
                for (int z = -1;z < 2;z++){
                    BlockPos pos2 = startpos.add(x,y,z);
                    BlockState block = world.getBlockState(pos2);
                    ans.add(new BlockWithData(x,y,z,block));
                }
            }
        }
        return ans;
    }
}
