package cc.jkob.bedwars.command;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.CommonGenerator;
import cc.jkob.bedwars.game.Game;
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

        Location loc = player.getLocation().getBlock().getLocation().clone().add(.5, 0, .5);

        game.getGenerators().add(new CommonGenerator(loc, genType));

        player.sendMessage(genType.getFormattedName() + ChatColor.RESET + ChatColor.GREEN + " generator added");
        return true;
    }
}
