package cc.jkob.bedwars.game;

import cc.jkob.bedwars.util.FileUtil;

import java.util.Collection;
import java.util.HashMap;

public class GameManager {
    private final HashMap<String, Game> games = new HashMap<>();

    public GameManager() {
        for (Game game : FileUtil.loadGames())
            games.put(game.getName(), game);
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
        for (Game game : getGames())
            if (game.getWorld().equals(world))
                return game;

        return null;
    }

    public boolean hasGame(String name) {
        return games.containsKey(name);
    }
}
