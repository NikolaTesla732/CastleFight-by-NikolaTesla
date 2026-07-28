package com.custom.castlefight.custom_castlefight.CustomFunc;

import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendCountPlayerS2CPacket;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendMatchAnswerS2CPacket;
import com.custom.castlefight.custom_castlefight.Network.PacketsS2C.SendMatchStateS2CPacket;
import com.custom.castlefight.custom_castlefight.Network.screenhandler.LobbyScreen;
import com.custom.castlefight.custom_castlefight.Network.screenhandler.MainGameScreen;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.custom.castlefight.custom_castlefight.Custom_castlefight.LOGGER;
import static com.custom.castlefight.custom_castlefight.Custom_castlefight.TEMPLATES;

public class MatchUtilities {
    public static class Match {
        private final UUID id;
        private MapUtilities.GamingMap gamingMap;
        private final int maxPlayerInTeam;
        private MatchState matchState;
        private int matchTime = 0;
        private int absoluteMatchTime = 0;
        private List<Team> teams = new ArrayList<>();
        private List<UUID> playersInMatch = new ArrayList<>();
        private Set<String> races;
        private boolean needUpdateScreen = false;

        public Match(UUID id, int maxPlayerInTeam) {
            this.id = id;
            this.matchState = MatchState.START_GAME;
            this.maxPlayerInTeam = maxPlayerInTeam;
            races = TEMPLATES.getRace();
        }

        public void write(RegistryByteBuf buf) {
            buf.writeUuid(id);
            gamingMap.write(buf);
            buf.writeInt(maxPlayerInTeam);
            buf.writeEnumConstant(matchState);
            buf.writeInt(matchTime);
            buf.writeInt(teams.size());
            for (Team team : teams) {
                team.write(buf);
            }
            buf.writeInt(playersInMatch.size());
            for (UUID id : playersInMatch) {
                buf.writeUuid(id);
            }
            buf.writeInt(races.size());
            for (String race : races) {
                buf.writeString(race);
            }
        }

        public static Match read(RegistryByteBuf buf) {
            UUID id1 = buf.readUuid();
            MapUtilities.GamingMap gamingMap1 = MapUtilities.GamingMap.read(buf);
            int maxPlayerInTeam1 = buf.readInt();
            Match match = new Match(id1, maxPlayerInTeam1);
            match.setMap(gamingMap1);
            match.setMatchState(buf.readEnumConstant(MatchState.class));
            match.setMatchTime(buf.readInt());
            int n = buf.readInt();
            for (int i = 0; i < n; i++) {
                match.teams.add(Team.read(buf));
            }
            n = buf.readInt();
            for (int i = 0; i < n; i++) {
                match.playersInMatch.add(buf.readUuid());
            }
            n = buf.readInt();
            for (int i = 0; i < n; i++) {
                match.races.add(buf.readString());
            }
            return match;
        }

        public MatchAnswer ban(String race) {
            if (matchState != MatchState.BAN_RACE) return MatchAnswer.INVALID_STAGE;
            if (!races.contains(race)) return MatchAnswer.INVALID_RACE;
            races.remove(race);
            return MatchAnswer.NONE;
        }

