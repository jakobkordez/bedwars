package cc.jkob.bedwars.game;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import cc.jkob.bedwars.BedWarsPlugin;

public class GameScoreboard {
    private static final int MAX_LINES = 15;
    public static final Scoreboard EMPTY = BedWarsPlugin.getInstance().getServer().getScoreboardManager().getNewScoreboard();
    
    private final Game game;
    private final Scoreboard board;
    private Objective ob;

    public GameScoreboard(Game game) {
        this.game = game;

        board = BedWarsPlugin.getInstance().getServer().getScoreboardManager().getNewScoreboard();
    }

    public Scoreboard getBoard() {
        return board;
    }

    public void setObjective() {
        int i = MAX_LINES;

        if (ob != null) ob.unregister();
        
        ob = board.registerNewObjective("Teams", "dummy");
        ob.setDisplayName(ChatColor.YELLOW.toString() + ChatColor.BOLD + "   Bed Wars   ");
        ob.setDisplaySlot(DisplaySlot.SIDEBAR);

        ob.getScore(" ").setScore(i--);
        ob.getScore("Emerald III in 5:99").setScore(i--);
        ob.getScore("  ").setScore(i--);

        for (Team team : game.getTeams().values())
            ob.getScore(team.getColor().getPrefix() + " " + team.getName() + ": " + getTeamStatus(team)).setScore(i--);
        
        ob.getScore("   ").setScore(i--);
        ob.getScore(ChatColor.YELLOW + "jkob.cc").setScore(i--);
    }

    private String getTeamStatus(Team team) {
        if (team.hasBed()) return Symbols.CHECK.toString();
        
        int n = team.playersAlive();
        if (n == 0) return Symbols.CROSS.toString();

        return ChatColor.GREEN.toString() + ChatColor.BOLD + n;
    }

    private enum Symbols {
        CHECK("✓", ChatColor.GREEN),
        CROSS("✗", ChatColor.RED),
        HEART("♥", ChatColor.RED);

        private final String string;

        private Symbols(String symbol, ChatColor color) {
            string = color.toString() + ChatColor.BOLD + symbol + ChatColor.RESET;
        }

        @Override
        public String toString() {
            return string;
        }
    }
}
