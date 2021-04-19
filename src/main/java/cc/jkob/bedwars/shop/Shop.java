package cc.jkob.bedwars.shop;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import cc.jkob.bedwars.game.PlayerData;
import cc.jkob.bedwars.game.PlayerData.GamePlayer;
import cc.jkob.bedwars.gui.InventoryGui;
import cc.jkob.bedwars.util.FileUtil;

public abstract class Shop implements InventoryGui {
    private static ItemShop itemShop;
    private static UpgradeShop upgradeShop;

    protected abstract void open(GamePlayer player);
    protected abstract void click(GamePlayer player, String id, InventoryAction action);

    @Override
    public final void open(PlayerData player) {
        if (!player.isInGame()) return;
        open(player.getGamePlayer());
    }

    @Override
    public final void click(PlayerData player, String id, InventoryAction action) {
        if (!player.isInGame()) return;
        click(player.getGamePlayer(), id, action);
    }

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
        for (ItemStack itemStack : inv) {
            if (itemStack == null) continue;

            Currency currency = Currency.valueOf(itemStack.getType());
            if (currency == null) continue;

            wallet.put(itemStack.getType(), wallet.getOrDefault(itemStack.getType(), 0) + itemStack.getAmount());
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
            for (Currency value : values)
                if (value.material == material)
                    return value;
            
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
