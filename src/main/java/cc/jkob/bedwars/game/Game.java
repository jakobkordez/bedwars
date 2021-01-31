package cc.jkob.bedwars.game;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import cc.jkob.bedwars.util.SortByPlayers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Game {
    private String name, world;
    private HashMap<String, Team> teams = new HashMap<>();
    private List<CommonGenerator> generators = new ArrayList<>();
    private Location lobby;
    private Type gameType = Type.FOURS;

    public Game(String name, String world) {
        this.name = name;
        this.world = world;
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public HashMap<String, Team> getTeams() {
        return teams;
    }

    public List<CommonGenerator> getGenerators() {
        return generators;
    }

    public Location getLobby() {
        return lobby;
    }

    public Type getGameType() {
        return gameType;
    }

    // transient
    private transient State state;
    private transient List<Player> players, spectators;
    private transient GameScoreboard scoreboard;

    public void initTransient() {
        state = State.STOPPED;
    }

    public State getState() {
        return state;
    }

    public void init() {
        if (state != State.STOPPED) return;

        state = State.WAITING;

        for (Team team : teams.values())
            team.init();
        
        for (Entity entity : lobby.getWorld().getEntities())
            if (!(entity instanceof Player))
                entity.remove();
        
        players = new ArrayList<>();
        spectators = new ArrayList<>();
    }

    public void start() {
        if (state != State.WAITING) return;

        state = State.RUNNING;
        
        autoAssignTeams();

        players.clear();
        for (Team team : teams.values())
            players.addAll(team.getPlayers());

        // Give scoreboard
        scoreboard = new GameScoreboard(this);
        scoreboard.setObjective();

        for (Player player : players)
            player.setScoreboard(scoreboard.getBoard());
        for (Player player : spectators)
            player.setScoreboard(scoreboard.getBoard());

        // Start generators
        for (Generator gen : generators)
            gen.start();
        for (Team team : teams.values())
            if (team.getPlayers().size() != 0) {
                team.getIronGen().start();
                team.getGoldGen().start();
            }
    }

    public void stop() {
        if (state == State.STOPPED) return;

        state = State.STOPPED;

        // Remove scoreboard
        for (Player player : players)
            player.setScoreboard(GameScoreboard.EMPTY);
        for (Player player : spectators)
            player.setScoreboard(GameScoreboard.EMPTY);
        scoreboard = null;

        // Stop generators
        for (Generator gen : generators)
            gen.stop();
        for (Team team : teams.values()) {
            team.getIronGen().stop();
            team.getGoldGen().stop();
        }

        players = spectators = null;
    }

    private void autoAssignTeams() {
        List<Team> fTeams = new ArrayList<>(teams.values());
        Collections.sort(fTeams, new SortByPlayers());
        Iterator<Team> teamIt = fTeams.iterator();

        Team cTeam = teamIt.next();
        while (cTeam.getPlayers().size() >= gameType.getPlayers())
            if (teamIt.hasNext()) cTeam = teamIt.next();
            else break;

        for (Player player : players)
            if (cTeam.getPlayers().size() < gameType.getPlayers())
                cTeam.getPlayers().add(player);
            else if (teamIt.hasNext())
                cTeam = teamIt.next();
            else
                spectators.add(player);
    }

    public boolean joinPlayer(Player player) {
        if (isPlayerInGame(player)) return false;
        
        switch (state) {
            
            case WAITING:
                players.add(player);
                return true;

            case RUNNING:
                spectators.add(player);
                return true;
            
            default:
                return false;
        }
    }

    public boolean isPlayerInGame(Player player) {
        switch (state) {

            case WAITING:
                if (players.contains(player)) return true;
                if (spectators.contains(player)) return true;
                for (Team team : teams.values())
                    if (team.getPlayers().contains(player))
                        return true;
                return false;

            case RUNNING:
                if (players.contains(player)) return true;
                if (spectators.contains(player)) return true;
                return false;

            default:
                return false;
        }
    }

    public enum State {
        STOPPED,
        WAITING,
        RUNNING,
        ENDED
    }

    public enum Type {
        SOLOS(1),
        DOUBLES(2),
        THREES(3),
        FOURS(4);

        private final int players;

        private Type(int players) {
            this.players = players;
        }

        public int getPlayers() {
            return players;
        }
    }
}
