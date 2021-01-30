package cc.jkob.bedwars.game;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.task.GeneratorDropTask;

public class Generator {
    private Location pos;
    private GeneratorType type;
    
    public Generator(Location pos, GeneratorType type) {
        this.pos = pos;
        this.type = type;
    }

    public Location getPos() {
        return pos;
    }

    public GeneratorType getType() {
        return type;
    }

    public int getInterval() {
        return interval;
    }

    // transient
    private transient BukkitRunnable dropRunnable;
    private transient BukkitTask dropTask;
    private transient int interval;

    public void start() {
        interval = type.getInterval();

        dropRunnable = new GeneratorDropTask(this);
        dropTask = dropRunnable.runTaskTimer(BedWarsPlugin.getInstance(), interval, interval);
    }

    public void upgrade() {
        dropTask.cancel();

        interval -= 10;
        dropTask = dropRunnable.runTaskTimer(BedWarsPlugin.getInstance(), interval, interval);
    }

    public void stop() {
        dropTask.cancel();

        dropTask = null;
        dropRunnable = null;
    }
}
