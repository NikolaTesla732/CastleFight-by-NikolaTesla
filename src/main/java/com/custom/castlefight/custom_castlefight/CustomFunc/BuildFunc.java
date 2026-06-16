package com.custom.castlefight.custom_castlefight.CustomFunc;

import com.custom.castlefight.custom_castlefight.blocks.BuildingBlock;
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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldEvents;


import java.util.*;

public class BuildFunc {

    public record BlockWithData(int x, int y, int z, BlockState state,boolean center) {
        public void write(RegistryByteBuf buf) {
            buf.writeInt(x);
            buf.writeInt(y);
            buf.writeInt(z);
            buf.writeInt(Block.getRawIdFromState(state));
            buf.writeBoolean(center);
        }

        public static BlockWithData read(RegistryByteBuf buf) {
            int x = buf.readInt();
            int y = buf.readInt();
            int z = buf.readInt();
            int rawId = buf.readInt();
            BlockState state = Block.getStateFromRawId(rawId);
            boolean center = buf.readBoolean();
            return new BlockWithData(x, y, z, state,center);
        }

        public static final PacketCodec<RegistryByteBuf, BlockWithData> PACKET_CODEC =
                PacketCodec.of(
                        BlockWithData::write,
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
        private final String race;
        private final int level;
        private final List<BlockWithData> blocks;
        private final int income;
        private final int spawnCD;
        private final int cost;
        private final String displayName;

        public BuildTemplate(String name, String race, int level,
                             List<BlockWithData> blocks, int income, int spawnCD, int cost) {
            this.blocks = blocks;
            this.name = normalizeName(name);
            this.level = level;
            this.income = income;
            this.spawnCD = spawnCD;
            this.cost = cost;
            this.displayName = name;
            this.race = normalizeName(race);
        }

        public static String normalizeName(String rawName) {
            return rawName.toLowerCase(Locale.ROOT).replace(' ', '_');
        }

        public BuildTemplate(NbtCompound data, RegistryWrapper.WrapperLookup lookup) {
            this.displayName = data.getString("display_name", "");
            this.name = normalizeName(displayName);
            this.race = data.getString("race", "тьма");
            this.income = data.getInt("income", 0);
            this.spawnCD = data.getInt("spawnCD", 40);
            this.cost = data.getInt("cost", 100);
            NbtList list = data.getListOrEmpty("blocks");
            List<BlockWithData> blocks = new ArrayList<>();
            RegistryEntryLookup<Block> blockLookup = lookup.getOrThrow(RegistryKeys.BLOCK);
            this.level = data.getInt("level", 1);
            for (int i = 0; i < list.size(); i++) {
                NbtCompound nbtTime = list.getCompoundOrEmpty(i);
                int x = nbtTime.getInt("x", 0);
                int y = nbtTime.getInt("y", 0);
                int z = nbtTime.getInt("z", 0);
                BlockState state = NbtHelper.toBlockState(
                        blockLookup,
                        nbtTime.getCompoundOrEmpty("state")
                );
                boolean center = nbtTime.getBoolean("center",false);

                blocks.add(new BlockWithData(x, y, z, state,center));
            }

            this.blocks = blocks;
        }

        public List<BlockWithData> getBlocks() {
            return blocks;
        }

        public String getName() {
            return name;
        }

        public int getCost() {
            return cost;
        }

        public int getIncome() {
            return income;
        }

        public int getSpawnCD() {
            return spawnCD;
        }

        public int getLevel() {
            return level;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getRace() {
            return race;
        }

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
            nbt.putString("race", this.race);
            nbt.putInt("income", this.income);
            nbt.putInt("spawnCD", this.spawnCD);
            nbt.putInt("cost", this.cost);
            nbt.putInt("level", this.level);
            NbtList nbtList = new NbtList();
            for (BlockWithData block : this.blocks) {
                NbtCompound nbtTime = new NbtCompound();
                nbtTime.putInt("x", block.x());
                nbtTime.putInt("y", block.y());
                nbtTime.putInt("z", block.z());
                nbtTime.put("state", NbtHelper.fromBlockState(block.state()));
                nbtTime.putBoolean("center",block.center());
                nbtList.add(nbtTime);
            }

            nbt.put("blocks", nbtList);
            return nbt;
        }

        public void write(RegistryByteBuf buf) {
            buf.writeString(this.displayName);
            buf.writeString(this.race);
            buf.writeInt(this.level);
            buf.writeInt(this.income);
            buf.writeInt(this.spawnCD);
            buf.writeInt(this.cost);
            buf.writeInt(this.blocks.size());
            for (BlockWithData block : this.blocks) {
                block.write(buf);
            }
        }

        public static BuildTemplate read(RegistryByteBuf buf) {
            String display_name = buf.readString();
            String race = buf.readString();
            int level = buf.readInt();
            int income = buf.readInt();
            int spawnCD = buf.readInt();
            int cost = buf.readInt();
            int size = buf.readInt();
            ArrayList<BlockWithData> blocks = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                blocks.add(BlockWithData.read(buf));
            }
            return new BuildTemplate(display_name, race, level, blocks, income, spawnCD, cost);
        }

        public static final PacketCodec<RegistryByteBuf, BuildTemplate> PACKET_CODEC = PacketCodec.of(
                ((value, buf) -> value.write(buf)),
                BuildTemplate::read
        );
    }

    public static void build(ServerWorld world, BlockWithData block, BlockPos origin) {
        BlockState state = block.state;
        world.setBlockState(origin.add(block.x, block.y, block.z),BuildingBlock.BUILDING_BLOCK.getDefaultState());
        if (world.isClient()) return;
        world.syncWorldEvent(
                WorldEvents.BLOCK_BROKEN,
                origin.add(block.x, block.y, block.z),
                Block.getRawIdFromState(state)
        );
    }

    public static List<BlockWithData> scanSection(BlockPos startpos, ServerWorld world) {
        List<BlockWithData> ans = new ArrayList<>();
        for (int y = 0; y < 20; y++) {
            boolean hasBlocks = false;
            for (int x = -1; x < 2; x++) {
                for (int z = -1; z < 2; z++) {
                    BlockPos pos2 = startpos.add(x, y, z);
                    BlockState blockState = world.getBlockState(pos2);
                    if (!blockState.isAir()) hasBlocks = true;
                    ans.add(new BlockWithData(x, y, z, blockState,false));
                }
            }
            if (!hasBlocks){
                for (int i = 0;i<9;i++){
                    ans.removeLast();
                }
                for (int i = ans.size()-1;i>=0;i--){
                    if (ans.get(i).x() == 0 && ans.get(i).z() == 0){
                        BlockState blockState = ans.get(i).state;
                        int yN = ans.get(i).y();
                        ans.set(i,new BlockWithData(0, yN, 0, blockState,true));
                        break;
                    }
                }
                break;
            }
        }
        return ans;
    }

}
