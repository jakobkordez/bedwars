package cc.jkob.bedwars.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cc.jkob.bedwars.game.Tool.ToolStage;
import cc.jkob.bedwars.util.BlockUtil;

public class PlayerInventory {
    private Map<Tool, StagedTool> tools = new HashMap<>();
    private Armor armor = Armor.LEATHER;
    protected TeamColor color;

    public PlayerInventory(TeamColor color) {
        this.color = color;
    }

    public Armor getArmor() {
        return armor;
    }

    public void setArmor(Armor armor) {
        this.armor = armor;
    }

    public StagedTool getTool(Tool tool) {
        return tools.get(tool);
    }

    public void downgradeTools() {
        for (StagedTool t : tools.values())
            t.downgrade();
    }

    public void upgradeTool(Tool tool) {
        if (!tools.containsKey(tool)) {
            tools.put(tool, new StagedTool(tool));
        } else
            tools.get(tool).upgrade();
    }

    public ItemStack[] buildInventory() {
        List<ItemStack> inv = new ArrayList<>();

        inv.add(buildUnbreakable(Material.WOOD_SWORD));
        for (StagedTool tool : tools.values())
            if (tool.getItem() != null)
                inv.add(tool.getItem());

        return inv.toArray(new ItemStack[0]);
    }

    public ItemStack[] buildArmor() {
        return armor.build(color.getDyeColor());
    }

    public static ItemStack buildUnbreakable(Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.spigot().setUnbreakable(true);
        item.setItemMeta(meta);
        return item;
    }

    public static class StagedTool {
        public final Tool tool;
        private int stage = 0;

        public StagedTool(Tool tool) {
            this.tool = tool;
        }

        public void upgrade() {
            stage = tool.upgrade(stage);
        }

        public void downgrade() {
            stage = tool.downgrade(stage);
        }

        public ItemStack getItem() {
            return tool.get(stage);
        }

        public ToolStage getNextStage() {
            return tool.getStage(tool.upgrade(stage));
        }

        public boolean canUpgrade() {
            return stage < tool.upgrade(stage);
        }
    }

    public static enum Armor {
        LEATHER(Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS),
        CHAINMAIL(Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS),
        IRON(Material.IRON_LEGGINGS, Material.IRON_BOOTS),
        DIAMOND(Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS);

        private final Material leggings, boots;

        private Armor(Material leggings, Material boots) {
            this.leggings = leggings;
            this.boots = boots;
        }

        public ItemStack[] build(DyeColor color) {
            ItemStack[] armor = new ItemStack[4];
            if (this == LEATHER) {
                armor[0] = BlockUtil.getColoredArmor(boots, color);
                armor[1] = BlockUtil.getColoredArmor(leggings, color);
            } else {
                armor[0] = new ItemStack(boots);
                armor[1] = new ItemStack(leggings);
            }
            armor[2] = BlockUtil.getColoredArmor(Material.LEATHER_CHESTPLATE, color);
            armor[3] = BlockUtil.getColoredArmor(Material.LEATHER_HELMET, color);
            armor[3].addEnchantment(Enchantment.WATER_WORKER, 1);
            for (ItemStack ap : armor) {
                ItemMeta meta = ap.getItemMeta();
                meta.spigot().setUnbreakable(true);
                ap.setItemMeta(meta);
            }
            return armor;
        }
    }
}