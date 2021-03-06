package cc.jkob.bedwars.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Game;
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
        Game game = findGame(args.get(0));

        if (!GameManager.instance.getPlayer(player).joinGame(game))
            throw new CommandException("Failed to join " + game.getName());

        player.sendMessage(ChatColor.GREEN + "Joined " + game.getName());
        return true;
    }
}
