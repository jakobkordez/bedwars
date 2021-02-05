package cc.jkob.bedwars.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class ShopCategory implements ConfigurationSerializable {
    private String name;
    private ItemStack icon;

    private List<Integer> items = new ArrayList<>();

    public ShopCategory(String name, ItemStack icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public List<Integer> getItems() {
        return items;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("icon", icon);
        data.put("items", items);
        return data;
    }

    @SuppressWarnings("unchecked")
    public static ShopCategory deserialize(Map<String, Object> args) {
        ShopCategory cat = new ShopCategory(
            String.valueOf(args.get("name")),
            (ItemStack) args.get("icon"));
        
        for (Object item : (List<Object>) args.get("items"))
            cat.items.add((int) item);
        
        return cat;
    }
}
