package cc.jkob.bedwars.game;

import org.bukkit.ChatColor;

public enum TeamColor {
    RED(ChatColor.RED, "R"),
    BLUE(ChatColor.BLUE, "B"),
    GREEN(ChatColor.GREEN, "G"),
    YELLOW(ChatColor.YELLOW, "Y"),
    AQUA(ChatColor.AQUA, "A"),
    WHITE(ChatColor.WHITE, "W"),
    PINK(ChatColor.LIGHT_PURPLE, "P"),
    GRAY(ChatColor.GRAY, "G");

    private final ChatColor chatColor;
    private final String prefix;

    TeamColor(ChatColor chatColor, String prefix) {
        this.chatColor = chatColor;
        this.prefix = chatColor.toString() + ChatColor.BOLD + prefix + ChatColor.RESET;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public String getPrefix() {
        return prefix;
    }
}
