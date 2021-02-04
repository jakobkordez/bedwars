package cc.jkob.bedwars.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.shop.Shopkeeper;
import cc.jkob.bedwars.util.SortByPlayers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public class Game {
    private String name, world;
    private HashMap<String, Team> teams = new HashMap<>(); // TODO: List
    private List<CommonGenerator> generators = new ArrayList<>();
    private List<Shopkeeper> shopkeepers = new ArrayList<>();
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

    public List<Shopkeeper> getShopkeepers() {
        return shopkeepers;
    }

    public Location getLobby() {
        return lobby;
    }

    public Type getGameType() {
        return gameType;
    }

    // transient
    private transient State state;
    private transient Set<UUID> players, spectators;
    private transient GameScoreboard scoreboard;
    private transient GameCycle gameCycle;

    public void initTransient() {
        state = State.STOPPED;
    }

    public State getState() {
        return state;
    }

    public Set<UUID> getPlayers() {
        return players;
    }

    public GameCycle getGameCycle() {
        return gameCycle;
    }

    public void init() {
        if (state != State.STOPPED) return;

        state = State.WAITING;

        teams.forEach((k, t) -> t.init(this));
        
        for (Entity entity : lobby.getWorld().getEntities())
            if (!(entity instanceof Player))
                entity.remove();
        
        players = new HashSet<>();
        spectators = new HashSet<>();
    }

    public void start() {
        if (state != State.WAITING) return;

        state = State.RUNNING;
        
        autoAssignTeams();

        players.clear();
        teams.forEach((k, t) -> players.addAll(t.getPlayers()));

        // Give scoreboard
        scoreboard = new GameScoreboard(this);
        scoreboard.startTask();
        getPlayerStream(true).forEach(p -> p.setScoreboard(scoreboard.getBoard()));

        // Start generators
        generators.forEach(Generator::start);
        for (Team team : teams.values())
            if (team.getPlayers().size() != 0)
                team.startGens();
        
        // Spawn shopkeepers
        shopkeepers.forEach(Shopkeeper::spawn);

        // Destroy beds without players
        for (Team team : teams.values())
            if (team.getPlayers().size() == 0)
                team.destroyBed();
        
        // Start game cycle
        gameCycle = new GameCycle(this);
        gameCycle.triggerNext();
    }

    public void end(Team winner) {
        if (state != State.RUNNING) return;

        state = State.ENDED;

        String ttitle = "" + ChatColor.GOLD + ChatColor.BOLD + "Tie";
        String subTitle = "" + ChatColor.RED + ChatColor.BOLD + "Game Over";
        if (winner != null) ttitle = "" + winner.getColor().getChatColor() + ChatColor.BOLD + " wins!";
        String title = ttitle;

        // TODO: packets?
        getPlayerStream(true).forEach(p -> p.sendTitle(title, subTitle));

        new BukkitRunnable(){
            public void run() {
                stop();
            }
        }.runTaskLater(BedWarsPlugin.getInstance(), 200);
    }

    public void stop() {
        if (state == State.STOPPED) return;

        state = State.STOPPED;

        // Remove scoreboard
        if (scoreboard != null) {
            scoreboard.stopTask();
            scoreboard = null;
            getPlayerStream(true).forEach(p -> p.setScoreboard(GameScoreboard.EMPTY));
        }

        // Stop generators
        generators.forEach(Generator::stop);
        teams.values().forEach(Team::stopGens);

        // Remove shopkeepers
        shopkeepers.forEach(Shopkeeper::remove);

        // Stop game cycle
        gameCycle.stop();
        gameCycle = null;

        players = spectators = null;
    }

    private void autoAssignTeams() {
        int maxPlayers = gameType.getPlayers();

        List<Team> fTeams = new ArrayList<>(teams.values());
        Collections.sort(fTeams, new SortByPlayers());
        Iterator<Team> teamIt = fTeams.iterator();

        Team cTeam = teamIt.next();
        while (cTeam.getPlayers().size() >= maxPlayers)
            if (teamIt.hasNext()) cTeam = teamIt.next();
            else break;

        for (UUID player : players)
            if (cTeam.getPlayers().size() < maxPlayers)
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
                players.add(player.getUniqueId());
                return true;

            case RUNNING:
                spectators.add(player.getUniqueId());
                return true;
            
            default:
                return false;
        }
    }

    public boolean isPlayerInGame(Player player) {
        UUID pUuid = player.getUniqueId();

        switch (state) {

            case WAITING:
                if (players.contains(pUuid)) return true;
                if (spectators.contains(pUuid)) return true;
                for (Team team : teams.values())
                    if (team.getPlayers().contains(pUuid))
                        return true;
                return false;

            case RUNNING:
                if (players.contains(pUuid)) return true;
                if (spectators.contains(pUuid)) return true;
                return false;

            default:
                return false;
        }
    }

    private Stream<? extends Player> getPlayerStream(boolean withSpectators) {
        if (withSpectators)
            return Bukkit.getServer().getOnlinePlayers().parallelStream()
                .filter(p -> players.contains(p.getUniqueId()) || spectators.contains(p.getUniqueId()));
        
        return Bukkit.getServer().getOnlinePlayers().parallelStream()
            .filter(p -> players.contains(p.getUniqueId()));
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
