package cc.jkob.bedwars.game;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import cc.jkob.bedwars.BedWarsPlugin;
import cc.jkob.bedwars.task.GeneratorDropTask;

public class Generator {
    protected Location pos;
    protected GeneratorType type;
    
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
    protected transient BukkitRunnable dropRunnable;
    protected transient BukkitTask dropTask;
    protected transient int interval;
    protected transient boolean running;

    public void start() {
        if (running) return;
        running = true;

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
        if (!running) return;
        running = false;

        dropTask.cancel();

        dropTask = null;
        dropRunnable = null;
    }
}
