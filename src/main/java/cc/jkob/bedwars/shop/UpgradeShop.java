package cc.jkob.bedwars.shop;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;

public class UpgradeShop extends Shop {
    private static final int ROWS = 4;

    @Override
    public void open(Player player) {
        // TODO: Implement
        player.openInventory(Bukkit.createInventory(null, 9 * ROWS, ShopType.UPGRADE.getName()));
    }

    @Override
    public void click(Player player, String id, InventoryAction action) {
        // TODO: Implement
    }
}
