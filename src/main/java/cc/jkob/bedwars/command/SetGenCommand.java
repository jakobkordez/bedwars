package cc.jkob.bedwars.command;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class SetGenCommand extends AdminCommand {
    public SetGenCommand(BedWarsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "setgen";
    }

    @Override
    public String[] getArgs() {
        return new String[]{"game", "team"};
    }

    @Override
    public boolean execute(Player player, List<String> args) {
        Team team = findTeam(args.get(0), args.get(1));
        team.setGens(player.getLocation());

        player.sendMessage(team.getFormattedName() + ChatColor.RESET + ChatColor.GREEN + " generators set");
        return true;
    }
}
