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
        GameManager manager = plugin.getGameManager();

        if (manager.isPlayerInGame(player))
            throw new CommandException("You are already in a game");
        
        if (!findGame(args.get(0)).joinPlayer(player))
            throw new CommandException("Could not join the game");
        
        player.sendMessage(ChatColor.GREEN + "Game joined");
        return true;
    }
    
}
