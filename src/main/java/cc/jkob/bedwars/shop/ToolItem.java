package cc.jkob.bedwars.shop;

import java.util.Map;

import com.google.common.collect.Lists;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cc.jkob.bedwars.game.Tool;
import cc.jkob.bedwars.game.PlayerData.GamePlayer;
import cc.jkob.bedwars.game.PlayerInventory.StagedTool;
import cc.jkob.bedwars.game.Tool.ToolStage;
import cc.jkob.bedwars.shop.Shop.Currency;
import cc.jkob.bedwars.util.PlayerUtil;

public class ToolItem extends ShopItem {
    private final Tool tool;

    public ToolItem(int id, Tool tool) {
        super(id, tool.name, null, null);
        this.tool = tool;
    }

    @Override
    public ItemStack getShopSlot(int cid, GamePlayer player, Map<Material, Integer> wallet) {
        ToolStage stage = getNextStage(player);
        ItemStack stack = stage.getItem().clone();
        ItemStack price = stage.getPrice();

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName((wallet.getOrDefault(price.getType(), 0) < price.getAmount() ? ChatColor.RED : ChatColor.GREEN) + getName());
        meta.setLore(Lists.newArrayList(
            ChatColor.GRAY + "Cost: " + Shop.formatCost(price),
            ItemShop.getTileData(cid, id)
        ));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public boolean canBuy(GamePlayer player) {
        StagedTool stagedTool = player.getTool(tool);
        if (stagedTool == null) return true;
        return stagedTool.canUpgrade();
    }

    @Override
    public void tryBuy(GamePlayer player) {
        if (!canBuy(player)) {
            player.player.getPlayer().sendMessage(ChatColor.RED + "You cannot buy that");
            return;
        }

        ToolStage stage = getNextStage(player);
        ItemStack price = stage.getPrice();

        if (!hasBalance(player, price)) {
            player.player.getPlayer().sendMessage(ChatColor.RED + "You do not have enough " + Currency.valueOf(price.getType()).toString(true));
            return;
        }

        takeBalance(player, price);
        give(player);
        player.player.getPlayer().sendMessage(ChatColor.GREEN + "You purchased " + ChatColor.GOLD + tool.name);
        PlayerUtil.play(player.player, Sound.NOTE_PLING, 1f, 1.5f);
    }

    @Override
    public void give(GamePlayer player) {
        player.upgradeTool(tool);
    }

    private ToolStage getNextStage(GamePlayer player) {
        StagedTool stagedTool = player.getTool(tool);
        if (stagedTool == null) return tool.getStage(0);
        else return stagedTool.getNextStage();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data = super.serialize();
        data.put("tool", tool);
        return data;
    }

    public static ShopItem deserialize(Map<String, Object> args) {
        return new ToolItem(
            (int) args.get("id"),
            (Tool) args.get("tool"));
    }
}
