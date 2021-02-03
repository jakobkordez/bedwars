package cc.jkob.bedwars.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class PlayerUseEntityEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Player player;
    private int entityId;

    public PlayerUseEntityEvent(Player player, int entityId) {
        this.player = player;
        this.entityId = entityId;
    }

    public Player getPlayer() {
        return player;
    }

    public int getEntityId() {
        return entityId;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
