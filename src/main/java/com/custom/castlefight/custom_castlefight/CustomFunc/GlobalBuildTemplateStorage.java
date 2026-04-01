package com.custom.castlefight.custom_castlefight.CustomFunc;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.registry.RegistryWrapper;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;

public class GlobalBuildTemplateStorage {
    private final Path filePath;
    private final Map<String, Map<String,Map<Integer, BuildFunc.BuildTemplate>>> templates = new HashMap<>();
    public GlobalBuildTemplateStorage() {
        Path configDir = FabricLoader.getInstance().getConfigDir().normalize();
        Path modDir = configDir.resolve("custom_castlefight");
        this.filePath = modDir.resolve("templates.nbt");
        try{
            Files.createDirectories(filePath.getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public void clear(){
        this.filePath.toFile().delete();
    }
    public void load(RegistryWrapper.WrapperLookup lookup) {
        try {
            NbtCompound nbt = NbtIo.read(this.filePath);
            if (nbt != null){
                templates.clear();
                NbtCompound allRace = nbt.getCompoundOrEmpty("BuildTemplates");
                for (String race : allRace.getKeys()){
                    Map <String,Map<Integer, BuildFunc.BuildTemplate>> races = new HashMap<>();
                    for (String buildName : allRace.getCompoundOrEmpty(race).getKeys()) {
                        Map<Integer, BuildFunc.BuildTemplate> buildLevels = new HashMap<>();
                        NbtCompound build = allRace.getCompoundOrEmpty(race).getCompoundOrEmpty(buildName);
                        for (String level : build.getKeys()) {
                            buildLevels.put(Integer.valueOf(level), new BuildFunc.BuildTemplate(build.getCompoundOrEmpty(level), lookup));
                        }
                        races.put(buildName, buildLevels);
                    }
                    this.templates.put(race,races);
                }
            }
        }catch (IOException e){
            LOGGER.error("Ошибка при чтении файла по пути:" + this.filePath,e);
        }

    }
    public void put(BuildFunc.BuildTemplate build){
        if(!this.templates.containsKey(build.getRace())){
            this.templates.put(build.getRace(),new HashMap<>());
        }
        Map<String,Map<Integer,BuildFunc.BuildTemplate>> buildTemp = this.templates.get(build.getRace());
        if(!buildTemp.containsKey(build.getName())){
            buildTemp.put(build.getName(),new HashMap<>());
        }
        buildTemp.get(build.getName()).put(build.getLevel(),build);
        this.templates.put(build.getRace(),buildTemp);
        save();
    }
    public boolean contains(BuildFunc.BuildTemplate build){
        return contains(build.getRace(),build.getName(),build.getLevel());
    }
    public boolean contains(String race){
        return this.templates.containsKey(race);
    }
    public boolean contains(String race,String name, int level){
        if (this.templates.containsKey(race)){
            if (this.templates.get(race).containsKey(name)){
                return this.templates.get(race).get(name).containsKey(level);
            }
        }
        return false;
    }
    public boolean contains(String race,String name){
        if (this.templates.containsKey(race)){
            return this.templates.get(race).containsKey(name);
        };
        return false;
    }
    public BuildFunc.BuildTemplate getBuild(BuildFunc.BuildTemplate build){
        return getBuild(build.getRace(),build.getName(),build.getLevel());
    }
    public BuildFunc.BuildTemplate getBuild(String race,String name,int level){
        if (contains(race,name,level)) return this.templates.get(race).get(name).get(level);
        return null;
    }
    public Map<String,Map<Integer, BuildFunc.BuildTemplate>> getRaceBuilds(String race){
        if (contains(race)) return this.templates.get(race);
        return new HashMap<>();
    }
    public Set<Integer> getLevels(String race,String name){
        if (contains(race,name)) return this.templates.get(race).get(name).keySet();
        return new HashSet<>();
    }

    /**
     * Функция удаления здания, по его объекту
     *
     * @param build BuildTemplate здания
     */
    public void removeBuild(BuildFunc.BuildTemplate build){
        removeBuild(build.getRace(),build.getName(),build.getLevel());
    }
    /**
     * Функция удаления здания, по его описанию
     *
     * @param race раса, к которой принадлежит здание
     * @param name название здания
     * @param level уровень здания
     */
    public void removeBuild(String race,String name,int level){
        if (contains(race,name,level)){
            this.templates.get(race).get(name).remove(level);
        }
        save();
    }
    public void removeAllLevelsBuild(BuildFunc.BuildTemplate build){
        removeAllLevelsBuild(build.getRace(),build.getName());
    }
    public void removeAllLevelsBuild(String race,String name){
        if (contains(race,name)) this.templates.get(race).remove(name);
        save();
    }
    public void removeRace(String race){
        if (contains(race)) this.templates.remove(race);
        save();
    }


    public void save() {
        NbtCompound nbt = new NbtCompound();
       for (String race : this.templates.keySet()) {
           NbtCompound raceAllBuilds = new NbtCompound();
            for (String name : this.templates.get(race).keySet()) {
                NbtCompound builds = new NbtCompound();
                for (int level : this.templates.get(race).get(name).keySet()) {
                    builds.put(String.valueOf(level), this.templates.get(race).get(name).get(level).toNbt());
                }
                raceAllBuilds.put(name,builds);
            }
           nbt.put(race, raceAllBuilds);
        }
        NbtCompound root = new NbtCompound();
        root.put("BuildTemplates",nbt);
        try {
            NbtIo.write(root,this.filePath);
        }catch (IOException e){
            LOGGER.error("Ошибка при записи файла по пути:" + this.filePath,e);
        }
    }
}
