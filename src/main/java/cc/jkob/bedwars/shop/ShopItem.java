package cc.jkob.bedwars.shop;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class ShopItem implements ConfigurationSerializable {
    private String name;

    private ItemStack item;
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
