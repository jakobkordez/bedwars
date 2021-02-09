package cc.jkob.bedwars.command;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Game;
import cc.jkob.bedwars.game.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import java.util.List;

public class AddGameCommand extends AdminCommand {
    public AddGameCommand(BedWarsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "addgame";
    }

    @Override
    public String[] getArgs() {
        return new String[]{"name"};
    }

    @Override
    public boolean execute(Player player, List<String> args) {
        if (GameManager.instance.getGameByName(args.get(0)) != null)
            throw new CommandException("Game with that name already exists");

        GameManager.instance.addGame(new Game(args.get(0), player.getWorld().getName()));

        player.sendMessage(ChatColor.GREEN + "Game created");
        return true;
    }
}
