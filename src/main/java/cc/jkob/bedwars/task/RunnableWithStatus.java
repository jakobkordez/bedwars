package cc.jkob.bedwars.task;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class RunnableWithStatus extends BukkitRunnable {
    private long period = -1, time = 0;

    public long getRemainingTicks() {
        return Long.max((time - System.currentTimeMillis()) / 50, 0);
    }

    @Override
    public void run() {
        if (period >= 0) time = System.currentTimeMillis() + period * 50;
    }

    @Override
    public synchronized BukkitTask runTaskLater(Plugin plugin, long delay)
            throws IllegalArgumentException, IllegalStateException {
        time = System.currentTimeMillis() + delay * 50;
        return super.runTaskLater(plugin, delay);
    }

    @Override
    public synchronized BukkitTask runTaskLaterAsynchronously(Plugin plugin, long delay)
            throws IllegalArgumentException, IllegalStateException {
        time = System.currentTimeMillis() + delay * 50;
        return super.runTaskLaterAsynchronously(plugin, delay);
    }

    @Override
    public synchronized BukkitTask runTaskTimer(Plugin plugin, long delay, long period)
            throws IllegalArgumentException, IllegalStateException {
        this.period = period;
        time = System.currentTimeMillis() + delay * 50;
        return super.runTaskTimer(plugin, delay, period);
    }
    
    @Override
    public synchronized BukkitTask runTaskTimerAsynchronously(Plugin plugin, long delay, long period)
            throws IllegalArgumentException, IllegalStateException {
        this.period = period;
        time = System.currentTimeMillis() + delay * 50;
        return super.runTaskTimerAsynchronously(plugin, delay, period);
    }
}
