package cc.jkob.bedwars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.WeatherEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldSaveEvent;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Game;
import cc.jkob.bedwars.game.GameManager;
import cc.jkob.bedwars.game.Game.GameState;

public class WorldListener extends BaseListener {

    public WorldListener(BedWarsPlugin plugin) {
		super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onChunkUnload(ChunkUnloadEvent event) {
        Game game = GameManager.instance.getGameByWorld(event.getWorld());
        if (game == null) return;

        if (game.getState() == GameState.STOPPED) return;

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldSave(WorldSaveEvent event) {
        Game game = GameManager.instance.getGameByWorld(event.getWorld());
        if (game == null) return;

        plugin.getLogger().warning("Saving world " + event.getWorld().getName());
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemDespawn(ItemDespawnEvent event) {
        if (isEventInGame(event))
            event.setCancelled(true);
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
        return GameManager.instance.getGameByWorld(event.getWorld()) != null;
    }

    private boolean isEventInGame(EntityEvent event) {
        return GameManager.instance.getGameByWorld(event.getEntity()) != null;
    }
}
