package cc.jkob.bedwars.shop;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class UpgradeShop extends Shop {
    @Override
    public Inventory buildInventory() {
        // TODO: Implement
        return Bukkit.createInventory(null, 9*3, ShopType.UPGRADE.getName());
    }
}
