package com.custom.castlefight.custom_castlefight.client.clientFunc;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildUtilities;
import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;

import java.util.*;

public class ClientTempStorage {

    public ClientTempStorage() {

    }

    private String race, name;
    private int level;
    private BuildUtilities.BuildTemplate newBuild, oldBuild;
    private Set<Integer> levelsSet;
    private Set<String> namesSet;
    private Set<String> racesSet;
    private MatchUtilities.MatchAnswer answer;
    private boolean changes = false;
    private List<UUID> matches;
    private Map<MatchUtilities.MatchFormat,Integer> countPlayer;

    
    public void markDirty(){
        this.changes = true;
    }
    public List<UUID> getMatches() {
        resetChanges();
        return matches;
    }
    public boolean hasAnswer(){
        return answer != null;
    }
    public boolean hasMatches(){
        return matches != null;
    }
    public boolean hasCountPlayer() {return countPlayer != null;}

    public Map<MatchUtilities.MatchFormat, Integer> getCountPlayer() {
        resetChanges();
        return new HashMap<>(countPlayer);
    }
    public Map<MatchUtilities.MatchFormat, Integer> getCountPlayerWithClean() {
        Map<MatchUtilities.MatchFormat,Integer> countPlayer1 = new HashMap<>(countPlayer);
        this.countPlayer = null;
        resetChanges();
        return countPlayer1;
    }
    public void setCountPlayer(Map<MatchUtilities.MatchFormat, Integer> countPlayer1){
        this.countPlayer = countPlayer1;
        markDirty();
    }
    public List<UUID> getMatchesWithClean() {
        List<UUID> matches1 = new ArrayList<>(matches);
        matches = null;
        resetChanges();
        return matches1;
    }

    public void setMatches(List<UUID> matches) {
        this.matches = matches;
        markDirty();
    }

    public void resetChanges() {
        this.changes = false;
    }

    public MatchUtilities.MatchAnswer getAnswer() {
        resetChanges();
        return answer;
    }

    public void setAnswer(MatchUtilities.MatchAnswer answer) {
        this.answer = answer;
        markDirty();
    }

    public MatchUtilities.MatchAnswer getAnswerWithClean() {
        MatchUtilities.MatchAnswer matchAnswer = answer;
        resetChanges();
        answer = null;
        return matchAnswer;
    }

    public void setNewBuild(BuildUtilities.BuildTemplate build) {
        this.newBuild = build;
        markDirty();
    }

    public void setOldBuild(BuildUtilities.BuildTemplate build) {
        this.oldBuild = build;
        markDirty();
    }

    public void setLevel(int level) {
        this.level = level;
        markDirty();
    }

    public void setName(String name) {
        this.name = name;
        markDirty();
    }

    public void setRace(String race) {
        this.race = race;
        markDirty();
    }

    public void setLevelsSet(Set<Integer> levelsSet) {
        this.levelsSet = levelsSet;
        markDirty();
    }

    public void setNamesSet(Set<String> namesSet) {
        this.namesSet = namesSet;
        markDirty();
    }

    public void setRacesSet(Set<String> racesSet) {
        this.racesSet = racesSet;
        markDirty();
    }

    public boolean getChanges() {
        return this.changes;
    }

    public String getNameWithClean() {
        String nameTemp = name;
        this.name = null;
        resetChanges();
        return nameTemp;
    }

    public BuildUtilities.BuildTemplate getNewBuildWithClean() {
        BuildUtilities.BuildTemplate build = newBuild;
        this.newBuild = null;
        resetChanges();
        return build;
    }

    public BuildUtilities.BuildTemplate getOldBuildWithClean() {
        BuildUtilities.BuildTemplate build = oldBuild;
        this.oldBuild = null;
        resetChanges();
        return build;
    }

    public int getLevelWithClean() {
        int levelTemp = level;
        this.level = 0;
        resetChanges();
        return levelTemp;
    }

    public String getRaceWithClean() {
        String raceTemp = race;
        this.race = null;
        resetChanges();
        return raceTemp;
    }

    public Set<Integer> getLevelsSetWithClean() {
        Set<Integer> set = new HashSet<>(levelsSet);
        this.levelsSet.clear();
        resetChanges();
        return set;
    }

    public Set<String> getNamesSetWithClean() {
        Set<String> set = new HashSet<>(namesSet);
        this.namesSet.clear();
        resetChanges();
        return set;
    }

    public Set<String> getRacesSetWithClean() {
        Set<String> set = new HashSet<>(racesSet);
        this.racesSet.clear();
        resetChanges();
        return set;
    }

    public Set<String> getRacesSet() {
        resetChanges();
        return new HashSet<>(racesSet);
    }

    public Set<String> getNamesSet() {
        resetChanges();
        return new HashSet<>(namesSet);
    }

    public Set<Integer> getLevelsSet() {
        resetChanges();
        return new HashSet<>(levelsSet);
    }

    public BuildUtilities.BuildTemplate getOldBuild() {
        resetChanges();
        return oldBuild;
    }

    public BuildUtilities.BuildTemplate getNewBuild() {
        resetChanges();
        return newBuild;
    }

    public String getRace() {
        resetChanges();
        return race;
    }

    public int getLevel() {
        resetChanges();
        return level;
    }

    public String getName() {
        resetChanges();
        return name;
    }

    public boolean hasRace() {
        return race != null && !race.isBlank();
    }

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasLevel() {
        return level > 0;
    }

    public boolean hasNewBuild() {
        return newBuild != null;
    }

    public boolean hasOldBuild() {
        return oldBuild != null;
    }

    public boolean hasRacesSet() {
        return racesSet != null && !racesSet.isEmpty();
    }

    public boolean hasNamesSet() {
        return namesSet != null && !namesSet.isEmpty();
    }

    public boolean hasLevelsSet() {
        return levelsSet != null && !levelsSet.isEmpty();
    }


}
