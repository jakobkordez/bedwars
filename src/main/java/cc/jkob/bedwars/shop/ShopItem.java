package cc.jkob.bedwars.shop;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cc.jkob.bedwars.game.PlayerData.GamePlayer;
import cc.jkob.bedwars.gui.GuiType;
import cc.jkob.bedwars.shop.ItemShop.TileType;
import cc.jkob.bedwars.shop.Shop.Currency;
import cc.jkob.bedwars.util.PlayerUtil;

public class ShopItem implements ConfigurationSerializable {

    protected int id;
    protected String name;
    protected ItemStack item;
    private ItemStack price;

    public ShopItem(int id, String name, ItemStack item, ItemStack price) {
        this.id = id;
        this.name = name;
        this.item = item;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItem() {
        return item;
    }

    public ItemStack getPrice() {
        return price;
    }

    public boolean canBuy(GamePlayer player) {
        return true;
    }

    public void tryBuy(GamePlayer player) {
        Inventory inv = player.player.getPlayer().getInventory();

        if (!inv.contains(price.getType(), price.getAmount())) {
            player.player.getPlayer().sendMessage(ChatColor.RED + "You do not have enough " + Currency.valueOf(price.getType()).toString(true));
            return;
        }

        Map<Integer, ? extends ItemStack> cMap = inv.all(price.getType());
        int sum = price.getAmount();
        for (Entry<Integer, ? extends ItemStack> stack : cMap.entrySet())
            if (sum <= 0) break;
            else {
                int old = stack.getValue().getAmount();
                int sub = Integer.min(sum, old);
                if (old > sub)
                    stack.getValue().setAmount(old - sub);
                else
                    inv.clear(stack.getKey());
                sum -= sub;
            }

        if (!canBuy(player)) {
            player.player.getPlayer().sendMessage(ChatColor.RED + "You cannot buy that");
            return;
        }

        give(player);
        player.player.getPlayer().sendMessage(ChatColor.GREEN + "You purchased " + ChatColor.GOLD + name);
        PlayerUtil.play(player.player, Sound.NOTE_PLING, 1f, 1.5f);
    }

    public void give(GamePlayer player) {
        ItemStack item = this.item.clone();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + name);
        item.setItemMeta(meta);

        player.player.getPlayer().getInventory().addItem(item);
    }

    public ItemStack getShopSlot(GuiType guiType, Map<Material, Integer> wallet) {
        ItemStack stack = new ItemStack(item);

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName((wallet.getOrDefault(price.getType(), 0) < price.getAmount() ? ChatColor.RED : ChatColor.GREEN) + getName());
        meta.setLore(Lists.newArrayList(
            ChatColor.GRAY + "Cost: " + Shop.formatCost(price),
            ItemShop.getTileData(TileType.BUY, id)
        ));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("name", name);
        data.put("item", item);
        data.put("price", price);
        return data;
    }

    public static ShopItem deserialize(Map<String, Object> args) {
        return new ShopItem(
            (int) args.get("id"),
            (String) args.get("name"),
            (ItemStack) args.get("item"),
            (ItemStack) args.get("price"));
    }

}
