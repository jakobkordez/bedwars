package cc.jkob.bedwars.game;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import cc.jkob.bedwars.BedWarsPlugin;
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
    private transient State state;
    private transient Map<UUID, PlayerData> players;
    private transient GameScoreboard scoreboard;
    private transient GameCycle gameCycle;
    private transient List<Location> placedBlocks;

    public void initTransient() {
        state = State.STOPPED;
    }

    public State getState() {
        return state;
    }

    public Map<UUID, PlayerData> getPlayers() {
        return players;
    }

    public PlayerData getPlayer(Player player) {
        return players.get(player.getUniqueId());
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

    public void onTeamElim(Team team) {
        PlayerUtil.send(getPlayerStream(true), ChatUtil.format(team.getFormattedName() + " Team", " has been eliminated"));
        List<Team> aliveT = teams.parallelStream()
            .filter(t -> t.playersAlive() != 0)
            .collect(Collectors.toList());
        if (aliveT.size() == 1) end(aliveT.get(0));
        else if (aliveT.size() == 0) end(null);
    }

    public void init() {
        if (state != State.STOPPED) return;

        state = State.WAITING;

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
        if (state != State.WAITING) return;

        state = State.RUNNING;
        
        autoAssignTeams();

        // Give scoreboard
        scoreboard = new GameScoreboard(this);
        scoreboard.startTask();
        PlayerUtil.send(getPlayerStream(true), scoreboard);

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

        getPlayerStream(false).sequential()
            .forEach(PlayerData::onStart);
    }

    public void end(Team winner) {
        if (state != State.RUNNING) return;

        state = State.ENDED;

        Title title = new Title(ChatColor.GOLD + "Tie", ChatColor.RED + "Game Over", 0, 80, 20);
        if (winner != null) title.setTitle(winner.getColor().getChatColor() + winner.getName() + " wins!");
        PlayerUtil.send(getPlayerStream(true), title);

        new BukkitRunnable(){
            public void run() {
                stop();
            }
        }.runTaskLater(BedWarsPlugin.getInstance(), 100);
    }

    public void stop() {
        if (state == State.STOPPED) return;

        state = State.STOPPED;

        // Remove scoreboard
        if (scoreboard != null) {
            scoreboard.stopTask();
            scoreboard = null;
            PlayerUtil.clearScoreboard(getPlayerStream(true));
        }

        // Stop generators
        generators.forEach(Generator::stop);
        teams.forEach(Team::stopGens);

        // Remove shopkeepers
        shopkeepers.forEach(Shopkeeper::remove);

        // Stop game cycle
        gameCycle.stop();
        gameCycle = null;

        // Leave players
        players.forEach((k, p) -> p.leaveGame());

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
        for (PlayerData player : players.values()) {
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

    public boolean joinPlayer(PlayerData player) {
        switch (state) {

            case WAITING:
            case RUNNING:
                if (isPlayerInGame(player.id)) return false;
                players.put(player.id, player);
                return true;

            default:
                return false;
        }
    }

    public boolean isPlayerInGame(UUID player) {
        switch (state) {

            case WAITING:
            case RUNNING:
                if (players.containsKey(player)) return true;

            default:
                return false;
        }
    }

    public void leavePlayer(UUID player) {
        if (state == State.STOPPED) return;
        players.remove(player);
    }

    public Stream<PlayerData> getPlayerStream(boolean withSpectators) {
        Stream<PlayerData> playerStream = players.values().parallelStream();

        if (withSpectators) return playerStream;

        return playerStream.filter(p -> !p.isSpectator());
    }


    public static enum State {
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
