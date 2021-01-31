package cc.jkob.bedwars.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ScoreboardUpdateEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
