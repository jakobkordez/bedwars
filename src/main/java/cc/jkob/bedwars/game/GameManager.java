package cc.jkob.bedwars.game;

import cc.jkob.bedwars.util.FileUtil;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GameManager {
    private final HashMap<String, Game> games = new HashMap<>();

    public GameManager() {
        for (Game game : FileUtil.loadGames()) {
            game.initTransient();
            games.put(game.getName(), game);
        }
    }

    public Collection<Game> getGames() {
        return games.values();
    }

    public void addGame(Game game) {
        games.put(game.getName(), game);
    }

    public Game getGame(String name) {
        return games.get(name);
    }

    public Game getGameByWorld(String world) {
        if (world == null) return null;

        for (Game game : getGames())
            if (game.getWorld().equals(world))
                return game;

        return null;
    }

    public Game getGameByLocation(Location location) {
        return getGameByWorld(location.getWorld().getName());
    }

    public boolean isLocationInGame(Location location) {
        return getGameByLocation(location) != null;
    }

    public Game getGameByPlayer(Player player) {
        for (Game game : games.values())
            if (game.isPlayerInGame(player))
                return game;

        return null;
    }

    public boolean isPlayerInGame(Player player) {
        return getGameByPlayer(player) != null;
    }

    public boolean hasGame(String name) {
        return games.containsKey(name);
    }
}