        public void nextStage() {
            switch (matchState) {
                case SETTINGS -> {
                    this.matchState = MatchState.START_GAME;
                    LOGGER.info(matchState.toString());
                }
                case START_GAME -> {
                    needUpdateScreen = true;
                    this.matchState = MatchState.CHOICE_TEAM;
                    this.matchTime = 900;
                    this.absoluteMatchTime = 900;
                    LOGGER.info(matchState.toString());
                }
                case CHOICE_TEAM -> {
                    needUpdateScreen = true;
                    this.matchState = MatchState.BAN_RACE;
                    this.matchTime = 900;
                    this.absoluteMatchTime = 900;
                    LOGGER.info(matchState.toString());
                }
                case BAN_RACE -> {
                    needUpdateScreen = true;
                    this.matchState = MatchState.CHOICE_RACE;
                    this.matchTime = 900;
                    this.absoluteMatchTime = 900;
                    LOGGER.info(matchState.toString());
                }
                case CHOICE_RACE -> {
                    needUpdateScreen = true;
                    this.matchState = MatchState.PLAYING;
                    this.matchTime = 72000;
                    this.absoluteMatchTime = 72000;
                    LOGGER.info(matchState.toString());
                }
                case PLAYING -> {
                    needUpdateScreen = true;
                    this.matchState = MatchState.END_GAME;
                    LOGGER.info(matchState.toString());
                }
                case END_GAME -> {
                    this.matchState = MatchState.ENDED_GAME;
                    LOGGER.info(matchState.toString());
                }
            }
        }


        public MatchAnswer addPlayerToTeam(UUID player, TeamColor teamColor) {
            if (matchState != MatchState.CHOICE_TEAM) return MatchAnswer.INVALID_STAGE;
            for (Team team : this.teams) {
                if (Objects.equals(team.getColor(), teamColor)) {
                    if (hasPlayerInTeam(player)) removePlayerFromTeam(player);
                    if (team.countPlayers() >= this.maxPlayerInTeam) return MatchAnswer.FULL_TEAM;
                    team.addPlayer(player);
                    removePlayer(player);
                    return MatchAnswer.NONE;
                }
            }
            return MatchAnswer.NOT_FOUND_TEAM;
        }

        public MatchAnswer addPlayerToMatch(UUID player) {
            if (matchState != MatchState.START_GAME) return MatchAnswer.INVALID_STAGE;
            if (getAllPlayer().size() < getMaxPlayers()) playersInMatch.add(player);
            if (getAllPlayer().size() >= getMaxPlayers()) nextStage();
            if (getAllPlayer().size() > getMaxPlayers()) {
                playersInMatch.clear();
                return MatchAnswer.FULL_MATCH;
            }
            return MatchAnswer.NONE;
        }

        public void removePlayerFromTeam(UUID player) {
            for (Team team : this.teams) {
                if (team.hasPlayer(player)) {
                    team.removePlayer(player);
                }
            }
        }

        private void removePlayer(UUID player) {
            playersInMatch.remove(player);
        }

        public boolean hasPlayerInTeam(UUID player) {
            for (Team team : this.teams) {
                if (team.hasPlayer(player)) return true;
            }
            return false;
        }
        public MatchAnswer addNTeam(int n){
            if (matchState != MatchState.START_GAME) return MatchAnswer.INVALID_STAGE;
            for (TeamColor teamColor : TeamColor.values()){
                this.teams.add(new Team(teamColor));
                n--;
                if (n<=0) break;
            }
            return MatchAnswer.NONE;
        }
        public MatchAnswer addTeam(TeamColor teamColor) {
            if (matchState != MatchState.START_GAME) return MatchAnswer.INVALID_STAGE;
            for (Team team : this.teams) {
                if (Objects.equals(team.teamColor, teamColor)) return MatchAnswer.ALREADY_HAS_TEAM;
            }
            this.teams.add(new Team(teamColor));
            return MatchAnswer.NONE;
        }

        public void addTeam(Team team) {
            if (matchState != MatchState.SETTINGS) return;
            this.teams.add(team);
        }

        public void removeTeam(Team team) {
            if (matchState != MatchState.SETTINGS) return;
            this.teams.remove(team);
        }

        public void removeTeam(TeamColor teamColor) {
            if (matchState != MatchState.SETTINGS) return;
            this.teams.removeIf(match -> Objects.equals(match.getColor(), teamColor));
        }

        public List<UUID> getAllPlayersInTeam() {
            List<UUID> players = new ArrayList<>();
            for (Team team : teams) {
                players.addAll(team.getPlayers());
            }
            return players;
        }

