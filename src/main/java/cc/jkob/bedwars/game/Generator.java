package cc.jkob.bedwars.game;

import org.bukkit.Location;

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
    protected transient GeneratorDropTask dropTask;
    protected transient int interval;
    protected transient boolean running;

    public long getTicksTillDrop() {
        if (!running) return -1;

        return dropTask.getRemainingTicks();
    }

    public void start() {
        if (running) return;
        running = true;

        interval = type.getInterval();

        dropTask = new GeneratorDropTask(this);
        dropTask.runTaskTimer(BedWarsPlugin.getInstance(), interval, interval);
    }

    public void upgrade() {
        dropTask.cancel();

        interval -= 10;
        dropTask = new GeneratorDropTask(this, dropTask.getDrop());
        dropTask.runTaskTimer(BedWarsPlugin.getInstance(), 0, interval);
    }

    public void stop() {
        if (!running) return;
        running = false;

        dropTask.cancel();

        dropTask = null;
    }
}
