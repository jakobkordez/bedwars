package cc.jkob.bedwars.command;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Game;
import cc.jkob.bedwars.util.FileUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import java.util.List;

public class SaveGameCommand extends AdminCommand {
    public SaveGameCommand(BedWarsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String[] getArgs() {
        return new String[]{"game"};
    }

    @Override
    public boolean execute(Player player, List<String> args) {
        Game game = findGame(args.get(0));

        if (!FileUtil.saveGame(game)) throw new CommandException("Failed to save game");

        player.sendMessage(ChatColor.GREEN + "Game saved");
        return true;
    }
}
