package cc.jkob.bedwars.shop;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import cc.jkob.bedwars.game.PlayerData;

public class ShopItem implements ConfigurationSerializable {

    protected String name;
    protected ItemStack item;
    private ItemStack price;

    public ShopItem(String name, ItemStack item, ItemStack price) {
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

    public boolean canBuy(PlayerData player) {
        return true;
    }

    public void give(PlayerData player) {
        player.getPlayer().getInventory().addItem(item.clone());
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("item", item);
        data.put("price", price);
        return data;
    }

    public static ShopItem deserialize(Map<String, Object> args) {
        return new ShopItem(
            String.valueOf(args.get("name")),
            (ItemStack) args.get("item"),
            (ItemStack) args.get("price"));
    }

}
