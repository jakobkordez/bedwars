package cc.jkob.bedwars.command;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Game;
import cc.jkob.bedwars.game.Team;
import cc.jkob.bedwars.game.TeamColor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import java.util.List;

public class AddTeamCommand extends AdminCommand {
    public AddTeamCommand(BedWarsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "addteam";
    }

    @Override
    public String[] getArgs() {
        return new String[]{"game", "name", "color"};
    }

    @Override
    public boolean execute(Player player, List<String> args) {
        Game game = findGame(args.get(0));

        TeamColor col;
        try {
            col = TeamColor.valueOf(args.get(2).toUpperCase());
        } catch (Exception e) {
            throw new CommandException("Color is not valid");
        }

        for (Team t : game.getTeams().values()) {
            if (t.getName().equals(args.get(1)))
                throw new CommandException("Team with that name already exists");
            if (t.getColor().equals(col))
                throw new CommandException("Team with that color already exists");
        }

        Team team = new Team(args.get(1), col);
        game.getTeams().put(args.get(1), team);

        player.sendMessage(ChatColor.GREEN + "Team " + team.getFormattedName() + ChatColor.RESET + ChatColor.GREEN + " created");
        return true;
    }
}