        public List<UUID> getAllPlayer() {
            List<UUID> players = getAllPlayersInTeam();
            players.addAll(playersInMatch);
            return players;
        }

        public int getMaxPlayers() {
            return teams.size() * maxPlayerInTeam;
        }

        public int getMaxPlayerInTeam() {
            return maxPlayerInTeam;
        }

        public UUID getId() {
            return id;
        }

        public Set<String> getRaces() {
            return races;
        }

        public void setRaces(Set<String> races) {
            this.races = races;
        }

        public int getMatchTime() {
            return matchTime;
        }

        public MapUtilities.GamingMap getMap() {
            return gamingMap;
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

        public void setMap(MapUtilities.GamingMap gamingMap) {
            this.gamingMap = gamingMap;
        }

        public void setTeams(List<Team> teams) {
            this.teams = teams;
        }
        private void updateMainScreenForAll(MinecraftServer server){
            PlayerManager playerManager = server.getPlayerManager();
            for (UUID playerId : getAllPlayer()){
                if (playerManager.getPlayer(playerId) == null) continue;
                ServerPlayerEntity player = playerManager.getPlayer(playerId);
                if (player.currentScreenHandler instanceof MainGameScreen gameScreen){
                    ServerPlayNetworking.send(player,new SendMatchStateS2CPacket(matchState));
                    ServerPlayNetworking.send(player,new SendMatchAnswerS2CPacket(MatchAnswer.NEED_UPDATE));
                }
            }
        }

        public int getAbsoluteMatchTime() {
            return absoluteMatchTime;
        }

        protected void tick(MinecraftServer server) {
            switch (this.matchState) {
                case SETTINGS -> {
                }
                case MatchState.START_GAME -> {
                }
                case MatchState.CHOICE_TEAM -> {
                    this.matchTime--;
                    if (matchTime <= 0) {
                        needUpdateScreen = true;
                        if (!playersInMatch.isEmpty()) {
                            for (Team team : teams) {
                                while (team.countPlayers() < maxPlayerInTeam && !playersInMatch.isEmpty()) {
                                    team.addPlayer(playersInMatch.removeFirst());
                                }
                            }
                        }
                        nextStage();
                    }
                }
                case MatchState.BAN_RACE -> {
                    this.matchTime--;
                    if (matchTime <= 0) {
                        nextStage();
                    }
                }
                case MatchState.CHOICE_RACE -> {
                    this.matchTime--;
                    if (matchTime <= 0) {
                        nextStage();
                    }
                }
                case MatchState.PLAYING -> {
                    this.matchTime--;
                    if (matchTime <= 0) {
                        nextStage();
                    }
                }
                case MatchState.END_GAME -> {
                }

            }
            if (needUpdateScreen) {
                updateMainScreenForAll(server);
                needUpdateScreen = false;
            }
        }
    }
    public static enum MatchAnswer {
        NONE,
        FULL_MATCH,
        ALREADY_IN_GAME,
        INVALID_STAGE,
        FULL_TEAM,
        NOT_FOUND_TEAM,
        INVALID_RACE,
        NOT_FOUND_MATCH,
        ALREADY_HAS_TEAM,
        NEED_UPDATE

    }

    public static class MatchManager {
        private final Map<UUID, Match> matches = new HashMap<>();
        private final Map<UUID, UUID> playersMatch = new HashMap<>();
        private final Map<MatchFormat, List<UUID>> waitings = new HashMap<>();
        private final Map<MatchFormat, Match> nonActiveMatches = new HashMap<>();

        private static final MatchManager matchManager = new MatchManager();

        public static MatchManager getInstance() {
            return matchManager;
        }

        private MatchManager() {
            for (MatchFormat format : MatchFormat.values()) {
                waitings.put(format, new ArrayList<>());
                Match match = new Match(UUID.randomUUID(),formatTeamSize.get(format));
                match.addNTeam(formatTeamCount.get(format));
                nonActiveMatches.put(format,match);
            }
        }

