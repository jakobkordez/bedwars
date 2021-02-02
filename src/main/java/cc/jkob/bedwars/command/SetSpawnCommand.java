package cc.jkob.bedwars.command;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class SetSpawnCommand extends AdminCommand {
    public SetSpawnCommand(BedWarsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "setspawn";
    }

    @Override
    public String[] getArgs() {
        return new String[]{"game", "team"};
    }

    @Override
    public boolean execute(Player player, List<String> args) {
        Team team = findTeam(args.get(0), args.get(1));
        team.setSpawn(player.getLocation());

        player.sendMessage(team.getFormattedName() + ChatColor.RESET + ChatColor.GREEN + " spawn set");
        return true;
    }
}
