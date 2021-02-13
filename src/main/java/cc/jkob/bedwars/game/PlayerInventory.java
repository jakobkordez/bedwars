package cc.jkob.bedwars.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cc.jkob.bedwars.util.BlockUtil;

public class PlayerInventory {
    private Pickaxe pickaxe = Pickaxe.NONE;
    private Axe axe = Axe.NONE;
    private Shears shears = Shears.NONE;
    private Armor armor = Armor.LEATHER;
    protected TeamColor color;

    public PlayerInventory(TeamColor color) {
        this.color = color;
    }

    public ItemStack[] buildInventory() {
        List<ItemStack> inv = new ArrayList<>();

        inv.add(buildUnbreakable(Material.WOOD_SWORD));
        if (shears.item != null) inv.add(shears.item);
        if (pickaxe.item != null) inv.add(pickaxe.item);
        if (axe.item != null) inv.add(axe.item);

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

    public void downgradeTools() {
        int p = pickaxe.ordinal() - 1;
        if (p > 0) pickaxe = Pickaxe.values()[p];

        int a = axe.ordinal() - 1;
        if (a > 0) axe = Axe.values()[a];
    }

    public void upgradePickaxe() {
        int p = pickaxe.ordinal() + 1;
        if (p < Pickaxe.values().length)
            pickaxe = Pickaxe.values()[p];
    }

    public void upgradeAxe() {
        int a = axe.ordinal() + 1;
        if (a < Axe.values().length)
            axe = Axe.values()[a];
    }

    public void upgradeShears() {
        int s = shears.ordinal() + 1;
        if (s < Shears.values().length)
            shears = Shears.values()[s];
    }

    public boolean upgradeArmor(Armor armor) {
        if (armor.ordinal() <= this.armor.ordinal())
            return false;
        this.armor = armor;
        return true;
    }

    private static enum Pickaxe {
        NONE,
        LEVEL_1(Material.WOOD_PICKAXE, new EnchPair(Enchantment.DIG_SPEED, 1)),
        LEVEL_2(Material.IRON_PICKAXE, new EnchPair(Enchantment.DIG_SPEED, 2)),
        LEVEL_3(Material.GOLD_PICKAXE, new EnchPair(Enchantment.DIG_SPEED, 3)),
        LEVEL_4(Material.DIAMOND_PICKAXE, new EnchPair(Enchantment.DIG_SPEED, 4));

        public final ItemStack item;

        private Pickaxe() {
            item = null;
        }

        private Pickaxe(Material material, EnchPair ...enchants) {
            item = buildUnbreakable(material);
            for (EnchPair p : enchants)
                item.addEnchantment(p.type, p.level);
        }
    }

    private static enum Axe {
        NONE,
        LEVEL_1(Material.WOOD_AXE, new EnchPair(Enchantment.DIG_SPEED, 1)),
        LEVEL_2(Material.STONE_AXE, new EnchPair(Enchantment.DIG_SPEED, 1)),
        LEVEL_3(Material.IRON_AXE, new EnchPair(Enchantment.DIG_SPEED, 2)),
        LEVEL_4(Material.DIAMOND_AXE, new EnchPair(Enchantment.DIG_SPEED, 3));

        public final ItemStack item;

        private Axe() {
            item = null;
        }

        private Axe(Material material, EnchPair ...enchants) {
            item = buildUnbreakable(material);
            for (EnchPair p : enchants)
                item.addEnchantment(p.type, p.level);
        }
    }

    private static enum Shears {
        NONE,
        LEVEL_1(Material.SHEARS);

        public final ItemStack item;

        private Shears() {
            item = null;
        }

        private Shears(Material material) {
            item = buildUnbreakable(material);
        }
    }

    private static enum Armor {
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
            for (ItemStack ap : armor) {
                ItemMeta meta = ap.getItemMeta();
                meta.spigot().setUnbreakable(true);
                ap.setItemMeta(meta);
            }
            return armor;
        }
    }

    private static class EnchPair {
        public Enchantment type;
        public int level;

        public EnchPair(Enchantment type, int level) {
            this.type = type;
            this.level = level;
        }
    }
}