        public boolean hasId(UUID id) {
            return this.matches.containsKey(id);
        }

        public boolean playerInMatch(UUID player) {
            return playersMatch.get(player) != null;
        }
        @Nullable
        public UUID getPlayerMatch(UUID player) {
            return playersMatch.get(player);
        }

        public void addWaiting(UUID player,MatchFormat format){
            if (playerInMatch(player)) return;
            waitings.get(format).addLast(player);
        }
        public void addMatch(Match match) {
            if (!hasId(match.getId())) {
                this.matches.put(match.getId(), match);
            }
            for (UUID player : match.getAllPlayersInTeam()) {
                playersMatch.computeIfAbsent(player, k -> match.getId());
            }
        }

        public void removeMatch(UUID id) {
            for (UUID player : matches.get(id).getAllPlayersInTeam()) {
                playersMatch.put(player, null);
            }
            this.matches.remove(id);
        }

        public Map<MatchFormat, List<UUID>> getWaitings() {
            return new HashMap<>(waitings);
        }
        public Map<MatchFormat,Integer> getCountPlayer(){
            Map<MatchFormat,Integer> countPlayer = new HashMap<>();
            for (MatchFormat format : MatchFormat.values()){
                countPlayer.put(format,this.nonActiveMatches.get(format).getAllPlayer().size());
            }
            return countPlayer;
        }
        public boolean playerCanViewLobby(UUID playerId){
            MatchUtilities.Match match = getMatch(playersMatch.get(playerId));
            if (match == null || match.getMatchState() == MatchUtilities.MatchState.START_GAME) return true;
            return false;
        }
        public Map<MatchFormat, Match> getNonActiveMatches() {
            return nonActiveMatches;
        }
        public void updateLobbyForAll(MinecraftServer server){
            Map<MatchFormat,Integer> countPlayer = getCountPlayer();
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()){
                if (player.currentScreenHandler instanceof LobbyScreen screen && playerCanViewLobby(player.getUuid())){
                   LOGGER.info("Игроку "+ player.getName() +"обновляется экран");
                    ServerPlayNetworking.send(player,new SendCountPlayerS2CPacket(countPlayer));
                }
            }
        }
        public void tick(MinecraftServer server) {
            for (Match match : this.matches.values()) {
                match.tick(server);
            }
            boolean needUpdateLobby = false;
            for (MatchFormat format : nonActiveMatches.keySet()){
                if (nonActiveMatches.get(format) == null) {
                    Match match1 = new Match(UUID.randomUUID(),formatTeamSize.get(format));
                    match1.addNTeam(formatTeamCount.get(format));
                    nonActiveMatches.put(format,match1);
                    continue;
                }
                if (nonActiveMatches.get(format) instanceof Match match){
                    if (match.matchState != MatchState.SETTINGS && match.matchState != MatchState.START_GAME){
                        matches.put(match.getId(),match);
                        Match match1 = new Match(UUID.randomUUID(),formatTeamSize.get(format));
                        match1.addNTeam(formatTeamCount.get(format));
                        nonActiveMatches.put(format,match1);
                        LOGGER.info("Матч формата: \""+format.toString()+"\" начался");
                        for (UUID playerId : match.getAllPlayer()){
                            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
                            if (player != null) player.closeHandledScreen();
                        }
                        needUpdateLobby = true;
                    }
                }
            }

            for (MatchFormat format : waitings.keySet()){
                if ( !waitings.get(format).isEmpty()){
                    ServerPlayerEntity player = null;
                    while (player == null){
                        UUID playerId = waitings.get(format).removeFirst();
                        player = server.getPlayerManager().getPlayer(playerId);
                    }
                    MatchAnswer answer = nonActiveMatches.get(format).addPlayerToMatch(player.getUuid());
                    if (answer == MatchAnswer.NONE) {
                        playersMatch.put(player.getUuid(), nonActiveMatches.get(format).getId());
                        needUpdateLobby = true;
                    }
                    LOGGER.info(String.valueOf(nonActiveMatches.get(format).getAllPlayer().size()));
                    ServerPlayNetworking.send(player,
                            new SendMatchAnswerS2CPacket(answer));
                }
            }
            if (needUpdateLobby) updateLobbyForAll(server);
        }

