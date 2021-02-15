package cc.jkob.bedwars.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Game;
import cc.jkob.bedwars.game.Game.GameState;

public class StartGameCommand extends AdminCommand {
    public StartGameCommand(BedWarsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String[] getArgs() {
        return new String[]{"game"};
    }

    @Override
    public boolean execute(Player player, List<String> args) throws CommandException {
        Game game = findGame(args.get(0));

        if (game.getState() != GameState.WAITING)
            throw new CommandException("Game must be initialized");

        game.start();

        player.sendMessage(ChatColor.GREEN + "Game started");
        return true;
    }
}
