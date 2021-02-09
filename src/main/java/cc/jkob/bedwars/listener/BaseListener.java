package cc.jkob.bedwars.listener;

import org.bukkit.event.Listener;

import cc.jkob.bedwars.BedWarsPlugin;

public abstract class BaseListener implements Listener {
    protected final BedWarsPlugin plugin;

    public BaseListener(BedWarsPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
}
