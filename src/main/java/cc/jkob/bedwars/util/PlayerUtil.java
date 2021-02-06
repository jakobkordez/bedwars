package cc.jkob.bedwars.util;

import java.util.stream.Stream;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import cc.jkob.bedwars.gui.Title;

public class PlayerUtil {
    // Titles //
    public static void sendTitle(Stream<Player> players, Title title) {
        players.parallel().forEach(p -> sendTitle(p, title));
    }
    
    public static void sendTitle(Player player, Title title) {
        resetTitle(player);
        PacketUtil.sendPacket(player, title.getTitlePacket());
        PacketUtil.sendPacket(player, title.getSubTitlePacket());
        PacketUtil.sendPacket(player, title.getTimesPacket());
    }

    public static void resetTitle(Player player) {
        PacketUtil.sendPacket(player, Title.getResetPacket());
    }

    // Sounds //
    public static void playSound(Stream<Player> players, Sound sound) {
        players.parallel().forEach(p -> playSound(p, sound));
    }

    public static void playSound(Player player, Sound sound) {
        playSound(player, sound, 1f, 1f);
    }

    public static void playSound(Stream<Player> players, Sound sound, float volume, float pitch) {
        players.parallel().forEach(p -> playSound(p, sound, volume, pitch));
    }

    public static void playSound(Player player, Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}
