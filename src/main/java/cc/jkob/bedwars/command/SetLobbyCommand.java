package cc.jkob.bedwars.command;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import java.util.List;

public class SetLobbyCommand extends AdminCommand {
    public SetLobbyCommand(BedWarsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "setlobby";
    }

    @Override
    public String[] getArgs() {
        return new String[]{"game"};
    }

    @Override
    public boolean execute(Player player, List<String> args) throws CommandException {
        Game game = findGame(args.get(0));
        game.setLobby(player.getLocation());

        player.sendMessage(ChatColor.GREEN + "Game lobby set");
        return true;
    }
}
