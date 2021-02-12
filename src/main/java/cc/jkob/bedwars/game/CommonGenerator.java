package cc.jkob.bedwars.game;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.util.LangUtil;

public class CommonGenerator extends Generator {
    public CommonGenerator(Location pos, GeneratorType type) {
        super(pos, type);
    }

    // transient
    private transient int tier;
    private transient Hologram hologram;
    private transient TextLine tierLine, timerLine;
    private transient BukkitTask hologramUpdater;
    
    @Override
    public void start() {
        if (running) return;

        super.start();
        tier = 1;

        hologram = HologramsAPI.createHologram(BedWarsPlugin.getInstance(), pos.clone().add(0, 5, 0));

        tierLine = hologram.appendTextLine(getTierString(tier));
        hologram.appendTextLine("");
        hologram.appendTextLine(type.getFormattedName(true));
        hologram.appendTextLine("");
        timerLine = hologram.appendTextLine(getTimerString(interval));
        hologram.appendItemLine(new ItemStack(type.getBlock()));

        hologramUpdater = new BukkitRunnable(){
            @Override
            public void run() {
                timerLine.setText(getTimerString(getTicksTillDrop()));
            }
        }.runTaskTimer(BedWarsPlugin.getInstance(), 0, 10);
    }

    @Override
    public void upgrade() {
        super.upgrade();
        tierLine.setText(getTierString(++tier));
    }

    @Override
    public void stop() {
        if (!running) return;

        hologramUpdater.cancel();
        hologramUpdater = null;
        
        super.stop();

        hologram.delete();
        hologram = null;
    }

    private static String getTierString(int tier) {
        return ChatColor.YELLOW + "Tier " + ChatColor.RED + LangUtil.getRomanNumber(tier);
    }

    private static String getTimerString(long ticks) {
        return ChatColor.YELLOW + "Spawning in " + ChatColor.RED + (int)(ticks/20) + ChatColor.YELLOW + " seconds";
    }
}
