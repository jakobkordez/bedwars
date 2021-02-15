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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cc.jkob.bedwars.game.PlayerData.GamePlayer;
import cc.jkob.bedwars.gui.GuiType;
import cc.jkob.bedwars.util.BlockUtil;
import cc.jkob.bedwars.util.LangUtil;

public class ItemShop extends Shop implements ConfigurationSerializable {
    private static final int ROWS = 6;
    private static final GuiType GUI_TYPE = GuiType.GAME_ITEM_SHOP;

    private Map<Integer, ShopItem> items = new HashMap<>();
    private List<ShopCategory> categories = new ArrayList<>();

    public List<ShopCategory> getCategories() {
        return categories;
    }

    @Override
    public void open(GamePlayer player) {
        open(player.player.getPlayer(), 1);
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
            for (int itemId : categories.get(cIndex - 1).getItems()) {
                ShopItem item = items.get(itemId);
                if (item == null) continue;
                invStacks[19 + i + 2 * (i / 7)] = item.getShopSlot(GUI_TYPE, wallet);
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
        meta.setLore(Lists.newArrayList(getTileData(TileType.CATEGORY, catId)));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public void click(GamePlayer player, String id, InventoryAction action) {
        if (action != InventoryAction.PICKUP_ALL) return; 
        
        String[] ids = id.split(";", 2);
        TileType tType = TileType.values()[Integer.parseInt(ids[0])];
        int sid = Integer.parseInt(ids[1]);

        switch (tType) {
            case CATEGORY:
                open(player.player.getPlayer(), sid);
                break;
            case BUY:
                ShopItem item = items.get(sid);
                if (item != null) item.tryBuy(player);
                break;
        }
    }

    static String getTileData(TileType type, int id) {
        return LangUtil.hideString(GUI_TYPE.ordinal() + ";" + type.ordinal() + ";" + id);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = new HashMap<>();
        data.put("items", items.values());
        data.put("categories", categories);
        return data;
    }

    @SuppressWarnings("unchecked")
    public static ItemShop deserialize(Map<String, Object> args) {
        ItemShop shop = new ItemShop();

        for (Object item : (List<Object>) args.get("items"))
            shop.items.put(((ShopItem) item).id, (ShopItem) item);

        for (Object cat : (List<Object>) args.get("categories"))
            shop.categories.add((ShopCategory) cat);

        return shop;
    }

    static enum TileType {
        CATEGORY,
        BUY
    }
}
