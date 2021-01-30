package cc.jkob.bedwars.game;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum GeneratorType {
    IRON(24, ChatColor.GRAY, Material.IRON_INGOT),
    GOLD(84, ChatColor.GOLD, Material.GOLD_INGOT),
    DIAMOND(600, ChatColor.AQUA, Material.DIAMOND),
    EMERALD(1100, ChatColor.GREEN, Material.EMERALD);

    private final int interval;
    private final ChatColor chatColor;
    private final Material material;

    GeneratorType(int interval, ChatColor chatColor, Material material) {
        this.interval = interval;
        this.chatColor = chatColor;
        this.material = material;
    }

    public int getInterval() {
        return interval;
    }

    public Material getMaterial() {
        return material;
    }

    @Override
    public String toString() {
        return chatColor.toString() + super.toString().charAt(0) + super.toString().substring(1).toLowerCase();
    }
}
