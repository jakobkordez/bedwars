package cc.jkob.bedwars.shop;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import cc.jkob.bedwars.util.FileUtil;
import cc.jkob.bedwars.util.LangUtil;

public abstract class Shop {
    private static ItemShop itemShop;
    private static UpgradeShop upgradeShop;

    public static ItemShop getItemShop() {
        if (itemShop != null) return itemShop;

        itemShop = (ItemShop) FileUtil.getShopConfig().get("item_shop");

        if (itemShop != null) return itemShop;

        return itemShop = new ItemShop();
    }

    public static UpgradeShop getUpgradeShop() {
        if (upgradeShop != null) return upgradeShop;

        return upgradeShop = new UpgradeShop();
    }

    public static Shop getShopByType(ShopType type) {
        switch (type) {
            case ITEM:
                return getItemShop();
            case UPGRADE:
                return getUpgradeShop();
        }

        return null;
    }

    public abstract Inventory buildInventory();

    protected static String formatCost(ItemStack cost) {
        String curr, ret = "";
        switch (cost.getType()) {
            case IRON_INGOT:
                ret += ChatColor.WHITE;
                curr = "Iron";
                break;
            case GOLD_INGOT:
                ret += ChatColor.GOLD;
                curr = "Gold";
                break;
            case DIAMOND:
                ret += ChatColor.AQUA;
                curr = "Diamond";
                break;
            case EMERALD:
                ret += ChatColor.DARK_GREEN;
                curr = "Emerald";
                break;
            default:
                ret += ChatColor.WHITE;
                curr = LangUtil.capitalize(cost.getType().toString());
        }
        ret += cost.getAmount() + " " + curr;

        if (cost.getAmount() > 1)
            if (cost.getType() == Material.DIAMOND || cost.getType() == Material.EMERALD)
                ret += "s";

        return ret;
    }

    public enum ShopType {
        ITEM("Item shop"),
        UPGRADE("Upgrades");

        private final String name;

        private ShopType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String getFormattedName() {
            return ChatColor.AQUA + name.toUpperCase();
        }
    }
}
