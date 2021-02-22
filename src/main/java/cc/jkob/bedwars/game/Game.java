package cc.jkob.bedwars.game;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.PlayerData.GamePlayer;
import cc.jkob.bedwars.gui.Title;
import cc.jkob.bedwars.shop.Shopkeeper;
import cc.jkob.bedwars.util.ChatUtil;
import cc.jkob.bedwars.util.PlayerUtil;
import cc.jkob.bedwars.util.SortByPlayers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game {
    private String name, world;
    private List<Team> teams = new ArrayList<>();
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

    public List<Team> getTeams() {
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

    public Team getTeamByBed(Location bed) {
        return teams.parallelStream().filter(t -> t.isTeamBed(bed)).findAny().orElse(null);
    }

    // transient
    private transient GameState state;
    private transient Map<UUID, GamePlayer> players;
    private transient GameScoreboard scoreboard;
    private transient GameCycle gameCycle;
    private transient List<Location> placedBlocks;

    public void initTransient() {
        state = GameState.STOPPED;
    }

    public GameState getState() {
        return state;
    }

    public Map<UUID, GamePlayer> getPlayers() {
        return players;
    }

    public GameScoreboard getScoreboard() {
        return scoreboard;
    }

    public GameCycle getGameCycle() {
        return gameCycle;
    }

    public List<Location> getPlacedBlocks() {
        return placedBlocks;
    }
    
    public boolean isPlacedBlock(Location location) {
        return placedBlocks.parallelStream().anyMatch(l -> l.equals(location));
    }

    public void broadcast(String msg) {
        PlayerUtil.send(getPlayersD(), msg);
    }

    public void onTeamElim(Team team) {
        broadcast(ChatUtil.format(team.getFormattedName() + " Team", " has been eliminated"));
        List<Team> aliveT = teams.parallelStream()
            .filter(t -> t.playersAlive() != 0)
            .collect(Collectors.toList());
        if (aliveT.size() == 1) end(aliveT.get(0));
        else if (aliveT.size() == 0) end(null);
    }

    public void init() {
        if (state != GameState.STOPPED) return;

        state = GameState.WAITING;

        teams.forEach(t -> t.init(this));
        
        for (Entity entity : lobby.getWorld().getEntities())
            if (!(entity instanceof Player))
                entity.remove();
        
        players = new HashMap<>();
        placedBlocks = new ArrayList<>();

        // Init game cycle
        gameCycle = new GameCycle(this);
    }

    public void start() {
        if (state != GameState.WAITING) return;

        state = GameState.RUNNING;
        
        autoAssignTeams();

        // Scoreboard
        scoreboard = new GameScoreboard(this);
        scoreboard.startTask();

        // Start generators
        generators.forEach(Generator::start);
        for (Team team : teams)
            if (team.getPlayerCount() != 0)
                team.startGens();
        
        // Spawn shopkeepers
        shopkeepers.forEach(Shopkeeper::spawn);

        // Destroy beds without players
        for (Team team : teams)
            if (team.getPlayerCount() == 0)
                team.destroyBed();

        // Start game cycle
        gameCycle.triggerNext();

        getPlayerStream().sequential()
            .forEach(GamePlayer::onStart);
    }

    public void end(Team winner) {
        if (state != GameState.RUNNING) return;

        state = GameState.ENDED;

        Title title = new Title(ChatColor.GOLD + "Tie", ChatColor.RED + "Game Over", 0, 80, 20);
        if (winner != null) title.setTitle(winner.getColor().getChatColor() + winner.getName() + " wins!");
        PlayerUtil.send(getPlayersD(), title);

        new BukkitRunnable(){
            public void run() {
                stop();
            }
        }.runTaskLater(BedWarsPlugin.getInstance(), 100);
    }

    public void stop() {
        if (state == GameState.STOPPED) return;

        state = GameState.STOPPED;

        // Leave players
        players.forEach((k, p) -> p.leaveGame());

        // Remove scoreboard
        if (scoreboard != null) {
            scoreboard.stopTask();
            scoreboard = null;
        }

        // Stop generators
        generators.forEach(Generator::stop);
        teams.forEach(Team::stopGens);

        // Remove shopkeepers
        shopkeepers.forEach(Shopkeeper::remove);

        // Stop game cycle
        gameCycle.stop();
        gameCycle = null;

        // Reset map
        placedBlocks.forEach(b -> b.getBlock().setType(Material.AIR));
        teams.forEach(t -> t.init(this));
        for (Entity entity : lobby.getWorld().getEntities())
            if (!(entity instanceof Player))
                entity.remove();

        players = null;
        placedBlocks = null;
    }

    private void autoAssignTeams() {
        int maxPlayers = gameType.getPlayers();

        List<Team> fTeams = new ArrayList<>(teams);
        Collections.sort(fTeams, new SortByPlayers());
        Iterator<Team> teamIt = fTeams.iterator();

        Team cTeam = teamIt.next();
        while (cTeam.getPlayerCount() >= maxPlayers)
            if (teamIt.hasNext()) cTeam = teamIt.next();
            else break;

        // TODO: Shuffle players
        for (GamePlayer player : players.values()) {
            while (player.getTeam() == null)
                if (player.isSpectator())
                    break;
                else if (cTeam.getPlayerCount() < maxPlayers)
                    player.setTeam(cTeam);
                else if (teamIt.hasNext())
                    cTeam = teamIt.next();
                else
                    player.setSpectator();
        }
    }

    public void joinPlayer(GamePlayer player) {
        switch (state) {
            case WAITING:
            case RUNNING:
                players.put(player.getPD().id, player);
                break;
            default:
                break;
        }
    }

    public void leavePlayer(UUID player) {
        if (state == GameState.STOPPED) return;
        players.remove(player);
    }

    public Stream<GamePlayer> getPlayerStream() {
        return players.values().parallelStream();
    }

    private Stream<PlayerData> getPlayersD() {
        return getPlayerStream().map(p -> p.getPD());
    }


    public static enum GameState {
        STOPPED,
        WAITING,
        RUNNING,
        ENDED
    }

    public static enum Type {
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
