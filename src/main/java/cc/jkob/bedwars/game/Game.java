package cc.jkob.bedwars.game;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Game {
    private String name, world;
    private HashMap<String, Team> teams = new HashMap<>();
    private List<Generator> generators = new ArrayList<>();
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

    public List<Generator> getGenerators() {
        return generators;
    }

    public Location getLobby() {
        return lobby;
    }

    // transient
    private transient State state = State.STOPPED;
    private transient List<Player> players, spectators;

    public void init() {
        state = State.WAITING;

        players = new ArrayList<>();
        spectators = new ArrayList<>();
    }

    public void start() {
        state = State.RUNNING;

        for (Generator gen : generators)
            gen.start();

        players = null;
    }

    public void stop() {
        state = State.STOPPED;

        for (Generator gen : generators)
            gen.stop();

        players = spectators = null;
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
