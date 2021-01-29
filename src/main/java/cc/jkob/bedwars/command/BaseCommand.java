package cc.jkob.bedwars.command;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Game;
import cc.jkob.bedwars.game.Team;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class BaseCommand {
    protected final BedWarsPlugin plugin;

    public BaseCommand(BedWarsPlugin plugin) {
        this.plugin = plugin;
    }

    public abstract String getName();

    public abstract String[] getArgs();

    public final String getUsage() {
        return String.format("/bw %s {%s}", getName(), String.join("} {", getArgs()));
    }

    public abstract boolean execute(Player player, List<String> args) throws CommandException;

    public abstract boolean requiresOp();

    protected final Game findGame(String gameName) throws CommandException {
        Game game = plugin.getGameManager().getGame(gameName);
        if (game == null) throw new CommandException("Game with that name doesn't exist");

        return game;
    }

    protected final Team findTeam(String gameName, String teamName) throws CommandException {
        Team team = findGame(gameName).getTeams().get(teamName);
        if (team == null) throw new CommandException("Team with that name doesn't exist");

        return team;
    }
}
