package cc.jkob.bedwars.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cc.jkob.bedwars.gui.GuiType;
import cc.jkob.bedwars.util.BlockUtil;
import cc.jkob.bedwars.util.LangUtil;
import cc.jkob.bedwars.util.PlayerUtil;

public class ItemShop extends Shop implements ConfigurationSerializable {
    private static final int ROWS = 6;
    private static final GuiType GUI_TYPE = GuiType.GAME_ITEM_SHOP;

    private List<ShopItem> items = new ArrayList<>();
    private List<ShopCategory> categories = new ArrayList<>();

    public List<ShopCategory> getCategories() {
        return categories;
    }

    @Override
    public void open(Player player) {
        open(player, 1);
    }

    public void open(Player player, int tab) {
        player.openInventory(buildInventory(player, tab));
    }

    private Inventory buildInventory(Player player, int cIndex) {
        ItemStack[] invStacks = new ItemStack[9 * ROWS];

        Map<Material, Integer> wallet = getWallet(player);

        // Categories
        int i;
        for (i = 0; i <= categories.size(); ++i)
            invStacks[i] = createCategory(i);

        // Glass panes
        ItemStack pane = BlockUtil.getColoredStack(Material.STAINED_GLASS_PANE, DyeColor.GRAY);
        for (i = 9; i < 18; ++i)
            invStacks[i] = pane;

        if (cIndex < 9)
            invStacks[9 + cIndex] = BlockUtil.getColoredStack(Material.STAINED_GLASS_PANE, DyeColor.GREEN);

        // Shop body
        String catTitle;
        if (cIndex > 0 && cIndex <= categories.size()) {
            catTitle = categories.get(cIndex - 1).getName();
            i = 0;
            for (int itemInd : categories.get(cIndex - 1).getItems()) {
                invStacks[19 + i + 2 * (i / 7)] = createStack(itemInd, wallet);
                ++i;
            }
        } else {
            catTitle = "Quick Buy";
            // TODO: Quick Buy
        }

        Inventory inv = Bukkit.createInventory(null, 9 * ROWS, Shop.ShopType.ITEM.getName() + " - " + catTitle);
        inv.setContents(invStacks);

        return inv;
    }

    private ItemStack createCategory(int catId) {
        ItemStack stack;
        String name;
        if (catId == 0) {
            stack = new ItemStack(Material.NETHER_STAR);
            name = "Quick Buy";
        }
        else {
            ShopCategory cat = categories.get(catId - 1);
            stack = new ItemStack(cat.getIcon());
            name = cat.getName();
        }

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + name);
        meta.setLore(Lists.newArrayList(
            LangUtil.hideString(GUI_TYPE.ordinal() + ";" + TileType.CATEGORY.ordinal() + ";" + catId)
        ));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        
        stack.setItemMeta(meta);
        return stack;
    }

    private ItemStack createStack(int itemId, Map<Material, Integer> wallet) {
        ShopItem item = items.get(itemId);
        ItemStack price = item.getPrice();

        ItemStack stack = new ItemStack(item.getItem());

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName((wallet.getOrDefault(price.getType(), 0) < price.getAmount() ? ChatColor.RED : ChatColor.GREEN) + item.getName());
        meta.setLore(Lists.newArrayList(
            ChatColor.GRAY + "Cost: " + formatCost(price),
            LangUtil.hideString(GUI_TYPE.ordinal() + ";" + TileType.BUY.ordinal() + ";" + itemId)
        ));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        stack.setItemMeta(meta);
        return stack;
    }

    private void buy(Player player, ShopItem item) {
        Inventory inv = player.getInventory();

        ItemStack price = item.getPrice();

        if (!inv.contains(price.getType(), price.getAmount())) {
            player.sendMessage(ChatColor.RED + "You do not have enough " + Currency.valueOf(price.getType()).toString(true));
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

        inv.addItem(new ItemStack(item.getItem()));

        player.sendMessage(ChatColor.GREEN + "You purchased " + ChatColor.GOLD + item.getName());
        PlayerUtil.playSound(player, Sound.NOTE_PLING, 1f, 1.5f);
    }

    @Override
    public void click(Player player, String id, InventoryAction action) {
        if (action != InventoryAction.PICKUP_ALL) return; 
        
        String[] ids = id.split(";", 2);
        TileType tType = TileType.values()[Integer.parseInt(ids[0])];

        switch (tType) {
            case CATEGORY:
                open(player, Integer.parseInt(ids[1]));
                break;
            case BUY:
                buy(player, items.get(Integer.parseInt(ids[1])));
                break;
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("items", items);
        data.put("categories", categories);
        return data;
    }

    @SuppressWarnings("unchecked")
    public static ItemShop deserialize(Map<String, Object> args) {
        ItemShop shop = new ItemShop();

        for (Object item : (List<Object>) args.get("items"))
            shop.items.add((ShopItem) item);

        for (Object cat : (List<Object>) args.get("categories"))
            shop.categories.add((ShopCategory) cat);

        return shop;
    }

    private enum TileType {
        CATEGORY,
        BUY
    }
}
