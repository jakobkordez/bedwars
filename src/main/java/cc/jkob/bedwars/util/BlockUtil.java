package cc.jkob.bedwars.util;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Bed;
import org.bukkit.material.Wool;
import org.bukkit.util.Vector;

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

    private static BlockFace getBedFacing(Location feet, Location head) {
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

    public static Block getOtherBedBlock(Block b) {
        if (b.getType() != Material.BED_BLOCK) return null;
        
        Bed bed = (Bed) b.getState().getData();
        BlockFace facing = bed.getFacing();

        if (bed.isHeadOfBed())
            return b.getRelative(facing.getOppositeFace());
        else
            return b.getRelative(facing);
    }

    public static ItemStack getColoredStack(Material material, DyeColor color) {
        return getColoredStack(material, color, 1);
    }

    public static ItemStack getColoredStack(Material material, DyeColor color, int amount) {
        Wool w = new Wool(material);
        w.setColor(color);
        return w.toItemStack(amount);
    }

    public static ItemStack getColoredArmor(Material part, DyeColor color) {
        ItemStack armor = new ItemStack(part);
        LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
        meta.setColor(color.getFireworkColor());
        armor.setItemMeta(meta);
        return armor;
    }

    public static boolean blocks(Vector source, Vector target, Vector blocker) {
        Vector temp = target.clone().subtract(source);
        double dSq = temp.clone().crossProduct(source.clone().subtract(blocker)).lengthSquared() / temp.lengthSquared();
        return dSq <= 0.25;
    }
}
