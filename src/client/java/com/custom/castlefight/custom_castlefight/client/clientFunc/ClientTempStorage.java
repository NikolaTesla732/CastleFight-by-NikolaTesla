package com.custom.castlefight.custom_castlefight.client.clientFunc;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildFunc;

import java.util.HashSet;
import java.util.Set;

public class ClientTempStorage {

    public ClientTempStorage(){

    }
    private String race,name;
    private int level;
    private BuildFunc.BuildTemplate newBuild,oldBuild;
    private Set<Integer> levelsSet;
    private Set<String> namesSet;
    private Set<String> racesSet;
    private boolean changes = false;

    public void hasChanges(){
        this.changes = true;
    }
    public void resetChanges(){
        this.changes = false;
    }
    public void setNewBuild(BuildFunc.BuildTemplate build) {
        this.newBuild = build;
        this.changes = true;
    }
    public void setOldBuild(BuildFunc.BuildTemplate build) {
        this.oldBuild = build;
        this.changes = true;
    }
    public void setLevel(int level) {
        this.level = level;
        this.changes = true;
    }
    public void setName(String name) {
        this.name = name;
        this.changes = true;
    }
    public void setRace(String race) {
        this.race = race;
        this.changes = true;
    }
    public void setLevelsSet(Set<Integer> levelsSet) {
        this.levelsSet = levelsSet;
        this.changes = true;
    }
    public void setNamesSet(Set<String> namesSet) {
        this.namesSet = namesSet;
        this.changes = true;
    }
    public void setRacesSet(Set<String> racesSet) {
        this.racesSet = racesSet;
        this.changes = true;
    }

    public boolean getChanges(){
        return this.changes;
    }
    public String getNameWithClean() {
        String nameTemp = name;
        this.name = null;
        this.changes = false;
        return nameTemp;
    }
    public BuildFunc.BuildTemplate getNewBuildWithClean() {
        BuildFunc.BuildTemplate build = newBuild;
        this.newBuild = null;
        this.changes = false;
        return build;
    }
    public BuildFunc.BuildTemplate getOldBuildWithClean() {
        BuildFunc.BuildTemplate build = oldBuild;
        this.oldBuild = null;
        this.changes = false;
        return build;
    }
    public int getLevelWithClean() {
        int levelTemp = level;
        this.level = 0;
        this.changes = false;
        return levelTemp;
    }
    public String getRaceWithClean() {
        String raceTemp = race;
        this.race = null;
        this.changes = false;
        return raceTemp;
    }
    public Set<Integer> getLevelsSetWithClean() {
        Set<Integer> set = new HashSet<>(levelsSet);
        this.levelsSet.clear();
        this.changes = false;
        return set;
    }
    public Set<String> getNamesSetWithClean() {
        Set<String> set = new HashSet<>(namesSet);
        this.namesSet.clear();
        this.changes = false;
        return set;
    }
    public Set<String> getRacesSetWithClean() {
        Set<String> set = new HashSet<>(racesSet);
        this.racesSet.clear();
        this.changes = false;
        return set;
    }

    public Set<String> getRacesSet() {
        this.changes = false;
        return new HashSet<>(racesSet);
    }
    public Set<String> getNamesSet() {
        this.changes = false;
        return new HashSet<>(namesSet);
    }
    public Set<Integer> getLevelsSet() {
        this.changes = false;
        return new HashSet<>(levelsSet);
    }
    public BuildFunc.BuildTemplate getOldBuild() {
        this.changes = false;
        return oldBuild;
    }
    public BuildFunc.BuildTemplate getNewBuild() {
        this.changes = false;
        return newBuild;
    }
    public String getRace() {
        this.changes = false;
        return race;
    }
    public int getLevel() {
        this.changes = false;
        return level;
    }
    public String getName() {
        this.changes = false;
        return name;
    }

    public boolean hasRace(){ return race != null && !race.isBlank();}
    public boolean hasName(){ return name != null && !name.isBlank();}
    public boolean hasLevel(){ return level > 0;}
    public boolean hasNewBuild(){ return newBuild != null;}
    public boolean hasOldBuild(){ return oldBuild != null;}
    public boolean hasRacesSet(){ return racesSet != null && !racesSet.isEmpty();}
    public boolean hasNamesSet(){ return namesSet != null && !namesSet.isEmpty();}
    public boolean hasLevelsSet(){ return levelsSet != null && !levelsSet.isEmpty();}


}
