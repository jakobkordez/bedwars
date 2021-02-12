package cc.jkob.bedwars.shop;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import cc.jkob.bedwars.gui.InventoryGui;
import cc.jkob.bedwars.util.FileUtil;

public abstract class Shop implements InventoryGui {
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

    protected static String formatCost(ItemStack cost) {
        Currency currency = Currency.valueOf(cost.getType());

        return "" + currency.color + cost.getAmount() + " " + currency.toString(cost.getAmount() > 1);
    }

    public static Map<Material, Integer> getWallet(Player player) {
        Map<Material, Integer> wallet = new HashMap<>();

        ItemStack[] inv = player.getInventory().getContents();
        for (int i = 0; i < inv.length; ++i) {
            if (inv[i] == null) continue;

            Currency currency = Currency.valueOf(inv[i].getType());
            if (currency == null) continue;
            
            wallet.put(inv[i].getType(), wallet.getOrDefault(inv[i].getType(), 0) + inv[i].getAmount());
        }

        return wallet;
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

        public Shop getShop() {
            switch (this) {
                case ITEM:
                    return getItemShop();
                case UPGRADE:
                    return getUpgradeShop();
            }
    
            return null;
        }
    }

    public enum Currency {
        IRON(Material.IRON_INGOT, "Iron", "Iron", ChatColor.WHITE),
        GOLD(Material.GOLD_INGOT, "Gold", "Gold", ChatColor.GOLD),
        DIAMOND(Material.DIAMOND, "Diamond", "Diamonds", ChatColor.AQUA),
        EMERALD(Material.EMERALD, "Emerald", "Emeralds", ChatColor.DARK_GREEN);

        public static Currency valueOf(Material material) {
            Currency[] values = values();
            for (int i = 0; i < values.length; ++i)
                if (values[i].material == material)
                    return values[i];
            
            return null;
        }

        public final Material material;
        private final String name, pluralName;
        public final ChatColor color;

        private Currency(Material material, String name, String pluralName, ChatColor color) {
            this.material = material;
            this.name = name;
            this.pluralName = pluralName;
            this.color = color;
        }

        @Override
        public String toString() {
            return name;
        }

        public String toString(boolean plural) {
            return plural ? pluralName : name;
        }

        public String getFormattedName(boolean plural) {
            return color + (plural ? pluralName : name);
        }
    }
}
