package cc.jkob.bedwars.util;

import java.util.stream.Stream;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import cc.jkob.bedwars.game.GameScoreboard;
import cc.jkob.bedwars.game.PlayerData;
import cc.jkob.bedwars.gui.Title;

public class PlayerUtil {
    // Messages //
    public static void send(Stream<PlayerData> players, String msg) {
        players.forEach(p -> sendMessage(p.getPlayer(), msg));
    }

    public static void send(PlayerData playerD, String msg) {
        sendMessage(playerD.getPlayer(), msg);
    }

    private static void sendMessage(Player player, String msg) {
        if (player == null) return;
        player.sendMessage(msg);
    }

    // Titles //
    public static void send(Stream<PlayerData> players, Title title) {
        players.forEach(p -> send(p, title));
    }

    public static void send(PlayerData playerD, Title title) {
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
    public static void play(Stream<PlayerData> players, Sound sound) {
        players.forEach(p -> play(p, sound));
    }

    public static void play(PlayerData player, Sound sound) {
        play(player, sound, 1f, 1f);
    }

    public static void play(Stream<PlayerData> players, Sound sound, float volume, float pitch) {
        players.forEach(p -> play(p, sound, volume, pitch));
    }

    public static void play(PlayerData playerD, Sound sound, float volume, float pitch) {
        Player player = playerD.getPlayer();
        if (player == null) return;
        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    // Scoreboard //
    public static void send(Stream<PlayerData> players, GameScoreboard scoreboard) {
        players.forEach(p -> send(p, scoreboard));
    }

    public static void send(PlayerData playerD, GameScoreboard scoreboard) {
        Player player = playerD.getPlayer();
        if (player == null) return;
        player.setScoreboard(scoreboard.getBoard());
    }

    public static void clearScoreboard(Stream<PlayerData> players) {
        players.forEach(p -> clearScoreboard(p));
    }

    public static void clearScoreboard(PlayerData playerD) {
        Player player = playerD.getPlayer();
        if (player == null) return;
        player.setScoreboard(GameScoreboard.EMPTY);
    }
}
