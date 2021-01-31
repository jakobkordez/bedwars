package cc.jkob.bedwars.util;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.command.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BedWarsCommandExecutor implements CommandExecutor {
    HashMap<String, BaseCommand> subCommands = new HashMap<>();

    public BedWarsCommandExecutor(BedWarsPlugin plugin) {
        // Game
        addSubCommand(new AddGameCommand(plugin));
        addSubCommand(new AddGenCommand(plugin));
        addSubCommand(new SaveGameCommand(plugin));
        addSubCommand(new SetLobbyCommand(plugin));
        addSubCommand(new InitGameCommand(plugin));
        addSubCommand(new StartGameCommand(plugin));
        addSubCommand(new StopGameCommand(plugin));
        addSubCommand(new JoinGameCommand(plugin));

        // Team
        addSubCommand(new AddTeamCommand(plugin));
        addSubCommand(new SetBedCommand(plugin));
        addSubCommand(new SetGenCommand(plugin));
        addSubCommand(new SetSpawnCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        if (!command.getName().equals("bw")) return false;

        if (args.length < 1) return false;

        List<String> arg = Arrays.asList(args).subList(1, args.length);
        Player player = (Player) sender;

        BaseCommand subCommand = subCommands.get(args[0]);
        if (subCommand == null) {
            player.sendMessage(ChatColor.RED + "Invalid sub-command");
            return false;
        }

        if (subCommand.requiresOp())
            if (!player.isOp()) {
                player.sendMessage(ChatColor.RED + "You do not have permission.");
                return false;
            }

        if (subCommand.getArgs().length != arg.size()) {
            player.sendMessage(ChatColor.RED + subCommand.getUsage());
            return false;
        }

        try {
            return subCommand.execute(player, arg);
        } catch (CommandException e) {
            player.sendMessage(ChatColor.RED + e.getMessage());
            return false;
        }
    }

    private void addSubCommand(BaseCommand command) {
        subCommands.put(command.getName(), command);
    }
}
