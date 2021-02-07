package cc.jkob.bedwars.listener;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.event.PlayerUseEntityEvent;
import cc.jkob.bedwars.game.Game;
import cc.jkob.bedwars.game.Game.State;
import cc.jkob.bedwars.gui.GuiType;
import cc.jkob.bedwars.shop.Shopkeeper;
import cc.jkob.bedwars.util.LangUtil;

public class PlayerListener implements Listener {
    private final BedWarsPlugin plugin;

    public PlayerListener(BedWarsPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (!isEventInGame(event)) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
            if (event.getClickedBlock().getType() == Material.BED_BLOCK) {
                event.setUseInteractedBlock(Result.DENY);
                event.setUseItemInHand(Result.ALLOW);
            }
    }

    @EventHandler
    public void onUseEntity(PlayerUseEntityEvent event) {
        Player player = event.getPlayer();

        Game game = plugin.getGameManager().getGameByLocation(player.getLocation());
        if (game == null) return;

        if (game.getState() != State.RUNNING) return;

        Shopkeeper shopkeeper = game.getShopkeepers().stream().filter(s -> s.geteId() == event.getEntityId()).findAny().orElse(null);
        if (shopkeeper == null) return;

        if (!game.getPlayers().containsKey(player.getUniqueId())) return;

        shopkeeper.getShopType().getShop().open(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        Game game = plugin.getGameManager().getGameByLocation(player.getLocation());
        if (game == null) return;

        // TODO: Split generators
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!plugin.getGameManager().isLocationInGame(player.getLocation())) return;

        if (event.getInventory().getName().startsWith("container.")) return;

        event.setCancelled(true); // Return item

        if (event.getRawSlot() >= event.getInventory().getSize()) return;
        if (event.getAction() == InventoryAction.NOTHING) return;

        List<String> lore = event.getCurrentItem().getItemMeta().getLore();
        if (lore == null || lore.size() == 0) return;

        String hiddenLore = LangUtil.revealString(lore.get(lore.size()-1));
        String[] hSplit = hiddenLore.split(";", 2);
        if (hSplit.length == 1) return;
        int gt = Integer.parseInt(hSplit[0]);
        GuiType.values()[gt].getGui().click(player, hSplit[1], event.getAction());
    }

    private boolean isEventInGame(PlayerEvent event) {
        return plugin.getGameManager().isLocationInGame(event.getPlayer().getLocation());
    }
}
