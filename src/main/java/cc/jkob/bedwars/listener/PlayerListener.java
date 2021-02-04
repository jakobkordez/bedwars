package cc.jkob.bedwars.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.event.PlayerUseEntityEvent;
import cc.jkob.bedwars.game.Game;
import cc.jkob.bedwars.game.Game.State;
import cc.jkob.bedwars.shop.Shopkeeper;

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

    @EventHandler
    public void onUseEntity(PlayerUseEntityEvent event) {
        Player player = event.getPlayer();

        Game game = plugin.getGameManager().getGameByLocation(player.getLocation());
        if (game == null) return;

        if (game.getState() != State.RUNNING) return;

        Shopkeeper shopkeeper = game.getShopkeepers().stream().filter(s -> s.geteId() == event.getEntityId()).findAny().orElse(null);
        if (shopkeeper == null) return;

        if (!game.getPlayers().contains(player.getUniqueId())) return;

        // TODO: Open shop
        event.getPlayer().sendMessage("" + ChatColor.GOLD + ChatColor.BOLD + "Opening shop");
    }

    private boolean isEventInGame(PlayerEvent event) {
        return plugin.getGameManager().isLocationInGame(event.getPlayer().getLocation());
    }
}
