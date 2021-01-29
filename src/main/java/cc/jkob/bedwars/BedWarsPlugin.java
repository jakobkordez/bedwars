package cc.jkob.bedwars;

import cc.jkob.bedwars.game.GameManager;
import cc.jkob.bedwars.listener.BlockListener;
import cc.jkob.bedwars.util.BedWarsCommandExecutor;
import multiworld.MultiWorldPlugin;
import org.bukkit.plugin.java.JavaPlugin;

public class BedWarsPlugin extends JavaPlugin {
    private static BedWarsPlugin instance;
    public static BedWarsPlugin getInstance() {
        return instance;
    }

    private MultiWorldPlugin multiWorld;

    private GameManager gameManager;

    @Override
    public void onEnable() {
        instance = this;
        gameManager = new GameManager();

        new BlockListener(this);

        getCommand("bw").setExecutor(new BedWarsCommandExecutor(this));
    }

    @Override
    public void onDisable() {
    }

    public MultiWorldPlugin getMultiWorld() {
        if (multiWorld != null) return multiWorld;

        return multiWorld = MultiWorldPlugin.getInstance();
    }

    public GameManager getGameManager() {
        return gameManager;
    }
}
