package com.custom.castlefight.custom_castlefight.CustomFunc;

import java.util.*;

public class MatchesUtilities {
    public static class Match {
        final UUID id;
        final MapUtilities.Map map;
        final int maxPlayerInTeam;
        MatchState matchState;
        int matchTime = 72000;
        List<Team> teams = new ArrayList<>();

        public Match(UUID id, MapUtilities.Map map,int maxPlayerInTeam) {
            this.id = id;
            this.map = map;
            this.matchState = MatchState.CHOICE_TEAM;
            this.maxPlayerInTeam = maxPlayerInTeam;
        }

        public void addPlayerToTeam(UUID player, Color color) {
            if (!hasPlayer(player)) {
                for (Team team : this.teams) {
                    if (Objects.equals(team.getColor(), color)) {
                        if (team.countPlayers() < this.maxPlayerInTeam)team.addPlayer(player);
                        return;
                    }
                }
            }
        }

        public void removePlayer(UUID player) {
            for (Team team : this.teams) {
                if (team.hasPlayer(player)) team.removePlayer(player);
            }
        }

        public boolean hasPlayer(UUID player) {
            boolean ans;
            for (Team team : this.teams) {
                ans = team.hasPlayer(player);
                if (ans) return true;
            }
            return false;
        }

        public void addTeam(Color color) {
            for (Team team : this.teams) {
                if (Objects.equals(team.color, color)) return;
            }
            this.teams.add(new Team(color));
        }

        public void removeTeam(Team team) {
            this.teams.remove(team);
        }

        public void removeTeam(Color color) {
            this.teams.removeIf(match -> Objects.equals(match.getColor(),color));
        }

        public UUID getId() {
            return id;
        }

        public int getMatchTime() {
            return matchTime;
        }

        public MapUtilities.Map getMap() {
            return map;
        }

        public MatchState getMatchState() {
            return matchState;
        }

        public void setMatchState(MatchState matchState) {
            this.matchState = matchState;
        }

        public void setMatchTime(int matchTime) {
            this.matchTime = matchTime;
        }

        public List<Team> getTeams() {
            return teams;
        }

        public void setTeams(List<Team> teams) {
            this.teams = teams;
        }

        protected void tick() {
            switch (this.matchState) {
                case MatchState.CHOICE_TEAM -> {
                }
                case MatchState.BAN_RACE -> {
                }
                case MatchState.CHOICE_RACE -> {
                }
                case MatchState.START_GAME -> {
                }
                case MatchState.PLAYING -> {
                }
                case MatchState.END_GAME -> {
                }
                default -> {
                }
            }
        }
    }

    public static class MatchManager {
        private final Map<UUID,Match> matches = new HashMap<>();
        private static final MatchManager matchManager = new MatchManager();
        public static MatchManager getInstance(){
            return matchManager;
        }

        private MatchManager() {

        }

        public boolean hasId(UUID id) {
            return this.matches.containsKey(id);
        }

        public void addMatch(Match match) {
            if (!hasId(match.getId())){
                this.matches.put(match.getId(),match);
            }
        }
        public void removeMatch(UUID id){
            this.matches.remove(id);
        }

        public void tickAllMatches(){
            for (Match match : this.matches.values()){
                match.tick();
            }
        }

        public Match getMatch(UUID id){
            return this.matches.get(id);
        }

        public Map<UUID, Match> getMatches() {
            return matches;
        }
    }

    public enum MatchState {
        CHOICE_TEAM,
        BAN_RACE,
        CHOICE_RACE,
        START_GAME,
        PLAYING,
        END_GAME,
        ENDED_GAME
    }
    public enum Color {
        RED,
        BLUE,
        GREEN,
        YELLOW,
    }

    public static class Team {
        List<UUID> players = new ArrayList<>();
        Color color;

        public Team(Color color) {
            this.color = color;
        }
        public int countPlayers(){
            return this.players.size();
        }
        public Color getColor() {
            return this.color;
        }

        public boolean hasPlayer(UUID player) {
            return this.players.contains(player);
        }

        public void addPlayer(UUID player) {
            this.players.add(player);
        }

        public void removePlayer(UUID player) {
            this.players.remove(player);
        }
    }
}
