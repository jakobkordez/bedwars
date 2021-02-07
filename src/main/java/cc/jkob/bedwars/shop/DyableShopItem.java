package cc.jkob.bedwars.shop;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import cc.jkob.bedwars.game.Team;
import cc.jkob.bedwars.game.Game.PlayerData;
import cc.jkob.bedwars.util.BlockUtil;

public class DyableShopItem extends ShopItem {

    private Material material;

    public DyableShopItem(String name, ItemStack item, ItemStack price) {
        super(name, item, price);
        material = item.getType();
    }

    public DyableShopItem(String name, ItemStack item, ItemStack price, Material material) {
        super(name, item, price);
        this.material = material;
    }

    @Override
    public void give(PlayerData player) {
        Team team = player.getTeam();
        ItemStack it = BlockUtil.getColoredStack(material, team.getColor().getDyeColor(), item.getAmount());
        it.setItemMeta(item.getItemMeta());
        player.getPlayer().getInventory().addItem(it);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = super.serialize();
        if (item.getType() == material) return data;
        data.put("material", material.toString());
        return data;
    }
    
    public static ShopItem deserialize(Map<String, Object> args) {
        Material material = Material.getMaterial((String) args.get("material"));

        if (material == null)
            return new DyableShopItem(
                String.valueOf(args.get("name")),
                (ItemStack) args.get("item"),
                (ItemStack) args.get("price"));
        
        return new DyableShopItem(
            String.valueOf(args.get("name")),
            (ItemStack) args.get("item"),
            (ItemStack) args.get("price"),
            material
        );
    }
    
}
