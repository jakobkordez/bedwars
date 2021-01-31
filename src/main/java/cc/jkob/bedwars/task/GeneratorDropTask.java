package cc.jkob.bedwars.task;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import cc.jkob.bedwars.game.Generator;

public class GeneratorDropTask extends BukkitRunnable {

    private final Generator generator;
    private Item drop;

    public GeneratorDropTask(Generator generator) {
        this.generator = generator;
    }

    @Override
    public void run() {
        Location pos = generator.getPos();
        Material material = generator.getType().getItem();

        if (drop != null && !drop.isDead()) {
            ItemStack stack = drop.getItemStack();

            int size = stack.getMaxStackSize() + 1;
            if (size > generator.getType().getMaxStack())
                size = generator.getType().getMaxStack();

            stack.setAmount(size);
        } else {
            drop = pos.getWorld().dropItem(pos.clone().add(0, 1, 0), new ItemStack(material));
            drop.setVelocity(new Vector());
        }
    }
}
