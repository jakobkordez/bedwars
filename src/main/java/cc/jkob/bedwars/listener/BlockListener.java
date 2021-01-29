package cc.jkob.bedwars.listener;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.world.StructureGrowEvent;

import java.util.logging.Level;

public final class BlockListener implements Listener {
    private final BedWarsPlugin plugin;

    public BlockListener(BedWarsPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        plugin.getLogger().log(Level.INFO, "Block Broken");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {

    }

    @EventHandler(ignoreCancelled = true)
    public void onIgnite(BlockIgniteEvent event) {

    }

    // Cancel all other block events in games
    @EventHandler(ignoreCancelled = true)
    public void onBlockGrow(BlockGrowEvent event) {
        if (isEventInGame(event))
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBurn(BlockBurnEvent event) {
        if (isEventInGame(event))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onFade(BlockFadeEvent event) {
        if (isEventInGame(event))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onForm(BlockFormEvent event) {
        if (isEventInGame(event))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onSpread(BlockSpreadEvent event) {
        if (isEventInGame(event))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onStructureGrow(StructureGrowEvent event) {
        GameManager manager = plugin.getGameManager();
        String world = event.getLocation().getWorld().getName();
        if (manager.getGameByWorld(world) != null)
            event.setCancelled(true);
    }

    private boolean isEventInGame(BlockEvent event) {
        GameManager manager = plugin.getGameManager();
        String world = event.getBlock().getWorld().getName();
        return manager.getGameByWorld(world) != null;
    }
}
