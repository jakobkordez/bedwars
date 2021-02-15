package cc.jkob.bedwars.command;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Game;
import cc.jkob.bedwars.game.GameManager;
import cc.jkob.bedwars.game.PlayerData;

public class AutoJoinCommand extends PlayerCommand {
    public AutoJoinCommand(BedWarsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "aj";
    }

    @Override
    public String[] getArgs() {
        return new String[]{};
    }

    @Override
    public boolean execute(Player player, List<String> args) throws CommandException {
        PlayerData playerD = GameManager.instance.getPlayer(player);

        if (playerD.isInGame() && playerD.getGamePlayer().tryRejoin()) {
            player.sendMessage(ChatColor.GREEN + "Rejoined " + playerD.getGamePlayer().game.getName());
            return true;
        }

        Game game = GameManager.instance.autoGetWaiting();
        if (game == null)
            throw new CommandException("Cannot autojoin any game");

        if (!playerD.joinGame(game))
            throw new CommandException("Failed to join " + game.getName());

        player.sendMessage(ChatColor.GREEN + "Joined " + game.getName());
        return true;
    }
}
