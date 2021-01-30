package cc.jkob.bedwars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.WeatherEvent;

import cc.jkob.bedwars.BedWarsPlugin;

public class WorldListener implements Listener {
    private final BedWarsPlugin plugin;

    public WorldListener(BedWarsPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onWeatherChange(WeatherChangeEvent event) {
        if (isEventInGame(event))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onThunderChange(ThunderChangeEvent event) {
        if (isEventInGame(event))
            event.setCancelled(true);
    }

    private boolean isEventInGame(WeatherEvent event) {
        return plugin.getGameManager().getGameByWorld(event.getWorld().getName()) != null;
    }
}
