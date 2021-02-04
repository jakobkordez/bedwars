package cc.jkob.bedwars.task;

import org.bukkit.scheduler.BukkitRunnable;

import cc.jkob.bedwars.game.GameScoreboard;

public class ScoreboardUpdateTask extends BukkitRunnable {
    private final GameScoreboard scoreboard;

    public ScoreboardUpdateTask(GameScoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    @Override
    public void run() {
        scoreboard.updateSidebar();
    }
}
