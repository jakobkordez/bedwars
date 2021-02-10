package cc.jkob.bedwars.listener;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.event.PlayerUseEntityEvent;
import cc.jkob.bedwars.game.Game;
import cc.jkob.bedwars.game.GameManager;
import cc.jkob.bedwars.game.PlayerData;
import cc.jkob.bedwars.game.Game.State;
import cc.jkob.bedwars.gui.GuiType;
import cc.jkob.bedwars.shop.Shopkeeper;
import cc.jkob.bedwars.util.LangUtil;

public class PlayerListener extends BaseListener {

    public PlayerListener(BedWarsPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        System.out.println(event.getEventName() + " " + event.hashCode() + " " + event.getCause());

        if (event.getEntityType() != EntityType.PLAYER) return;

        Player player = (Player) event.getEntity();
        PlayerData playerD = GameManager.instance.getPlayer(player);

        Game game = playerD.getGame();
        if (game == null) return;

        if (game.getState() != State.RUNNING) {
            event.setCancelled(true);
            return;
        }

        if (event instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
            if (damager instanceof Player)
                playerD.onDamage(GameManager.instance.getPlayer((Player) damager));
        }

        if (player.getHealth() - event.getDamage() < 1) {
            event.setCancelled(true);
            playerD.onDeath();
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (GameManager.instance.getGameByPlayer(event.getPlayer()) == null) return;

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
            if (event.getClickedBlock().getType() == Material.BED_BLOCK) {
                event.setUseInteractedBlock(Result.DENY);
                event.setUseItemInHand(Result.ALLOW);
            }
    }

    @EventHandler
    public void onUseEntity(PlayerUseEntityEvent event) {
        Player player = event.getPlayer();

        Game game = GameManager.instance.getGameByPlayer(player);
        if (game == null) return;

        if (game.getState() != State.RUNNING) return;

        Shopkeeper shopkeeper = game.getShopkeepers().parallelStream()
            .filter(s -> s.geteId() == event.getEntityId())
            .findAny().orElse(null);
        if (shopkeeper == null) return;

        if (!game.getPlayers().containsKey(player.getUniqueId())) return;

        shopkeeper.getShopType().getShop().open(player);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPickupItem(PlayerPickupItemEvent event) {
        Player player = event.getPlayer();

        Game game = GameManager.instance.getGameByPlayer(player);
        if (game == null) return;

        // TODO: Split generators
    }

    @EventHandler(ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        Player player = (Player) event.getWhoClicked();

        Game game = GameManager.instance.getGameByPlayer(player);
        if (game == null) return;

        if (game.getState() == State.STOPPED) return;

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (GameManager.instance.getGameByPlayer(player) == null) return;

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
}
