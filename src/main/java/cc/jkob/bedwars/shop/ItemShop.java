package cc.jkob.bedwars.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cc.jkob.bedwars.util.LangUtil;

public class ItemShop extends Shop implements ConfigurationSerializable {
    private static final int ROWS = 6;

    private List<ShopCategory> categories = new ArrayList<>();

    public List<ShopCategory> getCategories() {
        return categories;
    }

    @Override
    public Inventory buildInventory() {
        return buildInventory(1);
    }

    public Inventory buildInventory(int cIndex) {
        String catTitle;

        ItemStack[] invStacks = new ItemStack[9*ROWS];

        invStacks[0] = new ItemStack(Material.NETHER_STAR);

        int i = 0;
        for (ShopCategory category : categories)
            invStacks[++i] = category.getIcon();

        ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE);
        pane.setDurability(DyeColor.GRAY.getWoolData()); 
        
        for (i = 9; i < 18; ++i)
            invStacks[i] = pane;

        if (cIndex < 9) {
            invStacks[9 + cIndex] = new ItemStack(Material.STAINED_GLASS_PANE);
            invStacks[9 + cIndex].setDurability(DyeColor.GREEN.getWoolData());
        }

        if (cIndex > 0 && cIndex <= categories.size()) {
            catTitle = categories.get(cIndex - 1).getName();
            i = 0;
            for (ShopItem s : categories.get(cIndex - 1).getItems()) {
                invStacks[19 + i + 2*(i/7)] = createStack(s);
                ++i;
            }
        } else {
            catTitle = "Quick Buy";
            // TODO: Quick Buy
        }

        
        Inventory inv = Bukkit.createInventory(null, 9*ROWS, Shop.ShopType.ITEM.getName() + " - " + catTitle);
        inv.setContents(invStacks);
        
        return inv;
    }

    private static ItemStack createStack(ShopItem item) {
        ItemStack stack = new ItemStack(item.getItem());
        
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + item.getName());
        meta.setLore(Lists.newArrayList(ChatColor.GRAY + "Cost: " + formatCost(item.getPrice()), LangUtil.hideString(item.getName())));

        stack.setItemMeta(meta);
        return stack;
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
