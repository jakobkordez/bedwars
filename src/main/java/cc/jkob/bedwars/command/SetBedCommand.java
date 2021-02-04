package cc.jkob.bedwars.command;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Team;
import cc.jkob.bedwars.util.BlockUtil;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;
import org.bukkit.material.Bed;

import java.util.List;

public class SetBedCommand extends AdminCommand {
    public SetBedCommand(BedWarsPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "setbed";
    }

    @Override
    public String[] getArgs() {
        return new String[]{"game", "team"};
    }


    @Override
    public boolean execute(Player player, List<String> args) {
        Team team = findTeam(args.get(0), args.get(1));

        Location bFeet, bHead, p = player.getLocation().getBlock().getLocation();
        Block b = p.getBlock();

        if (b.getType() != Material.BED_BLOCK) throw new CommandException("You must stand on a bed");

        if (((Bed) b.getState().getData()).isHeadOfBed()) {
            bHead = p;
            bFeet = BlockUtil.getOtherBedBlock(b).getLocation();
        } else {
            bFeet = p;
            bHead = BlockUtil.getOtherBedBlock(b).getLocation();
        }

        team.setBed(bHead, bFeet);

        player.sendMessage(team.getFormattedName() + ChatColor.RESET + ChatColor.GREEN + " bed set");
        return true;
    }
}
