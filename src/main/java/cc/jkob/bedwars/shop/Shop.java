package cc.jkob.bedwars.shop;

import org.bukkit.ChatColor;

import cc.jkob.bedwars.util.FileUtil;

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

    public enum ShopType {
        ITEM(ChatColor.AQUA + "ITEM SHOP"),
        UPGRADE(ChatColor.AQUA + "UPGRADES");

        private final String name;

        private ShopType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
