package cc.jkob.bedwars.shop;

import java.util.Map;

import org.bukkit.inventory.ItemStack;

import cc.jkob.bedwars.game.PlayerData.GamePlayer;
import cc.jkob.bedwars.game.PlayerInventory.Armor;

public class ArmorItem extends ShopItem {
    public final Armor armor;

    public ArmorItem(int id, String name, ItemStack item, ItemStack price, Armor armor) {
        super(id, name, item, price);
        this.armor = armor;
    }

    @Override
    public boolean canBuy(GamePlayer player) {
        return player.getArmor().ordinal() < armor.ordinal();
    }

    @Override
    public void give(GamePlayer player) {
        player.setArmor(armor);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = super.serialize();
        data.put("armor", armor.toString());
        return data;
    }

    public static ArmorItem deserialize(Map<String, Object> args) {
        return new ArmorItem(
            (int) args.get("id"),
            (String) args.get("name"),
            (ItemStack) args.get("item"),
            (ItemStack) args.get("price"),
            Armor.valueOf((String) args.get("armor")));
    }
}
