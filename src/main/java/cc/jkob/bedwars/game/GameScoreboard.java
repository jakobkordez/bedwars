package cc.jkob.bedwars.game;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.task.ScoreboardUpdateTask;

public class GameScoreboard {
    private static final int MAX_LINES = 15;
    public static final Scoreboard EMPTY = BedWarsPlugin.getInstance().getServer().getScoreboardManager().getNewScoreboard();
    
    private final Game game;
    private final Scoreboard board;
    private Objective sidebar;
    private ScoreboardUpdateTask updateTask;

    public GameScoreboard(Game game) {
        this.game = game;

        board = BedWarsPlugin.getInstance().getServer().getScoreboardManager().getNewScoreboard();
    }

    public Scoreboard getBoard() {
        return board;
    }

    public boolean startTask() {
        if (updateTask != null) return false;

        updateTask = new ScoreboardUpdateTask(this);
        updateTask.runTaskTimer(BedWarsPlugin.getInstance(), 0, 10);
        return true;
    }

    public boolean stopTask() {
        if (updateTask == null) return false;

        updateTask.cancel();
        updateTask = null;
        return true;
    }

    private Score stageScore;
    private Map<TeamColor, Score> teamScores;
    private void setSidebar() {
        int i = MAX_LINES;

        if (sidebar != null) sidebar.unregister();
        
        sidebar = board.registerNewObjective("Teams", "dummy");
        sidebar.setDisplayName("" + ChatColor.YELLOW + ChatColor.BOLD + "    Bed Wars    ");
        sidebar.setDisplaySlot(DisplaySlot.SIDEBAR);

        sidebar.getScore(" ").setScore(i--);
        stageScore = sidebar.getScore(getNextStage());
        stageScore.setScore(i--);
        sidebar.getScore("  ").setScore(i--);

        teamScores = new HashMap<>();
        for (Team team : game.getTeams().values()) {
            Score ts = sidebar.getScore(getTeamStatus(team));
            teamScores.put(team.getColor(), ts);
            ts.setScore(i--);
        }
        
        sidebar.getScore("   ").setScore(i--);
        sidebar.getScore(ChatColor.YELLOW + "jkob.cc").setScore(i--);
    }

    public void updateSidebar() {
        if (sidebar == null) {
            setSidebar();
            return;
        }

        stageScore = updateScore(stageScore, getNextStage());
        for (Team team : game.getTeams().values()) {
            Score ts = teamScores.get(team.getColor());
            ts = updateScore(ts, getTeamStatus(team));
            teamScores.put(team.getColor(), ts);
        }
    }
    
    public Score updateScore(Score score, String entry) {
        if (score.getEntry() == entry) return score;

        int sc = score.getScore();
        board.resetScores(score.getEntry());
        score = sidebar.getScore(entry);
        score.setScore(sc);
        return score;
    }

    private String getNextStage() {
        GameCycle cycle = game.getGameCycle();
        long ticks = cycle.getStageRemainingTicks();
        return cycle.getStageTitle() + " in " + ChatColor.GREEN + String.format("%d:%02d", ticks/1200, (ticks%1200)/20);
    }

    private String getTeamStatus(Team team) {
        String status;
        int n = team.playersAlive();

        if (team.hasBed()) status = "" + Symbols.CHECK;
        else if (n == 0) status = "" + Symbols.CROSS;
        else status = "" + ChatColor.GREEN + n;

        return team.getColor().getPrefix() + " " + team.getName() + ": " + status;
    }

    private enum Symbols {
        CHECK("✓", ChatColor.GREEN),
        CROSS("✗", ChatColor.RED),
        HEART("♥", ChatColor.RED);

        private final String string;

        private Symbols(String symbol, ChatColor color) {
            string = "" + color + ChatColor.BOLD + symbol + ChatColor.RESET;
        }

        @Override
        public String toString() {
            return string;
        }
    }
}
