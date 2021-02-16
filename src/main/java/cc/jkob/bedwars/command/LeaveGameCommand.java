package cc.jkob.bedwars.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.GameManager;
import cc.jkob.bedwars.game.PlayerData;

public class LeaveGameCommand extends PlayerCommand {
    public LeaveGameCommand(BedWarsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "l";
    }

    @Override
    public String[] getArgs() {
        return new String[]{};
    }

    @Override
    public boolean execute(Player player, List<String> args) throws CommandException {
        PlayerData playerD = GameManager.instance.getPlayer(player);

        if (!playerD.isInGame())
            throw new CommandException("You are not in a game");

        player.sendMessage(ChatColor.GREEN + "Leaving the game...");
        playerD.getGamePlayer().leaveGame();
        return true;
    }
}