        public Match getMatch(UUID id) {
            return this.matches.get(id);
        }

        public Map<UUID, UUID> getPlayersMatch() {
            return playersMatch;
        }

        public Map<UUID, Match> getMatches() {
            return matches;
        }
    }
    public static enum MatchData{
        RACE,
        NAME,
        LEVEL,
        NEW_BUILD,
        OLD_BUILD,
        LEVELS_SET,
        NAMES_SET,
        RACES_SET,
        ANSWER,
        MATCHES,
        COUNT_PLAYER,
        PLAYERS_TEAM,
        MATCH_STATE
    }
    public enum MatchState {
        NOT_ACTIVE,
        SETTINGS,
        CHOICE_TEAM,
        BAN_RACE,
        CHOICE_RACE,
        START_GAME,
        PLAYING,
        END_GAME,
        ENDED_GAME
    }

    public enum TeamColor {
        RED,
        BLUE,
        GREEN,
        YELLOW,
    }

    public static class Team {
        List<UUID> players = new ArrayList<>();
        TeamColor teamColor;

        public void write(RegistryByteBuf buf) {
            buf.writeInt(players.size());
            for (UUID player : players) {
                buf.writeUuid(player);
            }
            buf.writeEnumConstant(teamColor);
        }

        public static Team read(RegistryByteBuf buf) {
            List<UUID> playerList = new ArrayList<>();
            int n = buf.readInt();
            for (int i = 0; i < n; i++) {
                playerList.add(buf.readUuid());
            }
            Team team = new Team(buf.readEnumConstant(TeamColor.class));
            for (UUID id : playerList) {
                team.addPlayer(id);
            }
            return team;
        }

        public Team(TeamColor teamColor) {
            this.teamColor = teamColor;
        }

        public int countPlayers() {
            return this.players.size();
        }

        public TeamColor getColor() {
            return teamColor;
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

        public List<String> getPlayersName(MinecraftServer server){
            List<String> playersList = new ArrayList<>();
            for (UUID playerId : players){
                playersList.add(server.getPlayerManager().getPlayer(playerId).getName().getString());
            }
            if (!playersList.isEmpty()) LOGGER.info(playersList.getFirst());
            return playersList;

        }
        public List<UUID> getPlayers() {
            return players;
        }
    }

    public enum ActionAdmin {
        ADD_MATCH,
        NEXT_STAGE
    }

    public static class AdminMatchAction {
        private final ActionAdmin action;
        private Match match;
        private MatchUtilities.MatchFormat matchFormat;
        public static PacketCodec<RegistryByteBuf, AdminMatchAction> PACKET_CODEC = PacketCodec.of(
                AdminMatchAction::write,
                AdminMatchAction::read
        );

        public MatchFormat getMatchFormat() {
            return matchFormat;
        }

        public void setMatchFormat(MatchFormat matchFormat) {
            this.matchFormat = matchFormat;
        }

        public AdminMatchAction(ActionAdmin actionAdmin) {
            this.action = actionAdmin;
        }

        public ActionAdmin getAction() {
            return action;
        }

        public void write(RegistryByteBuf buf) {
            buf.writeEnumConstant(action);
            buf.writeBoolean(this.match != null);
            if (this.match != null) match.write(buf);
            buf.writeBoolean(this.matchFormat != null);
            if ((this.matchFormat != null)) buf.writeEnumConstant(matchFormat);
        }

        public static AdminMatchAction read(RegistryByteBuf buf) {
            AdminMatchAction action = new AdminMatchAction(buf.readEnumConstant(ActionAdmin.class));
            if (buf.readBoolean()) action.setMatch(Match.read(buf));
            if (buf.readBoolean()) action.setMatchFormat(buf.readEnumConstant(MatchFormat.class));
            return action;
        }

