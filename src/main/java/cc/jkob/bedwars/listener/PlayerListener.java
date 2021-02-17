package cc.jkob.bedwars.listener;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.event.PlayerUseEntityEvent;
import cc.jkob.bedwars.game.Game;
import cc.jkob.bedwars.game.GameManager;
import cc.jkob.bedwars.game.PlayerData;
import cc.jkob.bedwars.game.Game.GameState;
import cc.jkob.bedwars.gui.GuiType;
import cc.jkob.bedwars.shop.Shopkeeper;
import cc.jkob.bedwars.util.LangUtil;
import cc.jkob.bedwars.util.PacketUtil;

public class PlayerListener extends BaseListener {

    public PlayerListener(BedWarsPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Player player = event.getPlayer();
        GameManager.instance.getPlayer(player).onJoin(player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        PlayerData player = GameManager.instance.getPlayer(event.getPlayer());
        player.onDisconnect();
    }

    @EventHandler(ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;

        PlayerData player = GameManager.instance.getPlayer(event.getEntity().getUniqueId());

        if (!player.isInGame()) return;
        
        Game game = player.getGamePlayer().game;
        if (game.getState() != GameState.RUNNING) {
            event.setCancelled(true);
            return;
        }

        if (event.getCause() == DamageCause.VOID) {
            event.setCancelled(true);
            player.onDeath(DamageCause.VOID);
            return;
        }

        if (event instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
            if (damager instanceof Player)
                player.onDamage(GameManager.instance.getPlayer(damager.getUniqueId()));
        }

        if (player.getPlayer().getHealth() - event.getDamage() < 1) {
            event.setCancelled(true);
            player.onDeath(event.getCause());
            return;
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Game game = GameManager.instance.getGameByPlayer(event.getPlayer());
        if (game == null) return;

        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (event.getClickedBlock().getType() == Material.BED_BLOCK) {
                event.setUseInteractedBlock(Result.DENY);
                event.setUseItemInHand(Result.ALLOW);
            }

            if (player.getItemInHand().getType() == Material.WATER_BUCKET) {
                Block block = event.getClickedBlock().getRelative(event.getBlockFace());
                player.setItemInHand(null);
                block.setType(Material.WATER);
                game.getPlacedBlocks().add(block.getLocation());
            }

            return;
        }

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (player.getItemInHand().getType() == Material.FIREBALL) {
                int amount = player.getItemInHand().getAmount() - 1;
                if (amount < 1) player.setItemInHand(null);
                else player.getItemInHand().setAmount(amount);

                player.launchProjectile(Fireball.class);
                return;
            }
        }
    }

    @EventHandler
    public void onUseEntity(PlayerUseEntityEvent event) {
        PlayerData player = GameManager.instance.getPlayer(event.getPlayer());

        if (!player.isInGame()) return;

        Game game = player.getGamePlayer().game;
        if (game.getState() != GameState.RUNNING) return;

        Shopkeeper shopkeeper = game.getShopkeepers().parallelStream()
            .filter(s -> s.geteId() == event.getEntityId())
            .findAny().orElse(null);
        if (shopkeeper == null) return;

        shopkeeper.getShopType().getShop().open(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.POTION) return;

        Player player = event.getPlayer();
        PlayerData playerD = GameManager.instance.getPlayer(player);

        if (!playerD.isInGame()) return;

        Game game = playerD.getGamePlayer().game;
        if (game.getState() != GameState.RUNNING) return;

        new BukkitRunnable(){
            @Override
            public void run() {
                player.getInventory().remove(Material.GLASS_BOTTLE);
            }
        }.runTaskLater(plugin, 10);
        new BukkitRunnable(){
            @Override
            public void run() {
                PacketUtil.updateEntity(player);
                if (!player.hasPotionEffect(PotionEffectType.INVISIBILITY)) cancel();
            }
        }.runTaskTimer(plugin, 10, 20);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (GameManager.instance.getGameByPlayer(event.getPlayer()) == null) return;

        ItemMeta meta = event.getItemDrop().getItemStack().getItemMeta();
        if (meta.spigot().isUnbreakable())
            event.setCancelled(true);
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

        if (game.getState() == GameState.STOPPED) return;

        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        PlayerData player = GameManager.instance.getPlayer(event.getWhoClicked().getUniqueId());
        if (!player.isInGame()) return;

        if (event.getSlotType() == SlotType.ARMOR) {
            event.setCancelled(true);
            return;
        }

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

    @EventHandler(ignoreCancelled = true)
    public void onHunger(FoodLevelChangeEvent event) {
        if (GameManager.instance.getGameByWorld(event.getEntity()) != null)
            event.setCancelled(true);
    }
}
