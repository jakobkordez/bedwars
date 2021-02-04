package cc.jkob.bedwars.listener;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.event.PlayerUseEntityEvent;
import cc.jkob.bedwars.game.Game;
import cc.jkob.bedwars.game.Game.State;
import cc.jkob.bedwars.shop.Shop;
import cc.jkob.bedwars.shop.Shopkeeper;
import cc.jkob.bedwars.util.LangUtil;

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

        player.openInventory(Shop.getShopByType(shopkeeper.getShopType()).buildInventory());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        plugin.getLogger().info(event.toString() + " " + (event.isCancelled() ? "canceled" : "not-canceled"));

        Player player = (Player) event.getWhoClicked();
        if (!plugin.getGameManager().isLocationInGame(player.getLocation())) return;

        if (event.getInventory().getName().startsWith("container.")) return;

        event.setCancelled(true); // Return item

        if (event.getRawSlot() >= event.getInventory().getSize()) return;

        switch (event.getAction()) {
            case PICKUP_ALL:
            case PICKUP_HALF:
                break;
            default:
                return;
        }

        List<String> lore = event.getCurrentItem().getItemMeta().getLore();
        System.out.println(LangUtil.revealString(lore.get(lore.size()-1)));
        // TODO: Act
    }

    private boolean isEventInGame(PlayerEvent event) {
        return plugin.getGameManager().isLocationInGame(event.getPlayer().getLocation());
    }
}
