package cc.jkob.bedwars.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Game;
import cc.jkob.bedwars.game.Game.State;

public class InitGameCommand extends AdminCommand {
    public InitGameCommand(BedWarsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "init";
    }

    @Override
    public String[] getArgs() {
        return new String[]{"game"};
    }

    @Override
    public boolean execute(Player player, List<String> args) throws CommandException {
        Game game = plugin.getGameManager().getGame(args.get(0));

        if (game.getState() != State.STOPPED)
            throw new CommandException("Game is " + game.getState().toString().toLowerCase());

        game.init();

        player.sendMessage(ChatColor.GREEN + "Game initialized");
        return true;
    }
}
