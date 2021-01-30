package cc.jkob.bedwars.task;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.game.Generator;

public class GeneratorDropTask extends BukkitRunnable {

    private final Generator generator;

    public GeneratorDropTask(Generator generator) {
        this.generator = generator;
    }

    @Override
    public void run() {
        Location pos = generator.getPos();
        Material material = generator.getType().getMaterial();

        BedWarsPlugin.getInstance().getLogger().info("Dropping " + material.toString());
        pos.getWorld().dropItem(pos, new ItemStack(material));
    }
}
