package cc.jkob.bedwars.util;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Game;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class FileUtil {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Location.class, new LocationAdapter())
            .create();

    public static List<Game> loadGames() {
        ArrayList<Game> games = new ArrayList<>();

        File files = new File(BedWarsPlugin.getInstance().getDataFolder(), "games");
        if (files.mkdirs()) return games;

        for (File file : files.listFiles()) {
            String data = readFile(file.getPath());
            if (data == null) continue;
            Game game = gson.fromJson(data, Game.class);
            games.add(game);
        }

        return games;
    }

    public static boolean saveGame(Game game) {
        return saveFile("games/" + game.getName() + ".json", gson.toJson(game));
    }

    private static String readFile(String path) {
        try {
            File f = new File(path);
            if (!f.exists() || !f.isFile()) return null;

            return String.join("\n", Files.readLines(f, Charset.defaultCharset()));
        } catch (Exception e) {
            BedWarsPlugin.getInstance().getLogger().log(Level.INFO, e.getMessage());
            return null;
        }
    }

    private static boolean saveFile(String path, String data) {
        try {
            File f = new File(BedWarsPlugin.getInstance().getDataFolder(), path);
            f.getParentFile().mkdirs();
            f.createNewFile();
            Files.write(data, f, Charset.defaultCharset());
            return true;
        } catch (Exception e) {
            BedWarsPlugin.getInstance().getLogger().log(Level.INFO, e.getMessage());
            return false;
        }
    }
}
