package cc.jkob.bedwars.shop;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import cc.jkob.bedwars.game.PlayerData.GamePlayer;
import cc.jkob.bedwars.util.LangUtil;

public class PotionItem extends ShopItem {

    public PotionItem(int id, String name, ItemStack item, ItemStack price) {
        super(id, name, item, price);

        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        item.setItemMeta(meta);
    }

    @Override
    public ItemStack getShopSlot(int cid, GamePlayer player, Map<Material, Integer> wallet) {
        ItemStack stack = super.getShopSlot(cid, player, wallet);

        PotionMeta meta = (PotionMeta) stack.getItemMeta();
        PotionEffect eff = meta.getCustomEffects().get(0);
        List<String> lore = meta.getLore();
        lore.add(1, "");
        lore.add(2, ChatColor.BLUE + LangUtil.capitalize(eff.getType().getName()) + " " + LangUtil.getRomanNumber(eff.getAmplifier() + 1) + " (" + eff.getDuration()/20 + " seconds)");
        meta.setLore(lore);

        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public Map<String, Object> serialize() {
        return super.serialize();
    }

    public static ShopItem deserialize(Map<String, Object> args) {
        return new PotionItem(
            (int) args.get("id"),
            (String) args.get("name"),
            (ItemStack) args.get("item"),
            (ItemStack) args.get("price"));
    }
}
