package cc.jkob.bedwars.util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Bed;

public class BlockUtil {
    public static void placeBed(Location feet, Location head) {
        BlockFace face = getBedFacing(feet, head);

        BlockState headS = head.getBlock().getState();
        headS.setType(Material.BED_BLOCK);
        Bed headD = new Bed(face);
        headD.setHeadOfBed(true);
        headS.setData(headD);

        BlockState feetS = feet.getBlock().getState();
        feetS.setType(Material.BED_BLOCK);
        Bed feetD = new Bed(face);
        feetD.setHeadOfBed(false);
        feetS.setData(feetD);

        feetS.update(true, false);
        headS.update(true, true);
    }

    public static BlockFace getBedFacing(Location feet, Location head) {
        int dx = (int) head.getX() - (int) feet.getX();
        int dz = (int) head.getZ() - (int) feet.getZ();

        int dd = dx*dx + dz*dz;
        if (dd == 1)
            if (dz == 0)
                return dx > 0 ? BlockFace.EAST : BlockFace.WEST;
            else
                return dz > 0 ? BlockFace.SOUTH : BlockFace.NORTH;
        
        return BlockFace.SELF;
    }
}
