package cc.jkob.bedwars.command;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Game;
import cc.jkob.bedwars.game.Generator;
import cc.jkob.bedwars.game.GeneratorType;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import java.util.List;

public class AddGenCommand extends AdminCommand {
    public AddGenCommand(BedWarsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "addgen";
    }

    @Override
    public String[] getArgs() {
        return new String[]{"game", "type"};
    }

    @Override
    public boolean execute(Player player, List<String> args) {
        Game game = findGame(args.get(0));

        GeneratorType genType;
        try {
            genType = GeneratorType.valueOf(args.get(1).toUpperCase());
        } catch (Exception e) {
            throw new CommandException("Invalid generator type");
        }

        Location loc = player.getLocation().getBlock().getLocation().clone().add(.5, 1, .5);
        loc.setPitch(90);

        game.getGenerators().add(new Generator(loc, genType));

        player.sendMessage(ChatColor.BOLD + genType.toString() + ChatColor.RESET + ChatColor.GREEN + " generator added");
        return true;
    }
}