        public Match getMatch() {
            return match;
        }

        public void setMatch(Match match) {
            this.match = match;
        }

        public boolean can() {
            switch (action) {
                case ADD_MATCH -> {
                    return match != null;
                }
                case NEXT_STAGE -> {
                    return matchFormat != null;
                }
            }
            return false;
        }

    }

    public enum MatchFormat {
        OneVsOne,
        TwoVsTwo,
        FourVsFour
    }
    public static Map<MatchFormat, Integer> formatTeamCount = Map.of(
            MatchFormat.OneVsOne,2,
            MatchFormat.TwoVsTwo,2,
            MatchFormat.FourVsFour,2
    );
    public static Map<MatchFormat, Integer> formatTeamSize = Map.of(
            MatchFormat.OneVsOne,1,
            MatchFormat.TwoVsTwo,2,
            MatchFormat.FourVsFour,4
    );
    public static Map<MatchFormat,String> formatDDescription = Map.of(
            MatchFormat.OneVsOne,"Стандартный матч 1x1",
            MatchFormat.TwoVsTwo,"Стандартный матч 2x2",
            MatchFormat.FourVsFour,"Стандартный матч 4x4"
    );
    public enum ActionPlayer {
        NONE,
        JOIN_MATCH,
        JOIN_TEAM,
        BAN_RACE,
        CHOOSE_RACE,
        GET_TEAMS,
        GET_MATCHES,
        OPEN_LOBBY,
        OPEN_MAIN

    }

    public static class MatchAction {
        private TeamColor team;
        private ActionPlayer action;
        private String race;
        private MatchFormat format;
        public static PacketCodec<RegistryByteBuf, MatchAction> PACKET_CODEC = PacketCodec.of(
                MatchAction::write,
                MatchAction::read
        );

        public void write(RegistryByteBuf buf) {
            buf.writeEnumConstant(this.action);
            buf.writeBoolean(this.team != null);
            if (this.team != null) buf.writeEnumConstant(this.team);
            buf.writeBoolean(this.race != null && !this.race.isBlank());
            if (this.race != null && !this.race.isBlank()) buf.writeString(this.race);
            buf.writeBoolean(this.format != null);
            if ((this.format != null))buf.writeEnumConstant(format);
        }

        public static MatchAction read(RegistryByteBuf buf) {
            MatchAction matchAction = new MatchAction();
            matchAction.setAction(buf.readEnumConstant(ActionPlayer.class));
            if (buf.readBoolean()) matchAction.setTeam(buf.readEnumConstant(TeamColor.class));
            if (buf.readBoolean()) matchAction.setRace(buf.readString());
            if (buf.readBoolean()) matchAction.setFormat(buf.readEnumConstant(MatchFormat.class));
            return matchAction;
        }

        public boolean can() {
            switch (this.action) {
                case JOIN_TEAM -> {
                    return this.team != null;
                }
                case BAN_RACE, CHOOSE_RACE -> {
                    return this.race != null;
                }
                case JOIN_MATCH -> {
                    return this.format != null;
                }
                case GET_TEAMS,GET_MATCHES,OPEN_LOBBY,OPEN_MAIN-> {
                    return true;
                }
            }
            return false;
        }

        public MatchAction() {
        }

        public MatchFormat getFormat() {
            return format;
        }

        public void setFormat(MatchFormat format) {
            this.format = format;
        }

        public TeamColor getTeam() {
            return team;
        }

        public void setTeam(TeamColor team) {
            this.team = team;
        }

        public ActionPlayer getAction() {
            return action;
        }

        public void setAction(ActionPlayer action) {
            this.action = action;
        }

        public String getRace() {
            return race;
        }

        public void setRace(String race) {
            this.race = race;
        }
    }
}
