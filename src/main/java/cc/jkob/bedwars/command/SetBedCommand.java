package cc.jkob.bedwars.command;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

        Bed bed = (Bed) b.getState().getData();
        BlockFace facing = bed.getFacing();
        if (bed.isHeadOfBed()) {
            bHead = p;
            bFeet = p.clone().add(-facing.getModX(), facing.getModY(), -facing.getModZ());
        } else {
            bFeet = p;
            bHead = p.clone().add(facing.getModX(), facing.getModY(), facing.getModZ());
        }

        team.setBed(bHead, bFeet);

        player.sendMessage(team.getFormattedName() + ChatColor.RESET + ChatColor.GREEN + " bed set");
        return true;
    }
}
