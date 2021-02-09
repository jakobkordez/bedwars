package cc.jkob.bedwars.util;

import java.util.stream.Stream;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import cc.jkob.bedwars.game.PlayerData;
import cc.jkob.bedwars.gui.Title;

public class PlayerUtil {
    // Messages //
    public static void sendMessage(Stream<PlayerData> players, String ...msgs) {
        String msg = String.join("" + ChatColor.GRAY, msgs);
        players.forEach(p -> p.getPlayer().sendMessage(msg));
    }

    public static void sendMessage(PlayerData playerD, String ...msgs) {
        
    }

    // Titles //
    public static void sendTitle(Stream<PlayerData> players, Title title) {
        players.forEach(p -> sendTitle(p, title));
    }
    
    public static void sendTitle(PlayerData playerD, Title title) {
        resetTitle(playerD);
        Player player = playerD.getPlayer();
        if (player == null) return;
        PacketUtil.sendPacket(player, title.getTitlePacket());
        PacketUtil.sendPacket(player, title.getSubTitlePacket());
        PacketUtil.sendPacket(player, title.getTimesPacket());
    }

    public static void resetTitle(PlayerData playerD) {
        Player player = playerD.getPlayer();
        if (player == null) return;
        PacketUtil.sendPacket(player.getPlayer(), Title.getResetPacket());
    }

    // Sounds //
    public static void playSound(Stream<PlayerData> players, Sound sound) {
        players.forEach(p -> playSound(p, sound));
    }

    public static void playSound(PlayerData player, Sound sound) {
        playSound(player, sound, 1f, 1f);
    }

    public static void playSound(Stream<PlayerData> players, Sound sound, float volume, float pitch) {
        players.forEach(p -> playSound(p, sound, volume, pitch));
    }

    public static void playSound(PlayerData playerD, Sound sound, float volume, float pitch) {
        Player player = playerD.getPlayer();
        if (player == null) return;
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}
