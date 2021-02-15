package cc.jkob.bedwars.game;

import cc.jkob.bedwars.game.Game.GameState;
import cc.jkob.bedwars.util.FileUtil;
import cc.jkob.bedwars.util.PlayerUtil;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class GameManager {
    public static final GameManager instance = new GameManager();

    private final HashMap<UUID, PlayerData> players = new HashMap<>();
    private final HashMap<String, Game> games = new HashMap<>();

    public final Location lobby = new Location(Bukkit.getWorld("lobby"), 0.5, 69, 0.5);

    private GameManager() {
        for (Game game : FileUtil.loadGames()) {
            game.initTransient();
            games.put(game.getName(), game);
        }
    }

    public void addGame(Game game) {
        games.put(game.getName(), game);
    }

    public void broadcastLobby(String msg) {
        PlayerUtil.send(getLobbyPlayers(), msg);
    }

    // Get Player Data //
    private Stream<PlayerData> getLobbyPlayers() {
        return players.values().parallelStream()
            .filter(p -> !p.isInGame());
    }

    public PlayerData getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    public PlayerData getPlayer(UUID id) {
        PlayerData player = players.get(id);
        if (player != null) return player;

        player = new PlayerData(id);
        players.put(id, player);
        return player;
    }

    // Get Game //
    public Game autoGetWaiting() {
        List<Game> gameList = games.values().parallelStream()
            .filter(g -> g.getState() == GameState.WAITING || g.getState() == GameState.RUNNING)
            .collect(Collectors.toList());

        if (gameList.size() == 1) return gameList.get(0);
        return null;
    }

    // By Name //
    public Game getGameByName(String name) {
        Game game = games.get(name);
        if (game != null) return game;

        // TODO: Autocomplete

        return null;
    }

    // By World //
    public Game getGameByWorld(Entity entity) {
        return getGameByWorld(entity.getWorld());
    }

    public Game getGameByWorld(Location location) {
        return getGameByWorld(location.getWorld());
    }

    public Game getGameByWorld(World world) {
        return getGameByWorld(world.getName());
    }

    public Game getGameByWorld(String world) {
        return games.values().parallelStream()
            .filter(g -> g.getWorld().equals(world))
            .findAny().orElse(null);
    }

    // By player //
    public Game getGameByPlayer(Player player) {
        return getGameByPlayer(player.getUniqueId());
    }

    public Game getGameByPlayer(UUID player) {
        PlayerData playerD = getPlayer(player);
        if (!playerD.isInGame()) return null;
        return playerD.getGamePlayer().game;
    }
}
