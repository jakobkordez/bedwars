package cc.jkob.bedwars.util;

import java.util.stream.Stream;

import org.bukkit.Sound;

import cc.jkob.bedwars.game.GameScoreboard;
import cc.jkob.bedwars.game.PlayerData;
import cc.jkob.bedwars.gui.Title;

public class PlayerUtil {
    // Messages //
    public static void send(Stream<PlayerData> players, String msg) {
        players.forEach(p -> send(p, msg));
    }

    public static void send(PlayerData player, String msg) {
        player.getPlayer().sendMessage(msg);
    }

    // Titles //
    public static void send(Stream<PlayerData> players, Title title) {
        players.forEach(p -> send(p, title));
    }

    public static void send(PlayerData player, Title title) {
        resetTitle(player);
        PacketUtil.sendPacket(player.getPlayer(), title.getTitlePacket());
        PacketUtil.sendPacket(player.getPlayer(), title.getSubTitlePacket());
        PacketUtil.sendPacket(player.getPlayer(), title.getTimesPacket());
    }

    public static void resetTitle(PlayerData player) {
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

    public static void play(PlayerData player, Sound sound, float volume, float pitch) {
        player.getPlayer().playSound(player.getPlayer().getLocation(), sound, volume, pitch);
    }

    // Scoreboard //
    public static void send(Stream<PlayerData> players, GameScoreboard scoreboard) {
        players.forEach(p -> send(p, scoreboard));
    }

    public static void send(PlayerData player, GameScoreboard scoreboard) {
        player.getPlayer().setScoreboard(scoreboard.getBoard());
    }

    public static void clearScoreboard(Stream<PlayerData> players) {
        players.forEach(p -> clearScoreboard(p));
    }

    public static void clearScoreboard(PlayerData player) {
        player.getPlayer().setScoreboard(GameScoreboard.EMPTY);
    }
}
