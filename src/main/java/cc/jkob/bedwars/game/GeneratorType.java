package cc.jkob.bedwars.game;

import org.bukkit.ChatColor;

public enum GeneratorType {
    IRON(1200, ChatColor.GRAY),
    GOLD(4200, ChatColor.GOLD),
    DIAMOND(30000, ChatColor.AQUA),
    EMERALD(55000, ChatColor.GREEN);

    private final int interval;
    private final ChatColor chatColor;

    GeneratorType(int interval, ChatColor chatColor) {
        this.interval = interval;
        this.chatColor = chatColor;
    }

    public int getInterval() {
        return interval;
    }

    @Override
    public String toString() {
        return chatColor.toString() + super.toString().charAt(0) + super.toString().substring(1).toLowerCase();
    }
}
