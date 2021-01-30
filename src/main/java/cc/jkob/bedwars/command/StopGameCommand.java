package cc.jkob.bedwars.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Game;

public class StopGameCommand extends AdminCommand {
    public StopGameCommand(BedWarsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String[] getArgs() {
        return new String[]{"game"};
    }

    @Override
    public boolean execute(Player player, List<String> args) throws CommandException {
        Game game = plugin.getGameManager().getGame(args.get(0));

        game.stop();
        
        player.sendMessage(ChatColor.GREEN + "Game stopped");
        return true;
    }
    
}
