package cc.jkob.bedwars.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

public class ItemShop extends Shop implements ConfigurationSerializable {
    private List<ShopCategory> categories = new ArrayList<>();

    public List<ShopCategory> getCategories() {
        return categories;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("categories", categories);
        return data;
    }

    @SuppressWarnings("unchecked")
    public static ItemShop deserialize(Map<String, Object> args) {
        ItemShop shop = new ItemShop();
        
        for (Object cat : (List<Object>) args.get("categories"))
            shop.categories.add((ShopCategory) cat);
        
        return shop;
    }
}
