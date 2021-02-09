package cc.jkob.bedwars.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.GameManager;

public class JoinGameCommand extends PlayerCommand {
    public JoinGameCommand(BedWarsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String[] getArgs() {
        return new String[]{"game"};
    }

    @Override
    public boolean execute(Player player, List<String> args) throws CommandException {
        if (!GameManager.instance.getPlayer(player).joinGame(findGame(args.get(0))))
            throw new CommandException("Could not join the game");
        
        player.sendMessage(ChatColor.GREEN + "Game joined");
        return true;
    }
    
}
