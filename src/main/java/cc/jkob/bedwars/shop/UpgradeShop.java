package cc.jkob.bedwars.shop;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryAction;

import cc.jkob.bedwars.game.PlayerData.GamePlayer;

public class UpgradeShop extends Shop {
    private static final int ROWS = 4;

    @Override
    protected void open(GamePlayer player) {
        // TODO: Implement
        player.player.getPlayer().openInventory(Bukkit.createInventory(null, 9 * ROWS, ShopType.UPGRADE.getName()));
    }

    @Override
    protected void click(GamePlayer player, String id, InventoryAction action) {
        // TODO: Implement
    }
}
