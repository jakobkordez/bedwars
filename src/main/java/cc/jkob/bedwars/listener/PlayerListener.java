package cc.jkob.bedwars.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import cc.jkob.bedwars.BedWarsPlugin;

public class PlayerListener implements Listener {
    private final BedWarsPlugin plugin;

    public PlayerListener(BedWarsPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        plugin.getLogger().info(event.toString() + " " + (event.isCancelled() ? "canceled" : "not-canceled"));

        if (event.isCancelled()) return;
        if (!isEventInGame(event)) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
            if (event.getClickedBlock().getType() == Material.BED_BLOCK)
                if (!event.getPlayer().isSneaking())
                    event.setCancelled(true);
    }

    private boolean isEventInGame(PlayerEvent event) {
        return plugin.getGameManager().isLocationInGame(event.getPlayer().getLocation());
    }
}
