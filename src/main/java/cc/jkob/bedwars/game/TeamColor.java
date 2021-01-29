package cc.jkob.bedwars.game;

import org.bukkit.ChatColor;

public enum TeamColor {
    RED(ChatColor.RED),
    BLUE(ChatColor.BLUE),
    GREEN(ChatColor.GREEN),
    YELLOW(ChatColor.YELLOW),
    AQUA(ChatColor.AQUA),
    WHITE(ChatColor.WHITE),
    PINK(ChatColor.LIGHT_PURPLE),
    GRAY(ChatColor.GRAY);

    private final ChatColor chatColor;

    TeamColor(ChatColor chatColor) {
        this.chatColor = chatColor;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }
}
