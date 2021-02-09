package cc.jkob.bedwars.game;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public enum TeamColor {
    RED(ChatColor.RED, "R", DyeColor.RED),
    BLUE(ChatColor.BLUE, "B", DyeColor.BLUE),
    GREEN(ChatColor.GREEN, "G", DyeColor.LIME),
    YELLOW(ChatColor.YELLOW, "Y", DyeColor.YELLOW),
    AQUA(ChatColor.AQUA, "A", DyeColor.CYAN),
    WHITE(ChatColor.WHITE, "W", DyeColor.WHITE),
    PINK(ChatColor.LIGHT_PURPLE, "P", DyeColor.PINK),
    GRAY(ChatColor.DARK_GRAY, "G", DyeColor.GRAY);

    private final ChatColor chatColor;
    private final String prefix;
    private final DyeColor dyeColor;

    TeamColor(ChatColor chatColor, String prefix, DyeColor dyeColor) {
        this.chatColor = chatColor;
        this.prefix = "" + chatColor + ChatColor.BOLD + prefix + ChatColor.RESET;
        this.dyeColor = dyeColor;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public String getPrefix() {
        return prefix;
    }

    public DyeColor getDyeColor() {
        return dyeColor;
    }
}
