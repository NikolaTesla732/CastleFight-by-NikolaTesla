package com.custom.castlefight.custom_castlefight.CustomFunc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import com.custom.castlefight.custom_castlefight.CustomFunc.BuildFunc.BuildTemplate;
import com.custom.castlefight.custom_castlefight.CustomFunc.BuildFunc.BlockWithData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.*;

public class Construction {
    public static class ConstructionTaskData {
        private BlockPos origin;

        private String race;
        private String name;
        private int level;

        private int nextBlockIndex;
        private int delayTicks;
        private int timer;

        public String getName() {
            return name;
        }

        public BlockPos getOrigin() {
            return origin;
        }

        public String getRace() {
            return race;
        }

        public int getLevel() {
            return level;
        }

        public int getNextBlockIndex() {
            return nextBlockIndex;
        }

        public int getDelayTicks() {
            return delayTicks;
        }

        public int getTimer() {
            return timer;
        }

        private static final Codec<ConstructionTaskData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockPos.CODEC
                        .fieldOf("origin")
                        .forGetter(ConstructionTaskData::getOrigin),
                Codec.STRING
                        .fieldOf("race")
                        .forGetter(ConstructionTaskData::getRace),
                Codec.STRING
                        .fieldOf("name")
                        .forGetter(ConstructionTaskData::getName),
                Codec.INT
                        .fieldOf("level")
                        .forGetter(ConstructionTaskData::getLevel),
                Codec.INT
                        .fieldOf("nextBlockIndex")
                        .forGetter(ConstructionTaskData::getNextBlockIndex),
                Codec.INT
                        .fieldOf("delayTicks")
                        .forGetter(ConstructionTaskData::getDelayTicks),
                Codec.INT
                        .fieldOf("timer")
                        .forGetter(ConstructionTaskData::getTimer)
        ).apply(instance, ConstructionTaskData::new));


        public ConstructionTaskData(BlockPos origin, String race, String name, int level,
                                    int nextBlockIndex, int delayTicks, int timer) {
            this.origin = origin;
            this.race = race;
            this.name = name;
            this.level = level;
            this.nextBlockIndex = nextBlockIndex;
            this.delayTicks = delayTicks;
            this.timer = timer;
        }
    }

    public static class ConstructionState extends PersistentState {
        private List<ConstructionTaskData> tasks = new ArrayList<>();

        private static final Codec<ConstructionState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ConstructionTaskData.CODEC.listOf().fieldOf("tasks").forGetter(ConstructionState::getTasks)
        ).apply(instance, ConstructionState::new));

        private static final PersistentStateType<ConstructionState> TYPE =
                new PersistentStateType<>(
                        MOD_ID + "_constructions_state",
                        ConstructionState::new,
                        CODEC,
                        null
                );

        public ConstructionState(List<ConstructionTaskData> taskData) {
            this.tasks = new ArrayList<>(taskData);
        }

        public ConstructionState() {
            this.tasks = new ArrayList<>();
        }

        public List<ConstructionTaskData> getTasks() {
            return tasks;
        }

        public void addTask(ConstructionTaskData task) {
            tasks.add(task);
            markDirty();
        }

        public static ConstructionState get(ServerWorld world) {
            return world.getPersistentStateManager().getOrCreate(TYPE);
        }

        public void tick(ServerWorld world) {
            if (!tasks.isEmpty()) markDirty();
            for (int i = 0; i < tasks.size(); i++) {
                ConstructionTaskData task = tasks.get(i);
                if (task.timer > 0) {
                    task.timer--;
                } else {
                    String race = task.getRace();
                    String name = task.getName();
                    int level = task.getLevel();
                    BuildTemplate build = TEMPLATES.getBuild(race, name, level);
                    if (build == null){
                        task.timer = task.getDelayTicks();
                        LOGGER.error("Не удалось выполнить задачу постройки здания из-за невозможноности получения шаблона здания - раса: {},название: {},уровень: {}",race,name,level);
                        continue;
                    }
                    if (task.getNextBlockIndex() >= build.getBlocks().size()) {
                        LOGGER.info("Задача постройки здания {} завершена",build.getDisplayName());
                        tasks.remove(i);
                        i--;
                        continue;
                    }
                    BlockWithData block = build.getBlocks().get(task.getNextBlockIndex());
                    task.nextBlockIndex++;
                    BuildFunc.build(world, block, task.getOrigin());
                    task.timer = task.getDelayTicks();
                }
            }
        }
    }

    public static class ConstructionTicker {
        public static void register() {
            ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> {
                for (ServerWorld world : minecraftServer.getWorlds()) {
                    ConstructionState state = ConstructionState.get(world);
                    state.tick(world);
                }
            });
        }
    }
}
