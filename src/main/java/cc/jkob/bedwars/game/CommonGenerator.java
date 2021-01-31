package cc.jkob.bedwars.game;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import cc.jkob.bedwars.BedWarsPlugin;

public class CommonGenerator extends Generator {
    public CommonGenerator(Location pos, GeneratorType type) {
        super(pos, type);
    }

    // transient
    private transient Hologram hologram;
    
    @Override
    public void start() {
        if (running) return;

        super.start();

        hologram = HologramsAPI.createHologram(BedWarsPlugin.getInstance(), pos.clone().add(0, 4, 0));

        hologram.appendTextLine(type.toString() + ChatColor.RESET + " generator");
        hologram.appendTextLine("Spawning in...");
        hologram.appendItemLine(new ItemStack(type.getBlock()));
    }

    @Override
    public void stop() {
        if (!running) return;
        
        super.stop();

        hologram.delete();
        hologram = null;
    }
}
