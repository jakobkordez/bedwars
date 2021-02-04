package cc.jkob.bedwars.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum GeneratorType {
    IRON(30, 64, ChatColor.GRAY, Material.IRON_INGOT, Material.IRON_BLOCK),
    GOLD(90, 16, ChatColor.GOLD, Material.GOLD_INGOT, Material.GOLD_BLOCK),
    DIAMOND(600, 4, ChatColor.AQUA, Material.DIAMOND, Material.DIAMOND_BLOCK),
    EMERALD(1100, 2, ChatColor.GREEN, Material.EMERALD, Material.EMERALD_BLOCK);

    private final int interval, maxStack;
    private final ChatColor chatColor;
    private final Material item, block;

    GeneratorType(int interval, int maxStack, ChatColor chatColor, Material item, Material block) {
        this.interval = interval;
        this.maxStack = maxStack;
        this.chatColor = chatColor;
        this.item = item;
        this.block = block;
    }

    public int getInterval() {
        return interval;
    }

    public int getMaxStack() {
        return maxStack;
    }

    public Material getItem() {
        return item;
    }

    public Material getBlock() {
        return block;
    }

    public String getFormattedName() {
        return "" + chatColor + super.toString().charAt(0) + super.toString().substring(1).toLowerCase();
    }
}
