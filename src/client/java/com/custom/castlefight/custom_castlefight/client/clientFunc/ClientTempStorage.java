package com.custom.castlefight.custom_castlefight.client.clientFunc;

import com.custom.castlefight.custom_castlefight.CustomFunc.BuildUtilities;
import com.custom.castlefight.custom_castlefight.CustomFunc.MatchUtilities;
import com.custom.castlefight.custom_castlefight.CustomFunc.StorageUpdateListener;

import java.util.*;

public class ClientTempStorage {

    public ClientTempStorage() {

    }

    private final List<StorageUpdateListener> listeners = new ArrayList<>();
    private String race, name;
    private int level, timer, fullTimer, goldTimer = MatchUtilities.Match.fullGoldTimer;
    private BuildUtilities.BuildTemplate newBuild, oldBuild;
    private Set<Integer> levelsSet;
    private Set<String> namesSet;
    private Set<String> racesSet;
    private MatchUtilities.MatchAnswer answer;
    private List<UUID> matches;
    private Map<MatchUtilities.MatchFormat, Integer> countPlayer;
    private Map<MatchUtilities.TeamColor, List<String>> playersTeam;
    private MatchUtilities.MatchState matchState;
    private MatchUtilities.PlayerData playerData;
    private boolean playerInMatch = false;
    public int goldEarnAnimationTime = 150;
    public long lastGoldEarn = 0;
    public int goldClip = 0;
    public int animationTime = goldEarnAnimationTime * 8;

    public void subscribe(StorageUpdateListener listener) {
        if (!listeners.contains(listener)) listeners.add(listener);
    }

    public void unsubscribe(StorageUpdateListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(MatchUtilities.MatchData data) {
        for (StorageUpdateListener listener : listeners) {
            listener.onStorageUpdate(data);
        }
    }

    public boolean isPlayerInMatch() {
        return playerInMatch;
    }

    public void setPlayerInMatch(boolean playerInMatch) {
        this.playerInMatch = playerInMatch;
        notifyListeners(MatchUtilities.MatchData.PLAYER_IN_MATCH);
    }

    public int getGoldTimer() {
        return goldTimer;
    }
    public void reduceTheGoldTimer(){
        goldTimer--;
    }
    public void setGoldTimer(int goldTimer) {
        this.goldTimer = goldTimer;
        notifyListeners(MatchUtilities.MatchData.GOLD_TIMER);
    }

    public MatchUtilities.PlayerData getPlayerData() {
        return playerData;
    }

    public void setPlayerData(MatchUtilities.PlayerData playerData) {
        this.playerData = playerData;
        notifyListeners(MatchUtilities.MatchData.PLAYER_DATA);
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
        notifyListeners(MatchUtilities.MatchData.TIMER);
    }

    public int getFullTimer() {
        return fullTimer;
    }

    public void setFullTimer(int fullTimer) {
        this.fullTimer = fullTimer;
        notifyListeners(MatchUtilities.MatchData.FULL_TIMER);
    }

    public void setMatchState(MatchUtilities.MatchState state) {
        matchState = state;
        notifyListeners(MatchUtilities.MatchData.MATCH_STATE);
    }

    public void setPlayersTeam(Map<MatchUtilities.TeamColor, List<String>> playersTeam) {
        this.playersTeam = playersTeam;
        notifyListeners(MatchUtilities.MatchData.PLAYERS_TEAM);
    }

    public void setCountPlayer(Map<MatchUtilities.MatchFormat, Integer> countPlayer1) {
        this.countPlayer = countPlayer1;
        notifyListeners(MatchUtilities.MatchData.COUNT_PLAYER);
    }

    public void setMatches(List<UUID> matches) {
        this.matches = matches;
        notifyListeners(MatchUtilities.MatchData.MATCHES);
    }

    public void setAnswer(MatchUtilities.MatchAnswer answer) {
        this.answer = answer;
        notifyListeners(MatchUtilities.MatchData.ANSWER);
    }

    public void setNewBuild(BuildUtilities.BuildTemplate build) {
        this.newBuild = build;
        notifyListeners(MatchUtilities.MatchData.NEW_BUILD);
    }

    public void setOldBuild(BuildUtilities.BuildTemplate build) {
        this.oldBuild = build;
        notifyListeners(MatchUtilities.MatchData.OLD_BUILD);
    }

    public void setLevel(int level) {
        this.level = level;
        notifyListeners(MatchUtilities.MatchData.LEVEL);
    }

    public void setName(String name) {
        this.name = name;
        notifyListeners(MatchUtilities.MatchData.NAME);
    }

    public void setRace(String race) {
        this.race = race;
        notifyListeners(MatchUtilities.MatchData.RACE);
    }

    public void setLevelsSet(Set<Integer> levelsSet) {
        this.levelsSet = levelsSet;
        notifyListeners(MatchUtilities.MatchData.LEVELS_SET);
    }

    public void setNamesSet(Set<String> namesSet) {
        this.namesSet = namesSet;
        notifyListeners(MatchUtilities.MatchData.NAMES_SET);
    }

    public void setRacesSet(Set<String> racesSet) {
        this.racesSet = racesSet;
        notifyListeners(MatchUtilities.MatchData.RACES_SET);
    }

    public String getRace() {
        return race;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public BuildUtilities.BuildTemplate getNewBuild() {
        return newBuild;
    }

    public BuildUtilities.BuildTemplate getOldBuild() {
        return oldBuild;
    }

    public Set<Integer> getLevelsSet() {
        return new HashSet<>(levelsSet);
    }

    public Set<String> getNamesSet() {
        return new HashSet<>(namesSet);
    }

    public Set<String> getRacesSet() {
        return new HashSet<>(racesSet);
    }

    public MatchUtilities.MatchAnswer getAnswer() {
        return answer;
    }

    public List<UUID> getMatches() {
        return new ArrayList<>(matches);
    }

    public Map<MatchUtilities.MatchFormat, Integer> getCountPlayer() {
        return new HashMap<>(countPlayer);
    }

    public MatchUtilities.MatchState getMatchState() {
        return matchState;
    }

    public Map<MatchUtilities.TeamColor, List<String>> getPlayersTeam() {
        return new HashMap<>(playersTeam);
    }
}
