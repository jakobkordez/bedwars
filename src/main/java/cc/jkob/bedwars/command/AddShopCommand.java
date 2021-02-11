package cc.jkob.bedwars.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Game;
import cc.jkob.bedwars.shop.Shopkeeper;
import cc.jkob.bedwars.shop.Shop.ShopType;

public class AddShopCommand extends AdminCommand {
    public AddShopCommand(BedWarsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "addshop";
    }

    @Override
    public String[] getArgs() {
        return new String[]{"game", "item|upgrade"};
    }

    @Override
    public boolean execute(Player player, List<String> args) throws CommandException {
        Game game = findGame(args.get(0));

        ShopType type;
        try {
            type = ShopType.valueOf(args.get(1).toUpperCase());
        } catch (Exception ignore) {
            throw new CommandException("Invalid shop type");
        }

        game.getShopkeepers().add(new Shopkeeper(player.getLocation(), type));

        player.sendMessage(type.getName() + ChatColor.RESET + ChatColor.GREEN + " added");
        return true;
    }
}
